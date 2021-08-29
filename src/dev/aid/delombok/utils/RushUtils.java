package dev.aid.delombok.utils;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.vfs.VirtualFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import groovy.util.XmlParser;

/**
 * 就靠你了
 *
 * @author: 04637@163.com
 * @date: 2020/8/4
 */
public class RushUtils {
    public static XmlParser xmlParser;
    // 提取字符串前面的空字符, 即缩进indent
    private static final Pattern pattern = Pattern.compile("^(\\s*)");
    private static String lombokPath;
    private static String toolsPath;

    static {
        URI uri = ZipUtils.getJarURI();
        lombokPath = "lombok.jar";
        toolsPath = "tools.jar";
        if (uri != null) {
            URI lombokUri = ZipUtils.getFile(uri, "lombok.jar");
            URI toolsUri = ZipUtils.getFile(uri, "tools.jar");
            if (lombokUri != null && toolsUri != null) {
                lombokPath = lombokUri.getPath();
                if (lombokPath.charAt(0) == '/') {
                    lombokPath = lombokPath.replaceFirst("/", "");
                }
                toolsPath = toolsUri.getPath();
                if (toolsPath.charAt(0) == '/') {
                    toolsPath = toolsPath.replaceFirst("/", "");
                }
            }
        }
        // 初始化xml解析器
        try {
            xmlParser = new XmlParser();
        } catch (ParserConfigurationException | SAXException parserConfigurationException) {
            parserConfigurationException.printStackTrace();
        }
    }

    private RushUtils() {
    }

    /**
     * 1. 备份 srcDir 至 tmpDir/timestamp 中
     * 2. 在 baseDir 中执行 delombok, 生成至 tmpDir/target 中
     * 3. 将 targetDir 中的代码处理后, 覆盖 srcDir
     *
     * @param baseDir rush的根目录, 所有命令将在此目录中执行
     * @param srcDir  需要 delombok 的源码目录, 注意该目录传值为 baseDir 的相对路径
     * @return errMsg     错误信息
     */
    public static String rush(String baseDir, String srcDir, ProgressIndicator indicator, Collection<VirtualFile> specifiedFiles, ConsoleView consoleView) {
        Printer printer = new Printer(consoleView);
        printer.print("==>start: " + baseDir + "/" + srcDir);
        indicator.setIndeterminate(false);
        indicator.setFraction(0);
        // 如果是相对路径, 拼接绝对路径
        if (StringUtils.isEmpty(srcDir)) {
            throw new IllegalArgumentException("未指定src目录");
        }
        srcDir = baseDir + "/" + srcDir;
        String tmpDir = baseDir + "/delombok";
        indicator.setFraction(0.1);
        try {
            // 1. 备份源文件目录
            FileUtils.copyDirectoryToDirectory(new File(srcDir),
                    new File(tmpDir + "/src-bak"));
            printer.print("\t==>copy: from " + srcDir + " to " + tmpDir + "/src-bak");
            indicator.setFraction(0.2);
            // 2. 反编译lombok注解
            String targetDir = tmpDir + "/target";
            File targetDirFile = new File(targetDir);
            deleteDir(targetDirFile);
            String cmd = "cmd /c " +
                    "java -cp \"" + lombokPath + ";" + toolsPath + "\" lombok.launch.Main delombok \""
                    + srcDir
                    + "\" -d \"" + targetDir + "\" -e UTF-8 --onlyChanged " +
                    "-f indent:4 " +
                    "-f generateDelombokComment:skip " +
                    "-f javaLangAsFQN:skip";
            printer.print("\t==>delombok...");
            Process process = Runtime.getRuntime().exec(cmd, null, new File(baseDir));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(),
                    StandardCharsets.UTF_8));
            StringBuilder processResult = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                processResult.append(line);
            }
            if (!StringUtils.isEmpty(processResult)) {
                // 忽略
                System.err.println("\t==>Processing failed: " + processResult);
                processResult = new StringBuilder();
            }
            int exitCode = process.waitFor();
            if (StringUtils.isEmpty(processResult)) {
                printer.print("\t==>Processing over!", ConsoleViewContentType.LOG_INFO_OUTPUT);
            } else {
                printer.print("\t==>Processing failed: " + processResult, ConsoleViewContentType.LOG_ERROR_OUTPUT);
            }
            if (exitCode != 0) {
                return "Delombok failed!";
            }
            indicator.setFraction(0.4);
            if (specifiedFiles == null) {
                // 未指定文件, 则覆写所有src
                traverseDir(new File(srcDir), srcDir.replaceAll("/", "\\\\"),
                        targetDir.replaceAll("/", "\\\\"), printer, indicator);
            } else {
                // 遍历指定文件集合
                traverseFiles(specifiedFiles, srcDir, targetDir, printer, indicator);
            }

            printer.print("==>done!", ConsoleViewContentType.LOG_INFO_OUTPUT);
            indicator.setFraction(1);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            printer.print("==>failed: " + e.getMessage(), ConsoleViewContentType.LOG_ERROR_OUTPUT);
        }
        return null;
    }

    /**
     * 处理多模块项目
     *
     * @param baseDir   项目路径
     * @param srcDir    代码路径
     * @param indicator 进度条
     * @param modules   多模块名列表
     */
    public static String dealModules(String baseDir, String srcDir, ProgressIndicator indicator, List<String> modules, Collection<VirtualFile> specifiedFiles, ConsoleView consoleView) {
        Printer printer = new Printer(consoleView);
        File rootSrc = new File(baseDir + "/" + srcDir);
        if (rootSrc.exists()) {
            String result = rush(baseDir, srcDir, indicator, specifiedFiles, consoleView);
            if (!StringUtils.isEmpty(result)) {
                return result;
            }
        }
        for (String module : modules) {
            String tmpBase = baseDir + "/" + module;
            if (!new File(tmpBase).exists()) {
                // 如果模块不存在，跳过
                continue;
            }
            printer.print("=>module: " + module);
            String result;
            result = rush(tmpBase, srcDir, indicator, specifiedFiles, consoleView);
            if (!StringUtils.isEmpty(result)) {
                return result;
            }
        }
        return null;
    }

    /**
     * 将源文件替换为目标文件, 请注意源文件需进行备份!
     *
     * @param originalPath 源文件
     * @param targetPath   目标文件, 会对其新增块包裹折叠注释
     */
    private static void patchFile(String originalPath, String targetPath) {
        try {
            List<String> originalLines = Files.readAllLines(new File(originalPath).toPath());
            List<String> targetLines = Files.readAllLines(new File(targetPath).toPath());

            Patch<String> patch = DiffUtils.diff(originalLines, targetLines);
            for (AbstractDelta delta : patch.getDeltas()) {
                // 仅对新增块做操作
                if (delta.getType() == DeltaType.INSERT) {
                    List<String> lines = delta.getTarget().getLines();
                    int emptyIndex = 0;
                    // 自定义折叠注释缩进
                    String indent = "";
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (line.isEmpty()) {
                            // 获取首个非空行位置
                            emptyIndex = i + 1;
                        } else {
                            // 获取首个非空行代码的缩进
                            Matcher matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                indent = matcher.group(1);
                            }
                            break;
                        }
                    }
                    // 在首个非空行位置及末尾位置插入自定义折叠注释
                    lines.add(emptyIndex, indent + "//<editor-fold defaultstate=\"collapsed\" desc=\"delombok\">");
                    emptyIndex = lines.size();
                    for (int i = lines.size() - 1; i >= 0; i--) {
                        String line = lines.get(i);
                        if (line.isEmpty()) {
                            emptyIndex = i;
                        } else {
                            break;
                        }
                    }
                    lines.add(emptyIndex, indent + "//</editor-fold>");
                }
            }
            List<String> result = patch.applyTo(originalLines);
            try (FileWriter fw = new FileWriter(originalPath, StandardCharsets.UTF_8)) {
                for (String s : result) {
                    fw.write(s + "\n");
                }
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | PatchFailedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历目录并覆写源码
     *
     * @param dir       待遍历目录
     * @param srcDir    源码目录, 该目录文件将被生成的文件替换
     * @param targetDir delombok 生成目录
     */
    private static void traverseDir(File dir, String srcDir, String targetDir, Printer printer, ProgressIndicator indicator) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        int i = 0;
        for (File f : files) {
            if (f.isDirectory()) {
                traverseDir(f, srcDir, targetDir, printer, indicator);
            } else if (f.isFile() && f.getName().endsWith(".java")) {
                String targetPath = f.getAbsolutePath().replace(srcDir, targetDir);
                if (new File(targetPath).exists()) {
                    // 如果delombok文件存在, 则进行整合覆写
                    patchFile(f.getAbsolutePath(), targetPath);
                    printer.print("\t\t=>file: " + f.getAbsolutePath());
                }
            }
            indicator.setFraction(0.4 + (0.6 / files.length) * ++i);
        }
    }

    /**
     * 遍历给定文件集合并覆写源码
     *
     * @param files     指定文件集合
     * @param srcDir    源码目录
     * @param targetDir delombok 生成目录
     */
    private static void traverseFiles(Collection<VirtualFile> files, String srcDir, String targetDir, Printer printer, ProgressIndicator indicator) {
        int i = 0;
        for (VirtualFile file : files) {
            String targetPath = file.getCanonicalPath().replace(srcDir, targetDir);
            if (file.getName().endsWith(".java") && new File(targetPath).exists()) {
                // 如果delombok文件存在, 则进行整合覆写
                patchFile(file.getPath(), targetPath);
                printer.print("\t\t=>file: " + file.getCanonicalPath());
            }
            indicator.setFraction(0.4 + (0.6 / files.size()) * ++i);
        }
    }

    private static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            return false;
        }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}

class Printer {
    private final ConsoleView consoleView;

    public Printer(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }

    public void print(String msg, ConsoleViewContentType type) {
        if (consoleView != null) {
            consoleView.print(msg + "\n", type);
        }
    }

    public void print(String msg) {
        print(msg, ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
    }
}

package dev.aid.delombok;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.aid.delombok.utils.FoldUtils;
import dev.aid.delombok.utils.RushUtils;
import groovy.util.Node;
import groovy.util.NodeList;
import groovy.xml.QName;

/**
 * Build菜单中的delombok动作
 *
 * @author: 04637@163.com
 * @date: 2020/8/1
 */
public class DelombokAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        FileDocumentManager.getInstance().saveAllDocuments();
        List<String> moduleList = new ArrayList<>();
        try {
            Node node = RushUtils.xmlParser.parse(project.getBasePath() + "/.idea/compiler.xml");
            NodeList modules = node.getAt(QName.valueOf("component"))
                    .getAt("annotationProcessing")
                    .getAt("profile")
                    .getAt("module");
            for (Object module : modules) {
                Node moduleNode = (Node) module;
                String moduleName = (String) moduleNode.attribute("name");
                moduleList.add(moduleName);
            }
        } catch (IOException | SAXException ioException) {
            ioException.printStackTrace();
        }
        Task.WithResult<String, RuntimeException> task = new Task.WithResult<>(project, "Delombok code...", false) {
            @Override
            protected String compute(@NotNull ProgressIndicator progressIndicator) throws RuntimeException {
                if (moduleList.isEmpty()) {
                    return RushUtils.rush(project.getBasePath(), "src", progressIndicator, null);
                } else {
                    return RushUtils.dealModules(project.getBasePath(), "src", progressIndicator, moduleList, null);
                }
            }
        };
        ProgressManager.getInstance().run(task);
        String msg = task.getResult();
        if (StringUtils.isEmpty(msg)) {
            FoldUtils.reloadFiles(project);
        }
    }
}

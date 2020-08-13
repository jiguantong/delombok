package dev.aid.delombok.utils;

import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * 隐藏代码
 *
 * @author: 04637@163.com
 * @date: 2020/8/5
 */
public class FoldUtils {

    public static void fold(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        CodeFoldingManager foldingManager = CodeFoldingManager.getInstance(project);
        if (editor == null) {
            return;
        }
        foldingManager.updateFoldRegions(editor);
        final FoldRegion[] foldRegions = editor.getFoldingModel().getAllFoldRegions();
        Runnable foldProcess = () -> {
            for (FoldRegion region : foldRegions) {
                String text = region.getPlaceholderText();
                if (text.contains("delombok")) {
                    if (region.isExpanded()) {
                        region.setExpanded(false);
                    }
                }
            }
        };
        editor.getFoldingModel().runBatchFoldingOperation(foldProcess);
    }

    public static void reloadFiles(Project project) {
        VirtualFile[] vfs = FileEditorManager.getInstance(project).getOpenFiles();
        for (VirtualFile vf : vfs) {
            FileEditorManager.getInstance(project).closeFile(vf);
            FileDocumentManager.getInstance()
                    .reloadFromDisk(FileDocumentManager.getInstance().getDocument(vf));
            FileEditorManager.getInstance(project).openFile(vf, false);
        }
    }
}

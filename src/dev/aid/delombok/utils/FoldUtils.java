package dev.aid.delombok.utils;

import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;

/**
 * todo
 *
 * @author: 04637@163.com
 * @date: 2020/8/5
 */
public class FoldUtils {

    public static void fold(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        CodeFoldingManager foldingManager = CodeFoldingManager.getInstance(project);
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
}

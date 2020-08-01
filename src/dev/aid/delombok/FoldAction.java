package dev.aid.delombok;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;

import org.jetbrains.annotations.NotNull;

/**
 * todo
 *
 * @author: 04637@163.com
 * @date: 2020/8/1
 */
public class FoldAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        System.out.println("123");
        editor.getFoldingModel().runBatchFoldingOperation(() -> {

        });
    }
}

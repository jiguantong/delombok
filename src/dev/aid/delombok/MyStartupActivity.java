package dev.aid.delombok;

import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;

import org.jetbrains.annotations.NotNull;

/**
 * 项目打开时动作
 *
 * @author: 04637@163.com
 * @date: 2020/8/2
 */
public class MyStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                CodeFoldingManager foldingManager = CodeFoldingManager.getInstance(project);
                foldingManager.updateFoldRegions(editor);
                final FoldRegion[] foldRegions = editor.getFoldingModel().getAllFoldRegions();
                Runnable foldProcess = () -> {
                    for (FoldRegion region : foldRegions) {
                        String text = region.getPlaceholderText();
                        if (text.contains("delombok:")) {
                            if(region.isExpanded()){
                                System.out.println("fold: " + text);
                                region.setExpanded(false);
                            }
                        }
                    }
                };
                editor.getFoldingModel().runBatchFoldingOperation(foldProcess);
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {

            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {

            }
        });
    }

}

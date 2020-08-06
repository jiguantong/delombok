package dev.aid.delombok.activity;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;

import org.jetbrains.annotations.NotNull;

import dev.aid.delombok.utils.FoldUtils;


/**
 * 项目打开时动作
 *
 * @author: 04637@163.com
 * @date: 2020/8/2
 */
public class MyStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        // 消息总线
        MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                FoldUtils.fold(project);
            }
        });
        messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                FoldUtils.fold(project);
            }
        });
        FoldUtils.fold(project);
    }
}

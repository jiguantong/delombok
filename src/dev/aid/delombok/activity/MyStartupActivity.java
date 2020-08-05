package dev.aid.delombok.activity;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBus;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.aid.delombok.utils.FoldUtils;


/**
 * 项目打开时动作
 *
 * @author: 04637@163.com
 * @date: 2020/8/2
 */
public class MyStartupActivity implements StartupActivity {

    private static boolean needFold;

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
        messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                if (needFold) {
                    FoldUtils.fold(project);
                    setNeedFold(false);
                }
            }
        });
    }

    public static void setNeedFold(boolean needFold) {
        MyStartupActivity.needFold = needFold;
    }
}

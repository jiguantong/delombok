package dev.aid.delombok;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import dev.aid.delombok.utils.FoldUtils;
import dev.aid.delombok.utils.RushUtils;

/**
 * Build菜单中的delombok动作
 *
 * @author: 04637@163.com
 * @date: 2020/8/1
 */
public class DelombokAction extends AnAction {
    private static ConsoleView consoleView = null;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        FileDocumentManager.getInstance().saveAllDocuments();
        // ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("Delombok");
        // // toolWindow.show(() -> {
        // // });
        // if (consoleView == null) {
        //     consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(e.getProject()).getConsole();
        // } else {
        //     consoleView.clear();
        // }
        // Content consoleContent = toolWindow.getContentManager().findContent("Console");
        // if (consoleContent == null) {
        //     consoleContent = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "Console", false);
        //     toolWindow.getContentManager().addContent(consoleContent);
        // }
        Task.WithResult<String, RuntimeException> task = new Task.WithResult<>(project, "Delombok code...", false) {
            @Override
            protected String compute(@NotNull ProgressIndicator progressIndicator) throws RuntimeException {
                return RushUtils.rush(project.getBasePath(), "src", consoleView, progressIndicator, null);
            }
        };
        ProgressManager.getInstance().run(task);
        String msg = task.getResult();
        if (StringUtils.isEmpty(msg)) {
            FoldUtils.reloadFiles(project);
            FoldUtils.fold(project);
        }
    }
}

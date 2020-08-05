package dev.aid.delombok;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

import org.jetbrains.annotations.NotNull;

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
        ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("Delombok");
        toolWindow.show(() -> {
        });
        if (consoleView == null) {
            consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(e.getProject()).getConsole();
        } else {
            consoleView.clear();
        }
        Content consoleContent = toolWindow.getContentManager().findContent("Console");
        if (consoleContent == null) {
            consoleContent = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "Console", false);
            toolWindow.getContentManager().addContent(consoleContent);
        }
        new Thread(() -> RushUtils.rush(project.getBasePath(), "src", consoleView)).start();
    }
}

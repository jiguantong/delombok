package dev.aid.delombok.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;

import org.jetbrains.annotations.NotNull;

/**
 * todo
 *
 * @author: 04637@163.com
 * @date: 2020/8/5
 */
public class DlCheckinHandlerFactory extends CheckinHandlerFactory {
    @NotNull
    @Override
    public CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
        Project project = panel.getProject();
        return new DlCheckinHandler(project, panel);
    }
}

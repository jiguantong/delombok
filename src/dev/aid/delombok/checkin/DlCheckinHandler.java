package dev.aid.delombok.checkin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.NonFocusableCheckBox;
import com.intellij.util.PairConsumer;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import dev.aid.delombok.utils.RushUtils;

/**
 * 提交前动作
 *
 * @author: 04637@163.com
 * @date: 2020/8/5
 */
public class DlCheckinHandler extends CheckinHandler {

    private static final String ACTIVATED_OPTION_NAME = "DELOMBOK_PRE_COMMIT";

    private JCheckBox checkBox;
    private final Project project;
    private final CheckinProjectPanel checkinPanel;

    public DlCheckinHandler(Project project, CheckinProjectPanel checkinPanel) {
        this.project = project;
        this.checkinPanel = checkinPanel;
    }

    @Nullable
    @Override
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        this.checkBox = new NonFocusableCheckBox("Delombok code");
        return new MyRefreshableOnComponent(checkBox);
    }

    @Override
    public ReturnResult beforeCheckin(@Nullable CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
        if (!this.checkBox.isSelected()) {
            return ReturnResult.COMMIT;
        }
        Collection<VirtualFile> affectedFiles = checkinPanel.getVirtualFiles();
        final String[] msg = {""};
        Task.Modal task = new Task.Modal(project, "Delombok code...", false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                msg[0] = RushUtils.rush(project.getBasePath(), "src", null, progressIndicator, affectedFiles);
            }
        };
        ProgressManager.getInstance().run(task);
        if (StringUtils.isEmpty(msg[0])) {
            return ReturnResult.COMMIT;
        } else {
            handleError(msg[0]);
            return ReturnResult.CANCEL;
        }
    }

    @Override
    public void checkinSuccessful() {
        super.checkinSuccessful();
    }

    private void handleError(String msg) {
        Messages.showErrorDialog(project, msg, "Error Delombok Code");
    }


    private class MyRefreshableOnComponent implements RefreshableOnComponent {
        private final JCheckBox checkBox;

        private MyRefreshableOnComponent(JCheckBox checkBox) {
            this.checkBox = checkBox;
        }


        @Override
        public JComponent getComponent() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(checkBox);
            boolean dumb = DumbService.isDumb(project);
            checkBox.setEnabled(!dumb);
            checkBox.setToolTipText(dumb ? "Delombok code is impossible until indices are up-to-date" : "");
            return panel;
        }

        @Override
        public void refresh() {
            // nothing to do
        }

        @Override
        public void saveState() {
            PropertiesComponent.getInstance(project)
                    .setValue(ACTIVATED_OPTION_NAME,
                            Boolean.toString(checkBox.isSelected()));
        }

        @Override
        public void restoreState() {
            PropertiesComponent props = PropertiesComponent.getInstance(project);
            checkBox.setSelected(props.getBoolean(ACTIVATED_OPTION_NAME));
        }
    }
}

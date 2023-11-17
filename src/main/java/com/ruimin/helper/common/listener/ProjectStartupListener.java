package com.ruimin.helper.common.listener;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.ruimin.helper.common.util.NotificationUtil;
import com.ruimin.helper.dtst.constans.DataSetConstants;
import com.ruimin.helper.rqlx.constans.RqlxConstants;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.URI;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/15 上午 03:33
 * @description
 */
public class ProjectStartupListener implements ProjectActivity {

    private static final Logger LOGGER = Logger.getInstance(ProjectStartupListener.class);

    /**
     * 是否已经通知
     */
    private volatile boolean hasNotice = false;

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        associateFileType();
        support();
        return null;
    }



    /**
     * 关联文件类型
     */
    private void associateFileType() {

        ApplicationManager.getApplication().invokeLater(()->{
            WriteAction.run(()->{
                FileTypeManager typeManager = FileTypeManager.getInstance();
                FileType rqlxFileType = typeManager.getFileTypeByExtension(RqlxConstants.RQLX_FILE_EXTENSION);
                FileType dtstFileType = typeManager.getFileTypeByExtension(DataSetConstants.DTST_FILE_EXTENSION);
                FileType dtmdFileType = typeManager.getFileTypeByExtension(DataSetConstants.DTMD_FILE_EXTENSION);
                if (!(rqlxFileType instanceof XmlFileType)) {
                    typeManager.associateExtension(XmlFileType.INSTANCE, RqlxConstants.RQLX_FILE_EXTENSION);
                }
                if (!(dtstFileType instanceof XmlFileType)) {
                    typeManager.associateExtension(XmlFileType.INSTANCE, DataSetConstants.DTST_FILE_EXTENSION);
                }
                if (!(dtmdFileType instanceof XmlFileType)) {
                    typeManager.associateExtension(XmlFileType.INSTANCE, DataSetConstants.DTMD_FILE_EXTENSION);
                }
            });
        });
    }


    /**
     * 支持
     */
    private void support() {
        if (hasNotice) {
            return;
        }
        AnAction gitHubStarAction = new NotificationAction("Github star") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://github.com/SvenShi/IfsSnowHelper"));
                    }
                } catch (Exception ex) {
                    LOGGER.error("打开链接失败:https://github.com/SvenShi/IfsSnowHelper", ex);
                }
            }
        };

        AnAction giteeStarAction = new NotificationAction("Gitee star") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://gitee.com/svenshi/IfsSnowHelper"));
                    }
                } catch (Exception ex) {
                    LOGGER.error("打开链接失败:https://gitee.com/svenshi/IfsSnowHelper", ex);
                }
            }
        };
        AnAction reviewsAction = new NotificationAction("五星好评") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://plugins.jetbrains.com/plugin/21046-ifssnowhelper/reviews"));
                    }
                } catch (Exception ex) {
                    LOGGER.error("打开链接失败:https://plugins.jetbrains.com/plugin/21046-ifssnowhelper/reviews", ex);
                }
            }
        };

        NotificationUtil.notify("支持IfsSnowHelper", "您的Star对我非常重要！Star本项目就是对作者开发维护的最大支持！！",
                gitHubStarAction, giteeStarAction, reviewsAction);

        hasNotice = true;
    }
}

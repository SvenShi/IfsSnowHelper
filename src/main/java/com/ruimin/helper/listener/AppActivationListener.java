package com.ruimin.helper.listener;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.IdeFrame;
import com.ruimin.helper.common.util.NotificationUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;

/**
 * @author shiwei
 * @date 2023/11/14 下午 02:52
 * @email shiwei@rmitec.cn
 */
public class AppActivationListener implements ApplicationActivationListener {

    private static final Logger LOGGER = Logger.getInstance(AppActivationListener.class);

    /**
     * 是否已经通知
     */
    private volatile boolean hasNotice = false;

    @Override
    public synchronized void applicationActivated(@NotNull IdeFrame ideFrame) {
        activate();
    }

    /**
     * 激活
     */
    public synchronized void activate() {
        support();
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

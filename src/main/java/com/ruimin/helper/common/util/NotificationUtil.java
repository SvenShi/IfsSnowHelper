package com.ruimin.helper.common.util;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 通知工具类
 *
 * @author wangchao
 * @date 2022/11/06
 */
public class NotificationUtil {

    /** 私有构造 */
    private NotificationUtil() {}

    /**
     * 通知
     *
     * @param title 标题
     * @param content 内容
     * @param actions 动作
     */
    public static void notify(String title, String content, AnAction... actions) {

        NotificationGroup notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("IfsSnowHelper");
        Notification notification = notificationGroup.createNotification(
            title, content,
            NotificationType.INFORMATION);


        if (ArrayUtils.isNotEmpty(actions)) {
            for (AnAction action : actions) {
                notification.addAction(action);
            }
        }

        notification.notify(null);
    }
}

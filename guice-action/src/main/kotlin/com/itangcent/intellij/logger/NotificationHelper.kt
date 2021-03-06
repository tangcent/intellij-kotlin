package com.itangcent.intellij.logger

import com.google.inject.ImplementedBy
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.itangcent.intellij.context.ActionContext

@ImplementedBy(DefaultNotificationHelper::class)
interface NotificationHelper {

    fun notify(notify: (NotificationGroup) -> Notification)

    companion object {

        fun instance(): NotificationHelper {
            return ActionContext.getContext()?.instance(NotificationHelper::class)
                ?: DefaultNotificationHelper()
        }
    }
}
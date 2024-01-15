package com.inkapplications.sleeps.android.alarms

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.inkapplications.android.createNotificationBuilder
import com.inkapplications.sleeps.android.R
import regolith.init.Initializer
import regolith.init.TargetManager

private const val AlarmChannelId = "notifications.alarm"

/**
 * Creates and initializes alarm notifications with Android's NotificationManager.
 */
class AlarmNotifications(
    private val context: Context,
    private val notificationManager: NotificationManager,
): Initializer {
    override suspend fun initialize(targetManager: TargetManager) {
        createNotificationChannel()
    }

    /**
     * Create a notification to display when the alarm is going off.
     */
    fun createAlarmNotification(): Notification {
        val dismissIntent = context.createStopAlarmServicePendingIntent()
        return context.createNotificationBuilder(AlarmChannelId)
            .setDeleteIntent(dismissIntent)
            .setSound(null)
            .setContentText(context.getString(R.string.notifications_alarm_content, "NOW"))
            .setContentTitle(context.getString(R.string.notifications_alarm_title))
            .setSmallIcon(R.drawable.ic_alarm)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            AlarmChannelId,
            context.getString(R.string.notifications_channel_alarm_name),
            NotificationManager.IMPORTANCE_HIGH,
        )
        channel.setSound(null, null)
        notificationManager.createNotificationChannel(channel)
    }
}

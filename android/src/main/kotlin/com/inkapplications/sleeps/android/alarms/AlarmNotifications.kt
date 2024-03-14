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
        createNotificationChannels()
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

    /**
     * Create a notification to display when the alarm is going off.
     */
    fun createMaintenanceNotification(): Notification {
        return context.createNotificationBuilder(AlarmChannelId)
            .setContentTitle("Scheduling Alarms")
            .setSmallIcon(R.drawable.ic_alarm_confirm)
            .setProgress(0, 0, true)
            .setSound(null)
            .setPriority(Notification.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val alarmChannel = NotificationChannel(
            AlarmChannelId,
            context.getString(R.string.notifications_channel_alarm_name),
            NotificationManager.IMPORTANCE_HIGH,
        )
        val maintenanceChannel = NotificationChannel(
            AlarmChannelId,
            context.getString(R.string.notifications_channel_maintenance_name),
            NotificationManager.IMPORTANCE_MIN,
        )

        alarmChannel.setSound(null, null)
        maintenanceChannel.setSound(null, null)

        notificationManager.createNotificationChannel(alarmChannel)
        notificationManager.createNotificationChannel(maintenanceChannel)
    }
}

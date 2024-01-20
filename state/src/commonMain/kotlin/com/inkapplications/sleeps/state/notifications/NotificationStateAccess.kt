package com.inkapplications.sleeps.state.notifications

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.inkapplications.sleeps.state.settings.AlarmSettingsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

/**
 * Provides access for retrieving and modifying the app's notification settings.
 */
internal class NotificationStateAccess(
    private val alarmSettings: AlarmSettingsQueries,
): NotificationController {

    val notificationsState = alarmSettings.currentState()
        .asFlow()
        .mapToOne(Dispatchers.IO)
        .map {
            NotificationsState.Configured(it.sleep_alarm, it.wake_alarm)
        }

    override fun onSleepNotificationClick() {
        alarmSettings.toggleSleepAlarmStatus()
    }

    override fun onWakeAlarmClick() {
        alarmSettings.toggleWakeAlarmStatus()
    }
}

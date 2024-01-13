package com.inkapplications.sleeps.state.notifications

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.getAndUpdate

/**
 * Provides access for retrieving and modifying the app's notification settings.
 */
internal class NotificationStateAccess: NotificationController {
    private val currentSleepNotifications = MutableStateFlow(false)
    private val currentWakeAlarm = MutableStateFlow(false)

    val notificationsState = combine(currentSleepNotifications, currentWakeAlarm) { sleep, wake ->
        NotificationsState.Configured(sleep, wake)
    }

    override fun onSleepNotificationClick() {
        currentSleepNotifications.getAndUpdate { !it }
    }

    override fun onWakeAlarmClick() {
        currentWakeAlarm.getAndUpdate { !it }
    }
}

package com.inkapplications.sleeps.state.notifications

import kotlin.time.Duration

/**
 * State of the notification settings.
 */
internal sealed interface NotificationsState {
    /**
     * Initial state, before any data has loaded.
     */
    object Initial: NotificationsState

    /**
     * Current notification and alarm settings.
     */
    data class Configured(
        val sleepNotifications: Boolean,
        val wakeAlarm: Boolean,
        val alarmMargin: Duration,
        val sleepMargin: Duration,
        val sleepTarget: Duration,
    ): NotificationsState
}

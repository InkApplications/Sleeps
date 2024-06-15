package com.inkapplications.sleeps.state.notifications

import kotlin.time.Duration

/**
 * Current notification and alarm settings.
 */
internal data class NotificationSettingsState(
    val sleepNotifications: Boolean,
    val wakeAlarm: Boolean,
    val alarmMargin: Duration,
    val sleepMargin: Duration,
    val sleepTarget: Duration,
)

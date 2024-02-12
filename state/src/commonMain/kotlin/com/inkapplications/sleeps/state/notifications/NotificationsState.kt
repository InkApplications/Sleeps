package com.inkapplications.sleeps.state.notifications

import kotlin.time.Duration

/**
 * Current notification and alarm settings.
 */
data class NotificationSettings(
    val sleepNotifications: Boolean,
    val wakeAlarm: Boolean,
    val alarmMargin: Duration,
    val sleepMargin: Duration,
    val sleepTarget: Duration,
)

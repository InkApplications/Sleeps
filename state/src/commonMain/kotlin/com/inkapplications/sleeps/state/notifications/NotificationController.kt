package com.inkapplications.sleeps.state.notifications

import kotlin.time.Duration

/**
 * Actions performed by the user for notification settings.
 */
interface NotificationController {
    /**
     * Invoked when the sleep notification toggle is clicked.
     */
    fun onSleepNotificationClick(currentState: Boolean)

    /**
     * Invoked when the wake alarm toggle is clicked.
     */
    fun onWakeAlarmClick(currentState: Boolean)

    /**
     * Invoked when the user increases the wake alarm margin.
     */
    fun onIncreaseWakeAlarmMargin(currentState: Duration)

    /**
     * Invoked when the user decreases the wake alarm margin.
     */
    fun onDecreaseWakeAlarmMargin(currentState: Duration)

    /**
     * Invoked when the user decreases the sleep alarm margin.
     */
    fun onIncreaseSleepAlarmMargin(currentState: Duration)

    /**
     * Invoked when the user decreases the sleep alarm margin.
     */
    fun onDecreaseSleepAlarmMargin(currentState: Duration)

    /**
     * Invoked when the user increases the sleep target.
     */
    fun onIncreaseSleepTarget(currentState: Duration)

    /**
     * Invoked when the user decreases the sleep target.
     */
    fun onDecreaseSleepTarget(currentState: Duration)
}

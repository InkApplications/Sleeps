package com.inkapplications.sleeps.state.notifications

/**
 * Actions performed by the user for notification settings.
 */
interface NotificationController {
    /**
     * Invoked when the sleep notification toggle is clicked.
     */
    fun onSleepNotificationClick()

    /**
     * Invoked when the wake alarm toggle is clicked.
     */
    fun onWakeAlarmClick()

    /**
     * Invoked when the user increases the wake alarm margin.
     */
    fun onIncreaseWakeAlarmMargin()

    /**
     * Invoked when the user decreases the wake alarm margin.
     */
    fun onDecreaseWakeAlarmMargin()

    /**
     * Invoked when the user decreases the sleep alarm margin.
     */
    fun onIncreaseSleepAlarmMargin()

    /**
     * Invoked when the user decreases the sleep alarm margin.
     */
    fun onDecreaseSleepAlarmMargin()

    /**
     * Invoked when the user increases the sleep target.
     */
    fun onIncreaseSleepTarget()

    /**
     * Invoked when the user decreases the sleep target.
     */
    fun onDecreaseSleepTarget()
}

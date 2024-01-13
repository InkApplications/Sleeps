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
}

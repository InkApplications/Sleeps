package com.inkapplications.sleeps.state.notifications

import kotlin.time.Duration

object NotificationControllerStub: NotificationController {
    override fun onSleepNotificationClick(currentState: Boolean) {}
    override fun onWakeAlarmClick(currentState: Boolean) {}
    override fun onIncreaseWakeAlarmMargin(currentState: Duration) {}
    override fun onDecreaseWakeAlarmMargin(currentState: Duration) {}
    override fun onIncreaseSleepAlarmMargin(currentState: Duration) {}
    override fun onDecreaseSleepAlarmMargin(currentState: Duration) {}
    override fun onIncreaseSleepTarget(currentState: Duration) {}
    override fun onDecreaseSleepTarget(currentState: Duration) {}
}

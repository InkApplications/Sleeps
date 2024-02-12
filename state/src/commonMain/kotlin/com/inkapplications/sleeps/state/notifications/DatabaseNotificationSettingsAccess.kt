package com.inkapplications.sleeps.state.notifications

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.inkapplications.sleeps.state.settings.AlarmSettingsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.minutes

internal class DatabaseNotificationSettingsAccess(
    private val alarmSettings: AlarmSettingsQueries,
): NotificationSettingsAccess, NotificationController {
    private val marginIncrements = 15.minutes
    private val targetIncrements = 30.minutes

    override val notificationsState = alarmSettings.currentState()
        .asFlow()
        .mapToOne(Dispatchers.IO)
        .map {
            NotificationSettings(
                sleepNotifications = it.sleep_alarm,
                wakeAlarm = it.wake_alarm,
                alarmMargin = it.alarm_margin,
                sleepMargin = it.sleep_margin,
                sleepTarget = it.sleep_target,
            )
        }

    override fun onSleepNotificationClick() {
        alarmSettings.toggleSleepAlarmStatus()
    }

    override fun onWakeAlarmClick() {
        alarmSettings.toggleWakeAlarmStatus()
    }

    override fun onIncreaseWakeAlarmMargin() {
        alarmSettings.increaseAlarmMargin(marginIncrements)
    }

    override fun onDecreaseWakeAlarmMargin() {
        alarmSettings.decreaseAlarmMargin(marginIncrements)
    }

    override fun onIncreaseSleepAlarmMargin() {
        alarmSettings.increaseSleepMargin(marginIncrements)
    }

    override fun onDecreaseSleepAlarmMargin() {
        alarmSettings.decreaseSleepMargin(marginIncrements)
    }

    override fun onIncreaseSleepTarget() {
        alarmSettings.increaseSleepTarget(targetIncrements)
    }

    override fun onDecreaseSleepTarget() {
        alarmSettings.decreaseSleepTarget(targetIncrements)
    }
}

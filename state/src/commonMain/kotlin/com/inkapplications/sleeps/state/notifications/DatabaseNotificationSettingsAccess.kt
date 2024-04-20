package com.inkapplications.sleeps.state.notifications

import com.inkapplications.data.validator.ValidationResult
import com.inkapplications.sleeps.state.settings.MinutesDurationSetting
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import regolith.data.settings.SettingsAccess
import regolith.data.settings.observeSetting
import regolith.data.settings.structure.BooleanSetting
import regolith.data.settings.structure.LongData
import regolith.data.settings.writeSetting
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

internal class DatabaseNotificationSettingsAccess(
    private val settingsAccess: SettingsAccess,
    private val writeScope: CoroutineScope,
    private val logger: KimchiLogger,
): NotificationSettingsAccess, NotificationController {
    private val marginIncrements = 15.minutes
    private val targetIncrements = 30.minutes

    private val sleepAlarmSetting = BooleanSetting(
        key = "notifications.sleep.enabled",
        name = "Sleep Alarm",
        defaultValue = false,
    )

    private val wakeAlarmSetting = BooleanSetting(
        key = "notifications.wake.enabled",
        name = "Wake Alarm",
        defaultValue = false,
    )

    private val alarmMarginSetting = MinutesDurationSetting(
        key = "notifications.wake.margin",
        name = "Wake Alarm Margin",
        defaultValue = 30.minutes,
    )

    private val sleepMarginSetting = MinutesDurationSetting(
        key = "notifications.sleep.margin",
        name = "Sleep Alarm Margin",
        defaultValue = 60.minutes,
    )

    private val sleepTargetSetting = MinutesDurationSetting(
        key = "notifications.sleep.target",
        name = "Sleep Target",
        defaultValue = 8.hours,
    )

    override val notificationsState = combine(
        settingsAccess.observeSetting(sleepAlarmSetting),
        settingsAccess.observeSetting(wakeAlarmSetting),
        settingsAccess.observeSetting(alarmMarginSetting),
        settingsAccess.observeSetting(sleepMarginSetting),
        settingsAccess.observeSetting(sleepTargetSetting),
    ) { sleepAlarm, wakeAlarm, alarmMargin, sleepMargin, sleepTarget ->
        NotificationSettings(
            sleepNotifications = sleepAlarm,
            wakeAlarm = wakeAlarm,
            alarmMargin = alarmMargin,
            sleepMargin = sleepMargin,
            sleepTarget = sleepTarget,
        )
    }

    override fun onSleepNotificationClick(currentState: Boolean) {
        writeScope.launch {
            settingsAccess.writeSetting(sleepAlarmSetting, !currentState)
        }
    }

    override fun onWakeAlarmClick(currentState: Boolean) {
        writeScope.launch {
            settingsAccess.writeSetting(wakeAlarmSetting, !currentState)
        }
    }

    override fun onIncreaseWakeAlarmMargin(currentState: Duration) {
        writeScope.launch {
            validateAndSet(alarmMarginSetting, currentState + marginIncrements)
        }
    }

    override fun onDecreaseWakeAlarmMargin(currentState: Duration) {
        writeScope.launch {
            settingsAccess.writeSetting(alarmMarginSetting, currentState - marginIncrements)
        }
    }

    override fun onIncreaseSleepAlarmMargin(currentState: Duration) {
        writeScope.launch {
            settingsAccess.writeSetting(sleepMarginSetting, currentState + marginIncrements)
        }
    }

    override fun onDecreaseSleepAlarmMargin(currentState: Duration) {
        writeScope.launch {
            settingsAccess.writeSetting(sleepMarginSetting, currentState - marginIncrements)
        }
    }

    override fun onIncreaseSleepTarget(currentState: Duration) {
        writeScope.launch {
            settingsAccess.writeSetting(sleepTargetSetting, currentState + targetIncrements)
        }
    }

    override fun onDecreaseSleepTarget(currentState: Duration) {
        writeScope.launch {
            settingsAccess.writeSetting(sleepTargetSetting, currentState - targetIncrements)
        }
    }

    private suspend fun validateAndSet(setting: LongData<Duration>, newState: Duration) {
        val validation = setting.inputValidator.validate(newState)
        if (validation is ValidationResult.Failed) {
            validation.reasons.forEach { logger.debug("Validation failed for new value", it) }
            return
        }
        settingsAccess.writeSetting(setting, newState)
    }
}

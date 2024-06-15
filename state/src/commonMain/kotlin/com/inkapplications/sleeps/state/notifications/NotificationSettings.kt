package com.inkapplications.sleeps.state.notifications

import com.inkapplications.sleeps.state.settings.MinutesDurationSetting
import kotlinx.coroutines.flow.combine
import regolith.data.settings.SettingCategory
import regolith.data.settings.SettingsAccess
import regolith.data.settings.observeSetting
import regolith.data.settings.structure.BooleanSetting
import regolith.data.settings.structure.Setting
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

internal class NotificationSettings {
    private val alarmsCategory = SettingCategory("Alarms")
    private val timeCategory = SettingCategory("Timing Adjustments")

    val sleepAlarmSetting = BooleanSetting(
        key = "notifications.sleep.enabled",
        name = "Sleep Alarm",
        category = alarmsCategory,
        defaultValue = false,
    )

    val wakeAlarmSetting = BooleanSetting(
        key = "notifications.wake.enabled",
        name = "Wake Alarm",
        category = alarmsCategory,
        defaultValue = false,
    )

    val alarmMarginSetting = MinutesDurationSetting(
        key = "notifications.wake.margin",
        name = "Wake Alarm Margin",
        category = timeCategory,
        defaultValue = 30.minutes,
    )

    val sleepMarginSetting = MinutesDurationSetting(
        key = "notifications.sleep.margin",
        name = "Sleep Alarm Margin",
        category = timeCategory,
        defaultValue = 60.minutes,
    )

    val sleepTargetSetting = MinutesDurationSetting(
        key = "notifications.sleep.target",
        name = "Sleep Target",
        category = timeCategory,
        defaultValue = 8.hours,
    )

    val settings: Collection<Setting<*>> = listOf(
        sleepAlarmSetting,
        wakeAlarmSetting,
        alarmMarginSetting,
        sleepMarginSetting,
        sleepTargetSetting,
    )
}

internal fun SettingsAccess.combineNotificationState(
    notificationSettings: NotificationSettings,
) = combine(
    observeSetting(notificationSettings.wakeAlarmSetting),
    observeSetting(notificationSettings.sleepAlarmSetting),
    observeSetting(notificationSettings.alarmMarginSetting),
    observeSetting(notificationSettings.sleepTargetSetting),
    observeSetting(notificationSettings.sleepMarginSetting),
) { wake, sleep, alarmMargin, sleepTarget, sleepMargin ->
    NotificationSettingsState(
        wakeAlarm = wake,
        sleepNotifications = sleep,
        alarmMargin = alarmMargin,
        sleepTarget = sleepTarget,
        sleepMargin = sleepMargin,
    )
}

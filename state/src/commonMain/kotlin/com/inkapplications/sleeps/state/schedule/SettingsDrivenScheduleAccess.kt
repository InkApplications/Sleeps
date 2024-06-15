package com.inkapplications.sleeps.state.schedule

import com.inkapplications.sleeps.state.notifications.NotificationSettings
import com.inkapplications.sleeps.state.notifications.combineNotificationState
import com.inkapplications.sleeps.state.sun.SunriseAccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import regolith.data.settings.SettingsAccess

internal class SettingsDrivenScheduleAccess(
    sunriseAccess: SunriseAccess,
    settingsAccess: SettingsAccess,
    notificationSettings: NotificationSettings,
): ScheduleAccess {
    override val schedule: Flow<Schedule> = combine(
        sunriseAccess.nextSunrise,
        settingsAccess.combineNotificationState(notificationSettings),
    ) { sunriseState, settings ->
        Schedule(
            sunrise = sunriseState.timestamp,
            wake = sunriseState.timestamp.minus(settings.alarmMargin),
            sleep = sunriseState.timestamp.minus(settings.alarmMargin + settings.sleepTarget + settings.sleepMargin)
        )
    }
}

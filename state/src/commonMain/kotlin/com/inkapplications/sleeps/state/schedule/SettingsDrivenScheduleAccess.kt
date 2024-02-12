package com.inkapplications.sleeps.state.schedule

import com.inkapplications.sleeps.state.notifications.NotificationSettingsAccess
import com.inkapplications.sleeps.state.sun.SunriseAccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class SettingsDrivenScheduleAccess(
    sunriseAccess: SunriseAccess,
    notificationSettings: NotificationSettingsAccess,
): ScheduleAccess {
    override val schedule: Flow<Schedule> = combine(
        sunriseAccess.nextSunrise,
        notificationSettings.notificationsState,
    ) { sunriseState, settings ->
        Schedule(
            sunrise = sunriseState.timestamp,
            wake = sunriseState.timestamp.minus(settings.alarmMargin),
            sleep = sunriseState.timestamp.minus(settings.alarmMargin + settings.sleepTarget + settings.sleepMargin)
        )
    }
}

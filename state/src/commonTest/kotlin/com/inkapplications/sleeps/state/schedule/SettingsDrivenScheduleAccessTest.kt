package com.inkapplications.sleeps.state.schedule

import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.doubles.SettingsAccessDummy
import com.inkapplications.sleeps.state.notifications.NotificationSettings
import com.inkapplications.sleeps.state.sun.Sunrise
import com.inkapplications.sleeps.state.sun.SunriseAccessFake
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import regolith.data.settings.SettingsAccess
import regolith.data.settings.structure.PrimitiveSetting
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class SettingsDrivenScheduleAccessTest {
    @Test
    fun scheduleCalculations() = runTest {
        val sunriseAccess = SunriseAccessFake()
        val notificationSettings = NotificationSettings()
        val scheduleAccess = SettingsDrivenScheduleAccess(
            sunriseAccess = sunriseAccess,
            settingsAccess = object: SettingsAccess by SettingsAccessDummy {
                override fun <STORED> observeSetting(setting: PrimitiveSetting<STORED>): Flow<STORED> {
                    return when (setting.key) {
                        notificationSettings.sleepAlarmSetting.key -> flow { emit(0L) }
                        notificationSettings.wakeAlarmSetting.key -> flow { emit(0L) }
                        notificationSettings.alarmMarginSetting.key -> flow { emit(1.minutes.inWholeMinutes) }
                        notificationSettings.sleepMarginSetting.key -> flow { emit(2.minutes.inWholeMinutes) }
                        notificationSettings.sleepTargetSetting.key -> flow { emit(4.minutes.inWholeMinutes) }
                        else -> error("Unexpected setting: ${setting.key}")
                    } as Flow<STORED>
                }
            },
            notificationSettings = notificationSettings,
        )

        val result = async { scheduleAccess.schedule.first() }
        runCurrent()

        sunriseAccess.nextSunrise.emit(Sunrise(
            timestamp = LocalDateTime(2021, 1, 1, 12, 0).atZone(TimeZone.UTC),
            isEstimate = false
        ))

        assertEquals(LocalDateTime(2021, 1, 1, 12, 0).atZone(TimeZone.UTC), result.await().sunrise)
        assertEquals(LocalDateTime(2021, 1, 1, 11, 59).atZone(TimeZone.UTC), result.await().wake)
        assertEquals(LocalDateTime(2021, 1, 1, 11, 53).atZone(TimeZone.UTC), result.await().sleep)
    }
}

package com.inkapplications.sleeps.state.schedule

import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.state.notifications.NotificationSettings
import com.inkapplications.sleeps.state.notifications.NotificationSettingsFake
import com.inkapplications.sleeps.state.sun.Sunrise
import com.inkapplications.sleeps.state.sun.SunriseAccessFake
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class SettingsDrivenScheduleAccessTest {
    @Test
    fun scheduleCalculations() = runTest {
        val sunriseAccess = SunriseAccessFake()
        val notificationAccess = NotificationSettingsFake()

        val scheduleAccess = SettingsDrivenScheduleAccess(
            sunriseAccess,
            notificationAccess
        )

        val result = async { scheduleAccess.schedule.first() }
        runCurrent()

        notificationAccess.notificationsState.emit(NotificationSettings(
            sleepNotifications = false,
            wakeAlarm = false,
            alarmMargin = 1.minutes,
            sleepMargin = 2.minutes,
            sleepTarget = 4.minutes,
        ))
        sunriseAccess.nextSunrise.emit(Sunrise(
            timestamp = LocalDateTime(2021, 1, 1, 12, 0).atZone(TimeZone.UTC),
            isEstimate = false
        ))

        assertEquals(LocalDateTime(2021, 1, 1, 12, 0).atZone(TimeZone.UTC), result.await().sunrise)
        assertEquals(LocalDateTime(2021, 1, 1, 11, 59).atZone(TimeZone.UTC), result.await().wake)
        assertEquals(LocalDateTime(2021, 1, 1, 11, 53).atZone(TimeZone.UTC), result.await().sleep)
    }
}

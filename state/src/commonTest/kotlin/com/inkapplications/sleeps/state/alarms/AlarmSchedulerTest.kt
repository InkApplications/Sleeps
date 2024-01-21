package com.inkapplications.sleeps.state.alarms

import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.state.notifications.NotificationStateFake
import com.inkapplications.sleeps.state.notifications.NotificationsState
import com.inkapplications.sleeps.state.sun.SunScheduleAccessFake
import com.inkapplications.sleeps.state.sun.SunScheduleState
import kimchi.logger.EmptyLogger
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.minutes

class AlarmSchedulerTest {
    private val fakeConfig = NotificationsState.Configured(
        sleepNotifications = true,
        wakeAlarm = true,
        alarmMargin = 10.minutes,
        sleepMargin = 20.minutes,
        sleepTarget = 30.minutes,
    )
    private val enabledNotificationStateFake = NotificationStateFake(fakeConfig)

    @Test
    fun alarmSchedule() = runTest {
        val sunriseTime = LocalDateTime(2021, 1, 1, 12, 0).atZone(TimeZone.UTC)
        val alarmAccess = AlarmAccessSpy()
        val alarmScheduler = AlarmScheduler(
            alarmAccess = alarmAccess,
            sunScheduleAccess = SunScheduleAccessFake(
                SunScheduleState.Known(
                    sunrise = sunriseTime,
                ),
            ),
            notificationSettings = enabledNotificationStateFake,
            logger = EmptyLogger
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(2, alarmAccess.clearCalls.size, "Should clear existing alarms")
        assertEquals("wake", alarmAccess.clearCalls[0].value)
        assertEquals(2, alarmAccess.addCalls.size)

        val wakeAlarm = alarmAccess.addCalls.find { it.first.value == "wake" }?.second
        assertNotNull(wakeAlarm, "Wake alarm should be scheduled")
        assertEquals(
            LocalDateTime(2021, 1, 1, 11, 15).atZone(TimeZone.UTC).instant,
            wakeAlarm,
            "Alarm is scheduled 45 mins before sunrise"
        )

        val sleepAlarm = alarmAccess.addCalls.find { it.first.value == "sleep" }?.second
        assertNotNull(sleepAlarm, "Sleep alarm should be scheduled")
        assertEquals(
            LocalDateTime(2021, 1, 1, 2, 15).atZone(TimeZone.UTC).instant,
            sleepAlarm,
            "Alarm is scheduled 9 hours and 45 mins before sunrise"
        )

        job.cancel()
    }

    @Test
    fun unknownAlarm() = runTest {
        val sunriseTime = LocalDateTime(2021, 1, 1, 12, 0).atZone(TimeZone.UTC)
        val alarmAccess = AlarmAccessSpy()
        val alarmScheduler = AlarmScheduler(
            alarmAccess = alarmAccess,
            sunScheduleAccess = SunScheduleAccessFake(
                SunScheduleState.Unknown(
                    centralUsSunrise = sunriseTime,
                ),
            ),
            notificationSettings = enabledNotificationStateFake,
            logger = EmptyLogger,
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(2, alarmAccess.clearCalls.size, "Should clear existing alarms")
        assertEquals("wake", alarmAccess.clearCalls[0].value)
        assertEquals(2, alarmAccess.addCalls.size)

        val wakeAlarm = alarmAccess.addCalls.find { it.first.value == "wake" }?.second
        assertNotNull(wakeAlarm, "Wake alarm should be scheduled")
        assertEquals(
            LocalDateTime(2021, 1, 1, 11, 15).atZone(TimeZone.UTC).instant,
            wakeAlarm,
            "Alarm is scheduled 45 mins before sunrise"
        )

        val sleepAlarm = alarmAccess.addCalls.find { it.first.value == "sleep" }?.second
        assertNotNull(sleepAlarm, "Sleep alarm should be scheduled")
        assertEquals(
            LocalDateTime(2021, 1, 1, 2, 15).atZone(TimeZone.UTC).instant,
            sleepAlarm,
            "Alarm is scheduled 9 hours and 45 mins before sunrise"
        )

        job.cancel()
    }

    @Test
    fun initializingSchedule() = runTest {
        val alarmAccess = AlarmAccessSpy()
        val alarmScheduler = AlarmScheduler(
            alarmAccess = alarmAccess,
            sunScheduleAccess = SunScheduleAccessFake(
                SunScheduleState.Initial,
            ),
            notificationSettings = enabledNotificationStateFake,
            logger = EmptyLogger
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(0, alarmAccess.clearCalls.size, "Should clear existing alarms during init")
        assertEquals(0, alarmAccess.addCalls.size, "Should not set any alarms during init")

        job.cancel()
    }

    @Test
    fun initializingSettings() = runTest {
        val notificationsFake = NotificationStateFake(NotificationsState.Initial)
        val sunriseTime = LocalDateTime(2021, 1, 1, 12, 0).atZone(TimeZone.UTC)
        val alarmAccess = AlarmAccessSpy()
        val alarmScheduler = AlarmScheduler(
            alarmAccess = alarmAccess,
            sunScheduleAccess = SunScheduleAccessFake(
                SunScheduleState.Unknown(
                    centralUsSunrise = sunriseTime,
                ),
            ),
            notificationSettings = notificationsFake,
            logger = EmptyLogger,
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(0, alarmAccess.clearCalls.size, "Should not clear any alarms during init")
        assertEquals(0, alarmAccess.addCalls.size, "Should not set any alarms during init")

        job.cancel()
    }

    @Test
    fun alarmsDisabled() = runTest {
        val notificationsFake = NotificationStateFake(fakeConfig.copy(
            sleepNotifications = false,
            wakeAlarm = false,
        ))
        val sunriseTime = LocalDateTime(2021, 1, 1, 12, 0).atZone(TimeZone.UTC)
        val alarmAccess = AlarmAccessSpy()
        val alarmScheduler = AlarmScheduler(
            alarmAccess = alarmAccess,
            sunScheduleAccess = SunScheduleAccessFake(
                SunScheduleState.Unknown(
                    centralUsSunrise = sunriseTime,
                ),
            ),
            notificationSettings = notificationsFake,
            logger = EmptyLogger,
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(2, alarmAccess.clearCalls.size, "Should clear existing alarms when disabled")
        assertEquals(0, alarmAccess.addCalls.size, "Should not set any alarms when disabled")

        job.cancel()
    }
}


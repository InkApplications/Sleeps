package com.inkapplications.sleeps.state.alarms

import com.inkapplications.datetime.atZone
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

class AlarmSchedulerTest {
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
            logger = EmptyLogger
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(1, alarmAccess.clearCalls.size, "Should clear existing alarms")
        assertEquals("wake", alarmAccess.clearCalls[0].value)
        assertEquals(1, alarmAccess.addCalls.size)
        assertEquals("wake", alarmAccess.addCalls[0].first.value)
        assertEquals(
            LocalDateTime(2021, 1, 1, 11, 30).atZone(TimeZone.UTC).instant,
            alarmAccess.addCalls[0].second,
            "Alarm is scheduled a half hour before sunrise"
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
            logger = EmptyLogger
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(1, alarmAccess.clearCalls.size, "Should clear existing alarms")
        assertEquals("wake", alarmAccess.clearCalls[0].value)
        assertEquals(1, alarmAccess.addCalls.size)
        assertEquals("wake", alarmAccess.addCalls[0].first.value)
        assertEquals(
            LocalDateTime(2021, 1, 1, 11, 30).atZone(TimeZone.UTC).instant,
            alarmAccess.addCalls[0].second,
            "Alarm is scheduled a half hour before sunrise"
        )

        job.cancel()
    }

    @Test
    fun initial() = runTest {
        val alarmAccess = AlarmAccessSpy()
        val alarmScheduler = AlarmScheduler(
            alarmAccess = alarmAccess,
            sunScheduleAccess = SunScheduleAccessFake(
                SunScheduleState.Initial,
            ),
            logger = EmptyLogger
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(0, alarmAccess.clearCalls.size, "Should clear existing alarms")
        assertEquals(0, alarmAccess.addCalls.size)

        job.cancel()
    }
}


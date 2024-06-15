package com.inkapplications.sleeps.state.alarms

import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.doubles.SettingsAccessDummy
import com.inkapplications.sleeps.state.notifications.NotificationSettings
import com.inkapplications.sleeps.state.schedule.Schedule
import com.inkapplications.sleeps.state.schedule.ScheduleAccessFake
import kimchi.logger.EmptyLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import regolith.data.settings.SettingsAccess
import regolith.data.settings.structure.PrimitiveSetting
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.INFINITE

class AlarmSchedulerTest {
    private val fakeSchedule = Schedule(
        sunrise = LocalDateTime(2021, 1, 2, 6, 0).atZone(TimeZone.UTC),
        wake = LocalDateTime(2021, 1, 2, 7, 1).atZone(TimeZone.UTC),
        sleep = LocalDateTime(2021, 1, 1, 20, 2).atZone(TimeZone.UTC),
    )
    private val afternoonBefore = object: Clock {
        override fun now(): Instant  = LocalDateTime(2021, 1, 1, 20, 0).atZone(TimeZone.UTC).instant
    }
    private val notificationSettings = NotificationSettings()

    @Test
    fun alarmSchedule() = runTest {
        val fakeScheduleAccess = ScheduleAccessFake()
        val alarmAccess = AlarmAccessSpy()
        val alarmScheduler = AlarmScheduler(
            alarmAccess = alarmAccess,
            scheduleAccess = fakeScheduleAccess,
            notificationSettings = notificationSettings,
            clock = afternoonBefore,
            settingsAccess = object: SettingsAccess by SettingsAccessDummy {
                override fun <STORED> observeSetting(setting: PrimitiveSetting<STORED>): Flow<STORED> {
                    return when (setting.key) {
                        notificationSettings.sleepAlarmSetting.key -> flow { emit(1L) }
                        notificationSettings.wakeAlarmSetting.key -> flow { emit(1L) }
                        notificationSettings.alarmMarginSetting.key -> flow { emit(INFINITE.inWholeMinutes) }
                        notificationSettings.sleepMarginSetting.key -> flow { emit(INFINITE.inWholeMinutes) }
                        notificationSettings.sleepTargetSetting.key -> flow { emit(INFINITE.inWholeMinutes) }
                        else -> error("Unexpected setting: ${setting.key}")
                    } as Flow<STORED>
                }
            },
            logger = EmptyLogger
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(0, alarmAccess.addCalls.size, "No calls before schedule is updated")
        assertEquals(0, alarmAccess.clearCalls.size, "No calls before schedule is updated")

        fakeScheduleAccess.schedule.emit(fakeSchedule)
        runCurrent()

        assertEquals(0, alarmAccess.clearCalls.size, "Should not clear alarms")
        assertEquals(2, alarmAccess.addCalls.size)

        val wakeAlarm = alarmAccess.addCalls.find { it.first.id == "wake" }?.second
        assertNotNull(wakeAlarm, "Wake alarm should be scheduled")
        assertEquals(
            LocalDateTime(2021, 1, 2, 7, 1).atZone(TimeZone.UTC).instant,
            wakeAlarm,
            "Alarm is scheduled"
        )

        val sleepAlarm = alarmAccess.addCalls.find { it.first.id == "sleep" }?.second
        assertNotNull(sleepAlarm, "Sleep alarm should be scheduled")
        assertEquals(
            LocalDateTime(2021, 1, 1, 20, 2).atZone(TimeZone.UTC).instant,
            sleepAlarm,
            "Sleep Notification is scheduled"
        )

        job.cancel()
    }

    @Test
    fun alarmsDisabled() = runTest {
        val fakeScheduleAccess = ScheduleAccessFake()
        val alarmAccess = AlarmAccessSpy()
        val alarmScheduler = AlarmScheduler(
            alarmAccess = alarmAccess,
            scheduleAccess = fakeScheduleAccess,
            notificationSettings = notificationSettings,
            settingsAccess = object: SettingsAccess by SettingsAccessDummy {
                override fun <STORED> observeSetting(setting: PrimitiveSetting<STORED>): Flow<STORED> {
                    return when (setting.key) {
                        notificationSettings.sleepAlarmSetting.key -> flow { emit(0L) }
                        notificationSettings.wakeAlarmSetting.key -> flow { emit(0L) }
                        notificationSettings.alarmMarginSetting.key -> flow { emit(INFINITE.inWholeMinutes) }
                        notificationSettings.sleepMarginSetting.key -> flow { emit(INFINITE.inWholeMinutes) }
                        notificationSettings.sleepTargetSetting.key -> flow { emit(INFINITE.inWholeMinutes) }
                        else -> error("Unexpected setting: ${setting.key}")
                    } as Flow<STORED>
                }
            },
            clock = afternoonBefore,
            logger = EmptyLogger,
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()
        fakeScheduleAccess.schedule.emit(fakeSchedule)
        runCurrent()

        assertEquals(2, alarmAccess.clearCalls.size, "Should clear existing alarms when disabled")
        assertNotNull(alarmAccess.clearCalls.find { it.id == "wake" }, " Wake alarm should be cleared")
        assertNotNull(alarmAccess.clearCalls.find { it.id == "sleep" }, " Sleep alarm should be cleared")
        assertEquals(0, alarmAccess.addCalls.size, "Should not set any alarms when disabled")

        job.cancel()
    }

    @Test
    fun midCycle() = runTest {
        val fakeScheduleAccess = ScheduleAccessFake()
        val alarmAccess = AlarmAccessSpy()
        val alarmScheduler = AlarmScheduler(
            alarmAccess = alarmAccess,
            scheduleAccess = fakeScheduleAccess,
            notificationSettings = notificationSettings,
            settingsAccess = object: SettingsAccess by SettingsAccessDummy {
                override fun <STORED> observeSetting(setting: PrimitiveSetting<STORED>): Flow<STORED> {
                    return when (setting.key) {
                        notificationSettings.sleepAlarmSetting.key -> flow { emit(1L) }
                        notificationSettings.wakeAlarmSetting.key -> flow { emit(1L) }
                        notificationSettings.alarmMarginSetting.key -> flow { emit(INFINITE.inWholeMinutes) }
                        notificationSettings.sleepMarginSetting.key -> flow { emit(INFINITE.inWholeMinutes) }
                        notificationSettings.sleepTargetSetting.key -> flow { emit(INFINITE.inWholeMinutes) }
                        else -> error("Unexpected setting: ${setting.key}")
                    } as Flow<STORED>
                }
            },
            clock = object: Clock {
                override fun now(): Instant  = LocalDateTime(2021, 1, 1, 21, 2).atZone(TimeZone.UTC).instant
            },
            logger = EmptyLogger
        )

        val job = launch {
            alarmScheduler.startDaemon()
        }

        runCurrent()

        assertEquals(0, alarmAccess.addCalls.size, "No calls before schedule is updated")
        assertEquals(0, alarmAccess.clearCalls.size, "No calls before schedule is updated")

        fakeScheduleAccess.schedule.emit(fakeSchedule)
        runCurrent()

        val sleepAlarms = alarmAccess.addCalls.count { it.first.id == "sleep" }
        assertEquals(0, sleepAlarms, "No sleep alarms scheduled after initial time")

        job.cancel()
    }
}


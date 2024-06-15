package com.inkapplications.sleeps.state.alarms

import com.inkapplications.sleeps.state.notifications.NotificationSettings
import com.inkapplications.sleeps.state.notifications.NotificationSettingsState
import com.inkapplications.sleeps.state.notifications.combineNotificationState
import com.inkapplications.sleeps.state.schedule.Schedule
import com.inkapplications.sleeps.state.schedule.ScheduleAccess
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import regolith.data.settings.SettingsAccess
import regolith.processes.daemon.Daemon
import regolith.processes.daemon.DaemonRunAttempt
import regolith.processes.daemon.FailureSignal

/**
 * Schedules alarms to be invoked at the appropriate times.
 */
internal class AlarmScheduler(
    private val alarmAccess: AlarmAccess,
    notificationSettings: NotificationSettings,
    settingsAccess: SettingsAccess,
    scheduleAccess: ScheduleAccess,
    private val clock: Clock,
    private val logger: KimchiLogger,
): Daemon, MaintenanceController {
    private val alarmParameters = combine(
        scheduleAccess.schedule,
        settingsAccess.combineNotificationState(notificationSettings),
    ) { sunSchedule, settings -> AlarmParameters(sunSchedule, settings) }

    override suspend fun startDaemon(): Nothing {
        alarmParameters.collectLatest { parameters ->
            scheduleAlarm(parameters)
        }

        throw IllegalStateException("Unexpected end of sun state flow")
    }

    private fun scheduleAlarm(alarmParameters: AlarmParameters) {
        logger.trace("Removing all alarms")
        if (alarmParameters.settings.wakeAlarm) {
            logger.trace("Scheduling Wake alarm for ${alarmParameters.schedule.wake}")
            alarmAccess.addAlarm(AlarmType.Wake, alarmParameters.schedule.wake.instant)
        } else {
            logger.trace("Wake alarm disabled")
            alarmAccess.removeAlarm(AlarmType.Wake)
        }

        when {
            !alarmParameters.settings.sleepNotifications -> {
                logger.trace("Sleep alarm disabled")
                alarmAccess.removeAlarm(AlarmType.Sleep)
            }
            clock.now() > alarmParameters.schedule.sleep.instant -> logger.trace("Sleep alarm time has passed")
            else -> alarmAccess.addAlarm(AlarmType.Sleep, alarmParameters.schedule.sleep.instant)
        }
    }

    override suspend fun onFailure(attempts: List<DaemonRunAttempt>): FailureSignal = FailureSignal.Panic

    /**
     * Assembled arguments necessary for scheduling an alarm.
     */
    private data class AlarmParameters(
        val schedule: Schedule,
        val settings: NotificationSettingsState,
    )

    override suspend fun performMaintenance() {
        scheduleAlarm(alarmParameters.first())
    }
}

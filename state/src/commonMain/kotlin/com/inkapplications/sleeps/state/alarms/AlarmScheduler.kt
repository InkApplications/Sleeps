package com.inkapplications.sleeps.state.alarms

import com.inkapplications.sleeps.state.notifications.NotificationStateAccess
import com.inkapplications.sleeps.state.notifications.NotificationsState
import com.inkapplications.sleeps.state.sun.SunScheduleState
import com.inkapplications.sleeps.state.sun.SunScheduleStateAccess
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import regolith.processes.daemon.Daemon
import regolith.processes.daemon.DaemonRunAttempt
import regolith.processes.daemon.FailureSignal
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Schedules alarms to be invoked at the appropriate times.
 */
internal class AlarmScheduler(
    private val alarmAccess: AlarmAccess,
    private val sunScheduleAccess: SunScheduleStateAccess,
    private val notificationSettings: NotificationStateAccess,
    private val logger: KimchiLogger,
): Daemon {
    private val wakeAlarm = AlarmId("wake")
    private val sleepAlarm = AlarmId("sleep")
    private val wakeLeadTime = 45.minutes
    private val sleepLeadTime = 9.hours + wakeLeadTime

    override suspend fun startDaemon(): Nothing {
        combine(
            sunScheduleAccess.sunState.filter { it !is SunScheduleState.Initial },
            notificationSettings.notificationsState.filterIsInstance<NotificationsState.Configured>(),
        ) { sunSchedule, settings ->
            val wakeTime = when (sunSchedule) {
                is SunScheduleState.Known -> sunSchedule.sunrise.minus(wakeLeadTime)
                is SunScheduleState.Unknown -> sunSchedule.centralUsSunrise.minus(wakeLeadTime)
                SunScheduleState.Initial -> throw IllegalStateException("Cannot determine time for initial schedule state")
            }
            val sleepTime = when (sunSchedule) {
                is SunScheduleState.Known -> sunSchedule.sunrise.minus(sleepLeadTime)
                is SunScheduleState.Unknown -> sunSchedule.centralUsSunrise.minus(sleepLeadTime)
                SunScheduleState.Initial -> throw IllegalStateException("Cannot determine time for initial schedule state")
            }

            logger.trace("Removing all alarms")
            alarmAccess.removeAlarm(wakeAlarm)
            alarmAccess.removeAlarm(sleepAlarm)

            if (settings.wakeAlarm) {
                logger.trace("Scheduling Wake alarm for $wakeTime")
                alarmAccess.addAlarm(wakeAlarm, wakeTime.instant)
            } else {
                logger.trace("Wake alarm disabled")
            }

            if (settings.sleepNotifications) {
                logger.trace("Scheduling Sleep alarm for $sleepTime")
                alarmAccess.addAlarm(sleepAlarm, sleepTime.instant)
            } else {
                logger.trace("Sleep alarm disabled")
            }
        }.collectLatest {}

        throw IllegalStateException("Unexpected end of sun state flow")
    }

    override suspend fun onFailure(attempts: List<DaemonRunAttempt>): FailureSignal = FailureSignal.Panic
}

package com.inkapplications.sleeps.state.alarms

import com.inkapplications.sleeps.state.DeviceBootController
import com.inkapplications.sleeps.state.notifications.NotificationStateAccess
import com.inkapplications.sleeps.state.notifications.NotificationsState
import com.inkapplications.sleeps.state.sun.SunScheduleState
import com.inkapplications.sleeps.state.sun.SunScheduleStateAccess
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import regolith.processes.daemon.Daemon
import regolith.processes.daemon.DaemonRunAttempt
import regolith.processes.daemon.FailureSignal

/**
 * Schedules alarms to be invoked at the appropriate times.
 */
internal class AlarmScheduler(
    private val alarmAccess: AlarmAccess,
    sunScheduleAccess: SunScheduleStateAccess,
    notificationSettings: NotificationStateAccess,
    private val logger: KimchiLogger,
): Daemon, DeviceBootController {
    private val wakeAlarm = AlarmId("wake")
    private val sleepAlarm = AlarmId("sleep")
    private val alarmParameters = combine(
        sunScheduleAccess.sunState.filterIsInstance<SunScheduleState.Initialized>(),
        notificationSettings.notificationsState.filterIsInstance<NotificationsState.Configured>(),
    ) { sunSchedule, settings -> AlarmParameters(sunSchedule, settings) }

    override fun onDeviceBoot() {
        runBlocking {
            scheduleAlarm(alarmParameters.first())
        }
    }

    override suspend fun startDaemon(): Nothing {
        alarmParameters.collectLatest { (sunSchedule, settings) ->
            val wakeTime = when (sunSchedule) {
                is SunScheduleState.Known -> sunSchedule.sunrise.minus(settings.alarmMargin)
                is SunScheduleState.Unknown -> sunSchedule.centralUsSunrise.minus(settings.alarmMargin)
            }
            val sleepTime = when (sunSchedule) {
                is SunScheduleState.Known -> sunSchedule.sunrise.minus(settings.alarmMargin + settings.sleepTarget + settings.sleepMargin)
                is SunScheduleState.Unknown -> sunSchedule.centralUsSunrise.minus(settings.alarmMargin + settings.sleepTarget + settings.sleepMargin)
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
        }

        throw IllegalStateException("Unexpected end of sun state flow")
    }

    private fun scheduleAlarm(alarmParameters: AlarmParameters) {
        val wakeTime = when (alarmParameters.sunSchedule) {
            is SunScheduleState.Known -> alarmParameters.sunSchedule.sunrise.minus(alarmParameters.settings.alarmMargin)
            is SunScheduleState.Unknown -> alarmParameters.sunSchedule.centralUsSunrise.minus(alarmParameters.settings.alarmMargin)
        }
        val sleepTime = when (alarmParameters.sunSchedule) {
            is SunScheduleState.Known -> alarmParameters.sunSchedule.sunrise.minus(alarmParameters.settings.alarmMargin + alarmParameters.settings.sleepTarget + alarmParameters.settings.sleepMargin)
            is SunScheduleState.Unknown -> alarmParameters.sunSchedule.centralUsSunrise.minus(alarmParameters.settings.alarmMargin + alarmParameters. settings.sleepTarget + alarmParameters.settings.sleepMargin)
        }

        logger.trace("Removing all alarms")
        alarmAccess.removeAlarm(wakeAlarm)
        alarmAccess.removeAlarm(sleepAlarm)

        if (alarmParameters.settings.wakeAlarm) {
            logger.trace("Scheduling Wake alarm for $wakeTime")
            alarmAccess.addAlarm(wakeAlarm, wakeTime.instant)
        } else {
            logger.trace("Wake alarm disabled")
        }

        if (alarmParameters.settings.sleepNotifications) {
            logger.trace("Scheduling Sleep alarm for $sleepTime")
            alarmAccess.addAlarm(sleepAlarm, sleepTime.instant)
        } else {
            logger.trace("Sleep alarm disabled")
        }
    }

    override suspend fun onFailure(attempts: List<DaemonRunAttempt>): FailureSignal = FailureSignal.Panic

    /**
     * Assembled arguments necessary for scheduling an alarm.
     */
    private data class AlarmParameters(
        val sunSchedule: SunScheduleState.Initialized,
        val settings: NotificationsState.Configured,
    )
}

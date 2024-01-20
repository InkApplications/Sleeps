package com.inkapplications.sleeps.state.alarms

import com.inkapplications.sleeps.state.sun.SunScheduleState
import com.inkapplications.sleeps.state.sun.SunScheduleStateAccess
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.collectLatest
import regolith.processes.daemon.Daemon
import regolith.processes.daemon.DaemonRunAttempt
import regolith.processes.daemon.FailureSignal
import kotlin.time.Duration.Companion.minutes

/**
 * Schedules alarms to be invoked at the appropriate times.
 */
internal class AlarmScheduler(
    private val alarmAccess: AlarmAccess,
    private val sunScheduleAccess: SunScheduleStateAccess,
    private val logger: KimchiLogger,
): Daemon {
    private val wakeAlarm = AlarmId("wake")
    private val leadTime = 30.minutes

    override suspend fun startDaemon(): Nothing {
        sunScheduleAccess.sunState.collectLatest { sunState ->
            when (sunState) {
                SunScheduleState.Initial -> {
                    logger.trace("Waiting for SunState to initialize before scheduling alarms")
                }
                is SunScheduleState.Known -> {
                    val time = sunState.sunrise.minus(leadTime)
                    logger.trace("Scheduling Wake alarm for $time")
                    alarmAccess.removeAlarm(wakeAlarm)
                    alarmAccess.addAlarm(wakeAlarm, time.instant)
                }
                is SunScheduleState.Unknown -> {
                    val time = sunState.centralUsSunrise.minus(leadTime)
                    logger.trace("SunState is unknown, Scheduling surrogate alarm for $time")
                    alarmAccess.removeAlarm(wakeAlarm)
                    alarmAccess.addAlarm(wakeAlarm, time.instant)
                }
            }
        }
        throw IllegalStateException("Unexpected end of sun state flow")
    }

    override suspend fun onFailure(attempts: List<DaemonRunAttempt>): FailureSignal = FailureSignal.Panic
}

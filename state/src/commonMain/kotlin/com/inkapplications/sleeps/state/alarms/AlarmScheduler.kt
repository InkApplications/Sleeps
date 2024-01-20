package com.inkapplications.sleeps.state.alarms

import com.inkapplications.sleeps.state.sun.SunScheduleState
import com.inkapplications.sleeps.state.sun.SunStateProvider
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
    private val sunStateProvider: SunStateProvider,
    private val logger: KimchiLogger,
): Daemon {
    private val wakeAlarm = AlarmId("wake")

    override suspend fun startDaemon(): Nothing {
        sunStateProvider.sunState.collectLatest { sunState ->
            when (sunState) {
                SunScheduleState.Initial -> {
                    logger.trace("Waiting for SunState to initialize before scheduling alarms")
                }
                is SunScheduleState.Known -> {
                    val time = sunState.schedule.sunrise.minus(30.minutes)
                    logger.trace("Scheduling Wake alarm for $time")
                    alarmAccess.removeAlarm(wakeAlarm)
                    alarmAccess.addAlarm(wakeAlarm, time.instant)
                }
                is SunScheduleState.Unknown -> {
                    logger.trace("SunState is unknown, Scheduling surrogate alarm for ${sunState.centralUs.sunrise}")
                    alarmAccess.removeAlarm(wakeAlarm)
                    alarmAccess.addAlarm(wakeAlarm, sunState.centralUs.sunrise.instant)
                }
            }
        }
        throw IllegalStateException("Unexpected end of sun state flow")
    }

    override suspend fun onFailure(attempts: List<DaemonRunAttempt>): FailureSignal = FailureSignal.Panic
}

package com.inkapplications.sleeps.state.alarms

import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.sun.SunScheduleProvider
import kimchi.logger.KimchiLogger
import regolith.init.Initializer
import regolith.init.TargetManager
import kotlin.time.Duration.Companion.seconds

/**
 * Schedules alarms to be invoked at the appropriate times.
 */
internal class AlarmScheduler(
    private val alarmAccess: AlarmAccess,
    private val sunScheduleProvider: SunScheduleProvider,
    private val clock: ZonedClock,
    private val logger: KimchiLogger,
): Initializer {
    override suspend fun initialize(targetManager: TargetManager) {
        logger.trace("Initializing Alarms")
        val time = clock.now().plus(10.seconds)
        alarmAccess.addAlarm(time)
    }
}

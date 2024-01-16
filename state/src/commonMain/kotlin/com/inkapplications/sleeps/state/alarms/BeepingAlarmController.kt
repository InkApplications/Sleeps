package com.inkapplications.sleeps.state.alarms

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds

internal class BeepingAlarmController(
    private val beeper: AlarmBeeper,
): AlarmController {
    override suspend fun onStartAlarm(id: AlarmId) {
        while (currentCoroutineContext().isActive) {
            beeper.play()
            delay(3.seconds)
        }
    }
}

package com.inkapplications.sleeps.state.alarms

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class BeepingAlarmController(
    private val beeper: AlarmBeeper,
): AlarmController {
    private val initialDelay = 1.minutes
    private val minimumDelay = 3.seconds
    private val acceleration = 0.9

    override suspend fun onStartAlarm(id: AlarmType) {
        var current = initialDelay
        while (currentCoroutineContext().isActive) {
            beeper.play()
            delay(current)
            current = (current * acceleration).coerceAtLeast(minimumDelay)
        }
    }
}

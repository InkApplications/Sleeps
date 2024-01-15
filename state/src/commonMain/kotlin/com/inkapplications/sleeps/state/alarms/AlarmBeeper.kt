package com.inkapplications.sleeps.state.alarms

/**
 * Provides access to alarm sounds.
 */
interface AlarmBeeper {
    /**
     * Play a single loop of the alarm sound.
     */
    suspend fun play()
}

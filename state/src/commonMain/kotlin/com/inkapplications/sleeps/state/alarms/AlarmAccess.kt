package com.inkapplications.sleeps.state.alarms

import kotlinx.datetime.Instant

/**
 * Provides access to managing alarms on the system.
 */
interface AlarmAccess {
    /**
     * Add a new alarm to be invoked at the given [time].
     */
    fun addAlarm(time: Instant)

    /**
     * Clear all set alarms.
     */
    fun clear()
}

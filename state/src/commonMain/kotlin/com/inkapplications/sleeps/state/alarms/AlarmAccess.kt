package com.inkapplications.sleeps.state.alarms

import kotlinx.datetime.Instant

/**
 * Provides access to managing alarms on the system.
 */
interface AlarmAccess {
    /**
     * Add a new alarm to be invoked at the given [time].
     */
    fun addAlarm(id: AlarmType, time: Instant)

    /**
     * Remove a specific alam by its ID used when the alarm was added.
     */
    fun removeAlarm(id: AlarmType)
}

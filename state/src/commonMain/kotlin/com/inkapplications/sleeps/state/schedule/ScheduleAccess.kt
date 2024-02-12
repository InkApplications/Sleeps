package com.inkapplications.sleeps.state.schedule

import kotlinx.coroutines.flow.Flow

/**
 * Data access for the alarm/sunrise schedule.
 */
internal interface ScheduleAccess {
    /**
     * Emits the latest schedule updates based on sunrise and user settings.
     */
    val schedule: Flow<Schedule>
}

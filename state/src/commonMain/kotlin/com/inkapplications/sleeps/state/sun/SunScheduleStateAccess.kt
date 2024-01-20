package com.inkapplications.sleeps.state.sun

import kotlinx.coroutines.flow.StateFlow

/**
 * Provides access to the current known sun schedule.
 */
internal interface SunScheduleStateAccess {
    /**
     * Current state of the current known sunrise schedule.
     */
    val sunState: StateFlow<SunScheduleState>
}

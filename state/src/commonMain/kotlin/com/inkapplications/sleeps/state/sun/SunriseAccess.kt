package com.inkapplications.sleeps.state.sun

import kotlinx.coroutines.flow.Flow

/**
 * Provides access to the current known sunrise times.
 */
internal interface SunriseAccess {
    /**
     * Current state of the current known sunrise schedule.
     */
    val nextSunrise: Flow<Sunrise>
}

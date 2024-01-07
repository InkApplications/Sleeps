package com.inkapplications.sleeps.state.location

import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.coroutines.flow.Flow

/**
 * Provides the current location of the device.
 */
interface LocationProvider {
    val location: Flow<GeoCoordinates?>
}

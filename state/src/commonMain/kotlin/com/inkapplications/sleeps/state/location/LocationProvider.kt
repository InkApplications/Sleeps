package com.inkapplications.sleeps.state.location

import inkapplications.spondee.spatial.GeoCoordinates

/**
 * Provides the current location of the device.
 */
interface LocationProvider {
    suspend fun getLastLocation(): GeoCoordinates?
}

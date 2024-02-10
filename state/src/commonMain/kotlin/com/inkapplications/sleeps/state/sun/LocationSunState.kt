package com.inkapplications.sleeps.state.sun

import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import regolith.sensors.location.LocationAccess
import regolith.sensors.location.LocationState

/**
 * Uses the current location to provide the sunrise times.
 */
internal class LocationSunState(
    private val sunScheduleProvider: SunScheduleProvider,
    locationAccess: LocationAccess,
    stateScope: CoroutineScope,
): SunScheduleStateAccess {
    private val usGeographicCenter = GeoCoordinates(
        latitude = (39.8283).latitude,
        longitude = (-98.5795).longitude,
    )

    override val sunState = locationAccess.locationUpdates
        .map { location -> createState(location) }
        .stateIn(stateScope, SharingStarted.WhileSubscribed(), SunScheduleState.Initial)

    private fun createState(location: LocationState): SunScheduleState {
        return when (location) {
            is LocationState.Known -> sunScheduleProvider.getNextSunriseForLocation(
                coordinates = location.coordinates,
            ).let(SunScheduleState::Known)
            is LocationState.Unknown -> sunScheduleProvider.getNextSunriseForLocation(
                coordinates = usGeographicCenter,
            ).let(SunScheduleState::Unknown)
        }
    }
}

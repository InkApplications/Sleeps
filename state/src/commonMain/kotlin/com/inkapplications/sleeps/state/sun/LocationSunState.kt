package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.location.LocationProvider
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Uses the current location to provide the sunrise times.
 */
internal class LocationSunState(
    private val sunScheduleProvider: SunScheduleProvider,
    locationProvider: LocationProvider,
    stateScope: CoroutineScope,
): SunScheduleStateAccess {
    private val usGeographicCenter = GeoCoordinates(
        latitude = (39.8283).latitude,
        longitude = (-98.5795).longitude,
    )

    override val sunState = locationProvider.location
        .map { location -> createState(location) }
        .stateIn(stateScope, SharingStarted.WhileSubscribed(), SunScheduleState.Initial)

    private fun createState(location: GeoCoordinates?): SunScheduleState {
        return if (location == null) {
            sunScheduleProvider.getNextSunriseForLocation(
                coordinates = usGeographicCenter,
            ).let(SunScheduleState::Unknown)
        } else {
            sunScheduleProvider.getNextSunriseForLocation(
                coordinates = location,
            ).let(SunScheduleState::Known)
        }
    }
}

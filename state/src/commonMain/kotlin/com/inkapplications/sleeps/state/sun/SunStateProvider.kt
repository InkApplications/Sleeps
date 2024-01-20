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
 * Uses the current location to provide the sunrise and sunset times.
 */
internal class SunStateProvider(
    private val sunScheduleProvider: SunScheduleProvider,
    locationProvider: LocationProvider,
    private val clock: ZonedClock,
    stateScope: CoroutineScope,
) {
    private val denver = GeoCoordinates(39.7392.latitude, 104.9903.longitude)

    val sunState = locationProvider.location
        .map { location -> createState(location) }
        .stateIn(stateScope, SharingStarted.WhileSubscribed(), SunScheduleState.Initial)

    private fun createState(location: GeoCoordinates?): SunScheduleState {
        return if (location == null) {
            sunScheduleProvider.getScheduleForLocation(
                coordinates = denver,
                date = clock.zonedDate(),
            ).let(SunScheduleState::Unknown)
        } else {
            sunScheduleProvider.getScheduleForLocation(
                coordinates = location,
                date = clock.zonedDate(),
            ).let(SunScheduleState::Known)
        }
    }
}

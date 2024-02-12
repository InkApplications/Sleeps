package com.inkapplications.sleeps.state.sun

import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import regolith.sensors.location.LocationAccess
import regolith.sensors.location.LocationState

/**
 * Uses the current location to provide the sunrise times.
 */
internal class LocationSunriseAccess(
    private val sunScheduleProvider: SunScheduleProvider,
    locationAccess: LocationAccess,
    private val stateScope: CoroutineScope,
    private val clock: Clock = Clock.System,
): SunriseAccess {
    private val usGeographicCenter = GeoCoordinates(
        latitude = (39.8283).latitude,
        longitude = (-98.5795).longitude,
    )
    private val refresh = MutableStateFlow(0)

    override val nextSunrise = locationAccess.locationUpdates
        .combine(refresh) { location, _ -> location }
        .map { location -> createState(location) }
        .onEach { scheduleRefresh(it) }

    private fun createState(location: LocationState): Sunrise {
        return when (location) {
            is LocationState.Known -> sunScheduleProvider.getNextSunriseForLocation(
                coordinates = location.coordinates,
            ).let { Sunrise(it, isEstimate = false) }
            is LocationState.Unknown -> sunScheduleProvider.getNextSunriseForLocation(
                coordinates = usGeographicCenter,
            ).let { Sunrise(it, isEstimate = true) }
        }
    }

    private fun scheduleRefresh(sunriseState: Sunrise) {
        stateScope.launch {
            delay(sunriseState.timestamp.instant - clock.now())
            refresh.value++
        }
    }
}

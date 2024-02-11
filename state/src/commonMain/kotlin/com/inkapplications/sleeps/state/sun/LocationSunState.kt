package com.inkapplications.sleeps.state.sun

import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import regolith.sensors.location.LocationAccess
import regolith.sensors.location.LocationState

/**
 * Uses the current location to provide the sunrise times.
 */
internal class LocationSunState(
    private val sunScheduleProvider: SunScheduleProvider,
    locationAccess: LocationAccess,
    private val stateScope: CoroutineScope,
    private val clock: Clock = Clock.System,
): SunScheduleStateAccess {
    private val usGeographicCenter = GeoCoordinates(
        latitude = (39.8283).latitude,
        longitude = (-98.5795).longitude,
    )
    private val refresh = MutableStateFlow(0)

    override val sunState = locationAccess.locationUpdates
        .combine(refresh) { location, _ -> location }
        .map { location -> createState(location) }
        .onEach { scheduleRefresh(it) }
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

    private fun scheduleRefresh(sunScheduleState: SunScheduleState) {
        val nextSunrise = when (sunScheduleState) {
            is SunScheduleState.Known -> sunScheduleState.sunrise
            is SunScheduleState.Unknown -> sunScheduleState.centralUsSunrise
            SunScheduleState.Initial -> return
        }
        stateScope.launch {
            delay(nextSunrise.instant - clock.now())
            refresh.value++
        }
    }
}

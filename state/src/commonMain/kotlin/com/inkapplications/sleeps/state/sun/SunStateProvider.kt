package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.location.LocationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Uses the current location to provide the sunrise and sunset times.
 */
internal class SunStateProvider(
    sunScheduleProvider: SunScheduleProvider,
    locationProvider: LocationProvider,
    clock: ZonedClock,
    stateScope: CoroutineScope,
) {
    val sunState = locationProvider.location
        .map { it?.let { location ->
            sunScheduleProvider.getScheduleForLocation(
                coordinates = location,
                date = clock.zonedDate(),
            )
        }}
        .map {
            it?.let { SunScheduleState.Known(it) }
                ?: SunScheduleState.Unknown
        }
        .stateIn(stateScope, SharingStarted.WhileSubscribed(), SunScheduleState.Initial)
}

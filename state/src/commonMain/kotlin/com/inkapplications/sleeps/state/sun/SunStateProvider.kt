package com.inkapplications.sleeps.state.sun

import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Uses the current location to provide the sunrise and sunset times.
 */
internal class SunStateProvider(
    sunScheduleProvider: SunScheduleProvider,
    locationUpdates: Flow<GeoCoordinates?>,
    clock: Clock,
    timeZone: TimeZone,
    stateScope: CoroutineScope,
) {
    val sunState = locationUpdates
        .map { it?.let { location ->
            sunScheduleProvider.getScheduleForLocation(
                coordinates = location,
                date = clock.now().toLocalDateTime(timeZone).date,
                timeZone = timeZone
            )
        }}
        .map {
            it?.let { SunScheduleState.Known(it) }
                ?: SunScheduleState.Unknown
        }
        .stateIn(stateScope, SharingStarted.WhileSubscribed(), SunScheduleState.Initial)
}

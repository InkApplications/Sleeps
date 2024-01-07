package com.inkapplications.sleeps.state.location

import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object DummyLocationProvider: LocationProvider {
    override val location: Flow<GeoCoordinates?> = flow {}
}

class FakeLocationProvider(location: GeoCoordinates?): LocationProvider {
    override val location: Flow<GeoCoordinates?> = flow { emit(location) }
}

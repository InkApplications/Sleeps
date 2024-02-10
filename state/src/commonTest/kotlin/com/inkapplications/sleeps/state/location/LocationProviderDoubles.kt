package com.inkapplications.sleeps.state.location

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import regolith.sensors.location.LocationAccess
import regolith.sensors.location.LocationState

object DummyLocationAccess: LocationAccess {
    override val locationUpdates: Flow<LocationState> = flow {}
}

class FakeLocationAccess(location: LocationState): LocationAccess {
    override val locationUpdates: Flow<LocationState> = flow { emit(location) }
}

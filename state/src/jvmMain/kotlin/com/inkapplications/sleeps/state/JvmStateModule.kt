package com.inkapplications.sleeps.state

import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.location.LocationProvider
import com.inkapplications.sleeps.state.sun.JvmSunScheduleProvider
import kotlinx.coroutines.*

/**
 * Create the state module with defaults for the JVM.
 */
fun createJvmStateModule(
    locationProvider: LocationProvider,
    stateScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    clock: ZonedClock = ZonedClock.System,
) = StateModule(
    locationProvider = locationProvider,
    sunScheduleProvider = JvmSunScheduleProvider(),
    stateScope = stateScope,
    clock = clock,
)

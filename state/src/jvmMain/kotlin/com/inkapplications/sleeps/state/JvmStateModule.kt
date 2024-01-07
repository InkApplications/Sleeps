package com.inkapplications.sleeps.state

import com.inkapplications.sleeps.state.location.LocationProvider
import com.inkapplications.sleeps.state.sun.JvmSunScheduleProvider
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone

/**
 * Create the state module with defaults for the JVM.
 */
fun createJvmStateModule(
    locationProvider: LocationProvider,
    stateScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) = StateModule(
    locationProvider = locationProvider,
    sunScheduleProvider = JvmSunScheduleProvider(),
    stateScope = stateScope,
    clock = clock,
    timeZone = timeZone,
)

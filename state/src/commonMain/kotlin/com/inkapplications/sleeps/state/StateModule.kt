package com.inkapplications.sleeps.state

import com.inkapplications.sleeps.state.location.LocationProvider
import com.inkapplications.sleeps.state.sun.SunScheduleProvider
import com.inkapplications.sleeps.state.sun.SunStateProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlin.time.Duration.Companion.milliseconds

class StateModule(
    locationProvider: LocationProvider,
    sunScheduleProvider: SunScheduleProvider,
    stateScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
) {
    private val sunStateProvider = SunStateProvider(
        sunScheduleProvider = sunScheduleProvider,
        clock = clock,
        timeZone = timeZone,
        stateScope = stateScope,
        locationProvider = locationProvider,
    )

    private val waiter = flow {
        delay(400.milliseconds)
        emit(true)
    }

    private val screenLayoutFactory = ScreenLayoutFactory()

    val screenState = combine(
        sunStateProvider.sunState,
        waiter,
    ) { sunScheduleState, _ ->
        screenLayoutFactory.create(sunScheduleState)
    }.stateIn(stateScope, SharingStarted.WhileSubscribed(), screenLayoutFactory.initial)
}


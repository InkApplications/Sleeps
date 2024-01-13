package com.inkapplications.sleeps.state

import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.location.LocationProvider
import com.inkapplications.sleeps.state.notifications.NotificationStateAccess
import com.inkapplications.sleeps.state.sun.SunScheduleProvider
import com.inkapplications.sleeps.state.sun.SunStateProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

class StateModule(
    locationProvider: LocationProvider,
    sunScheduleProvider: SunScheduleProvider,
    stateScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    clock: ZonedClock = ZonedClock.System,
) {
    private val sunStateProvider = SunStateProvider(
        sunScheduleProvider = sunScheduleProvider,
        clock = clock,
        stateScope = stateScope,
        locationProvider = locationProvider,
    )

    private val notificationStateAccess = NotificationStateAccess()

    private val waiter = flow {
        delay(400.milliseconds)
        emit(true)
    }

    private val screenLayoutFactory = ScreenLayoutFactory()

    val screenState = combine(
        sunStateProvider.sunState,
        notificationStateAccess.notificationsState,
        waiter,
    ) { sunScheduleState, notificationState, _ ->
        screenLayoutFactory.create(
            sunScheduleState = sunScheduleState,
            notificationsState = notificationState,
            notificationController = notificationStateAccess,
        )
    }.stateIn(stateScope, SharingStarted.WhileSubscribed(), screenLayoutFactory.initial)
}


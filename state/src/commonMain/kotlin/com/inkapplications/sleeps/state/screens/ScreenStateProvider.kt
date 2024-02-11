package com.inkapplications.sleeps.state.screens

import com.inkapplications.sleeps.state.notifications.NotificationController
import com.inkapplications.sleeps.state.notifications.NotificationStateAccess
import com.inkapplications.sleeps.state.sun.SunScheduleStateAccess
import ink.ui.structures.layouts.UiLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds

internal class ScreenStateProvider(
    sunStateProvider: SunScheduleStateAccess,
    notificationStateAccess: NotificationStateAccess,
    notificationController: NotificationController,
    screenLayoutFactory: ScreenLayoutFactory,
    stateScope: CoroutineScope,
): ScreenState {
    private val waiter = flow {
        kotlinx.coroutines.delay(400.milliseconds)
        emit(true)
    }

    override val screenState: StateFlow<UiLayout> = combine(
        sunStateProvider.sunState,
        notificationStateAccess.notificationsState,
        waiter,
    ) { sunScheduleState, notificationState, _ ->
        screenLayoutFactory.create(
            sunScheduleState = sunScheduleState,
            notificationsState = notificationState,
            notificationController = notificationController,
        )
    }.stateIn(stateScope, SharingStarted.WhileSubscribed(), screenLayoutFactory.initial)
}

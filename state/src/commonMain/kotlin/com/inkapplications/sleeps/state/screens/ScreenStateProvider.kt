package com.inkapplications.sleeps.state.screens

import com.inkapplications.sleeps.state.notifications.NotificationController
import com.inkapplications.sleeps.state.notifications.NotificationSettingsAccess
import com.inkapplications.sleeps.state.schedule.SettingsDrivenScheduleAccess
import ink.ui.structures.layouts.UiLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds

internal class ScreenStateProvider(
    scheduleAccess: SettingsDrivenScheduleAccess,
    notificationSettingsAccess: NotificationSettingsAccess,
    notificationController: NotificationController,
    screenLayoutFactory: ScreenLayoutFactory,
    stateScope: CoroutineScope,
): ScreenState {
    private val waiter = flow {
        kotlinx.coroutines.delay(400.milliseconds)
        emit(true)
    }

    override val screenState: StateFlow<UiLayout> = combine(
        scheduleAccess.schedule,
        notificationSettingsAccess.notificationsState,
        waiter,
    ) { schedule, notificationState, _ ->
        screenLayoutFactory.create(
            schedule = schedule,
            notificationsState = notificationState,
            notificationController = notificationController,
        )
    }.stateIn(stateScope, SharingStarted.WhileSubscribed(), LoadingScreen)
}

package com.inkapplications.sleeps.state.notifications

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class NotificationStateFake(
    initial: NotificationsState,
): NotificationStateAccess {
    val mutableState = MutableStateFlow(initial)
    override val notificationsState: Flow<NotificationsState> = mutableState
}

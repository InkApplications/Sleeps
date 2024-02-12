package com.inkapplications.sleeps.state.notifications

import kotlinx.coroutines.flow.MutableSharedFlow

internal class NotificationSettingsFake: NotificationSettingsAccess {
    override val notificationsState = MutableSharedFlow<NotificationSettings>()
}

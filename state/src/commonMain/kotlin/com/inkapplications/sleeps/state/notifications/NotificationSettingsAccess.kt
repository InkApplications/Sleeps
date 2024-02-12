package com.inkapplications.sleeps.state.notifications

import kotlinx.coroutines.flow.Flow

/**
 * Provides access for retrieving and modifying the app's notification settings.
 */
internal interface NotificationSettingsAccess {
    val notificationsState: Flow<NotificationSettings>
}

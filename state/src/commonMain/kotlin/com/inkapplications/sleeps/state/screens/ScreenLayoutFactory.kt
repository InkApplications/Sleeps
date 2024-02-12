package com.inkapplications.sleeps.state.screens

import com.inkapplications.sleeps.state.notifications.NotificationController
import com.inkapplications.sleeps.state.notifications.NotificationSettings
import com.inkapplications.sleeps.state.schedule.Schedule
import ink.ui.structures.layouts.ScrollingListLayout
import ink.ui.structures.layouts.UiLayout

/**
 * Creates the application screen layout schema based on current application state.
 */
internal class ScreenLayoutFactory {
    /**
     * Create a screen layout for the given state fields.
     */
    fun create(
        schedule: Schedule,
        notificationsState: NotificationSettings,
        notificationController: NotificationController,
    ): UiLayout {
        return ScrollingListLayout(
            items = listOf(
                *ScheduleElements.create(schedule),
                *NotificationSettingElements.create(notificationsState, notificationController)
            )
        )
    }
}

package com.inkapplications.sleeps.state.screens

import com.inkapplications.sleeps.state.notifications.NotificationController
import com.inkapplications.sleeps.state.notifications.NotificationsState
import com.inkapplications.sleeps.state.sun.SunScheduleState
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.*
import ink.ui.structures.layouts.CenteredElementLayout
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
        sunScheduleState: SunScheduleState,
        notificationsState: NotificationsState,
        notificationController: NotificationController,
    ): UiLayout {
        return when (sunScheduleState) {
            SunScheduleState.Initial -> LoadingScreen
            is SunScheduleState.Unknown -> CenteredElementLayout(
                body = TextElement("Unknown"),
            )
            is SunScheduleState.Known -> ScrollingListLayout(
                items = listOf(
                    TextElement("Schedule", style = TextStyle.H1),
                    TextElement("Sunrise: ${sunScheduleState.sunrise.localTime}"),
                    TextElement(
                        text = "Settings",
                        style = TextStyle.H2,
                    ),
                    *NotificationSettings.create(notificationsState, notificationController)
                )
            )
        }
    }
}

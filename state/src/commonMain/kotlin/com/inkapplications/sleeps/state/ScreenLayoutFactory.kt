package com.inkapplications.sleeps.state

import com.inkapplications.sleeps.state.notifications.NotificationController
import com.inkapplications.sleeps.state.notifications.NotificationsState
import com.inkapplications.sleeps.state.sun.SunScheduleState
import ink.ui.structures.GroupingStyle
import ink.ui.structures.Sentiment
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.*
import ink.ui.structures.layouts.CenteredElementLayout
import ink.ui.structures.layouts.ScrollingListLayout
import ink.ui.structures.layouts.UiLayout

/**
 * Creates the application screen layout schema based on current application state.
 */
internal class ScreenLayoutFactory {
    private val loadingScreen = CenteredElementLayout(
        body = ProgressElement.Indeterminate(
            caption = "Loading...",
            sentiment = Sentiment.Primary,
        )
    )

    /**
     * Default state to use when the application first starts.
     */
    val initial: UiLayout = loadingScreen

    /**
     * Create a screen layout for the given state fields.
     */
    fun create(
        sunScheduleState: SunScheduleState,
        notificationsState: NotificationsState,
        notificationController: NotificationController,
    ): UiLayout {
        return when (sunScheduleState) {
            SunScheduleState.Initial -> loadingScreen
            is SunScheduleState.Unknown -> CenteredElementLayout(
                body = TextElement("Unknown"),
            )
            is SunScheduleState.Known -> ScrollingListLayout(
                items = listOf(
                    TextElement("Schedule", style = TextStyle.H1),
                    TextElement("Sunrise: ${sunScheduleState.schedule.sunrise.localTime}"),
                    ElementList(
                        items = listOf(
                            TextElement(
                                text = "Settings",
                                style = TextStyle.H2,
                            ),
                            *createSettings(notificationsState, notificationController)
                        ),
                        groupingStyle = GroupingStyle.Unified,
                    )
                )
            )
        }
    }

    private fun createSettings(
        state: NotificationsState,
        notificationController: NotificationController,
    ): Array<MenuRowElement> {
        val sleepNotifications = MenuRowElement(
            text = "Sleep Notifications",
            onClick = notificationController::onSleepNotificationClick,
            rightElement = CheckBoxElement(
                checked = state is NotificationsState.Configured && state.sleepNotifications,
                onClick = notificationController::onSleepNotificationClick,
            )
        )

        val alarm = MenuRowElement(
            text = "Wake Alarm",
            onClick = notificationController::onWakeAlarmClick,
            rightElement = CheckBoxElement(
                checked = state is NotificationsState.Configured && state.wakeAlarm,
                onClick = notificationController::onWakeAlarmClick,
            )
        )

        return arrayOf(sleepNotifications, alarm)
    }
}

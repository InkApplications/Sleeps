package com.inkapplications.sleeps.state

import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.state.notifications.NotificationControllerStub
import com.inkapplications.sleeps.state.notifications.NotificationsState
import com.inkapplications.sleeps.state.sun.SunSchedule
import com.inkapplications.sleeps.state.sun.SunScheduleState
import ink.ui.structures.elements.*
import ink.ui.structures.layouts.CenteredElementLayout
import ink.ui.structures.layouts.ScrollingListLayout
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScreenLayoutFactoryTest {
    private val factory = ScreenLayoutFactory()

    @Test
    fun initialLoading() {
        assertTrue(factory.initial is CenteredElementLayout)
        assertTrue(factory.initial.body is ProgressElement.Indeterminate)

        val layout = factory.create(
            sunScheduleState = SunScheduleState.Initial,
            notificationsState = NotificationsState.Initial,
            notificationController = NotificationControllerStub,
        )
        assertTrue(layout is CenteredElementLayout)
        assertTrue(layout.body is ProgressElement.Indeterminate)
    }

    @Test
    fun initial() {
        val layout = factory.create(
            sunScheduleState = SunScheduleState.Unknown,
            notificationsState = NotificationsState.Initial,
            notificationController = NotificationControllerStub,
        )

        assertTrue(layout is CenteredElementLayout)
        assertTrue(layout.body is TextElement)
        assertTrue((layout.body as TextElement).text == "Unknown")
    }

    @Test
    fun loaded() {
        val layout = factory.create(
            sunScheduleState = SunScheduleState.Known(
                schedule = SunSchedule(
                    sunrise = LocalTime(7, 1, 0).atZone(TimeZone.UTC),
                )
            ),
            notificationsState = NotificationsState.Configured(
                sleepNotifications = true,
                wakeAlarm = false,
            ),
            notificationController = NotificationControllerStub,
        )

        assertTrue(layout is ScrollingListLayout, "Screen is a scrolling list")
        val sunDescriptionBody = layout.items[1]
        assertTrue(sunDescriptionBody is TextElement, "2nd item in list is the sun's description")
        assertEquals("Sunrise: 07:01", sunDescriptionBody.text)

        val menu = layout.items[2]
        assertTrue(menu is ElementList, "3rd item in list the settings menu")
        val sleepNotificationItem = menu.items[1]
        assertTrue(sleepNotificationItem is MenuRowElement, "1st item in menu is sleep notifications")
        val sleepNotificationCheckbox = sleepNotificationItem.rightElement
        assertTrue(sleepNotificationCheckbox is CheckBoxElement, "Sleep notification item has a checkbox")
        assertTrue(sleepNotificationCheckbox.checked, "Sleep notification checkbox is checked when configured to true")

        val wakeAlarmItem = menu.items[2]
        assertTrue(wakeAlarmItem is MenuRowElement, "2nd item in menu is wake alarm")
        val wakeAlarmCheckbox = wakeAlarmItem.rightElement
        assertTrue(wakeAlarmCheckbox is CheckBoxElement, "Wake alarm item has a checkbox")
        assertFalse(wakeAlarmCheckbox.checked, "Wake alarm checkbox is not checked when configured to false")
    }
}

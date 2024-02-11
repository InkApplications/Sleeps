package com.inkapplications.sleeps.state

import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.state.notifications.NotificationControllerStub
import com.inkapplications.sleeps.state.notifications.NotificationsState
import com.inkapplications.sleeps.state.screens.ScreenLayoutFactory
import com.inkapplications.sleeps.state.sun.SunScheduleState
import ink.ui.structures.elements.*
import ink.ui.structures.layouts.CenteredElementLayout
import ink.ui.structures.layouts.ScrollingListLayout
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class ScreenLayoutFactoryTest {
    private val testDateTime = LocalTime(1, 2).atDate(2004, 5, 6)
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
            sunScheduleState = SunScheduleState.Unknown(
                centralUsSunrise = testDateTime.atZone(TimeZone.UTC),
            ),
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
                sunrise = testDateTime.atZone(TimeZone.UTC),
            ),
            notificationsState = NotificationsState.Configured(
                wakeAlarm = true,
                sleepNotifications = false,
                alarmMargin = 15.minutes,
                sleepMargin = 30.minutes,
                sleepTarget = 60.minutes,
            ),
            notificationController = NotificationControllerStub,
        )

        assertTrue(layout is ScrollingListLayout, "Screen is a scrolling list")
        val sunDescriptionBody = layout.items[1]
        assertTrue(sunDescriptionBody is TextElement, "2nd item in list is the sun's description")
        assertEquals("Sunrise: 01:02", sunDescriptionBody.text)

        assertTrue(layout.items[2] is TextElement, "3rd item in list is the settings header")

        val alarmSettings = layout.items[3]
        assertTrue(alarmSettings is ElementList, "3rd item in list the settings menu")
        assertTrue(alarmSettings.items[0] is TextElement, "1st item in menu is the wake alarm header")
        val sleepNotificationItem = alarmSettings.items[1]
        assertTrue(sleepNotificationItem is MenuRowElement, "2nd item in menu is sleep notifications")
        val sleepNotificationCheckbox = sleepNotificationItem.rightElement
        assertTrue(sleepNotificationCheckbox is CheckBoxElement, "Sleep notification item has a checkbox")
        assertTrue(sleepNotificationCheckbox.checked, "Sleep notification checkbox is checked when configured to true")

        val alarmMargin = alarmSettings.items[2]
        assertTrue(alarmMargin is MenuRowElement, "2nd item in menu is alarm margin")
        val alarmMarginSpinner = alarmMargin.rightElement
        assertTrue(alarmMarginSpinner is SpinnerElement, "Alarm margin item has a spinner")
        assertEquals("0.25", alarmMarginSpinner.value, "Alarm margin spinner is configured to 10 minutes")

        val sleepMenu = layout.items[4]
        assertTrue(sleepMenu is ElementList, "4th item in list the settings menu")
        assertTrue(sleepMenu.items[0] is TextElement, "1st item in menu is the sleep notifications header")
        val wakeAlarmItem = sleepMenu.items[1]
        assertTrue(wakeAlarmItem is MenuRowElement, "2nd item in menu is wake alarm")
        val wakeAlarmCheckbox = wakeAlarmItem.rightElement
        assertTrue(wakeAlarmCheckbox is CheckBoxElement, "Wake alarm item has a checkbox")
        assertFalse(wakeAlarmCheckbox.checked, "Wake alarm checkbox is not checked when configured to false")

        val sleepTargetItem = sleepMenu.items[2]
        assertTrue(sleepTargetItem is MenuRowElement, "3rd item in menu is sleep target")
        val sleepTargetSpinner = sleepTargetItem.rightElement
        assertTrue(sleepTargetSpinner is SpinnerElement, "Sleep target item has a spinner")
        assertEquals("1.0", sleepTargetSpinner.value, "Sleep target spinner is configured to 60 minutes")

        val sleepMarginItem = sleepMenu.items[3]
        assertTrue(sleepMarginItem is MenuRowElement, "4th item in menu is sleep margin")
        val sleepMarginSpinner = sleepMarginItem.rightElement
        assertTrue(sleepMarginSpinner is SpinnerElement, "Sleep margin item has a spinner")
        assertEquals("0.5", sleepMarginSpinner.value, "Sleep margin spinner is configured to 30 minutes")
    }
}

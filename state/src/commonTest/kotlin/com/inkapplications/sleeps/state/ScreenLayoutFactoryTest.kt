package com.inkapplications.sleeps.state

import com.inkapplications.sleeps.state.sun.SunSchedule
import com.inkapplications.sleeps.state.sun.SunScheduleState
import ink.ui.structures.elements.ProgressElement
import ink.ui.structures.elements.TextElement
import ink.ui.structures.layouts.CenteredElementLayout
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScreenLayoutFactoryTest {
    private val factory = ScreenLayoutFactory()

    @Test
    fun initialLoading() {
        assertTrue(factory.initial is CenteredElementLayout)
        assertTrue(factory.initial.body is ProgressElement.Indeterminate)

        val layout = factory.create(SunScheduleState.Initial)
        assertTrue(layout is CenteredElementLayout)
        assertTrue(layout.body is ProgressElement.Indeterminate)
    }

    @Test
    fun initial() {
        val layout = factory.create(SunScheduleState.Unknown)

        assertTrue(layout is CenteredElementLayout)
        assertTrue(layout.body is TextElement)
        assertTrue((layout.body as TextElement).text == "Unknown")
    }

    @Test
    fun known() {
        val layout = factory.create(SunScheduleState.Known(
            schedule = SunSchedule(
                sunrise = LocalTime(7, 1, 0),
                sunset = LocalTime(17, 19, 0),
            )
        ))

        assertTrue(layout is CenteredElementLayout)
        val body = layout.body
        assertTrue(body is TextElement)
        assertEquals("Sunrise: 07:01\nSunset: 17:19", body.text)
    }
}

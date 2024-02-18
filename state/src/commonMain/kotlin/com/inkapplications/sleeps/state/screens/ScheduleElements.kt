package com.inkapplications.sleeps.state.screens

import com.inkapplications.sleeps.state.schedule.Schedule
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.TextElement
import ink.ui.structures.elements.UiElement

internal object ScheduleElements {
    fun create(schedule: Schedule): Array<UiElement> {
        return arrayOf(
            TextElement("Schedule", style = TextStyle.H1),
            TextElement("Wake: ${schedule.wake.localTime}"),
            TextElement("Sunrise: ${schedule.sunrise.localTime}"),
            TextElement("Sleep: ${schedule.sleep.localTime}"),
        )
    }
}

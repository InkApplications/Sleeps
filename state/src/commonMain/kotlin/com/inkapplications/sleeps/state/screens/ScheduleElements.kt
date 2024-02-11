package com.inkapplications.sleeps.state.screens

import com.inkapplications.sleeps.state.sun.SunScheduleState
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.TextElement
import ink.ui.structures.elements.UiElement

internal object ScheduleElements {
    fun create(sunScheduleState: SunScheduleState.Known): Array<UiElement> {
        return arrayOf(
            TextElement("Schedule", style = TextStyle.H1),
            TextElement("Sunrise: ${sunScheduleState.sunrise.localTime}"),
            TextElement(
                text = "Settings",
                style = TextStyle.H2,
            )
        )
    }
}

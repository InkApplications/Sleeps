package com.inkapplications.sleeps.state

import com.inkapplications.sleeps.state.sun.SunScheduleState
import ink.ui.structures.Sentiment
import ink.ui.structures.elements.ProgressElement
import ink.ui.structures.elements.TextElement
import ink.ui.structures.layouts.CenteredElementLayout
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
    fun create(sunScheduleState: SunScheduleState): UiLayout {
        return when (sunScheduleState) {
            SunScheduleState.Initial -> loadingScreen
            SunScheduleState.Unknown -> CenteredElementLayout(
                body = TextElement("Unknown"),
            )
            is SunScheduleState.Known -> CenteredElementLayout(
                body = TextElement("Sunrise: ${sunScheduleState.schedule.sunrise}\nSunset: ${sunScheduleState.schedule.sunset}"),
            )
        }
    }
}

package com.inkapplications.sleeps.state.screens

import ink.ui.structures.Sentiment
import ink.ui.structures.elements.ProgressElement
import ink.ui.structures.layouts.CenteredElementLayout

/**
 * Screen shown when the application is first starting, before data is loaded.
 */
internal val LoadingScreen = CenteredElementLayout(
    body = ProgressElement.Indeterminate(
        caption = "Loading...",
        sentiment = Sentiment.Primary,
    )
)

package com.inkapplications.sleeps.state.screens

import ink.ui.structures.layouts.UiLayout
import kotlinx.coroutines.flow.StateFlow

/**
 * Provides UI states for the application
 */
interface ScreenState {
    /**
     * Emits the latest renderable screen UI.
     */
    val screenState: StateFlow<UiLayout>
}


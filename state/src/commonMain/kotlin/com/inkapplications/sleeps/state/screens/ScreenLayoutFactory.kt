package com.inkapplications.sleeps.state.screens

import com.inkapplications.sleeps.state.schedule.Schedule
import ink.ui.structures.layouts.ScrollingListLayout
import ink.ui.structures.layouts.UiLayout
import regolith.data.settings.SettingCategory
import regolith.data.settings.structure.SettingEntry

/**
 * Creates the application screen layout schema based on current application state.
 */
internal class ScreenLayoutFactory {
    /**
     * Create a screen layout for the given state fields.
     */
    fun create(
        schedule: Schedule,
        settings: List<Pair<SettingCategory?, List<SettingEntry<*, *>>>>,
        settingsController: SettingsController,
    ): UiLayout {
        return ScrollingListLayout(
            items = listOf(
                *ScheduleElements.create(schedule),
                *SettingElements.createGroups(settings, settingsController)
            )
        )
    }
}

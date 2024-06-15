package com.inkapplications.sleeps.state.screens

import com.inkapplications.sleeps.state.settings.DurationEntry
import ink.ui.structures.TextStyle
import ink.ui.structures.elements.TextElement
import ink.ui.structures.elements.UiElement
import regolith.data.settings.SettingCategory
import regolith.data.settings.structure.BooleanSetting
import regolith.data.settings.structure.SettingEntry

/**
 * Creates the UI elements for the settings screen.
 */
internal object SettingElements {
    fun createGroups(
        settings: List<Pair<SettingCategory?, List<SettingEntry<*, *>>>>,
        settingsController: SettingsController,
    ): Array<UiElement> {
        return arrayOf(
            TextElement("Settings", TextStyle.H1),
            *settings.flatMap { (category, settings) ->
                createGroup(category, settings, settingsController).toList()
            }.toTypedArray()
        )
    }

    fun createGroup(category: SettingCategory?, settings: List<SettingEntry<*, *>>, controller: SettingsController): Array<UiElement> {
        return listOf(
            category?.name?.let { TextElement(it, TextStyle.H2) },
            *create(settings, controller),
        ).filterNotNull().toTypedArray()
    }

    fun create(settings: List<SettingEntry<*, *>>, controller: SettingsController): Array<UiElement> {
        return settings.map { entry ->
            when (entry) {
                is BooleanSetting.Entry -> SettingsRows.toggleRow(
                    text = entry.setting.name,
                    checked = entry.value,
                    onClick = { controller.onToggleBooleanSetting(entry.setting, entry.value) }
                )
                is DurationEntry -> SettingsRows.durationRow(
                    name = entry.setting.name,
                    value = entry.value,
                    onIncrease = { controller.onIncreaseDurationSetting(entry.setting, entry.value) },
                    onDecrease = { controller.onDecreaseDurationSetting(entry.setting, entry.value) },
                )
                else -> {
                    TextElement("${entry.setting.name}: ${entry.value}")
                }
            }
        }.toTypedArray()
    }
}

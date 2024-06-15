package com.inkapplications.sleeps.state.screens

import regolith.data.settings.structure.BooleanSetting
import regolith.data.settings.structure.LongData
import kotlin.time.Duration

/**
 * Actions that can be invoked from the settings UI.
 */
internal interface SettingsController {
    /**
     * Invoked when the user toggles a boolean setting.
     *
     * @param setting The setting that is being changed by the user
     * @param current The value of the setting before being toggled
     */
    fun onToggleBooleanSetting(setting: BooleanSetting, current: Boolean)

    /**
     * Invoked when the user increases a duration setting.
     *
     * @param setting The setting that is being changed by the user
     * @param current The value of the setting before being increased
     */
    fun onIncreaseDurationSetting(setting: LongData<Duration>, current: Duration)

    /**
     * Invoked when the user decreases a duration setting.
     *
     * @param setting The setting that is being changed by the user
     * @param current The value of the setting before being decreased
     */
    fun onDecreaseDurationSetting(setting: LongData<Duration>, current: Duration)
}

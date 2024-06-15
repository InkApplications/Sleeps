package com.inkapplications.sleeps.state.screens

import com.inkapplications.sleeps.state.schedule.SettingsDrivenScheduleAccess
import ink.ui.structures.layouts.UiLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import regolith.data.settings.SettingsAccess
import regolith.data.settings.observeSettings
import regolith.data.settings.structure.*
import regolith.data.settings.writeSetting
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

internal class ScreenStateProvider(
    scheduleAccess: SettingsDrivenScheduleAccess,
    settingsAccess: SettingsAccess,
    settings: Collection<Setting<*>>,
    screenLayoutFactory: ScreenLayoutFactory,
    stateScope: CoroutineScope,
): ScreenState {
    private val waiter = flow {
        kotlinx.coroutines.delay(400.milliseconds)
        emit(true)
    }

    override val screenState: StateFlow<UiLayout> = combine(
        scheduleAccess.schedule,
        settingsAccess.observeSettings(settings),
        waiter,
    ) { schedule, settings, _ ->
        screenLayoutFactory.create(
            schedule = schedule,
            settings = settings.toList()
                .groupBy { it.setting.category }
                .toList()
                .sortedBy { it.first?.name },
            settingsController = object: SettingsController {
                override fun onToggleBooleanSetting(setting: BooleanSetting, current: Boolean) {
                    stateScope.launch {
                        settingsAccess.writeSetting(setting, current.not())
                    }
                }

                override fun onIncreaseDurationSetting(setting: LongData<Duration>, current: Duration) {
                    stateScope.launch {
                        settingsAccess.writeSetting(setting, current + 15.minutes)
                    }
                }

                override fun onDecreaseDurationSetting(setting: LongData<Duration>, current: Duration) {
                    stateScope.launch {
                        settingsAccess.writeSetting(setting, current - 15.minutes)
                    }
                }
            }
        )
    }.stateIn(stateScope, SharingStarted.WhileSubscribed(), LoadingScreen)
}

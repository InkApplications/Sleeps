package com.inkapplications.sleeps.state

import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.alarms.AlarmAccess
import com.inkapplications.sleeps.state.alarms.AlarmBeeper
import com.inkapplications.sleeps.state.alarms.MaintenanceScheduler
import com.inkapplications.sleeps.state.sun.JvmSunScheduleProvider
import kimchi.logger.LogWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import regolith.data.settings.SettingsAccess
import regolith.init.Initializer
import regolith.sensors.location.LocationAccess

/**
 * Create the state module with defaults for the JVM.
 */
fun createJvmStateModule(
    locationAccess: LocationAccess,
    logWriter: LogWriter,
    beeper: AlarmBeeper,
    maintenanceScheduler: MaintenanceScheduler,
    alarmAccess: AlarmAccess,
    settingsAccess: SettingsAccess,
    initializers: List<Initializer> = emptyList(),
    stateScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    clock: ZonedClock = ZonedClock.System,
) = StateModule(
    locationAccess = locationAccess,
    sunScheduleProvider = JvmSunScheduleProvider(clock),
    stateScope = stateScope,
    clock = clock,
    alarmAccess = alarmAccess,
    logWriter = logWriter,
    beeper = beeper,
    maintenanceScheduler = maintenanceScheduler,
    settingsAccess = settingsAccess,
    initializers = initializers,
)

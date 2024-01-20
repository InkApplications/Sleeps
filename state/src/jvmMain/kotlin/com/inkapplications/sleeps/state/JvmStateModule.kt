package com.inkapplications.sleeps.state

import app.cash.sqldelight.db.SqlDriver
import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.alarms.AlarmAccess
import com.inkapplications.sleeps.state.alarms.AlarmBeeper
import com.inkapplications.sleeps.state.location.LocationProvider
import com.inkapplications.sleeps.state.sun.JvmSunScheduleProvider
import kimchi.logger.LogWriter
import kotlinx.coroutines.*
import regolith.init.Initializer

/**
 * Create the state module with defaults for the JVM.
 */
fun createJvmStateModule(
    locationProvider: LocationProvider,
    logWriter: LogWriter,
    beeper: AlarmBeeper,
    alarmAccess: AlarmAccess,
    settingsDriver: SqlDriver,
    initializers: List<Initializer> = emptyList(),
    stateScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    clock: ZonedClock = ZonedClock.System,
) = StateModule(
    locationProvider = locationProvider,
    sunScheduleProvider = JvmSunScheduleProvider(),
    stateScope = stateScope,
    clock = clock,
    alarmAccess = alarmAccess,
    logWriter = logWriter,
    beeper = beeper,
    settingsDriver = settingsDriver,
    initializers = initializers,
)

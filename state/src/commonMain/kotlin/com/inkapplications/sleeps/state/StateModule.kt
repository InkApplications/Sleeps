package com.inkapplications.sleeps.state

import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.alarms.*
import com.inkapplications.sleeps.state.notifications.NotificationSettings
import com.inkapplications.sleeps.state.schedule.SettingsDrivenScheduleAccess
import com.inkapplications.sleeps.state.screens.ScreenLayoutFactory
import com.inkapplications.sleeps.state.screens.ScreenState
import com.inkapplications.sleeps.state.screens.ScreenStateProvider
import com.inkapplications.sleeps.state.sun.LocationSunriseAccess
import com.inkapplications.sleeps.state.sun.SunScheduleProvider
import kimchi.Kimchi
import kimchi.logger.LogWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import regolith.data.settings.SettingsAccess
import regolith.init.Initializer
import regolith.init.RegolithInitRunner
import regolith.processes.daemon.DaemonInitializer
import regolith.sensors.location.LocationAccess

class StateModule(
    locationAccess: LocationAccess,
    sunScheduleProvider: SunScheduleProvider,
    alarmAccess: AlarmAccess,
    beeper: AlarmBeeper,
    maintenanceScheduler: MaintenanceScheduler,
    logWriter: LogWriter,
    initializers: List<Initializer>,
    settingsAccess: SettingsAccess,
    stateScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    clock: ZonedClock = ZonedClock.System,
) {
    private val kimchi = Kimchi.apply { addLog(logWriter) }
    private val kimchiRegolithAdapter = KimchiRegolithAdapter(kimchi)
    private val sunriseAccess = LocationSunriseAccess(
        sunScheduleProvider = sunScheduleProvider,
        stateScope = stateScope,
        locationAccess = locationAccess,
    )

    private val notificationSettings = NotificationSettings()
    private val screenLayoutFactory = ScreenLayoutFactory()

    private val scheduleAccess = SettingsDrivenScheduleAccess(
        sunriseAccess = sunriseAccess,
        notificationSettings = notificationSettings,
        settingsAccess = settingsAccess,
    )

    val screenProvider: ScreenState = ScreenStateProvider(
        screenLayoutFactory = screenLayoutFactory,
        scheduleAccess = scheduleAccess,
        settings = notificationSettings.settings,
        settingsAccess = settingsAccess,
        stateScope = stateScope,
    )

    private val alarmScheduler = AlarmScheduler(
        alarmAccess = alarmAccess,
        scheduleAccess = scheduleAccess,
        notificationSettings = notificationSettings,
        settingsAccess = settingsAccess,
        clock = clock,
        logger = kimchi,
    )

    private val daemonInitializer = DaemonInitializer(
        daemons = listOf(alarmScheduler),
        callbacks = kimchiRegolithAdapter,
        daemonScope = stateScope,
        clock = clock,
    )

    private val internalInitializers: List<Initializer> = listOf(
        daemonInitializer,
    )

    val alarmController: AlarmController = BeepingAlarmController(
        beeper = beeper,
    )

    val bootController: DeviceBootController = MaintenanceBootController(
        maintenanceScheduler = maintenanceScheduler,
    )

    val maintenanceController: MaintenanceController = alarmScheduler

    val init = RegolithInitRunner(
        initializers = initializers + internalInitializers,
        callbacks = kimchiRegolithAdapter,
        initializerScope = stateScope,
    )
}


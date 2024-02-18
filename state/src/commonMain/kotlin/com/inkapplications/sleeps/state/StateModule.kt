package com.inkapplications.sleeps.state

import app.cash.sqldelight.db.SqlDriver
import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.alarms.*
import com.inkapplications.sleeps.state.notifications.DatabaseNotificationSettingsAccess
import com.inkapplications.sleeps.state.schedule.SettingsDrivenScheduleAccess
import com.inkapplications.sleeps.state.screens.ScreenLayoutFactory
import com.inkapplications.sleeps.state.screens.ScreenState
import com.inkapplications.sleeps.state.screens.ScreenStateProvider
import com.inkapplications.sleeps.state.settings.AlarmSettings
import com.inkapplications.sleeps.state.settings.MinutesDurationAdapter
import com.inkapplications.sleeps.state.settings.Settings
import com.inkapplications.sleeps.state.sun.LocationSunriseAccess
import com.inkapplications.sleeps.state.sun.SunScheduleProvider
import kimchi.Kimchi
import kimchi.logger.LogWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import regolith.init.Initializer
import regolith.init.RegolithInitRunner
import regolith.processes.daemon.DaemonInitializer
import regolith.sensors.location.LocationAccess

class StateModule(
    locationAccess: LocationAccess,
    sunScheduleProvider: SunScheduleProvider,
    alarmAccess: AlarmAccess,
    beeper: AlarmBeeper,
    logWriter: LogWriter,
    initializers: List<Initializer>,
    settingsDriver: SqlDriver,
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

    private val settingsDatabase = Settings(
        driver = settingsDriver,
        AlarmSettingsAdapter = AlarmSettings.Adapter(
            alarm_marginAdapter = MinutesDurationAdapter,
            sleep_targetAdapter = MinutesDurationAdapter,
            sleep_marginAdapter = MinutesDurationAdapter,
        )
    )

    private val notificationStateAccess = DatabaseNotificationSettingsAccess(
        alarmSettings = settingsDatabase.alarmSettingsQueries,
    )

    private val screenLayoutFactory = ScreenLayoutFactory()

    private val scheduleAccess = SettingsDrivenScheduleAccess(
        sunriseAccess = sunriseAccess,
        notificationSettings = notificationStateAccess,
    )

    val screenProvider: ScreenState = ScreenStateProvider(
        notificationSettingsAccess = notificationStateAccess,
        notificationController = notificationStateAccess,
        screenLayoutFactory = screenLayoutFactory,
        scheduleAccess = scheduleAccess,
        stateScope = stateScope,
    )

    private val alarmScheduler = AlarmScheduler(
        alarmAccess = alarmAccess,
        scheduleAccess = scheduleAccess,
        notificationSettings = notificationStateAccess,
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

    val bootController: DeviceBootController = alarmScheduler

    val init = RegolithInitRunner(
        initializers = initializers + internalInitializers,
        callbacks = kimchiRegolithAdapter,
        initializerScope = stateScope,
    )
}


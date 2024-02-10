package com.inkapplications.sleeps.state

import app.cash.sqldelight.db.SqlDriver
import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.alarms.*
import com.inkapplications.sleeps.state.alarms.AlarmScheduler
import com.inkapplications.sleeps.state.alarms.BeepingAlarmController
import com.inkapplications.sleeps.state.notifications.DatabaseNotificationStateAccess
import com.inkapplications.sleeps.state.settings.AlarmSettings
import com.inkapplications.sleeps.state.settings.MinutesDurationAdapter
import com.inkapplications.sleeps.state.settings.Settings
import com.inkapplications.sleeps.state.sun.SunScheduleProvider
import com.inkapplications.sleeps.state.sun.LocationSunState
import kimchi.Kimchi
import kimchi.logger.LogWriter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import regolith.init.Initializer
import regolith.init.RegolithInitRunner
import regolith.processes.daemon.DaemonInitializer
import regolith.sensors.location.LocationAccess
import kotlin.time.Duration.Companion.milliseconds

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
    private val sunStateProvider = LocationSunState(
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

    private val notificationStateAccess = DatabaseNotificationStateAccess(
        alarmSettings = settingsDatabase.alarmSettingsQueries,
    )

    private val waiter = flow {
        delay(400.milliseconds)
        emit(true)
    }

    private val screenLayoutFactory = ScreenLayoutFactory()

    val screenState = combine(
        sunStateProvider.sunState,
        notificationStateAccess.notificationsState,
        waiter,
    ) { sunScheduleState, notificationState, _ ->
        screenLayoutFactory.create(
            sunScheduleState = sunScheduleState,
            notificationsState = notificationState,
            notificationController = notificationStateAccess,
        )
    }.stateIn(stateScope, SharingStarted.WhileSubscribed(), screenLayoutFactory.initial)

    private val alarmScheduler = AlarmScheduler(
        alarmAccess = alarmAccess,
        sunScheduleAccess = sunStateProvider,
        notificationSettings = notificationStateAccess,
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


package com.inkapplications.sleeps.state

import app.cash.sqldelight.db.SqlDriver
import com.inkapplications.datetime.ZonedClock
import com.inkapplications.sleeps.state.alarms.*
import com.inkapplications.sleeps.state.alarms.AlarmScheduler
import com.inkapplications.sleeps.state.alarms.BeepingAlarmController
import com.inkapplications.sleeps.state.location.LocationProvider
import com.inkapplications.sleeps.state.notifications.NotificationStateAccess
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
import kotlin.time.Duration.Companion.milliseconds

class StateModule(
    locationProvider: LocationProvider,
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
        clock = clock,
        stateScope = stateScope,
        locationProvider = locationProvider,
    )

    private val settingsDatabase = Settings(settingsDriver)

    private val notificationStateAccess = NotificationStateAccess(
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

    val init = RegolithInitRunner(
        initializers = initializers + internalInitializers,
        callbacks = kimchiRegolithAdapter,
        initializerScope = stateScope,
    )
}


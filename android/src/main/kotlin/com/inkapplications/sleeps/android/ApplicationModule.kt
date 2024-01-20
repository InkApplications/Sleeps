package com.inkapplications.sleeps.android

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.location.LocationManager
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.inkapplications.sleeps.android.alarms.AlarmBeeper
import com.inkapplications.sleeps.android.alarms.AndroidAlarmAccess
import com.inkapplications.sleeps.android.alarms.AlarmNotifications
import com.inkapplications.sleeps.state.createJvmStateModule
import com.inkapplications.sleeps.state.settings.Settings
import kimchi.logger.defaultWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Application-wide Dependency module.
 *
 * The modules in this module scope live for the entire application's lifecycle.
 */
class ApplicationModule(
    application: Application,
) {
    val backgroundScope = CoroutineScope(Dispatchers.Default)
    val beeper = AlarmBeeper(application)
    val locationProvider = AndroidLocationProvider(
        locationManager = application.getSystemService(Activity.LOCATION_SERVICE) as LocationManager,
        context = application,
    )
    val alarmAccess = AndroidAlarmAccess(
        context = application,
        alarmManager = application.getSystemService(Activity.ALARM_SERVICE) as AlarmManager,
    )
    val notifications = AlarmNotifications(
        context = application,
        notificationManager = application.getSystemService(Activity.NOTIFICATION_SERVICE) as android.app.NotificationManager,
    )
    private val settingsDriver = AndroidSqliteDriver(Settings.Schema, application, "settings.db")

    private val stateModule = createJvmStateModule(
        locationProvider = locationProvider,
        alarmAccess = alarmAccess,
        beeper = beeper,
        initializers = listOf(
            notifications,
        ),
        settingsDriver = settingsDriver,
        stateScope = backgroundScope,
        logWriter = defaultWriter,
    )

    val screenState = stateModule.screenState
    val initRunner = stateModule.init
    val alarmExecutor = stateModule.alarmController
}

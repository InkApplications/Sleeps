package com.inkapplications.sleeps.android

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.location.LocationManager
import androidx.core.location.LocationRequestCompat
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.inkapplications.sleeps.android.alarms.AlarmBeeper
import com.inkapplications.sleeps.android.alarms.AlarmNotifications
import com.inkapplications.sleeps.android.alarms.AndroidAlarmAccess
import com.inkapplications.sleeps.android.maintenance.AndroidMaintenanceScheduler
import com.inkapplications.sleeps.state.createJvmStateModule
import com.inkapplications.sleeps.state.settings.Settings
import kimchi.logger.defaultWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import regolith.sensors.location.AndroidLocationAccess
import regolith.sensors.location.LocationUpdateConfig
import kotlin.time.Duration.Companion.minutes

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
    val locationAccess = AndroidLocationAccess(
        locationManager = application.getSystemService(Activity.LOCATION_SERVICE) as LocationManager,
        context = application,
        config = LocationUpdateConfig(
            request = LocationRequestCompat
                .Builder(LocationRequestCompat.PASSIVE_INTERVAL)
                .setMinUpdateIntervalMillis(1.minutes.inWholeMilliseconds)
                .build(),
        ),
    )
    val alarmManager = application.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
    val alarmAccess = AndroidAlarmAccess(
        context = application,
        alarmManager = alarmManager,
    )
    val notifications = AlarmNotifications(
        context = application,
        notificationManager = application.getSystemService(Activity.NOTIFICATION_SERVICE) as android.app.NotificationManager,
    )
    private val settingsDriver = AndroidSqliteDriver(Settings.Schema, application, "settings.db")

    private val stateModule = createJvmStateModule(
        locationAccess = locationAccess,
        alarmAccess = alarmAccess,
        beeper = beeper,
        maintenanceScheduler = AndroidMaintenanceScheduler(
            alarmManager = alarmManager,
            context = application,
        ),
        initializers = listOf(
            notifications,
        ),
        settingsDriver = settingsDriver,
        stateScope = backgroundScope,
        logWriter = defaultWriter,
    )

    val screens = stateModule.screenProvider
    val initRunner = stateModule.init
    val alarmExecutor = stateModule.alarmController
    val bootController = stateModule.bootController
    val maintenanceController = stateModule.maintenanceController
}

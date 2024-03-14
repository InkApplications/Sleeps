package com.inkapplications.sleeps.android.maintenance

import android.app.Service
import android.content.Context
import android.content.Intent
import com.inkapplications.sleeps.android.SleepApplication
import kimchi.Kimchi
import kotlinx.coroutines.launch

private const val NOTIFICATION_ID = 859

/**
 * Background service used to perform maintenance tasks.
 */
class MaintenanceService: Service() {
    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Kimchi.trace("onStartCommand")
        with (SleepApplication.module) {
            startForeground(NOTIFICATION_ID, notifications.createMaintenanceNotification())
            backgroundScope.launch {
                maintenanceController.performMaintenance()
                Kimchi.debug("Maintenance Complete")
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}

fun Context.createMaintenanceServiceIntent(): Intent {
    return Intent(this, MaintenanceService::class.java)
}

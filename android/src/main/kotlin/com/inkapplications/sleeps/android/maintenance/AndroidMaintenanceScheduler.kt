package com.inkapplications.sleeps.android.maintenance

import android.app.AlarmManager
import android.content.Context
import com.inkapplications.sleeps.state.alarms.MaintenanceScheduler
import kimchi.Kimchi
import kotlin.time.Duration

/**
 * Uses Android's [AlarmManager] to schedule background maintenance.
 */
class AndroidMaintenanceScheduler(
    private val alarmManager: AlarmManager,
    private val context: Context
): MaintenanceScheduler {
    override fun scheduleMaintenance(repeating: Duration) {
        Kimchi.info("Scheduling Maintenance repeating $repeating")
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            0,
            repeating.inWholeMilliseconds,
            context.createMaintenanceReceiverIntent()
        )
    }
}

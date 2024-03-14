package com.inkapplications.sleeps.state

import com.inkapplications.sleeps.state.alarms.MaintenanceScheduler
import kotlin.time.Duration.Companion.hours

class MaintenanceBootController(
    val maintenanceScheduler: MaintenanceScheduler,
): DeviceBootController {
    override fun onDeviceBoot() {
        maintenanceScheduler.scheduleMaintenance(2.hours)
    }
}

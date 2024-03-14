package com.inkapplications.sleeps.state.alarms

/**
 * Controls regular background tasks used to maintain the alarm schedule.
 */
interface MaintenanceController {
    /**
     * Run any maintenance tasks on the alarms/data.
     */
    suspend fun performMaintenance()
}

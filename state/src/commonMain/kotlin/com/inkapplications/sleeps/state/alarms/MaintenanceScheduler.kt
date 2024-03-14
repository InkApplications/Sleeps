package com.inkapplications.sleeps.state.alarms

import kotlin.time.Duration

/**
 * Schedule regular maintenance tasks.
 */
interface MaintenanceScheduler {
    /**
     * Instruct the system to perform maintenance on a repeating duration.
     */
    fun scheduleMaintenance(
        repeating: Duration,
    )
}

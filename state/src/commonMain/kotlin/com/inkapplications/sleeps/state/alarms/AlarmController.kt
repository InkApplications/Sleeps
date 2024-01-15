package com.inkapplications.sleeps.state.alarms

/**
 * Actions invoked when the system sets off alarms.
 */
interface AlarmController {
    /**
     * Start any operations that should be performed when an alarm goes off.
     */
    suspend fun onStartAlarm()
}


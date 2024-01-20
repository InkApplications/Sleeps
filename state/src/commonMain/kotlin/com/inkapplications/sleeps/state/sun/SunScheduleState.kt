package com.inkapplications.sleeps.state.sun

/**
 * Data state of the current sunrise/sunset schedule.
 */
sealed interface SunScheduleState {
    /**
     * State used before any data has been loaded.
     */
    object Initial: SunScheduleState

    /**
     * Indicates that a sunrise/sunset schedule cannot be determined.
     *
     * This is likely due to unavailable location.
     */
    data class Unknown(
        val centralUs: SunSchedule
    ): SunScheduleState

    /**
     * Sunrise/Sunset schedule for the current day.
     */
    data class Known(
        val schedule: SunSchedule,
    ): SunScheduleState
}

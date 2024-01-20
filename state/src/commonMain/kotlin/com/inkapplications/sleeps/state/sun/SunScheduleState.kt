package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDateTime

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
        /**
         * The time of sunrise in central US timezone.
         */
        val centralUsSunrise: ZonedDateTime
    ): SunScheduleState

    /**
     * Sunrise/Sunset schedule for the current day.
     */
    data class Known(
        /**
         * Local sunrise time in the current timezone.
         */
        val sunrise: ZonedDateTime,
    ): SunScheduleState
}

package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDateTime

/**
 * Data state of the current sunrise/sunset schedule.
 */
data class Sunrise(
    val timestamp: ZonedDateTime,
    val isEstimate: Boolean,
)

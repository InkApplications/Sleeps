package com.inkapplications.sleeps.state.schedule

import com.inkapplications.datetime.ZonedDateTime

/**
 * Times for the user's alarm/sleep schedule.
 */
data class Schedule(
    val wake: ZonedDateTime,
    val sleep: ZonedDateTime,
    val sunrise: ZonedDateTime,
)

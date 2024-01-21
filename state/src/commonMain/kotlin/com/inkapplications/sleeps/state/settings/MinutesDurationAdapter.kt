package com.inkapplications.sleeps.state.settings

import app.cash.sqldelight.ColumnAdapter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Adapts a duration to be saved as a long in minutes.
 */
internal object MinutesDurationAdapter: ColumnAdapter<Duration, Long> {
    override fun decode(databaseValue: Long): Duration = databaseValue.minutes
    override fun encode(value: Duration): Long = value.inWholeMinutes
}

package com.inkapplications.sleeps.state.alarms

/**
 * Identifies a scheduled alarm.
 */
@JvmInline
value class AlarmId(val value: String) {
    override fun toString(): String = "($value)"
}

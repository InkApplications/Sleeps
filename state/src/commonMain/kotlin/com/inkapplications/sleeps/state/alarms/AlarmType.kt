package com.inkapplications.sleeps.state.alarms

/**
 * Identifies a scheduled alarm.
 */
@JvmInline
value class AlarmType(val id: String) {
    override fun toString(): String = "($id)"

    companion object {
        val Wake = AlarmType("wake")
        val Sleep = AlarmType("sleep")

        fun findById(key: String?): AlarmType = when (key) {
            "wake" -> Wake
            "sleep" -> Sleep
            else -> throw IllegalArgumentException("Unknown Alarm Key: $key")
        }
    }
}

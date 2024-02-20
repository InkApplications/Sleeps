package com.inkapplications.sleeps.state.alarms

import kotlinx.datetime.Instant

class AlarmAccessSpy: AlarmAccess {
    val addCalls = mutableListOf<Pair<AlarmType, Instant>>()
    val clearCalls = mutableListOf<AlarmType>()

    override fun addAlarm(id: AlarmType, time: Instant) {
        addCalls += id to time
    }

    override fun removeAlarm(id: AlarmType) {
        clearCalls += id
    }
}

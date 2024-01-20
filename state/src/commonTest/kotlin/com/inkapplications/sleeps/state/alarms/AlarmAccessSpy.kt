package com.inkapplications.sleeps.state.alarms

import kotlinx.datetime.Instant

class AlarmAccessSpy: AlarmAccess {
    val addCalls = mutableListOf<Pair<AlarmId, Instant>>()
    val clearCalls = mutableListOf<AlarmId>()

    override fun addAlarm(id: AlarmId, time: Instant) {
        addCalls += id to time
    }

    override fun removeAlarm(id: AlarmId) {
        clearCalls += id
    }
}

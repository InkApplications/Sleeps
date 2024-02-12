package com.inkapplications.sleeps.state.schedule

import kotlinx.coroutines.flow.MutableSharedFlow

class ScheduleAccessFake: ScheduleAccess {
    override val schedule = MutableSharedFlow<Schedule>()
}

package com.inkapplications.sleeps.state.sun

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SunScheduleAccessFake(
    initial: SunScheduleState,
): SunScheduleStateAccess {
    val mutableState = MutableStateFlow(initial)
    override val sunState: StateFlow<SunScheduleState> = mutableState
}

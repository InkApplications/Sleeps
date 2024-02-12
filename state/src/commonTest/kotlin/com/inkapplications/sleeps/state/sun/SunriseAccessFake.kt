package com.inkapplications.sleeps.state.sun

import kotlinx.coroutines.flow.MutableSharedFlow

class SunriseAccessFake(): SunriseAccess {
    override val nextSunrise = MutableSharedFlow<Sunrise>()
}

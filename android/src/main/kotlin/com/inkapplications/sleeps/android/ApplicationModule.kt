package com.inkapplications.sleeps.android

import com.inkapplications.sleeps.state.createJvmStateModule
import com.inkapplications.sleeps.state.location.LocationProvider

object ApplicationModule {
    private val module = createJvmStateModule(
        locationProvider = object : LocationProvider {
            override suspend fun getLastLocation() = null
        }
    )

    val screenState = module.screenState
}

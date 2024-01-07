package com.inkapplications.sleeps.android

import android.app.Activity
import android.location.LocationManager
import com.inkapplications.sleeps.state.createJvmStateModule

class ApplicationModule(
    activity: MainActivity,
) {
    val locationProvider = AndroidLocationProvider(
        locationManager = activity.getSystemService(Activity.LOCATION_SERVICE) as LocationManager,
        activity = activity,
    )

    val stateModule = createJvmStateModule(
        locationProvider = locationProvider,
    )

    val screenState = stateModule.screenState
}

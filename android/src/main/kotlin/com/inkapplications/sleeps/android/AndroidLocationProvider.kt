package com.inkapplications.sleeps.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.inkapplications.sleeps.state.location.LocationProvider
import inkapplications.spondee.measure.us.miles
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import inkapplications.spondee.structure.toFloat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes

/**
 * Adapt Android's Location manager callbacks into a Flow of geocoordinates.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AndroidLocationProvider(
    private val locationManager: LocationManager,
    private val context: Context,
): LocationProvider {
    private val permissionsChannel = MutableStateFlow(checkPermissions())

    @SuppressLint("MissingPermission")
    private val locationGrantedFlow = callbackFlow {
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySendBlocking(
                    GeoCoordinates(
                        latitude = location.latitude.latitude,
                        longitude = location.longitude.longitude,
                    )
                )
            }
            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
            override fun onProviderEnabled(p0: String) {}
            override fun onProviderDisabled(p0: String) {}
        }

        if (checkPermissions()) {
            withContext(Dispatchers.Main) {
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                    ?.let {
                        GeoCoordinates(
                            latitude = it.latitude.latitude,
                            longitude = it.longitude.longitude,
                        )
                    }
                    .run(::trySendBlocking)

                locationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    5.minutes.inWholeMilliseconds,
                    20.miles.toMeters().toFloat(),
                    listener
                )
            }
        } else {
            trySend(null)
        }

        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }

    override val location: Flow<GeoCoordinates?> = permissionsChannel
        .flatMapLatest { if (it) locationGrantedFlow else flowOf(null) }

    fun onPermissionsChange() {
        permissionsChannel.value = checkPermissions()
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

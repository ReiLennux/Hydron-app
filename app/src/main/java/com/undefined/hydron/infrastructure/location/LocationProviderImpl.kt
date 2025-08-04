package com.undefined.hydron.infrastructure.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*
import com.undefined.hydron.domain.interfaces.ILocationProvider
import com.undefined.hydron.domain.models.Location
import javax.inject.Inject

class LocationProviderImpl @Inject constructor(
    private val context: Context
) : ILocationProvider {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private var lastLocation: android.location.Location? = null

    @SuppressLint("MissingPermission") // asegúrate de pedir permisos desde UI
    override fun startLocationUpdates(
        minDistanceMeters: Float,
        onLocationChanged: (Location) -> Unit
    ) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateDistanceMeters(minDistanceMeters)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (loc in result.locations) {
                    if (shouldSend(loc, minDistanceMeters)) {
                        lastLocation = loc
                        onLocationChanged(
                            Location(
                                latitude = loc.latitude,
                                longitude = loc.longitude
                            )
                        )
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, locationCallback!!, Looper.getMainLooper())
    }

    override fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    private fun shouldSend(newLoc: android.location.Location, threshold: Float): Boolean {
        return lastLocation == null || lastLocation!!.distanceTo(newLoc) >= threshold
    }
}

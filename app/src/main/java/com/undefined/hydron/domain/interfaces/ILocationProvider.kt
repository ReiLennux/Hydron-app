package com.undefined.hydron.domain.interfaces

import com.undefined.hydron.domain.models.Location

interface ILocationProvider {
    fun startLocationUpdates(
        minDistanceMeters: Float = 5f,
        onLocationChanged: (Location) -> Unit
    )

    fun stopLocationUpdates()
}

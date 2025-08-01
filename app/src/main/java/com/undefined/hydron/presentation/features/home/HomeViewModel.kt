package com.undefined.hydron.presentation.features.home

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    @ApplicationContext private val context: Context
) : AndroidViewModel(application) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null
    private val minDistanceMeters = 3f // 3mts

    fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000L // 5s
            fastestInterval = 2000L
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val newLocation = locationResult.lastLocation
                if (newLocation != null && shouldUpdate(newLocation)) {
                    _location.postValue(newLocation)
                    lastLocation = newLocation
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                context.mainLooper
            )
        } catch (e: SecurityException) {
            Log.e("HomeViewModel", "Permiso de ubicación no otorgado", e)
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    private fun shouldUpdate(newLoc: Location): Boolean {
        val last = lastLocation ?: return true
        return last.distanceTo(newLoc) >= minDistanceMeters
    }

    fun fetchLocationOnce() {
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.lastLocation
                location.addOnSuccessListener {
                    _location.postValue(it)
                }
            } catch (_: SecurityException) {
                _location.postValue(null)
            }
        }
    }

}


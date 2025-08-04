package com.undefined.hydron.presentation.shared.viewmodels

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.Manifest
import com.google.android.gms.location.FusedLocationProviderClient
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    application: Application
) : AndroidViewModel(application) {

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    fun requestLocation(context: Context) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return

        locationClient.lastLocation.addOnSuccessListener {
            _location.value = it
        }
    }
}

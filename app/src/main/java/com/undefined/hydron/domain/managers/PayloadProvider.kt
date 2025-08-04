package com.undefined.hydron.domain.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.undefined.hydron.core.Constants.USER_AGE
import com.undefined.hydron.core.Constants.USER_NAME
import com.undefined.hydron.core.Constants.USER_SEX
import com.undefined.hydron.domain.interfaces.IPayloadProvider
import com.undefined.hydron.domain.models.Location
import com.undefined.hydron.domain.models.SexType
import com.undefined.hydron.domain.models.UserInfo
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PayloadProvider @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val context: Context,
    private val auth: FirebaseAuth,
    private val dataStoreUseCases: DataStoreUseCases
): IPayloadProvider {

    override suspend fun getUserInfo(): UserInfo {
        val user = auth.currentUser ?: throw IllegalStateException("No user logged in")

        return UserInfo(
            userId = user.uid,
            userName = dataStoreUseCases.getDataString(USER_NAME),
            gender = SexType.valueOf(dataStoreUseCases.getDataString(USER_SEX).takeIf { it.isNotEmpty() } ?: "PrefieroNoDecirlo"),
            isActive = true,
            riskLevel = 0.0,
            age = dataStoreUseCases.getDataInt(USER_AGE)
        )
    }

    override suspend fun getCurrentLocation(): Location {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (hasPermission) {
            try {
                fusedLocationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()
                    ?.let { Location(it.latitude, it.longitude) }
                    ?: Location(0.0, 0.0)
            } catch (e: Exception) {
                Log.e("PayloadProvider", "Error obteniendo ubicación: ${e.message}")
                Location(0.0, 0.0)
            }
        } else {
            Log.w("PayloadProvider", "Sin permisos de ubicación")
            Location(0.0, 0.0)
        }
    }


}

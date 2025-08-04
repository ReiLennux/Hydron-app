package com.undefined.hydron.presentation.shared.helpers

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.undefined.hydron.infrastructure.services.ApiForegroundService

object TrackingServiceHelper {

    fun startTrackingService(context: Context) {
        val intent = Intent(context, ApiForegroundService::class.java).apply {
            action = "START_TRACKING"
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopTrackingService(context: Context) {
        val intent = Intent(context, ApiForegroundService::class.java).apply {
            action = "STOP_TRACKING"
        }
        context.startService(intent)
    }
}

package com.undefined.hydron.infrastructure.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import com.undefined.hydron.R


class ApiForegroundService : Service() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        startForeground(
            1,
            createNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    Log.d("ApiService", "Enviando datos...")
                } catch (e: Exception) {
                    Log.e("ApiService", "Error: ${e.message}")
                }
                delay(1 * 10 * 1000L)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // If the service is killed, it will be restarted
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val channelId = "api_channel"
        val chan = NotificationChannel(channelId, "API Service", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Hydron activo")
            .setContentText("Monitoreando salud...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}

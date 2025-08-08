package com.undefined.hydron.infrastructure.services

import android.app.Service
import android.content.Intent
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.*
import com.undefined.hydron.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import com.google.android.gms.wearable.Wearable
import com.undefined.hydron.domain.interfaces.ITrackingController
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class ApiForegroundService : Service(), DataClient.OnDataChangedListener {

    @Inject lateinit var trackingController: ITrackingController
    @Inject lateinit var messageClient: MessageClient

    private var serviceJob = SupervisorJob()
    private var serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    companion object {
        const val ACTION_START_TRACKING = "START_TRACKING"
        const val ACTION_STOP_TRACKING = "STOP_TRACKING"
        const val ACTION_NOTIFICATION_DISMISSED = "NOTIFICATION_DISMISSED"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "api_channel"
    }

    private var isStopping = false

    private val dismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_NOTIFICATION_DISMISSED) {
                try {
                    val notification = createNotification()
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, notification)
                } catch (e: Exception) {
                    Log.e("ApiForegroundService", "Error recreando notificación: ${e.message}")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerReceiver(
            dismissReceiver,
            IntentFilter(ACTION_NOTIFICATION_DISMISSED),
            RECEIVER_NOT_EXPORTED
        )
        messageClient = Wearable.getMessageClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!serviceScope.isActive) {
            serviceJob = SupervisorJob()
            serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
        }

        when (intent?.action) {
            ACTION_START_TRACKING, ACTION_NOTIFICATION_DISMISSED -> {
                val notification = createNotification()
                startForeground(NOTIFICATION_ID, notification)
            }

            ACTION_STOP_TRACKING -> {
                if (!isStopping) {
                    isStopping = true
                    serviceScope.launch {
                        try {
                            trackingController.toggleTracking(false, this)
                            sendStopTrackingToWearable()
                        } catch (e: Exception) {
                            Log.e("ApiForegroundService", "Error deteniendo tracking: ${e.message}")
                        } finally {
                            stopForeground(STOP_FOREGROUND_REMOVE)
                            stopSelf()
                            isStopping = false
                        }
                    }
                }
            }
        }

        return START_REDELIVER_INTENT
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, ApiForegroundService::class.java).apply {
            action = ACTION_STOP_TRACKING
        }

        val dismissIntent = Intent(this, ApiForegroundService::class.java).apply {
            action = ACTION_NOTIFICATION_DISMISSED
        }

        val pendingStopIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val deleteIntent = PendingIntent.getService(
            this,
            1,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Hydron Activo")
            .setContentText("Monitoreando en segundo plano")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDeleteIntent(deleteIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Detener Monitoreo",
                pendingStopIntent
            )
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Servicio Esencial de Hydron",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Monitoreo continuo de salud"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path != null && path.startsWith("/your_wear_path")) {
                    serviceScope.launch {
                        try {
                            // Procesar datos del wearable
                        } catch (e: Exception) {
                            Log.e("ApiForegroundService", "Error procesando datos wearable: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(dismissReceiver)
        } catch (e: Exception) {
            Log.e("ApiForegroundService", "Error unregistering receiver: ${e.message}")
        }
        serviceJob.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    private suspend fun sendStopTrackingToWearable() {
        try {
            val nodeId = getWearNodeId() ?: return
            messageClient.sendMessage(nodeId, "/stop_tracking", null).await()
        } catch (e: Exception) {
            Log.e("ApiForegroundService", "Error enviando stop_tracking: ${e.message}")
        }
    }

    private suspend fun getWearNodeId(): String? {
        return try {
            val nodes = Wearable.getNodeClient(this).connectedNodes.await()
            nodes.firstOrNull()?.id
        } catch (e: Exception) {
            Log.e("ApiForegroundService", "Error obteniendo node ID: ${e.message}")
            null
        }
    }
}

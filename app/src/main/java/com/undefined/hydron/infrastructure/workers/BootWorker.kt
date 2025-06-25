package com.undefined.hydron.infrastructure.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.undefined.hydron.R
import com.undefined.hydron.infrastructure.services.ApiForegroundService

class BootWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "DataSyncChannel"
    }

    override suspend fun doWork(): Result {
        Log.d("MySyncWorker", "Starting data sync work")
        return try {
            setForegroundAsync(createForegroundInfo())

            // Start
            val serviceIntent = Intent(applicationContext, ApiForegroundService::class.java)
            applicationContext.startForegroundService(serviceIntent)

            // in addition
            performDataSync()

            Log.d("MySyncWorker", "Data sync and service start completed")
            Result.success()
        } catch (e: Exception) {
            Log.e("MySyncWorker", "Error", e)
            Result.failure()
        }
    }


    private suspend fun performDataSync() {
        kotlinx.coroutines.delay(5000)
    }

    @SuppressLint("ServiceCast")
    private fun createForegroundInfo(): ForegroundInfo {
        val title = "Syncing Data"
        val message = "Preparing your data..."
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Data Sync",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        return ForegroundInfo(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )    }
}

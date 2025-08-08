package com.undefined.hydron.infrastructure.workers


import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class BootWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
//        val notification = NotificationCompat.Builder(applicationContext, "api_channel")
//            .setContentTitle("Hydron activo")
//            .setContentText("Monitoreando salud desde Worker...")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .build()
//
//        val foregroundInfo = ForegroundInfo(
//            1,
//            notification,
//            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
//
//        )
//        setForegroundAsync(foregroundInfo)

        //repeat(5) {
        //    Log.d("BootWorker", "Enviando datos desde Worker...")
        //    delay(10_000L)
        //}

        return Result.success()
    }
}

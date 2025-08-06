package com.undefined.hydron.domain.managers

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.undefined.hydron.core.Constants.KEY_IS_MONITORING_TOGGLE
import com.undefined.hydron.core.Constants.PATH_TOGGLE_MONITOR
import com.undefined.hydron.domain.interfaces.ITrackerManager
import com.undefined.hydron.domain.interfaces.ITrackingController
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.hydron.presentation.shared.helpers.TrackingServiceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await

class TrackingController @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dataStoreUseCases: DataStoreUseCases,
    private val trackerManager: ITrackerManager,
    private val centralizedDataSync: OrchestratorDataSyncManager,
    private val dataClient: DataClient

): ITrackingController {

    override suspend fun toggleTracking(enable: Boolean, scope: CoroutineScope) {
        dataStoreUseCases.setDataBoolean(KEY_IS_MONITORING_TOGGLE, enable)

        if (enable) {
            trackerManager.start(scope)
            TrackingServiceHelper.startTrackingService(appContext)
        } else {
            trackerManager.stop()
            centralizedDataSync.endMonitoringSession()
            TrackingServiceHelper.stopTrackingService(appContext)
        }

        sendToggleToWearable(enable)
    }


    private suspend fun sendToggleToWearable(enable: Boolean) {

        try {
            val putDataMapReq = PutDataMapRequest.create(PATH_TOGGLE_MONITOR).apply {
                dataMap.putBoolean(KEY_IS_MONITORING_TOGGLE, enable)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }
            dataClient.putDataItem(putDataMapReq.asPutDataRequest().setUrgent()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun isTrackingEnabled(): Boolean {
        return dataStoreUseCases.getDataBoolean(KEY_IS_MONITORING_TOGGLE)
    }

    override fun getMonitoringFlow(): Flow<Boolean> {
        return dataStoreUseCases.getDataBooleanFlow(KEY_IS_MONITORING_TOGGLE)
    }

    //override suspend fun performBatchUpload() = centralizedDataSync.performBatchUpload()
}
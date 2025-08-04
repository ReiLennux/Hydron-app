package com.undefined.hydron.domain.interfaces

import com.undefined.hydron.domain.managers.BatchUploadResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface ITrackingController {

    suspend fun toggleTracking(enable: Boolean, scope: CoroutineScope)

    suspend fun isTrackingEnabled(): Boolean

    fun getMonitoringFlow(): Flow<Boolean>

    suspend fun performBatchUpload(): BatchUploadResult

}
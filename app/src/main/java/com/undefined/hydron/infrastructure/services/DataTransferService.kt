package com.undefined.hydron.infrastructure.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.undefined.hydron.domain.models.TransferResult
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.toFirebaseMap
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.min

class DataTransferService @Inject constructor(
    private val sensorDataUseCases: SensorDataUseCases,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isTransferActive = false

    fun startDataTransfer(onResult: (TransferResult) -> Unit) {
        if (isTransferActive) {
            onResult(TransferResult.Error(IllegalStateException("Transfer already in progress")))
            return
        }
        isTransferActive = true

        coroutineScope.launch {
            try {
                val totalRecords = sensorDataUseCases.getTotalCount()
                if (totalRecords == 0) {
                    onResult(TransferResult.Success)
                    isTransferActive = false
                    return@launch
                }

                val batchSize = calculateOptimalBatchSize(totalRecords)
                var uploadedRecords = 0
                val failedBatches = mutableListOf<List<SensorData>>()

                for (offset in 0 until totalRecords step batchSize) {
                    if (!isTransferActive) {
                        onResult(TransferResult.Cancelled)
                        isTransferActive = false
                        return@launch
                    }

                    val currentBatchSize = min(batchSize, totalRecords - offset)
                    val batch = sensorDataUseCases.getRecordsBatch(offset, currentBatchSize)

                    val success = uploadAndMarkBatch(batch, onResult)
                    if (success) {
                        uploadedRecords += batch.size
                        val progress = (uploadedRecords * 100) / totalRecords
                        onResult(TransferResult.Progress(progress))
                    } else {
                        failedBatches.add(batch)
                    }
                }

                if (failedBatches.isEmpty()) {
                    onResult(TransferResult.Success)
                    deleteUploadedRecords()
                } else {
                    onResult(TransferResult.Error(Exception("Failed uploading ${failedBatches.size} batches")))
                }
            } catch (e: Exception) {
                onResult(TransferResult.Error(e))
            } finally {
                isTransferActive = false
            }
        }
    }

    private suspend fun uploadAndMarkBatch(batch: List<SensorData>, onResult: (TransferResult) -> Unit): Boolean {
        return try {
            uploadBatch(batch)
            markBatchAsUploaded(batch)
            true
        } catch (e: Exception) {
            // TODO: Retry no data updated
            false
        }
    }

    private suspend fun uploadBatch(batch: List<SensorData>) {
        val userId = firebaseAuth.currentUser?.uid
        val updatesMap = batch.associate { entity ->
            "sensor_data/$userId/${entity.id}" to entity.toFirebaseMap()
        }
        firebaseDatabase.reference.updateChildren(updatesMap).await()
    }

    private suspend fun markBatchAsUploaded(batch: List<SensorData>) {
        val ids = batch.map { it.id }
        sensorDataUseCases.markAsUploaded(ids)
    }

    private suspend fun deleteUploadedRecords() {
        sensorDataUseCases.resetRecords()
    }

    private fun calculateOptimalBatchSize(totalRecords: Int): Int = when {
        totalRecords <= 0 -> 0
        totalRecords < 100 -> 10
        totalRecords < 1_000 -> 50
        totalRecords < 10_000 -> 100
        totalRecords < 50_000 -> 200
        else -> 500
    }

    fun cancelTransfer() {
        if (isTransferActive) {
            coroutineScope.coroutineContext.cancelChildren()
            isTransferActive = false
        }
    }

    class DataTransferException(message: String, cause: Exception?) : Exception(message, cause)
}



package com.undefined.hydron.domain.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "sensor_data")
data class SensorData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sensorType: SensorType,
    val value: Double,
    val takenAt: Long = System.currentTimeMillis(),
    val isUploaded: Boolean = false
)

@Serializable
data class SensorMessage(
    val sensorType: String,
    val value: Float,
    val timestamp: Long
)


enum class SensorType {
    HEART_RATE,
    STEP_COUNT,
    TEMPERATURE,
}

fun SensorData.toFirebaseMap(): Map<String, Any> = mapOf(
    "sensorType" to sensorType.name,
    "value" to value,
    "takenAt" to takenAt,
    "uploadedAt" to System.currentTimeMillis(),
)

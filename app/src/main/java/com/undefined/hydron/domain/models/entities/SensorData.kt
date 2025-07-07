package com.undefined.hydron.domain.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_data")
data class SensorData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sensorType: SensorType,
    val value: Float,
    val takenAt: Long = System.currentTimeMillis()
)



enum class SensorType {
    HEART_RATE,
    STEPS,
    TEMPERATURE,
}

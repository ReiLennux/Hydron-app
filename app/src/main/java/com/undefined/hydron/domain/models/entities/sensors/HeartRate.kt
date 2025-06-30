package com.undefined.hydron.domain.models.entities.sensors

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heart_rates")
data class HeartRate(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bpm: Int,
    val takenAt: Long = System.currentTimeMillis()
)

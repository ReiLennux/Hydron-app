package com.undefined.hydron.domain.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PresionArterial(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val edadMin: Int,
    val edadMax: Int,
    val estadoSalud: String, // "Saludable", "Crónica"
    val sistolicaMin: Int,
    val sistolicaMax: Int,
    val diastolicaMin: Int,
    val diastolicaMax: Int,
    val clasificacion: String,
    val aumentoLeve: String?, // "+3-4"
    val aumentoModerado: String?,
    val aumentoSevero: String?
)

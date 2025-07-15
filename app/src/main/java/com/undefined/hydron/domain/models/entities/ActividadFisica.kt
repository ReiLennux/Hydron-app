package com.undefined.hydron.domain.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ActividadFisica(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val edadMin: Int,
    val edadMax: Int,
    val grupo: String, // "General", "Diabetes", "ERC", "FQ"
    val horasPorSemana: Float
)

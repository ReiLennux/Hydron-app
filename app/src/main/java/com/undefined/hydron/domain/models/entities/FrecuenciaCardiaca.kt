package com.undefined.hydron.domain.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FrecuenciaCardiaca(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val edadMin: Int,
    val edadMax: Int,
    val fcReposoMin: Int?,
    val fcReposoMax: Int?,
    val fcDormidoMin: Int?,
    val fcDormidoMax: Int?,
    val fcMax: Int?,
    val diabetesMin: Int?,
    val diabetesMax: Int?,
    val renalMin: Int?,
    val renalMax: Int?,
    val fibrosisMin: Int?,
    val fibrosisMax: Int?
)

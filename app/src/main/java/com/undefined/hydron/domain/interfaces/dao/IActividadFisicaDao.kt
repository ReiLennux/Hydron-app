package com.undefined.hydron.domain.interfaces.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.undefined.hydron.domain.models.entities.ActividadFisica

@Dao
interface IActividadFisicaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<ActividadFisica>)

    @Query("SELECT * FROM ActividadFisica WHERE :edad BETWEEN edadMin AND edadMax AND grupo = :grupo LIMIT 1")
    suspend fun getByEdadYGrupo(edad: Int, grupo: String): ActividadFisica?
}

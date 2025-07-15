package com.undefined.hydron.domain.interfaces.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.undefined.hydron.domain.models.entities.FrecuenciaCardiaca

@Dao
interface IFrecuenciaCardiacaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<FrecuenciaCardiaca>)

    @Query("SELECT * FROM FrecuenciaCardiaca WHERE :edad BETWEEN edadMin AND edadMax LIMIT 1")
    suspend fun getByEdad(edad: Int): FrecuenciaCardiaca?

    @Query("SELECT COUNT(*) FROM FrecuenciaCardiaca")
    suspend fun getCount(): Int
}

package com.undefined.hydron.domain.interfaces.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.undefined.hydron.domain.models.entities.PresionArterial

@Dao
interface IPresionArterialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<PresionArterial>)

    @Query("SELECT * FROM PresionArterial WHERE :edad BETWEEN edadMin AND edadMax AND estadoSalud = :estado LIMIT 1")
    suspend fun getByEdadYEstado(edad: Int, estado: String): PresionArterial?
}

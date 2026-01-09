package de.diskostu.spoolstack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FilamentDao {

    @Insert
    suspend fun insert(filament: Filament): Long

    @Update
    suspend fun update(filament: Filament)

    @Query("SELECT COUNT(*) FROM filament")
    suspend fun getCount(): Int

    @Query("SELECT DISTINCT vendor FROM filament ORDER BY vendor COLLATE NOCASE ASC")
    suspend fun getDistinctVendors(): List<String>

    @Query("SELECT * FROM filament ORDER BY changeDate DESC")
    fun getAllFilaments(): Flow<List<Filament>>

    @Query("SELECT * FROM filament WHERE archived = 0 ORDER BY changeDate DESC")
    fun getActiveFilaments(): Flow<List<Filament>>

    @Query("SELECT * FROM filament WHERE id = :id")
    suspend fun getFilamentById(id: Int): Filament?

    @Query("DELETE FROM filament")
    suspend fun deleteAll()
}

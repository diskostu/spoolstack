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

    @Query("SELECT COUNT(*) FROM filament")
    fun getCountFlow(): Flow<Int>

    @Query("SELECT DISTINCT vendor FROM filament ORDER BY vendor COLLATE NOCASE ASC")
    suspend fun getDistinctVendors(): List<String>

    @Query("SELECT * FROM filament ORDER BY changeDate DESC")
    fun getAllFilaments(): Flow<List<Filament>>

    @Query("SELECT * FROM filament WHERE deleted = 0 ORDER BY changeDate DESC")
    fun getActiveFilaments(): Flow<List<Filament>>

    @Query("SELECT * FROM filament WHERE id = :id")
    suspend fun getFilamentById(id: Int): Filament?

    @Query("DELETE FROM filament")
    suspend fun deleteAll()

    @Query("SELECT color, colorHex FROM filament WHERE deleted = 0 AND colorHex IS NOT NULL GROUP BY color, colorHex ORDER BY COUNT(*) DESC LIMIT :limit")
    suspend fun getFrequentColors(limit: Int): List<FrequentColor>

    @Query("SELECT color, colorHex FROM filament WHERE deleted = 0 AND colorHex IS NOT NULL GROUP BY color, colorHex ORDER BY MAX(changeDate) DESC LIMIT :limit")
    suspend fun getRecentColors(limit: Int): List<FrequentColor>
}

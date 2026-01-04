package de.diskostu.spoolstack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FilamentDao {

    @Insert
    suspend fun insert(filament: Filament): Long

    @Query("SELECT COUNT(*) FROM filament")
    suspend fun getCount(): Int

    @Query("DELETE FROM filament")
    suspend fun deleteAll()

    @Query("SELECT DISTINCT vendor FROM filament ORDER BY vendor ASC")
    suspend fun getDistinctVendors(): List<String>
}
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
}
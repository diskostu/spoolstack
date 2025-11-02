package de.diskostu.spoolstack.data

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface FilamentDao {

    @Insert
    suspend fun insert(filament: Filament)
}
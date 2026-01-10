package de.diskostu.spoolstack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrintDao {
    @Insert
    suspend fun insert(print: Print): Long

    @Query("SELECT * FROM prints ORDER BY printDate DESC")
    fun getAllPrints(): Flow<List<Print>>

    @Query("SELECT * FROM prints WHERE filamentId = :filamentId ORDER BY printDate DESC")
    fun getPrintsForFilament(filamentId: Int): Flow<List<Print>>

    @Query("SELECT COUNT(*) FROM prints")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM prints")
    fun getCountFlow(): Flow<Int>
}

package de.diskostu.spoolstack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ColorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColorDefinitions(definitions: List<ColorDefinition>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColorNames(names: List<ColorName>)

    @Query(
        """
        SELECT name FROM ColorName 
        WHERE hex = :hex AND language = :language
    """
    )
    suspend fun getColorName(hex: String, language: String): String?

    @Query("SELECT * FROM ColorDefinition")
    suspend fun getAllColors(): List<ColorDefinition>

    @Query("SELECT name FROM ColorName WHERE hex = :hex")
    suspend fun getAllNamesForHex(hex: String): List<String>
}

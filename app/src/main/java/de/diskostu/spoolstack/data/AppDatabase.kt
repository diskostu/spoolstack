package de.diskostu.spoolstack.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Filament::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filamentDao(): FilamentDao
}

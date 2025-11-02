package de.diskostu.spoolstack.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Filament::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filamentDao(): FilamentDao
}
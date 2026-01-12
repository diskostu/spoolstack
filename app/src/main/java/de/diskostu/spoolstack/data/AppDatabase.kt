package de.diskostu.spoolstack.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Filament::class, Print::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filamentDao(): FilamentDao
    abstract fun printDao(): PrintDao
}

package de.diskostu.spoolstack.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Filament::class,
        Print::class,
        ColorDefinition::class,
        ColorName::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filamentDao(): FilamentDao
    abstract fun printDao(): PrintDao
    abstract fun colorDao(): ColorDao
}

package de.diskostu.spoolstack.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Filament::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filamentDao(): FilamentDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val currentTime = System.currentTimeMillis()
                db.execSQL("ALTER TABLE Filament ADD COLUMN createdDate INTEGER NOT NULL DEFAULT $currentTime")
                db.execSQL("ALTER TABLE Filament ADD COLUMN changeDate INTEGER NOT NULL DEFAULT $currentTime")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Filament ADD COLUMN boughtAt TEXT")
                db.execSQL("ALTER TABLE Filament ADD COLUMN boughtDate INTEGER")
                db.execSQL("ALTER TABLE Filament ADD COLUMN price REAL")
            }
        }
    }
}

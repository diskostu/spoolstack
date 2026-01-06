package de.diskostu.spoolstack.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Filament::class, Print::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filamentDao(): FilamentDao
    abstract fun printDao(): PrintDao

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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `prints` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `filamentId` INTEGER NOT NULL, 
                        `amountUsed` REAL NOT NULL, 
                        `url` TEXT, 
                        `comment` TEXT, 
                        `printDate` INTEGER NOT NULL, 
                        FOREIGN KEY(`filamentId`) REFERENCES `Filament`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_prints_filamentId` ON `prints` (`filamentId`)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE prints ADD COLUMN name TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}

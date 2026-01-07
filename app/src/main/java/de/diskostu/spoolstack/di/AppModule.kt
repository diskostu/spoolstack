package de.diskostu.spoolstack.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.diskostu.spoolstack.data.AppDatabase
import de.diskostu.spoolstack.data.FilamentDao
import de.diskostu.spoolstack.data.PrintDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "spoolstack"
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_6
            )
            .build()
    }

    @Provides
    fun provideFilamentDao(appDatabase: AppDatabase): FilamentDao {
        return appDatabase.filamentDao()
    }

    @Provides
    fun providePrintDao(appDatabase: AppDatabase): PrintDao {
        return appDatabase.printDao()
    }
}

package com.convenience.store.assessment.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.convenience.store.assessment.database.AppDatabase
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.data.datasources.EventLogOffsetDao
import com.convenience.store.products.data.datasources.local.ProductDao
import com.convenience.store.products.data.datasources.local.ProductRemoteKeyDao
import com.convenience.store.stocks.data.datasources.local.StockDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {
        @Provides
        fun provideEventLogEntityDao(db: AppDatabase): EventLogDao {
            return db.eventLogDao()
        }

        @Provides
        fun provideEventLogOffsetDao(db: AppDatabase): EventLogOffsetDao {
            return db.eventLogOffsetDao()
        }

        @Provides
        fun provideStockEntityDao(db: AppDatabase): StockDao {
            return db.stockDao()
        }

        @Provides
        fun provideProductEntityDao(db: AppDatabase): ProductDao {
            return db.productDao()
        }

        @Provides
        fun provideProductRemoteKeyDao(db: AppDatabase): ProductRemoteKeyDao {
            return db.productRemoteKeyDao()
        }

        @Singleton
        @Provides
        fun provideAppDatabase(
            @ApplicationContext context: Context,
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration(false) // Utile in fase di sviluppo
                .build()
        }

        @Singleton
        @Provides
        fun provideRoomDatabase(appDatabase: AppDatabase): RoomDatabase {
            return appDatabase
        }

    }

}
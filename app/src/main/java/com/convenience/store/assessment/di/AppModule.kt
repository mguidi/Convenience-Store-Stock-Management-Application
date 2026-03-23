package com.convenience.store.assessment.di

import android.content.Context
import androidx.room.Room
import com.convenience.store.assessment.database.AppDatabase
import com.convenience.store.core.data.datasources.EventLogEntityDao
import com.convenience.store.products.data.datasources.ProductEntityDao
import com.convenience.store.stocks.data.datasources.StockEntityDao
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
        fun provideEventLogEntityDao(db: AppDatabase): EventLogEntityDao {
            return db.eventLogEntityDao()
        }

        @Provides
        fun provideStockEntityDao(db: AppDatabase): StockEntityDao {
            return db.stockEntityDao()
        }

        @Provides
        fun provideProductEntityDao(db: AppDatabase): ProductEntityDao {
            return db.productEntityDao()
        }

        @Singleton
        @Provides
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            )
//                    .fallbackToDestructiveMigration() // Utile in fase di sviluppo
                .build()
        }
    }

}
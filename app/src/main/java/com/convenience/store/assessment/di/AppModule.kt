package com.convenience.store.assessment.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.convenience.store.assessment.database.AppDatabase
import com.convenience.store.core.data.datasources.EventLogEntityDao
import com.convenience.store.products.data.datasources.ProductEntityDao
import com.convenience.store.products.data.models.ProductEntity
import com.convenience.store.stocks.data.datasources.StockEntityDao
import com.convenience.store.stocks.data.models.StockEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID
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
        fun provideAppDatabase(
            @ApplicationContext context: Context,
            productEntityDaoProvider: javax.inject.Provider<ProductEntityDao>,
            stockEntityDaoProvider: javax.inject.Provider<StockEntityDao>,
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration(false) // Utile in fase di sviluppo
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            seedDatabase(
                                productEntityDaoProvider.get(),
                                stockEntityDaoProvider.get()
                            )
                        }
                    }
                })
                .build()
        }

        @Singleton
        @Provides
        fun provideRoomDatabase(appDatabase: AppDatabase): RoomDatabase {
            return appDatabase
        }

        private suspend fun seedDatabase(
            productEntityDao: ProductEntityDao,
            stockEntityDao: StockEntityDao
        ) {
            val initialProducts = listOf(
                ProductEntity(
                    id = UUID.fromString("019d1d3d-ed99-7be7-9e8c-d74fd0d83d97"),
                    name = "Organic Milk",
                    description = "Fresh organic whole milk from local farms. 1 Gallon.",
                    price = BigDecimal("4.99"),
                    barcode = "1234567890123",
                    categoryId = UUID.randomUUID(),
                    supplierId = UUID.randomUUID(),
                ),
                ProductEntity(
                    id = UUID.fromString("4f7edd9a-236a-4921-b720-dbe451646a0f"),
                    name = "Wheat Bread",
                    description = "Whole wheat sliced bread. No preservatives.",
                    price = BigDecimal("3.49"),
                    barcode = "9876543210987",
                    categoryId = UUID.randomUUID(),
                    supplierId = UUID.randomUUID(),
                )
            )
            productEntityDao.insertAll(initialProducts)


            val initialStock = listOf(
                StockEntity(
                    productId = UUID.fromString("019d1d3d-ed99-7be7-9e8c-d74fd0d83d97"),
                    quantity = BigDecimal("10")
                ),
                StockEntity(
                    productId = UUID.fromString("4f7edd9a-236a-4921-b720-dbe451646a0f"),
                    quantity = BigDecimal("12")
                )
            )
            stockEntityDao.insertAll(initialStock)
        }
    }

}
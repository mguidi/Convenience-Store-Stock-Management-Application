package com.convenience.store.assessment.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.convenience.store.core.data.datasources.EventLogEntityDao
import com.convenience.store.core.data.models.EventLogEntity
import com.convenience.store.products.data.datasources.ProductEntityDao
import com.convenience.store.products.data.models.ProductEntity
import com.convenience.store.stocks.data.datasources.StockEntityDao
import com.convenience.store.stocks.data.models.StockEntity

@Database(
    entities = [
        EventLogEntity::class,
        StockEntity::class,
        ProductEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(UUIDConverter::class, BigDecimalConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventLogEntityDao(): EventLogEntityDao
    abstract fun stockEntityDao(): StockEntityDao

    abstract fun productEntityDao(): ProductEntityDao
}
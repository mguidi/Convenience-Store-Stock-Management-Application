package com.convenience.store.assessment.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.data.models.EventLogDto
import com.convenience.store.products.data.datasources.local.ProductDao
import com.convenience.store.products.data.models.local.ProductDto
import com.convenience.store.stocks.data.datasources.local.StockDao
import com.convenience.store.stocks.data.models.local.StockDto

@Database(
    entities = [
        EventLogDto::class,
        StockDto::class,
        ProductDto::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(UUIDConverter::class, BigDecimalConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventLogEntityDao(): EventLogDao
    abstract fun stockEntityDao(): StockDao

    abstract fun productEntityDao(): ProductDao
}
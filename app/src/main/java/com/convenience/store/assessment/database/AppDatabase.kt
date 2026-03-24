package com.convenience.store.assessment.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.data.models.EventLogDto
import com.convenience.store.products.data.datasources.ProductDao
import com.convenience.store.products.data.models.ProductDto
import com.convenience.store.stocks.data.datasources.StockDao
import com.convenience.store.stocks.data.models.StockDto

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
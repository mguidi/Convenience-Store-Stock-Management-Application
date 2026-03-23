package com.convenience.store.stocks.data.repositories

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import arrow.core.Either
import com.convenience.store.core.data.datasources.EventLogEntityDao
import com.convenience.store.core.data.models.EventLogEntity
import com.convenience.store.core.domain.events.StockAddEvent
import com.convenience.store.core.domain.events.StockRemoveEvent
import com.convenience.store.stocks.data.datasources.StockEntityDao
import com.convenience.store.stocks.domain.entities.StockError
import com.convenience.store.stocks.domain.repositories.StockRepository
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val database: RoomDatabase,
    private val stockEntityDao: StockEntityDao,
    private val eventLogEntityDao: EventLogEntityDao
) : StockRepository {

    override suspend fun addStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockError, Unit> {
        if (quantity < BigDecimal.ZERO) return Either.Left(StockError.InvalidQuantity)

        database
            .withTransaction {
                stockEntityDao.updateStock(productId, quantity)
                val event = EventLogEntity(
                    type = StockAddEvent.NAME,
                    dataId = productId,
                    payload = StockAddEvent(productId, quantity).toString() // TODO to json
                )
                eventLogEntityDao.insertEvent(event)
            }

        return Either.Right(Unit)
    }

    override suspend fun removeStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockError, Unit> {
        if (quantity < BigDecimal.ZERO) return Either.Left(StockError.InvalidQuantity)

        database
            .withTransaction {
                stockEntityDao.updateStock(productId, quantity.negate())
                val event = EventLogEntity(
                    type = StockRemoveEvent.NAME,
                    dataId = productId,
                    payload = StockRemoveEvent(productId, quantity).toString() // TODO to json
                )
                eventLogEntityDao.insertEvent(event)
            }

        return Either.Right(Unit)
    }
}
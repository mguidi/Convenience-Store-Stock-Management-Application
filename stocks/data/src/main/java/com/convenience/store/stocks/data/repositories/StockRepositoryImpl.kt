package com.convenience.store.stocks.data.repositories

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import arrow.core.Either
import arrow.core.right
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.data.models.EventLogDto
import com.convenience.store.core.domain.events.StockAddEvent
import com.convenience.store.core.domain.events.StockRemoveEvent
import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.stocks.data.datasources.local.StockDao
import com.convenience.store.stocks.data.models.local.StockDto
import com.convenience.store.stocks.data.models.local.toDomain
import com.convenience.store.stocks.data.models.local.toDto
import com.convenience.store.stocks.domain.entities.Stock
import com.convenience.store.stocks.domain.entities.StockError
import com.convenience.store.stocks.domain.repositories.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val database: RoomDatabase,
    private val stockEntityDao: StockDao,
    private val eventLogEntityDao: EventLogDao,
    private val uuidService: UuidService,
) : StockRepository {

    override fun getStockById(productId: UUID): Flow<Stock?> {
        return stockEntityDao.getStockByProductId(productId).map { it?.toDomain() }
    }

    override suspend fun addStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockError, Unit> {
        database
            .withTransaction {
                val result = stockEntityDao.updateStock(productId, quantity)
                if (result == 0) stockEntityDao.insertOrUpdateStock(StockDto(productId, quantity))

                val eventPayload = StockAddEvent(productId, quantity)

                val event = EventLogDto(
                    id = uuidService.createSortableUuid(),
                    type = StockAddEvent.NAME,
                    payload = Json.encodeToString(eventPayload.toDto())
                )
                eventLogEntityDao.insert(event)
            }

        return Unit.right()
    }

    override suspend fun removeStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockError, Unit> {
        database
            .withTransaction {
                stockEntityDao.updateStock(productId, quantity.negate())

                val eventPayload = StockRemoveEvent(productId, quantity)

                val event = EventLogDto(
                    id = uuidService.createSortableUuid(),
                    type = StockRemoveEvent.NAME,
                    payload = Json.encodeToString(eventPayload.toDto())
                )
                eventLogEntityDao.insert(event)
            }

        return Unit.right()
    }
}
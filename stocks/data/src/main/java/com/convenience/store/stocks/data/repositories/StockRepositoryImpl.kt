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
import com.convenience.store.stocks.data.datasources.remote.StockApiService
import com.convenience.store.stocks.data.models.events.toDto
import com.convenience.store.stocks.data.models.local.StockDto
import com.convenience.store.stocks.data.models.local.toDomain
import com.convenience.store.stocks.domain.entities.Stock
import com.convenience.store.stocks.domain.entities.StockError
import com.convenience.store.stocks.domain.repositories.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val database: RoomDatabase,
    private val eventLogDao: EventLogDao,
    private val stockDao: StockDao,
    private val stockApiServiceo: StockApiService,
    private val uuidService: UuidService,
) : StockRepository {

    override fun getStockById(productId: UUID): Flow<Stock?> = flow {
        val localStock = stockDao.getStockByProductId(productId).firstOrNull()
        if (localStock != null) {
            emit(localStock.toDomain())
        }

        val remoteResult = try {
            stockApiServiceo.getStockByProductId(productId)
        } catch (e: Exception) {
            null
        }

        remoteResult?.onRight { remoteStock ->
            database
                .withTransaction {
                    val newQuantity = remoteStock.quantity
                    val result = stockDao.updateStockQuantitySync(productId, newQuantity)
                    if (result == 0) stockDao.insertOrUpdateStock(
                        StockDto(
                            productId,
                            newQuantity,
                            BigDecimal.ZERO
                        )
                    )
                }
        }

        emitAll(stockDao.getStockByProductId(productId).map { it?.toDomain() })
    }

    override suspend fun addStock(
        productId: UUID,
        quantityChange: BigDecimal
    ): Either<StockError, Unit> {
        database
            .withTransaction {
                val result = stockDao.addStockQuantityNotSync(productId, quantityChange)
                if (result == 0) stockDao.insertOrUpdateStock(
                    StockDto(productId, quantityChange, BigDecimal.ZERO)
                )

                val eventPayload = StockAddEvent(productId, quantityChange)

                val event = EventLogDto(
                    id = uuidService.createSortableUuid(),
                    type = StockAddEvent.NAME,
                    payload = Json.encodeToString(eventPayload.toDto())
                )
                eventLogDao.insert(event)
            }

        return Unit.right()
    }

    override suspend fun removeStock(
        productId: UUID,
        quantityChange: BigDecimal
    ): Either<StockError, Unit> {
        database
            .withTransaction {
                val result = stockDao.addStockQuantityNotSync(productId, quantityChange.negate())
                if (result == 0) stockDao.insertOrUpdateStock(
                    StockDto(
                        productId,
                        quantityChange.negate(),
                        BigDecimal.ZERO
                    )
                )

                val eventPayload = StockRemoveEvent(productId, quantityChange)

                val event = EventLogDto(
                    id = uuidService.createSortableUuid(),
                    type = StockRemoveEvent.NAME,
                    payload = Json.encodeToString(eventPayload.toDto())
                )
                eventLogDao.insert(event)
            }

        return Unit.right()
    }
}

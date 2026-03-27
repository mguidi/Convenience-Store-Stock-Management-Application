package com.convenience.store.alerts.data.repositories

import androidx.paging.PagingData
import com.convenience.store.alerts.domain.entities.StockAlert
import com.convenience.store.alerts.domain.repositories.StockAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class StockAlertRepositoryImpl @Inject constructor() : StockAlertRepository {

    private val _stockAlertsMock = listOf(
        "019d2616-21ee-71eb-a428-283494f0caf0",
        "019d2616-21ee-7302-a429-8133cd4cf2aa",
        "019d2616-21ee-7358-a42a-71c7f41c307b",
        "019d2616-21ee-73a5-a42b-df04c4301556",
    ).map { StockAlert(UUID.randomUUID(), UUID.fromString(it), BigDecimal.TEN) }


    override fun getStockAlertByProductId(productId: UUID): Flow<StockAlert?> {
        val stockAlert = _stockAlertsMock.find { it.productId == productId }
        return flowOf(stockAlert)
    }

    override fun getStockAlerts(): Flow<PagingData<StockAlert>> {
        return flowOf(PagingData.from(_stockAlertsMock))
    }
}
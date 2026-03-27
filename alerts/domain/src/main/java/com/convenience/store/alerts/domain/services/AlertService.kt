package com.convenience.store.alerts.domain.services

import com.convenience.store.products.domain.entities.Product
import java.math.BigDecimal
import java.util.UUID

interface AlertService {
    suspend fun notifyStockAlert(
        product: Product,
        quantity: BigDecimal,
        thresholdQuantity: BigDecimal
    )
}
package com.convenience.store.alerts.domain.entities

import java.math.BigDecimal
import java.util.UUID

/**
 * Represents a stock alert.
 *
 * @property alertId The unique identifier of the stock alert.
 * @property productId The unique identifier of the product associated with the stock alert.
 * @property thresholdLevel The threshold level for the stock alert.
 */
data class StockAlert(
    val alertId: UUID,
    val productId: UUID,
    val thresholdLevel: BigDecimal,
)
package com.convenience.store.alerts.domain.usecases

import arrow.core.Either
import arrow.core.right
import com.convenience.store.alerts.domain.entities.StockAlert
import com.convenience.store.alerts.domain.repositories.StockAlertRepository
import com.convenience.store.alerts.domain.services.AlertService
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.stocks.domain.repositories.StockRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

interface StockAlertCheckUseCase {

    suspend operator fun invoke(productId: UUID): Either<StockAlert, Unit>
}

class StockAlertCheckUseCasesImpl(
    private val stockAlertRepository: StockAlertRepository,
    private val stockRepository: StockRepository,
    private val productRepository: ProductRepository,
    private val alertService: AlertService
) : StockAlertCheckUseCase {

    override suspend fun invoke(productId: UUID): Either<StockAlert, Unit> {
        val alert = stockAlertRepository.getStockAlertByProductId(productId).first()

        alert?.let {
            val stock = stockRepository.getStockById(productId).first()
            stock?.let {
                if (stock.quantity < alert.thresholdLevel) {
                    val product = productRepository.getProductById(productId).first()
                    product?.let {
                        alertService.notifyStockAlert(product, stock.quantity, alert.thresholdLevel)
                    }
                }
            }
        }

        return Unit.right()

    }
}

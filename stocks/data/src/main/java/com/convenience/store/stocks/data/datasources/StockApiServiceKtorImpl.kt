package com.convenience.store.stocks.data.datasources

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.convenience.store.stocks.data.models.StockApiDto
import com.convenience.store.stocks.data.models.StockApiError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class StockApiServiceKtorImpl @Inject constructor(
    private val client: HttpClient
) : StockApiService {

    override suspend fun addStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockApiError, Unit> {
        return try {
            client.post("stocks/add") {
                contentType(ContentType.Application.Json)
                setBody(
                    StockApiDto(
                        productId = productId,
                        quantity = quantity
                    )
                )
            }
            Unit.right()

        } catch (e: Exception) {
            StockApiError.UnknowError.left()
        }
    }

    override suspend fun removeStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockApiError, Unit> {
        return try {
            client.post("stocks/remove") {
                contentType(ContentType.Application.Json)
                setBody(
                    StockApiDto(
                        productId = productId,
                        quantity = quantity
                    )
                )
            }
            Unit.right()

        } catch (e: Exception) {
            StockApiError.UnknowError.left()
        }
    }

    override suspend fun getStockByProductId(productId: UUID): Either<StockApiError, StockApiDto> {
        return try {
            val response: StockApiDto = client.get("stocks/$productId") {
                contentType(ContentType.Application.Json)
            }.body()
            response.right()
        } catch (e: Exception) {
            StockApiError.UnknowError.left()
        }
    }
}

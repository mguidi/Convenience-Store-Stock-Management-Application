package com.convenience.store.products.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.convenience.store.products.data.datasources.local.ProductDao
import com.convenience.store.products.data.datasources.remote.ProductApiService
import com.convenience.store.products.data.models.local.ProductDto
import com.convenience.store.products.data.models.remote.toDto
import java.util.UUID

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val database: RoomDatabase,
    private val productDao: ProductDao,
    private val productApiService: ProductApiService,
    private val categoryId: UUID? = null
) : RemoteMediator<Int, ProductDto>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductDto>
    ): MediatorResult {
        return try {
            // In questa implementazione semplificata (senza paginazione lato server ancora definita nell'API),
            // carichiamo tutti i prodotti se è il caricamento iniziale.
            if (loadType == LoadType.REFRESH) {
                val response = productApiService.getProducts()
                
                response.fold(
                    { MediatorResult.Error(Exception("API Error")) },
                    { products ->
                        database.withTransaction {
                            // Qui potresti voler filtrare o gestire la cancellazione se necessario
                            productDao.insertAll(products.map { it.toDto() })
                        }
                        MediatorResult.Success(endOfPaginationReached = true)
                    }
                )
            } else {
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

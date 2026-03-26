package com.convenience.store.products.data.repositories

import android.content.Context
import androidx.core.content.edit
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
    private val context: Context,
    private val database: RoomDatabase,
    private val productDao: ProductDao,
    private val productApiService: ProductApiService,
    private val categoryId: UUID? = null
) : RemoteMediator<Int, ProductDto>() {

    companion object {
        const val PREFS = "NEXT_REMOTE_PAGE"
        const val PRODUCTS_KEY = "products"
    }

    private val _prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductDto>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                // on refresh start always from the first page
                1
            }

            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            LoadType.APPEND -> {
                _prefs.getInt(PRODUCTS_KEY, 1)
            }
        }

        return try {
            val response = if (categoryId == null) {
                productApiService.getProducts(page = page, pageSize = state.config.pageSize)
            } else {
                productApiService.getProductsByCategoryId(
                    categoryId,
                    page = page,
                    pageSize = state.config.pageSize
                )
            }

            response.fold(
                { MediatorResult.Error(Exception("API Error")) },
                { products ->
                    val endOfPaginationReached = products.isEmpty()
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            productDao.deleteAllSynced()
                            _prefs.edit(commit = true) { remove(PRODUCTS_KEY) }
                        }
                        _prefs.edit(commit = true) { putInt(PRODUCTS_KEY, page + 1) }
                        productDao.insertOrUpdateAll(products.map { it.toDto() })
                    }
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

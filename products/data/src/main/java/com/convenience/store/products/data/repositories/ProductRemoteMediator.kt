package com.convenience.store.products.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.convenience.store.products.data.datasources.local.ProductDao
import com.convenience.store.products.data.datasources.local.ProductRemoteKeyDao
import com.convenience.store.products.data.datasources.remote.ProductApiService
import com.convenience.store.products.data.models.local.ProductDto
import com.convenience.store.products.data.models.local.ProductRemoteKeyDto
import com.convenience.store.products.data.models.remote.toDto
import java.util.UUID

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val database: RoomDatabase,
    private val productDao: ProductDao,
    private val productRemoteKeyDao: ProductRemoteKeyDao,
    private val productApiService: ProductApiService,
    private val categoryId: UUID? = null
) : RemoteMediator<Int, ProductDto>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductDto>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val response = productApiService.getProducts(page = page, pageSize = state.config.pageSize)

            response.fold(
                { MediatorResult.Error(Exception("API Error")) },
                { products ->
                    val endOfPaginationReached = products.isEmpty()
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            productRemoteKeyDao.clearRemoteKeys()
                            // Note: We might not want to clear products if they are used elsewhere
                        }
                        val prevKey = if (page == 1) null else page - 1
                        val nextKey = if (endOfPaginationReached) null else page + 1
                        val keys = products.map {
                            ProductRemoteKeyDto(productId = it.id, prevKey = prevKey, nextKey = nextKey)
                        }
                        productRemoteKeyDao.insertAll(keys)
                        productDao.insertOrUpdateAll(products.map { it.toDto() })
                    }
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ProductDto>): ProductRemoteKeyDto? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { product ->
                productRemoteKeyDao.remoteKeysProductId(product.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ProductDto>): ProductRemoteKeyDto? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { product ->
                productRemoteKeyDao.remoteKeysProductId(product.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ProductDto>
    ): ProductRemoteKeyDto? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { productId ->
                productRemoteKeyDao.remoteKeysProductId(productId)
            }
        }
    }
}

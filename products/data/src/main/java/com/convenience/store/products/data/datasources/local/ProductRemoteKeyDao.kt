package com.convenience.store.products.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.convenience.store.products.data.models.local.ProductRemoteKeyDto
import java.util.UUID

@Dao
interface ProductRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ProductRemoteKeyDto>)

    @Query("SELECT * FROM product_remote_keys WHERE productId >= :productId ORDER BY productId ASC LIMIT 1")
    suspend fun remoteKeysCloseToProductIdUp(productId: UUID): ProductRemoteKeyDto?

    @Query("SELECT * FROM product_remote_keys WHERE productId <= :productId ORDER BY productId DESC LIMIT 1")
    suspend fun remoteKeysCloseToProductIdDown(productId: UUID): ProductRemoteKeyDto?

    @Query("DELETE FROM product_remote_keys")
    suspend fun deleteAll()
}
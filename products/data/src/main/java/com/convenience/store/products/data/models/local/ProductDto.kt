package com.convenience.store.products.data.models.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.convenience.store.products.domain.entities.Product
import java.math.BigDecimal
import java.util.UUID

@Entity(
    tableName = "products",
    indices = [Index(value = ["barcode"], unique = true)]
)
data class ProductDto(
    @PrimaryKey val id: UUID,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val barcode: String,
    val categoryId: UUID,
    val supplierId: UUID,
    val version: Long,
    val timestamp: Long = System.currentTimeMillis()
)

fun ProductDto.toDomain() = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    barcode = barcode,
    categoryId = categoryId,
    supplierId = supplierId,
    version = version,
)

fun Product.toDto() = ProductDto(
    id = id,
    name = name,
    description = description,
    price = price,
    barcode = barcode,
    categoryId = categoryId,
    supplierId = supplierId,
    version = version,
)

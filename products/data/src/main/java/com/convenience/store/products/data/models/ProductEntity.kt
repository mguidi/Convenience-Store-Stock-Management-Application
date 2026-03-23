package com.convenience.store.products.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.convenience.store.products.domain.entities.Product
import java.math.BigDecimal
import java.util.UUID

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: UUID,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val barcode: String,
    val categoryId: UUID,
    val supplierId: UUID,
    val availableQuantity: BigDecimal,
    val timestamp: Long = System.currentTimeMillis()
)

fun ProductEntity.toDomain() = Product(
    id = this.id,
    name = this.name,
    description = this.description,
    price = this.price,
    barcode = this.barcode,
    categoryId = this.categoryId,
    supplierId = this.supplierId,
    availableQuantity = this.availableQuantity,
)

fun Product.toEntity() = ProductEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    price = this.price,
    barcode = this.barcode,
    categoryId = this.categoryId,
    supplierId = this.supplierId,
    availableQuantity = this.availableQuantity,
)

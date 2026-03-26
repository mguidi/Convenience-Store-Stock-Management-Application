package com.convenience.store.products.domain.repositories

import com.convenience.store.products.domain.entities.Category
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface CategoryRepository {

    fun getCategories(): Flow<List<Category>>

    fun getCategoryById(id: UUID): Flow<Category?>

}
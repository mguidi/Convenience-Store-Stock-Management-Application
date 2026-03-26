package com.convenience.store.products.data.repositories

import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class CategoryRepositoryMock @Inject constructor() : CategoryRepository {

    private val _categories = listOf(
        Category(
            UUID.fromString("019d2616-21ee-78b4-a43c-fef07b5ff7ab"),
            "Beverages",
        ),
        Category(
            UUID.fromString("019d2616-21ee-78f9-a43d-976eddb2f099"),
            "Snacks",
        ),
        Category(
            UUID.fromString("019d2616-21ee-7943-a43e-b38a2961adb6"),
            "Dairy",
        )
    )

    override fun getCategories(): Flow<List<Category>> {
        return flowOf(_categories)
    }

    override fun getCategoryById(id: UUID): Flow<Category?> {
        return flowOf(_categories.find { it.id == id })
    }
}
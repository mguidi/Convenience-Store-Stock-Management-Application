package com.convenience.store.products.domain.usecases

import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface CategoryGetUseCase {

    operator fun invoke(categoryId: UUID): Flow<Category?>
}

class CategoryGetUseCaseImpl(
    private val categoryRepository: CategoryRepository
) : CategoryGetUseCase {
    override fun invoke(categoryId: UUID): Flow<Category?> {
        return categoryRepository.getCategoryById(categoryId)
    }
}
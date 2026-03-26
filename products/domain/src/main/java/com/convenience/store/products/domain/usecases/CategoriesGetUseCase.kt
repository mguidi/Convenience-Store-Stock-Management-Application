package com.convenience.store.products.domain.usecases

import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow

interface CategoriesGetUseCase {

    operator fun invoke(): Flow<List<Category>>
}

class CategoriesGetUseCaseImpl(
    private val categoryRepository: CategoryRepository
) : CategoriesGetUseCase {
    override fun invoke(): Flow<List<Category>> {
        return categoryRepository.getCategories()
    }
}
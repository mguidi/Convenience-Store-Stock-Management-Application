package com.convenience.store.products.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.usecases.CategoriesGetUseCase
import com.convenience.store.products.domain.usecases.ProductsGetUseCase
import com.convenience.store.products.domain.usecases.ProductsGetsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsGetUseCase: ProductsGetUseCase,
    private val productsGetsByCategoryUseCase: ProductsGetsByCategoryUseCase,
    categoriesGetUseCase: CategoriesGetUseCase
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<UUID?>(null)
    val selectedCategoryId: StateFlow<UUID?> = _selectedCategoryId.asStateFlow()
    val categories: Flow<List<Category>> = categoriesGetUseCase()

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: Flow<PagingData<Product>> = _selectedCategoryId
        .flatMapLatest { categoryId ->
            if (categoryId == null) {
                productsGetUseCase()

            } else {
                productsGetsByCategoryUseCase(categoryId)
            }
        }
        .cachedIn(viewModelScope)

    fun onCategorySelected(categoryId: UUID?) {
        _selectedCategoryId.value = categoryId
    }
}

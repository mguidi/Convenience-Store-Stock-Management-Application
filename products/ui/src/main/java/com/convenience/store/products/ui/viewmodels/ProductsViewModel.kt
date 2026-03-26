package com.convenience.store.products.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.entities.Product
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
    private val productsGetsByCategoryUseCase: ProductsGetsByCategoryUseCase
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<UUID?>(null)
    val selectedCategoryId: StateFlow<UUID?> = _selectedCategoryId.asStateFlow()


    // Mocked categories for now, in a real app these would come from a CategoryRepository
    private val _categories = MutableStateFlow(
        listOf(
            Category(
                UUID.fromString("019d2616-21ee-78b4-a43c-fef07b5ff7ab"),
                "Beverages",
                "Drinks and sodas"
            ),
            Category(
                UUID.fromString("019d2616-21ee-78f9-a43d-976eddb2f099"),
                "Snacks",
                "Chips and cookies"
            ),
            Category(
                UUID.fromString("019d2616-21ee-7943-a43e-b38a2961adb6"),
                "Dairy",
                "Milk and cheese"
            )
        )
    )
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

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

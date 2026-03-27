package com.convenience.store.products.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.usecases.CategoriesGetUseCase
import com.convenience.store.products.domain.usecases.ProductsGetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val productsGetUseCase: ProductsGetUseCase,
    categoriesGetUseCase: CategoriesGetUseCase
) : ViewModel() {

    val barcodeState = TextFieldState(initialText = "")

    private val _selectedCategoryId = MutableStateFlow<UUID?>(null)
    val selectedCategoryId: StateFlow<UUID?> = _selectedCategoryId.asStateFlow()
    val categories: Flow<List<Category>> = categoriesGetUseCase()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products: Flow<PagingData<Product>> = combine(
        _selectedCategoryId,
        snapshotFlow { barcodeState.text }.debounce(300L)
    ) { categoryId, barcode ->
        categoryId to barcode.toString().takeIf { it.isNotBlank() }
    }
        .flatMapLatest { (categoryId, barcode) ->
            productsGetUseCase(categoryId, barcode)
        }
        .cachedIn(viewModelScope)

    fun onCategorySelected(categoryId: UUID?) {
        _selectedCategoryId.value = categoryId
    }
}

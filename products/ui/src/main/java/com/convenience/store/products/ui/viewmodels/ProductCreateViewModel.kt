package com.convenience.store.products.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.usecases.CategoriesGetUseCase
import com.convenience.store.products.domain.usecases.ProductCreateUseCase
import com.convenience.store.suppliers.domain.entities.Supplier
import com.convenience.store.suppliers.domain.usecases.SuppliersGetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductCreateViewModel @Inject constructor(
    private val productCreateUseCase: ProductCreateUseCase,
    categoriesGetUseCase: CategoriesGetUseCase,
    suppliersGetUseCase: SuppliersGetUseCase
) : ViewModel() {

    // State for Product fields
    val nameState = TextFieldState()
    val descriptionState = TextFieldState()
    val priceState = TextFieldState()
    val barcodeState = TextFieldState()

    // State for Categories
    val categories = categoriesGetUseCase()
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    // State for Suppliers
    val suppliers = suppliersGetUseCase()
    private val _selectedSupplier = MutableStateFlow<Supplier?>(null)
    val selectedSupplier = _selectedSupplier.asStateFlow()

    // Ui state
    private val _uiState: MutableStateFlow<ProductCreateScreenState> =
        MutableStateFlow(ProductCreateScreenState.Init)
    val uiState = _uiState.asStateFlow()

    private var _isInitialized = false

    fun init() {
        if (!_isInitialized) {
            clearFields()
            _isInitialized = true
        }
    }

    fun reset() {
        clearFields()
        _isInitialized = false
        _uiState.value = ProductCreateScreenState.Init
    }

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }

    fun onSupplierSelected(supplier: Supplier) {
        _selectedSupplier.value = supplier
    }

    fun save() {
        val categoryId = _selectedCategory.value?.id ?: return
        val supplierId = _selectedSupplier.value?.id ?: return
        val name = nameState.text.toString()
        val description = descriptionState.text.toString()
        val price = priceState.text.toString().toBigDecimal()
        val barcode = barcodeState.text.toString()

        viewModelScope.launch {
            _uiState.value = ProductCreateScreenState.Loading
            productCreateUseCase(name, description, price, barcode, categoryId, supplierId).fold(
                ifLeft = {
                    _uiState.value = ProductCreateScreenState.Error(it)
                },
                ifRight = {
                    reset()
                    _uiState.value = ProductCreateScreenState.Success
                }
            )
        }
    }


    private fun clearFields() {
        nameState.clearText()
        descriptionState.clearText()
        priceState.clearText()
        barcodeState.clearText()
        _selectedCategory.value = null
        _selectedSupplier.value = null
    }
}

sealed interface ProductCreateScreenState {
    data object Init : ProductCreateScreenState
    data object Loading : ProductCreateScreenState
    data class Error(val errors: List<ProductError>) : ProductCreateScreenState
    data object Success : ProductCreateScreenState
}

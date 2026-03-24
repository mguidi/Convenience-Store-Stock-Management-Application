package com.convenience.store.products.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.usecases.ProductAddUseCase
import com.convenience.store.suppliers.domain.entities.Supplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductAddViewModel @Inject constructor(
    private val productAddUseCase: ProductAddUseCase
) : ViewModel() {

    // State for Product fields
    val nameState = TextFieldState()
    val descriptionState = TextFieldState()
    val priceState = TextFieldState()
    val barcodeState = TextFieldState()

    // State for Categories
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    // State for Suppliers
    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers = _suppliers.asStateFlow()
    private val _selectedSupplier = MutableStateFlow<Supplier?>(null)
    val selectedSupplier = _selectedSupplier.asStateFlow()

    // Ui state
    private val _uiState: MutableStateFlow<ProductAddScreenState> =
        MutableStateFlow(ProductAddScreenState.Init)
    val uiState = _uiState.asStateFlow()

    private var isInitialized = false

    fun init() {
        if (!isInitialized) {
            clearFields()
            loadData()
            isInitialized = true
        }
    }

    fun reset() {
        clearFields()
        isInitialized = false
        _uiState.value = ProductAddScreenState.Init
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
            _uiState.value = ProductAddScreenState.Loading
            productAddUseCase(name, description, price, barcode, categoryId, supplierId).fold(
                ifLeft = {
                    _uiState.value = ProductAddScreenState.Error(it)
                },
                ifRight = {
                    reset()
                    _uiState.value = ProductAddScreenState.Success
                }
            )
        }
    }

    private fun loadData() {
        // Mocking data. In a real app, load this from use cases/repositories
        val mockCategories = listOf(
            Category(UUID.randomUUID(), "Beverages", "Drinks and sodas"),
            Category(UUID.randomUUID(), "Snacks", "Chips and cookies"),
            Category(UUID.randomUUID(), "Dairy", "Milk and cheese")
        )
        _categories.value = mockCategories

        val mockSuppliers = listOf(
            Supplier(UUID.randomUUID(), "Global Foods Inc."),
            Supplier(UUID.randomUUID(), "Local Dairy Farm"),
            Supplier(UUID.randomUUID(), "Soda Distributing Co.")
        )
        _suppliers.value = mockSuppliers
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

sealed interface ProductAddScreenState {
    data object Init : ProductAddScreenState
    data object Loading : ProductAddScreenState
    data class Error(val errors: List<ProductError>) : ProductAddScreenState
    data object Success : ProductAddScreenState
}

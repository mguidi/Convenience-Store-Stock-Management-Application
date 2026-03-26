package com.convenience.store.products.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.usecases.CategoriesGetUseCase
import com.convenience.store.products.domain.usecases.CategoryGetUseCase
import com.convenience.store.products.domain.usecases.ProductDeleteUseCase
import com.convenience.store.products.domain.usecases.ProductGetUseCase
import com.convenience.store.products.domain.usecases.ProductUpdateUseCase
import com.convenience.store.suppliers.domain.entities.Supplier
import com.convenience.store.suppliers.domain.usecases.SupplierGetUseCase
import com.convenience.store.suppliers.domain.usecases.SuppliersGetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductEditViewModel @Inject constructor(
    private val productGetUseCase: ProductGetUseCase,
    private val productUpdateUseCase: ProductUpdateUseCase,
    private val productDeleteUseCase: ProductDeleteUseCase,
    private val categoryGetUseCase: CategoryGetUseCase,
    private val supplierGetUseCase: SupplierGetUseCase,
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

    private var _currentProduct: Product? = null

    // Ui state
    private val _uiState: MutableStateFlow<ProductEditScreenState> =
        MutableStateFlow(ProductEditScreenState.Init)
    val uiState = _uiState.asStateFlow()

    fun loadProduct(productId: UUID) {
        viewModelScope.launch {
            _currentProduct = productGetUseCase(productId).firstOrNull()
            _currentProduct?.let {
                nameState.edit { replace(0, length, it.name) }
                descriptionState.edit { replace(0, length, it.description) }
                priceState.edit { replace(0, length, it.price.toString()) }
                barcodeState.edit { replace(0, length, it.barcode) }
                _selectedCategory.value = categoryGetUseCase(it.categoryId).firstOrNull()
                _selectedSupplier.value = supplierGetUseCase(it.supplierId).firstOrNull()
            }
        }
    }

    fun reset() {
        clearFields()
        _uiState.value = ProductEditScreenState.Init
    }

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }

    fun onSupplierSelected(supplier: Supplier) {
        _selectedSupplier.value = supplier
    }

    fun save() {
        val id = _currentProduct?.id ?: return
        val categoryId = _selectedCategory.value?.id ?: return
        val supplierId = _selectedSupplier.value?.id ?: return
        val name = nameState.text.toString()
        val description = descriptionState.text.toString()
        val price = priceState.text.toString().toBigDecimal()
        val barcode = barcodeState.text.toString()

        viewModelScope.launch {
            _uiState.value = ProductEditScreenState.Loading
            productUpdateUseCase(
                id,
                name,
                description,
                price,
                barcode,
                categoryId,
                supplierId,
                0,
            ).fold(
                ifLeft = {
                    _uiState.value = ProductEditScreenState.Error(it)
                },
                ifRight = {
                    reset()
                    _uiState.value = ProductEditScreenState.Success
                }
            )
        }
    }

    fun delete() {
        val id = _currentProduct?.id ?: return

        viewModelScope.launch {
            _uiState.value = ProductEditScreenState.Loading
            productDeleteUseCase(id).fold(
                ifLeft = {
                    _uiState.value = ProductEditScreenState.Error(it)
                },
                ifRight = {
                    reset()
                    _uiState.value = ProductEditScreenState.Success
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

sealed interface ProductEditScreenState {
    data object Init : ProductEditScreenState
    data object Loading : ProductEditScreenState
    data class Error(val errors: List<ProductError>) : ProductEditScreenState
    data object Success : ProductEditScreenState
}

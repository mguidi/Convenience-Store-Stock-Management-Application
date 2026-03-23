package com.convenience.store.products.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.usecases.ProductAddUseCase
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

    val nameState = TextFieldState()
    val descriptionState = TextFieldState()
    val priceState = TextFieldState()
    val barcodeState = TextFieldState()
    val availableQuantityState = TextFieldState()

    private val _uiState: MutableStateFlow<ProductAddScreenState> =
        MutableStateFlow(ProductAddScreenState.Init)
    val uiState = _uiState.asStateFlow()

    private var isInitialized = false

    fun save() {
        viewModelScope.launch {
            _uiState.value = ProductAddScreenState.Loading
            val result = productAddUseCase.newProduct(
                nameState.text.toString(),
                descriptionState.text.toString(),
                priceState.text.toString().toBigDecimal(),
                barcodeState.text.toString(),
                UUID.randomUUID(), // TODO need to add the category selection on the screen
                UUID.randomUUID(),// TODO need to add the supplier selection on the screen
                availableQuantityState.text.toString().toBigDecimal()
            )

            result.fold(
                ifLeft = {
                    _uiState.value = ProductAddScreenState.Error(it)
                },
                ifRight = {
                    clearFields()
                    _uiState.value = ProductAddScreenState.Success
                }
            )
        }
    }

    fun init() {
        if (!isInitialized) {
            clearFields()
            isInitialized = true
        }
    }

    private fun clearFields() {
        nameState.clearText()
        descriptionState.clearText()
        priceState.clearText()
        barcodeState.clearText()
        availableQuantityState.clearText()
    }

}

sealed interface ProductAddScreenState {
    data object Init : ProductAddScreenState

    data object Loading : ProductAddScreenState

    data class Error(val error: ProductError) : ProductAddScreenState

    data object Success : ProductAddScreenState

}

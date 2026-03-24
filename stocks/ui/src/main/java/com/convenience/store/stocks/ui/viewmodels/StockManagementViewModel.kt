package com.convenience.store.stocks.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convenience.store.stocks.domain.entities.Stock
import com.convenience.store.stocks.domain.entities.StockError
import com.convenience.store.stocks.domain.usecases.StockAddUseCase
import com.convenience.store.stocks.domain.usecases.StockGetByIdUseCase
import com.convenience.store.stocks.domain.usecases.StockRemoveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StockManagementViewModel @Inject constructor(
    private val stockGetByIdUseCase: StockGetByIdUseCase,
    private val stockAddUseCase: StockAddUseCase,
    private val stockRemoveUseCase: StockRemoveUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StockManagementScreenState>(StockManagementScreenState.Init)
    val uiState = _uiState.asStateFlow()

    private val _currentStock = MutableStateFlow<Stock?>(null)
    val currentStock = _currentStock.asStateFlow()

    val quantityState = TextFieldState()

    private var productId: UUID? = null

    fun loadStock(id: UUID) {
        productId = id
        viewModelScope.launch {
            stockGetByIdUseCase(id).collect {
                _currentStock.value = it
            }
        }
    }

    fun addStock() {
        val id = productId ?: return
        val amount = quantityState.text.toString().toBigDecimalOrNull() ?: return
        
        viewModelScope.launch {
            _uiState.value = StockManagementScreenState.Loading
            stockAddUseCase(id, amount).fold(
                ifLeft = { _uiState.value = StockManagementScreenState.Error(listOf(it)) },
                ifRight = { 
                    _uiState.value = StockManagementScreenState.Success
                }
            )
        }
    }

    fun removeStock() {
        val id = productId ?: return
        val amount = quantityState.text.toString().toBigDecimalOrNull() ?: return

        viewModelScope.launch {
            _uiState.value = StockManagementScreenState.Loading
            stockRemoveUseCase(id, amount).fold(
                ifLeft = { _uiState.value = StockManagementScreenState.Error(listOf(it)) },
                ifRight = { 
                    _uiState.value = StockManagementScreenState.Success
                    quantityState.clearText()
                }
            )
        }
    }

    fun reset() {
        _uiState.value = StockManagementScreenState.Init
        quantityState.clearText()
    }
}

sealed interface StockManagementScreenState {
    data object Init : StockManagementScreenState
    data object Loading : StockManagementScreenState
    data object Success : StockManagementScreenState
    data class Error(val errors: List<StockError>) : StockManagementScreenState
}

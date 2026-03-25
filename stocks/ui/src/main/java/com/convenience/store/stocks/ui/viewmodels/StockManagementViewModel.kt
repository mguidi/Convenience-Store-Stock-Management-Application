package com.convenience.store.stocks.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convenience.store.stocks.domain.entities.StockError
import com.convenience.store.stocks.domain.usecases.StockAddUseCase
import com.convenience.store.stocks.domain.usecases.StockGetByIdUseCase
import com.convenience.store.stocks.domain.usecases.StockRemoveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StockManagementViewModel @Inject constructor(
    private val stockGetByIdUseCase: StockGetByIdUseCase,
    private val stockAddUseCase: StockAddUseCase,
    private val stockRemoveUseCase: StockRemoveUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<StockManagementScreenState>(StockManagementScreenState.Init)
    val uiState = _uiState.asStateFlow()

    private val _productId = MutableStateFlow<UUID?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentStock = _productId
        .filterNotNull()
        .flatMapLatest { id ->
            stockGetByIdUseCase(id).onStart { emit(null) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val quantityState = TextFieldState()

    fun loadStock(id: UUID) {
        _productId.value = id
    }

    fun addStock() {
        val id = _productId.value ?: return
        val amount = quantityState.text.toString().toBigDecimalOrNull() ?: return

        viewModelScope.launch {
            _uiState.value = StockManagementScreenState.Loading
            stockAddUseCase(id, amount).fold(
                ifLeft = { _uiState.value = StockManagementScreenState.Error(listOf(it)) },
                ifRight = {
                    _uiState.value = StockManagementScreenState.Success
                    quantityState.clearText()
                }
            )
        }
    }

    fun removeStock() {
        val id = _productId.value ?: return
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
        _productId.value = null
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

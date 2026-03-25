package com.convenience.store.products.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.usecases.ProductsGetUseCase
import com.convenience.store.stocks.domain.entities.Stock
import com.convenience.store.stocks.domain.usecases.StockGetByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductItemViewModel @Inject constructor(
    private val stockGetByIdUseCase: StockGetByIdUseCase,
) : ViewModel() {

    fun getStockById(productId: UUID): Flow<Stock?> = stockGetByIdUseCase(productId)

}
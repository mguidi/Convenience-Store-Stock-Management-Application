package com.convenience.store.products.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    repository: ProductRepository
) : ViewModel() {

    val products: Flow<PagingData<Product>> = repository.getProductsStream()
        .cachedIn(viewModelScope)

}
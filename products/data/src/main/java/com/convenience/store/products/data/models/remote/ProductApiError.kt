package com.convenience.store.products.data.models.remote

sealed interface ProductApiError {

    data object UnknowError : ProductApiError
}
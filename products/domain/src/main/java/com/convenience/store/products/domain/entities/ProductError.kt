package com.convenience.store.products.domain.entities

sealed interface ProductError {

    sealed interface ValidationError : ProductError {
        data object InvalidName : ValidationError
        data object InvalidDescription : ValidationError
        data object InvalidPrice : ValidationError
        data object InvalidBarcode : ValidationError
        data object InvalidCategory : ValidationError
        data object InvalidSupplier : ValidationError
    }

    sealed interface RepositoryError : ProductError {
        data object AlreadyExists : RepositoryError
        data object DatabaseError : RepositoryError
        data object UnknownError : RepositoryError
    }
}
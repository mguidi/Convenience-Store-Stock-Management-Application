package com.convenience.store.products.domain.entities

sealed interface ProductError {

    sealed interface ValidationError : ProductError {
        object InvalidName : ValidationError
        object InvalidDescription : ValidationError
        object InvalidPrice : ValidationError
        object InvalidBarcode : ValidationError
        object InvalidCategory : ValidationError
        object InvalidSupplier : ValidationError
    }

    sealed interface RepositoryError : ProductError {
        data object AlreadyExists : RepositoryError
        data object DatabaseError : RepositoryError
        data object UnknownError : RepositoryError
    }
}
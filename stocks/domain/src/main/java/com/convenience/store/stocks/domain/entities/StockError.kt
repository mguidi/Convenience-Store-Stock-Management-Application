package com.convenience.store.stocks.domain.entities

sealed interface StockError {

    sealed interface ValidationError : StockError {
        data object InvalidQuantity : StockError
    }

    sealed interface RepositoryError : StockError {
        data object InsufficientStock : StockError
        data object DatabaseError : StockError
        data object Unknown : StockError
    }

}
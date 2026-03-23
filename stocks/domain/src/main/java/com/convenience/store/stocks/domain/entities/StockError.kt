package com.convenience.store.stocks.domain.entities

sealed interface StockError {

    data object InvalidQuantity : StockError
    data object InsufficientStock : StockError
    data object Unknown : StockError


}
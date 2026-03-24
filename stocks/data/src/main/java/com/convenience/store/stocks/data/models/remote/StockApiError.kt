package com.convenience.store.stocks.data.models.remote

sealed interface StockApiError {

    data object UnknowError : StockApiError

}
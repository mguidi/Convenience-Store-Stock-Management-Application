package com.convenience.store.stocks.data.models

sealed interface StockApiError {

    data object UnknowError : StockApiError

}
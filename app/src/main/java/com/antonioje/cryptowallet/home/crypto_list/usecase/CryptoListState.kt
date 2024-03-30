package com.antonioje.cryptowallet.home.crypto_list.usecase

sealed class CryptoListState {
    data object NoDataError:CryptoListState()
    data class  Loading(var value:Boolean):CryptoListState()
    data object Success:CryptoListState()
}
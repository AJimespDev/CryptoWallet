package com.antonioje.cryptowallet.home.list.usecase

sealed class CryptoListState {
    data object NoDataError:CryptoListState()
    data object Success:CryptoListState()
}
package com.antonioje.cryptowallet.home.crypto_list.usecase

sealed class CryptoListState {
    data object NoDataError:CryptoListState()
    data object Success:CryptoListState()
}
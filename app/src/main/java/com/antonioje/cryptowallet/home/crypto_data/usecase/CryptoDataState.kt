package com.antonioje.cryptowallet.home.crypto_data.usecase

import com.antonioje.cryptowallet.data.model.CryptoData

sealed class CryptoDataState {
    data object NoDataError:CryptoDataState()
    data class Success(var data: CryptoData):CryptoDataState()

}
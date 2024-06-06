package com.antonioje.cryptowallet.home.portfolio.usecase

import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.home.crypto_list.usecase.CryptoListState

sealed class PortfolioListState {
    data object NoDataError:PortfolioListState()
    data class Loading(var value:Boolean):PortfolioListState()
    data object onSuccess:PortfolioListState()
}
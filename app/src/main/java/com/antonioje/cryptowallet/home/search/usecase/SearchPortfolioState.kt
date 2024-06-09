package com.antonioje.cryptowallet.home.search.usecase

import com.antonioje.cryptowallet.data.model.CryptoSearch
import com.antonioje.cryptowallet.data.model.Portfolio

sealed class SearchPortfolioState(){
    data object NoData:SearchPortfolioState()
    data class Loading(val value:Boolean):SearchPortfolioState()
    data class Success(val data: List<Portfolio>):SearchPortfolioState()
}

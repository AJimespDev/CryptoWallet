package com.antonioje.cryptowallet.home.search.usecase

import com.antonioje.cryptowallet.data.model.CryptoSearch

sealed class SearchState(){
    data object NoData:SearchState()
    data class Success(val data: ArrayList<CryptoSearch>):SearchState()
}

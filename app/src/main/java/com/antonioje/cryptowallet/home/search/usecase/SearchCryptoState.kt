package com.antonioje.cryptowallet.home.search.usecase

import com.antonioje.cryptowallet.data.model.CryptoSearch

sealed class SearchCryptoState(){
    data object NoData:SearchCryptoState()
    data class Success(val data: ArrayList<CryptoSearch>):SearchCryptoState()
}

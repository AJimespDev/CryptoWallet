package com.antonioje.cryptowallet.home.search.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoSearch
import com.antonioje.cryptowallet.utils.HttpUtil
import kotlinx.coroutines.launch

class SearchViewModel:ViewModel() {
    private var state = MutableLiveData<SearchState>()
    private val URL =
        "https://api.coingecko.com/api/v3/coins/list?include_platform=false"


    fun getState(): MutableLiveData<SearchState> {
        return state
    }



    fun getList(){
        viewModelScope.launch {
            val listAllCryptos = HttpUtil.instance?.getResponseData(URL, Array<CryptoSearch>::class.java)
            when{
                listAllCryptos.isNullOrEmpty() -> state.value = SearchState.NoData
                else ->  state.value = SearchState.Success(listAllCryptos.toList() as ArrayList<CryptoSearch>)
            }
        }
    }


}
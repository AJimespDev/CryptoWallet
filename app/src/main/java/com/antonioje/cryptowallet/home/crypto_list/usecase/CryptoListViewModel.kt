package com.antonioje.cryptowallet.home.crypto_list.usecase


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.repository.CryptoRepository
import com.antonioje.cryptowallet.utils.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch


class CryptoListViewModel : ViewModel() {
    private var state = MutableLiveData<CryptoListState>()
    private val URL =
        "https://api.coingecko.com/api/v3/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=250&price_change_percentage=24'"


    fun getState(): MutableLiveData<CryptoListState> {
        return state
    }

    val listCrypto = HttpUtil.instance?.getResponseData(URL, Array<CryptoCurrency>::class.java)

    fun getList() {
        viewModelScope.launch {
            state.value = CryptoListState.Loading(false)
            when {
                listCrypto?.isEmpty() == true -> state.value = CryptoListState.NoDataError
                else -> state.value = CryptoListState.Success
            }
        }
    }

    fun initFavoriteList() {
        viewModelScope.launch {
            state.value = CryptoListState.Loading(true)
            CryptoRepository.getFavouritesCryptos{
                getList()
            }
        }
    }

    fun deleteFavourite(crypto: CryptoCurrency) {
        CryptoRepository.deleteFavouriteCrypto(crypto.name) {}
    }

    fun addFavourite(crypto: CryptoCurrency) {
        CryptoRepository.addFavouriteCrypto(crypto)
    }
}
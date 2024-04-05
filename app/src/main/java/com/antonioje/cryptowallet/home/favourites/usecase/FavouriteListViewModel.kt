package com.antonioje.cryptowallet.home.favourites.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.repository.CryptoRepository

class FavouriteListViewModel:ViewModel() {
    private var state = MutableLiveData<FavouriteListState>()

    var favouriteList = CryptoRepository.favouritesCrypto.toList().sortedBy { it.market_cap_rank }

    fun getState(): MutableLiveData<FavouriteListState> {
        return state
    }

    fun getFavourites(){
        when{
            favouriteList.isEmpty() -> state.value = FavouriteListState.NoData
            else -> state.value = FavouriteListState.Success
        }
    }

    fun deleteFavourite(crypto: CryptoCurrency, onDelete:() -> Unit) {
        CryptoRepository.deleteFavouriteCrypto(crypto) {onDelete()}
    }

    fun updateList() {
        favouriteList = CryptoRepository.favouritesCrypto.toList().sortedBy { it.market_cap_rank }
    }

}
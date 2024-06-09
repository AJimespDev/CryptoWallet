package com.antonioje.cryptowallet.home.search.usecase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonioje.cryptowallet.data.model.CryptoSearch
import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.data.repository.CryptoRepository
import kotlinx.coroutines.launch

class SearchPortfolioViewModel:ViewModel() {
    private var state = MutableLiveData<SearchPortfolioState>()
    lateinit var portfolioList:List<Portfolio>

    fun getState(): MutableLiveData<SearchPortfolioState> {
        return state
    }



    fun getList(){
        portfolioList = CryptoRepository.allPublicPortfolios.toList()

        state.value = SearchPortfolioState.Loading(false)
        when{
            portfolioList.isEmpty() -> state.value = SearchPortfolioState.NoData
            else -> state.value = SearchPortfolioState.Success(portfolioList)
        }
    }

    fun initPublicPortfolioList() {
        viewModelScope.launch {
            state.value = SearchPortfolioState.Loading(true)
            CryptoRepository.getAllPortfolios{
                getList()
            }
        }
    }
}
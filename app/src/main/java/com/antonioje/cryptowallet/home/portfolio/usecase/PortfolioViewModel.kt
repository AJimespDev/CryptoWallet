package com.antonioje.cryptowallet.home.portfolio.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.data.repository.CryptoRepository
import com.antonioje.cryptowallet.home.crypto_list.usecase.CryptoListState
import com.antonioje.cryptowallet.utils.HttpUtil
import kotlinx.coroutines.launch


class PortfolioViewModel:ViewModel() {
    private var state = MutableLiveData<PortfolioListState>()

    fun getState(): MutableLiveData<PortfolioListState> {
        return state
    }

    var portfolioCrypto = listOf<Portfolio>()

    fun getPortfolio(){
        viewModelScope.launch {
            portfolioCrypto = CryptoRepository.portfolioCrypto.toList()

            when{
                portfolioCrypto.isEmpty() ->  {
                    CryptoRepository.addPortfolioCrypto(Portfolio("My Portfolio",listOf<Crypto>(),0.0,0.0,0.0,0.0,0.0))
                    state.value = PortfolioListState.NoDataError
                }
                else -> state.value = PortfolioListState.onSuccess
            }
        }
    }

    fun initPortfolio() {
        viewModelScope.launch {
            CryptoRepository.getPortfolioCrypto {
                getPortfolio()
            }
        }
    }



}
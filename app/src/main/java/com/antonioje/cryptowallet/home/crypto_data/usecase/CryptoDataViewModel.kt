package com.antonioje.cryptowallet.home.crypto_data.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonioje.cryptowallet.data.CryptoCurrency
import com.antonioje.cryptowallet.data.CryptoData
import com.antonioje.cryptowallet.utils.HttpUtil

class CryptoDataViewModel:ViewModel() {

    private var state = MutableLiveData<CryptoDataState>()




    fun getState(): MutableLiveData<CryptoDataState> {
        return state
    }

    fun getCrypto(cryptoID: String) {
        var url = "https://api.coingecko.com/api/v3/coins/" + cryptoID + "?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false&sparkline=false"

        var crypto = HttpUtil.instance?.getResponseData(url,CryptoData::class.java)

        if(crypto == null){
            state.value = CryptoDataState.NoDataError
        }else{
            state.value = CryptoDataState.Success(crypto)
        }
    }


}
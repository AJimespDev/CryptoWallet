package com.antonioje.cryptowallet.home.crypto_data.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonioje.cryptowallet.data.model.CryptoChange
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.utils.HttpUtil
import okhttp3.Request

class CryptoDataViewModel:ViewModel() {

    private var state = MutableLiveData<CryptoDataState>()




    fun getState(): MutableLiveData<CryptoDataState> {
        return state
    }

    fun getCrypto(cryptoID: String) {
        var url = "https://api.coingecko.com/api/v3/coins/" + cryptoID + "?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false"

        var crypto = HttpUtil.instance?.getResponseData(url, CryptoData::class.java)

        if(crypto == null){
            state.value = CryptoDataState.NoDataError
        }else{
            state.value = CryptoDataState.Success(crypto)
        }
    }

    fun getGraphicData(cryptoData: CryptoData): CryptoChange {
        var url = "https://api.coingecko.com/api/v3/coins/${cryptoData.id}/market_chart?vs_currency=eur&days=7"

        return HttpUtil.instance?.getResponseData(url, CryptoChange::class.java)!!
    }


}
package com.antonioje.cryptowallet.home.portfolio.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.model.CryptoTransaction
import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.data.repository.CryptoRepository
import com.antonioje.cryptowallet.utils.HttpUtil

class CryptoTransactionViewModel:ViewModel() {
    fun addTransaction(crypto: Crypto, transaction: CryptoTransaction) {
        var url = "https://api.coingecko.com/api/v3/coins/" + crypto.cryptoID + "?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false"
        var cryptoData = HttpUtil.instance?.getResponseData(url, CryptoData::class.java)

        CryptoRepository.addTransaction(cryptoData!!,transaction)
    }

    fun getCoinsFromCrypto(cryptoName: String): Double {
        val portfolio = CryptoRepository.portfolioCrypto
        val index =  portfolio.coinList.indexOfFirst { it.cryptoName == cryptoName }

        if(index != -1) {
            return portfolio.coinList[index].totalCoins
        }

        return 0.0
    }
}
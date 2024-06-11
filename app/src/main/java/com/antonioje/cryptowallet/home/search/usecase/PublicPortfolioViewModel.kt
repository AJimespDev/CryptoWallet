package com.antonioje.cryptowallet.home.search.usecase

import androidx.lifecycle.ViewModel
import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.data.repository.CryptoRepository

class PublicPortfolioViewModel: ViewModel() {

    fun updatePortfolio(portfolio: Portfolio): Portfolio {
        return CryptoRepository.actualizarPortfolio(portfolio)
    }
}
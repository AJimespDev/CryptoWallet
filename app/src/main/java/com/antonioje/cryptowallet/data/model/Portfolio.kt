package com.antonioje.cryptowallet.data.model

import java.util.Date

data class Portfolio(
    var name: String = "My Portfolio",
    var coinList: List<Crypto> = mutableListOf(),
    var totalValue: Double = 0.0,
    var valueChange24H: Double = 0.0,
    var valueChange24HPorcentage: Double = 0.0,
    var allTimePrice: Double = 0.0,
    var allTimePricePorcentage: Double = 0.0
) {
}

data class Crypto(
    var cryptoSymbol: String = "",
    var cryptoName: String = "",
    val image: Image = Image(),
    var currentPrice: Double = 0.0,
    var price_change_percentage_24h: Double = 0.0,
    var totalValue: Double = 0.0,
    var totalCoins: Double = 0.0,
    var initialCost: Double = 0.0,
    var averageCost: Double = 0.0,
    var profitOrLossPorcentage: Double = 0.0,
    var transactions: List<CryptoTransaction> = mutableListOf()
)

data class CryptoTransaction(
    val type: TRANSACTIONTYPE = TRANSACTIONTYPE.COMPRAR,
    val date: Date = Date(),
    val coinPrice: Double = 0.0,
    val coinCuantity: Double = 0.0,
    val cost: Double = 0.0
)

enum class TRANSACTIONTYPE{
    COMPRAR,
    VENDER
}
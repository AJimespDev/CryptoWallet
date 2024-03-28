package com.antonioje.cryptowallet.data.model

data class CryptoChange(
    val prices: List<List<Float>>,
    val market_caps: List<List<Float>>,
    val total_volumes: List<List<Float>>
)
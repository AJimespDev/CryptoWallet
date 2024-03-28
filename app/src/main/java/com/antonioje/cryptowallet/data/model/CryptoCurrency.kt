package com.antonioje.cryptowallet.data.model

import java.io.Serializable
import java.text.NumberFormat
import java.util.Locale

data class CryptoCurrency(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val market_cap: Long,
    val market_cap_rank: Int,
    val fully_diluted_valuation: Long?,
    val total_volume: Double,
    val high_24h: Double,
    val low_24h: Double,
    val price_change_24h: Double,
    val price_change_percentage_24h: Double,
    val market_cap_change_24h: Double,
    val market_cap_change_percentage_24h: Double,
    val circulating_supply: Double,
    val total_supply: Double,
    val max_supply: Double?,
    val ath: Double,
    val ath_change_percentage: Double,
    val ath_date: String,
    val atl: Double,
    val atl_change_percentage: Double,
    val atl_date: String,
    val roi: ROI?,
    val last_updated: String
):Serializable{
    companion object{
        fun formatPrice(price: Double): String {
            return if (price > 10) {
                String.format("%.2f", price)
            } else if (price < 10 && price > 1) {
                String.format("%.3f", price)
            } else if (price < 1 && price > 0.001) {
                String.format("%.4f", price)
            } else {
                String.format("%.8f", price)
            }

        }

         fun formatLargeNumber(number: Long): String {
            val suffixes = listOf("", "K", "M", "B", "T") // Sufijos para cada escala de magnitud
            var value = number.toDouble()
            var magnitude = 0

            while (value >= 1000) {
                value /= 1000
                magnitude++
            }

            // Limita la precisión a dos decimales
            val formattedValue = String.format("%.2f", value)

            return if (magnitude < suffixes.size) {
                "$formattedValue${suffixes[magnitude]}"
            } else {
                "Número demasiado grande"
            }
        }

        fun getFormattedNumber(number:Long):String{
            val numberFormat = NumberFormat.getNumberInstance(Locale("es", "ES"))
            numberFormat.minimumFractionDigits = 2
            numberFormat.maximumFractionDigits = 2

            return numberFormat.format(number)
        }


    }
}
data class ROI(
    val times: Double,
    val currency: String,
    val percentage: Double
)

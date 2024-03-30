package com.antonioje.cryptowallet.data.model

import java.io.Serializable
import java.text.NumberFormat
import java.util.Locale

data class CryptoCurrency(
    val id: String = "",
    val symbol: String = "",
    val name: String = "",
    val image: String = "",
    val current_price: Double = 0.0,
    val market_cap: Long = 0,
    val market_cap_rank: Int = 0,
    val fully_diluted_valuation: Long? = null,
    val total_volume: Double = 0.0,
    val high_24h: Double = 0.0,
    val low_24h: Double = 0.0,
    val price_change_24h: Double = 0.0,
    val price_change_percentage_24h: Double = 0.0,
    val market_cap_change_24h: Double = 0.0,
    val market_cap_change_percentage_24h: Double = 0.0,
    val circulating_supply: Double = 0.0,
    val total_supply: Double = 0.0,
    val max_supply: Double? = null,
    val ath: Double = 0.0,
    val ath_change_percentage: Double = 0.0,
    val ath_date: String = "",
    var favorite: Boolean = false,
    val atl: Double = 0.0,
    val atl_change_percentage: Double = 0.0,
    val atl_date: String = "",
    val roi: ROI? = null,
    val last_updated: String = ""
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
):Serializable

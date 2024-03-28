package com.antonioje.cryptowallet.data.model

data class Fiat(
    val usd: Double,
    val jpy: Double,
    val eur: Double
){
    companion object{
    fun formatDouble(value: Double): String {
        return if (value % 1 == 0.0) {
            value.toInt().toString() // Si es un número entero, devuelve solo la parte entera
        } else {
            String.format("%.2f", value) // Si tiene decimales, muestra dos decimales
        }
    }
}}
package com.antonioje.cryptowallet.data.repository

import android.util.Log
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.model.CryptoTransaction
import com.antonioje.cryptowallet.data.model.Portfolio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CryptoRepository private constructor() {


    companion object {
        private val db = FirebaseFirestore.getInstance()

        var favouritesCrypto = mutableSetOf<CryptoCurrency>()
        var portfolioCrypto = Portfolio()

        fun addFavouriteCrypto(crypto: CryptoCurrency) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let { userEmail ->
                val userFavoritesRef = db.collection("users").document(userEmail)
                    .collection("favourites").document(crypto.name)
                userFavoritesRef.set(crypto)
                    .addOnSuccessListener {
                        favouritesCrypto.add(crypto)
                        Log.d("TAG", "Favorite added to Firestore for user: $userEmail")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding favorite to Firestore for user: $userEmail", e)
                    }
            }
        }

        fun addFavouriteCrypto(crypto: CryptoData) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let { userEmail ->

                val cryptoCurrency = CryptoCurrency(
                    crypto.id,
                    crypto.symbol,
                    crypto.name,
                    crypto.image.large,
                    crypto.market_data.market_cap.eur.toLong(),
                    crypto.market_data.current_price.eur,
                    crypto.market_data.price_change_percentage_24h,
                    crypto.market_cap_rank
                )

                val userFavoritesRef = db.collection("users").document(userEmail)
                    .collection("favourites").document(cryptoCurrency.name)
                userFavoritesRef.set(cryptoCurrency)
                    .addOnSuccessListener {
                        favouritesCrypto.add(cryptoCurrency)
                        Log.d("TAG", "Favorite added to Firestore for user: $userEmail")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding favorite to Firestore for user: $userEmail", e)
                    }
            }
        }


        fun getFavouritesCryptos(onSuccess: () -> Unit) {
            favouritesCrypto = mutableSetOf()
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let { userEmail ->
                db.collection("users").document(userEmail)
                    .collection("favourites")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val crypto = document.toObject(CryptoCurrency::class.java)
                            favouritesCrypto.add(crypto)
                            addFavouriteCrypto(crypto)
                        }
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        // FALLA EL OBTENER LISTA
                    }
            }
        }

        fun getPortfolioCrypto(onSuccess: () -> Unit) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let { userEmail ->
                db.collection("users").document(userEmail)
                    .collection("portfolio")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val portfolio = document.toObject(Portfolio::class.java)
                            portfolioCrypto = portfolio
                        }
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->

                    }
            }
        }

        fun addPortfolioCrypto(portfolio: Portfolio) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let { userEmail ->
                val userPortfolioRef = db.collection("users").document(userEmail)
                    .collection("portfolio").document(portfolio.name)
                    userPortfolioRef.set(portfolio)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e ->

                    }
            }
        }


        fun isCryptoFavourite(name: String): Boolean {
            return favouritesCrypto.map { it.name }.contains(name)
        }

        fun deleteFavouriteCrypto(crypto: String, onDelete: () -> Unit) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email.let { userEmail ->
                val userFavoritesRef = db.collection("users").document(userEmail.toString())
                    .collection("favourites").document(crypto)

                userFavoritesRef.delete()
                    .addOnSuccessListener {
                        favouritesCrypto.removeIf { it.name == crypto}
                        onDelete()
                        Log.d("TAG", "Favorite deleted from Firestore for user: $currentUser")
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "TAG",
                            "Error deleting favorite from Firestore for user: $currentUser",
                            e
                        )
                    }
            }

        }

        fun addTransaction(crypto: CryptoData, transaction: CryptoTransaction) {
            var portfolio = portfolioCrypto

            val index = portfolio.coinList.indexOfFirst { it.cryptoSymbol == crypto.symbol }

            if (index != -1) {
                // Si el Crypto ya existe en la lista, añadir la transacción al Crypto existente
                val existingCrypto = portfolio.coinList[index]
                val updatedCrypto = existingCrypto.copy(
                    transactions = existingCrypto.transactions.toMutableList().apply { add(transaction) }
                )
                portfolio =   portfolio.copy(
                    coinList = portfolio.coinList.toMutableList().apply { set(index, updatedCrypto) }
                )
            } else {
                // Si el Crypto no existe en la lista, añadir un nuevo Crypto con la transacción
                val newCrypto = Crypto(cryptoSymbol = crypto.symbol, cryptoName = crypto.name , image = crypto.image,
                    price_change_percentage_24h = crypto.market_data.price_change_percentage_24h  ,
                    transactions = mutableListOf(transaction), totalCoins = transaction.coinCuantity,
                    totalValue = (transaction.coinCuantity * crypto.market_data.current_price.eur),
                    initialCost = transaction.cost)
                newCrypto.averageCost = newCrypto.totalValue / newCrypto.totalCoins
                newCrypto.profitOrLossPorcentage = ((newCrypto.totalValue - newCrypto.initialCost) / newCrypto.initialCost) * 100
                newCrypto.totalCoins = transaction.coinCuantity

                portfolio = portfolio.copy(
                    coinList = portfolio.coinList.toMutableList().apply { add(newCrypto) }
                )

                portfolio.totalValue += newCrypto.totalValue

            }
            addPortfolioCrypto(portfolio)
        }
    }
}
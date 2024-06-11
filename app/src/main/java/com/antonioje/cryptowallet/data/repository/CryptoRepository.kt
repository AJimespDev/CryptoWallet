package com.antonioje.cryptowallet.data.repository

import android.util.Log
import com.antonioje.cryptowallet.data.enum.TRANSACTIONTYPE
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.model.CryptoTransaction
import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.utils.HttpUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CryptoRepository private constructor() {


    companion object {
        private val db = FirebaseFirestore.getInstance()

        var favouritesCrypto = mutableSetOf<CryptoCurrency>()
        var allPublicPortfolios = mutableSetOf<Portfolio>()
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

        fun getAllPortfolios(onSuccess: () -> Unit) {
            allPublicPortfolios = mutableSetOf()

            db.collection("portfolios")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val portfolio = document.toObject(Portfolio::class.java)
                        allPublicPortfolios.add(portfolio)
                    }
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    // FALLA EL OBTENER LISTA
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
                        actualizarPortfolio()
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
                    .collection("portfolio").document("My Portfolio")
                userPortfolioRef.set(portfolio)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e ->

                    }
            }
        }

        private fun addPublicPortfolioCrypto(portfolio: Portfolio) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let { userEmail ->
                val userPortfolioRef = db.collection("portfolios").document(userEmail)
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
                        favouritesCrypto.removeIf { it.name == crypto }
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

        private fun deletePublicPortfolioCrypto() {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email.let { userEmail ->
                val userFavoritesRef = db.collection("portfolios").document(userEmail.toString())
                userFavoritesRef.delete()
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "TAG",
                            "Error deleting your public portfolio from Firestore for user: $currentUser",
                            e
                        )
                    }
            }
        }


        fun addTransaction(crypto: CryptoData, transaction: CryptoTransaction) {
            getPortfolioCrypto {
                val index = portfolioCrypto.coinList.indexOfFirst { it.cryptoSymbol == crypto.symbol }

                if (index != -1) {
                    // Si el Crypto ya existe en la lista, añadir la transacción al Crypto existente
                    val existingCrypto = portfolioCrypto.coinList[index]
                    val newTransactions = existingCrypto.transactions.toMutableList()
                    newTransactions.add(transaction)
                    existingCrypto.transactions = newTransactions
                    existingCrypto.cryptoID = crypto.id
                    existingCrypto.currentPrice = crypto.market_data.current_price.eur
                    existingCrypto.price_change_percentage_24h = crypto.market_data.price_change_percentage_24h

                    if (transaction.type == TRANSACTIONTYPE.VENDER) {
                        // Ajustar cantidad total de monedas
                        val coinsSold = transaction.coinCuantity
                        existingCrypto.totalCoins -= coinsSold

                        // Calcular el totalValue basado en la cantidad restante de monedas y el precio actual
                        existingCrypto.totalValue = existingCrypto.currentPrice * existingCrypto.totalCoins

                        val allVentasProfit = existingCrypto.transactions.filter { it.type == TRANSACTIONTYPE.VENDER }.sumByDouble { it.cost }
                        existingCrypto.profitOrLossMoney = (existingCrypto.totalValue - existingCrypto.initialCost) + allVentasProfit

                        existingCrypto.profitOrLossPorcentage =
                            if (existingCrypto.initialCost > 0) {
                                (existingCrypto.profitOrLossMoney * 100) / existingCrypto.initialCost
                            } else 0.0

                        portfolioCrypto.coinList = portfolioCrypto.coinList.toMutableList().apply { set(index, existingCrypto) }

                    } else {
                        existingCrypto.initialCost += transaction.cost
                        existingCrypto.totalCoins += transaction.coinCuantity
                        existingCrypto.averageCost = existingCrypto.transactions.map { it.coinPrice }.average()
                        existingCrypto.totalValue = existingCrypto.currentPrice * existingCrypto.totalCoins

                        existingCrypto.profitOrLossPorcentage =
                            if (existingCrypto.initialCost > 0) {
                                (existingCrypto.profitOrLossMoney * 100) / existingCrypto.initialCost
                            } else 0.0


                        portfolioCrypto.coinList = portfolioCrypto.coinList.toMutableList().apply { set(index, existingCrypto) }
                    }
                } else {
                    // Si el Crypto no existe en la lista, añadir un nuevo Crypto con la transacción
                    val newCrypto = Crypto(
                        cryptoSymbol = crypto.symbol,
                        cryptoName = crypto.name,
                        image = crypto.image,
                        price_change_percentage_24h = crypto.market_data.price_change_percentage_24h,
                        transactions = mutableListOf(transaction),
                        totalCoins = transaction.coinCuantity,
                        totalValue = transaction.cost,
                        initialCost = transaction.cost
                    )
                    newCrypto.cryptoID = crypto.id
                    newCrypto.currentPrice = crypto.market_data.current_price.eur
                    newCrypto.averageCost = transaction.coinPrice
                    newCrypto.profitOrLossPorcentage = 0.0
                    newCrypto.profitOrLossMoney = 0.0

                    portfolioCrypto.coinList = portfolioCrypto.coinList.toMutableList().apply { add(newCrypto) }
                }

                portfolioCrypto.totalValue = portfolioCrypto.coinList.sumByDouble { it.totalValue }
                portfolioCrypto.allTimePrice = portfolioCrypto.coinList.sumByDouble { it.initialCost }
                portfolioCrypto.profitOrLossMoney = portfolioCrypto.coinList.map { it.profitOrLossMoney }.sum()
                portfolioCrypto.profitOrLossPorcentage = portfolioCrypto.coinList.map { it.profitOrLossPorcentage}.sum()

                addPortfolioCrypto(portfolioCrypto)

                if (portfolioCrypto.visibilityPublic) {
                    addPublicPortfolioCrypto(portfolioCrypto)
                }
            }
        }


        fun actualizarPortfolio() {
            for (crypto in portfolioCrypto.coinList) {
                val newCrypto = getCurrentCrypto(crypto.cryptoID)
                crypto.currentPrice = newCrypto.market_data.current_price.eur
                crypto.price_change_percentage_24h =
                    newCrypto.market_data.price_change_percentage_24h
                crypto.totalValue = crypto.currentPrice * crypto.totalCoins

                val allVentasProfit = crypto.transactions.filter { it.type == TRANSACTIONTYPE.VENDER }.sumByDouble { it.cost }
                crypto.profitOrLossMoney = (crypto.totalValue - crypto.initialCost) + allVentasProfit

                crypto.profitOrLossPorcentage =
                    if (crypto.initialCost > 0) {
                        (crypto.profitOrLossMoney * 100) / crypto.initialCost
                    } else 0.0
            }

            portfolioCrypto.totalValue = portfolioCrypto.coinList.sumByDouble { it.totalValue }
            portfolioCrypto.allTimePrice = portfolioCrypto.coinList.sumByDouble { it.initialCost }
            portfolioCrypto.profitOrLossMoney = portfolioCrypto.coinList.map { it.profitOrLossMoney }.sum()
            portfolioCrypto.profitOrLossPorcentage = portfolioCrypto.coinList.map { it.profitOrLossPorcentage}.sum()
        }

        fun actualizarPortfolio(portfolio:Portfolio): Portfolio {
            for (crypto in portfolio.coinList) {
                val newCrypto = getCurrentCrypto(crypto.cryptoID)
                crypto.currentPrice = newCrypto.market_data.current_price.eur
                crypto.price_change_percentage_24h =
                    newCrypto.market_data.price_change_percentage_24h
                crypto.totalValue = crypto.currentPrice * crypto.totalCoins

                val allVentasProfit = crypto.transactions.filter { it.type == TRANSACTIONTYPE.VENDER }.sumByDouble { it.cost }
                crypto.profitOrLossMoney = (crypto.totalValue - crypto.initialCost) + allVentasProfit

                crypto.profitOrLossPorcentage =
                    if (crypto.initialCost > 0) {
                        (crypto.profitOrLossMoney * 100) / crypto.initialCost
                    } else 0.0


            }

            portfolio.totalValue = portfolio.coinList.sumByDouble { it.totalValue }
            portfolio.allTimePrice = portfolio.coinList.sumByDouble { it.initialCost }
            portfolio.profitOrLossMoney = portfolio.coinList.map { it.profitOrLossMoney }.sum()
            portfolio.profitOrLossPorcentage = portfolio.coinList.map { it.profitOrLossPorcentage}.sum()

            return portfolio
        }

        fun addNewPortfolioConfig(newName: String, public: Boolean) {
            getPortfolioCrypto {
                portfolioCrypto.name = newName

                if (portfolioCrypto.visibilityPublic && !public) {
                    deletePublicPortfolioCrypto()
                }

                portfolioCrypto.visibilityPublic = public
                if (portfolioCrypto.visibilityPublic) {
                    addPublicPortfolioCrypto(portfolioCrypto)
                }

                addPortfolioCrypto(portfolioCrypto)
            }
        }

        fun getCurrentCrypto(cryptoID: String): CryptoData {
            var url =
                "https://api.coingecko.com/api/v3/coins/" + cryptoID + "?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false"
            var crypto = HttpUtil.instance?.getResponseData(url, CryptoData::class.java)

            return crypto!!
        }


    }
}
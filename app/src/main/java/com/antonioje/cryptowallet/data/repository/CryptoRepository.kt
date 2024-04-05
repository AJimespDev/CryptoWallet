package com.antonioje.cryptowallet.data.repository

import android.util.Log
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CryptoRepository private constructor() {


    companion object {
        private val db = FirebaseFirestore.getInstance()

        var favouritesCrypto = mutableSetOf<CryptoCurrency>()


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

        fun isCryptoFavourite(id: String): Boolean {
            return favouritesCrypto.map { it.id }.contains(id)
        }

        fun deleteFavouriteCrypto(crypto: CryptoCurrency, onDelete: () -> Unit) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email.let { userEmail ->
                val userFavoritesRef = db.collection("users").document(userEmail.toString())
                    .collection("favourites").document(crypto.name)

                userFavoritesRef.delete()
                    .addOnSuccessListener {
                        favouritesCrypto.removeIf { it.id == crypto.id }
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

        fun deleteFavouriteCrypto(crypto: CryptoData, onDelete: () -> Unit) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email.let { userEmail ->
                val userFavoritesRef = db.collection("users").document(userEmail.toString())
                    .collection("favourites").document(crypto.name)

                userFavoritesRef.delete()
                    .addOnSuccessListener {
                        favouritesCrypto.removeIf { it.id == crypto.id }
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


    }
}
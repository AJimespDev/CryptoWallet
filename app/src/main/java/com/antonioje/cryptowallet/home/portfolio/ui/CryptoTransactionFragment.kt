package com.antonioje.cryptowallet.home.portfolio.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.model.CryptoTransaction
import com.antonioje.cryptowallet.databinding.FragmentCryptoTransactionBinding
import com.antonioje.cryptowallet.home.portfolio.adapter.CryptoTransactionAdapter
import com.squareup.picasso.Picasso


class CryptoTransactionFragment : Fragment() {
    private var _binding:FragmentCryptoTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var crypto: Crypto
    private lateinit var _adapter: CryptoTransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCryptoTransactionBinding.inflate(inflater,container,false)

        crypto = requireArguments().getSerializable(CryptoData.CRYPTO_KEY) as Crypto

        initVariables()
        return binding.root
    }

    private fun initVariables() {
        with(binding){
            _adapter = CryptoTransactionAdapter (crypto.cryptoSymbol) {onClick(it) }
            recyclerView.adapter = _adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            _adapter.submitList(crypto.transactions)

            Picasso.get().load(crypto.image.large).into(imCryptoTransaction)
            tvCryptoName.text = crypto.cryptoName
            tvTotalCoinsTransaction.text = crypto.totalCoins.toString()
            tvTotalCostTransaction.text = crypto.initialCost.toString() + "€"

            tvTotalValueTransaction.text = (crypto.currentPrice * crypto.totalCoins).toString()  + "€"
            tvAverageCostTransaction.text = crypto.transactions.map { it.coinPrice }.average().toString() + "€"
            tvCoinSymbol.text = crypto.cryptoSymbol.toUpperCase()

            val profitOrLossMoney = (crypto.currentPrice * crypto.totalCoins) - crypto.initialCost

            if(profitOrLossMoney > 0){
                tvAllTimeProfitLossTransaction.text = CryptoCurrency.formatPrice(profitOrLossMoney) + "€"
            }else if(profitOrLossMoney < 0){
                tvAllTimeProfitLossTransaction.text = CryptoCurrency.formatPrice(profitOrLossMoney) + "€"
            }else{
                tvAllTimeProfitLossTransaction.text = "0€"
            }
        }
    }

    private fun onClick(cryptoTransaction: CryptoTransaction) {

    }


}
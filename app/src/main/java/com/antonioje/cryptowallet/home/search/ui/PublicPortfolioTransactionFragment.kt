package com.antonioje.cryptowallet.home.search.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.model.CryptoTransaction
import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.databinding.FragmentPublicPortfolioTransactionBinding
import com.antonioje.cryptowallet.home.portfolio.adapter.CryptoTransactionAdapter
import com.squareup.picasso.Picasso

class PublicPortfolioTransactionFragment : Fragment() {
   private var _binding:FragmentPublicPortfolioTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var _adapter: CryptoTransactionAdapter
    private lateinit var crypto: Crypto

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublicPortfolioTransactionBinding.inflate(inflater,container,false)
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

            if(crypto.totalCoins > 1000000.0){
                tvTotalCoinsTransaction.text = String.format("%s",
                    CryptoCurrency.formatLargeNumber(crypto.totalCoins.toLong())
                )
            } else if(crypto.totalCoins >= 1.0){
                tvTotalCoinsTransaction.text = String.format("%.2f",
                    crypto.totalCoins)
            } else {
                tvTotalCoinsTransaction.text = String.format("%s",
                    CryptoCurrency.formatPrice(crypto.totalCoins)
                )
            }

            tvTotalCostTransaction.text = String.format("%.2f€", crypto.initialCost)
            tvTotalValueTransaction.text =    String.format("%.2f€", crypto.currentPrice * crypto.totalCoins)
            tvAverageCostTransaction.text =  String.format("%s€", CryptoCurrency.formatPrice(crypto.transactions.map { it.coinPrice }.average()))
            tvCoinSymbol.text = crypto.cryptoSymbol.toUpperCase()

            val profitOrLossMoney = (crypto.currentPrice * crypto.totalCoins) - crypto.initialCost

            if(profitOrLossMoney >= 0){
                tvAllTimeProfitLossTransaction.text =  String.format("%.2f€",profitOrLossMoney)
                tvAllTimeProfitLossTransaction.setTextColor(Color.GREEN)
                imvLast24H.setImageResource(R.drawable.icon_last24h_up)
                tvAllTimeProfitLossTransactionPorcentage.text = String.format("%.2f%%",crypto.profitOrLossPorcentage).replace("+","")
                tvAllTimeProfitLossTransactionPorcentage.setTextColor(Color.GREEN)
            } else {
                tvAllTimeProfitLossTransaction.text =  String.format("%.2f€",profitOrLossMoney)
                tvAllTimeProfitLossTransaction.setTextColor(Color.RED)
                imvLast24H.setImageResource(R.drawable.icon_last24h_down)
                tvAllTimeProfitLossTransactionPorcentage.text = String.format("%.2f%%",crypto.profitOrLossPorcentage).replace("-","")
                tvAllTimeProfitLossTransactionPorcentage.setTextColor(Color.RED)
            }
        }
    }

    private fun onClick(it: CryptoTransaction) {

    }

}
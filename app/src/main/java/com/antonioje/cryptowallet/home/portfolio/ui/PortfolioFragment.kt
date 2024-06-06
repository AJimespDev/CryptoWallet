package com.antonioje.cryptowallet.home.portfolio.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.databinding.FragmentPortfolioBinding
import com.antonioje.cryptowallet.home.portfolio.adapter.PortfolioAdapter
import com.antonioje.cryptowallet.home.portfolio.usecase.PortfolioListState
import com.antonioje.cryptowallet.home.portfolio.usecase.PortfolioViewModel
import java.net.URL
import org.json.JSONObject

class PortfolioFragment : Fragment() {
    private var _binding:FragmentPortfolioBinding? = null
    private val binding get() = _binding!!
    private val _viewmodel:PortfolioViewModel by viewModels()
    private lateinit var _adapter:PortfolioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPortfolioBinding.inflate(inflater,container,false)
        initVariables()
        return binding.root
    }

    private fun initVariables() {
        _adapter = PortfolioAdapter { onClick(it) }

        with(binding.rvCryptos) {
            adapter = _adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        _viewmodel.getState().observe(viewLifecycleOwner, Observer {
            when(it){
                PortfolioListState.NoDataError -> showNoData()
                is PortfolioListState.Loading -> showLoading(it.value)
                else -> {
                    _adapter.submitList(_viewmodel.portfolioCrypto.coinList)
                    onSuccess(_viewmodel.portfolioCrypto)
                }
            }
        })

        _viewmodel.initPortfolio()
    }

    private fun showLoading(value: Boolean) {
        if(value){
            binding.cardViewPortfolio.visibility = View.GONE
            binding.llTexts.visibility = View.GONE
            binding.rvCryptos.visibility = View.GONE
            binding.portfolioLottie.visibility = View.VISIBLE
        }else{
            binding.cardViewPortfolio.visibility = View.VISIBLE
            binding.llTexts.visibility = View.VISIBLE
            binding.rvCryptos.visibility = View.VISIBLE
            binding.portfolioLottie.visibility = View.GONE
        }
    }

    private fun onClick(crypto: Crypto) {
        var bundle = Bundle()
        bundle.putSerializable(CryptoData.CRYPTO_KEY,crypto)

        findNavController().navigate(R.id.action_portfolioFragment_to_cryptoTransactionFragment,bundle)
    }

    private fun onSuccess(portfolio: Portfolio) {
        with(binding){
            tvPortfolioName.text = portfolio.name
            tvPortfolioTotalPrice.text =  String.format("%.2f€", portfolio.totalValue)
            if(portfolio.allTimePrice >= 0){
                tvPortfolioAllProfit.text = String.format("+%.2f€",portfolio.totalValue - portfolio.allTimePrice)
                tvPortfolioAllProfit.setTextColor(Color.GREEN)
                imvLast24H.setImageResource(R.drawable.icon_last24h_up)
                tvPortfolioAllProfitPorcentage.text = String.format("%.2f%%",portfolio.allTimePricePorcentage)
                tvPortfolioAllProfitPorcentage.setTextColor(Color.GREEN)
            }else{
                tvPortfolioAllProfit.text = String.format("-%.2f€",portfolio.totalValue - portfolio.allTimePrice)
                tvPortfolioAllProfit.setTextColor(Color.RED)
                imvLast24H.setImageResource(R.drawable.icon_last24h_down)
                tvPortfolioAllProfitPorcentage.text = String.format("%.2f%%",portfolio.allTimePricePorcentage)
                tvPortfolioAllProfitPorcentage.setTextColor(Color.RED)
            }

            tvListMonedas.setOnClickListener {

                _adapter.sortByName()
            }

            tvListLast24h.setOnClickListener {
                _adapter.sortBy24H()
            }

            tvTotalPrice.setOnClickListener {
                _adapter.sortByTotal()
            }
        }
    }

    private fun showNoData() {
        Toast.makeText(requireContext(),"NO DATA TOAST",Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
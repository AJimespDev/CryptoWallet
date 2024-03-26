package com.antonioje.cryptowallet.home.crypto_data.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.antonioje.cryptowallet.MainActivity
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.CryptoCurrency
import com.antonioje.cryptowallet.data.CryptoData
import com.antonioje.cryptowallet.data.enum.DIVISAS
import com.antonioje.cryptowallet.databinding.FragmentCryptoDataBinding
import com.antonioje.cryptowallet.home.crypto_data.usecase.CryptoDataState
import com.antonioje.cryptowallet.home.crypto_data.usecase.CryptoDataViewModel
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale


class CryptoDataFragment : Fragment() {
    private var _binding : FragmentCryptoDataBinding ? = null
    private val binding get() = _binding!!
    private val _viewmodel : CryptoDataViewModel by viewModels()
    private lateinit var cryptoCurrency:CryptoCurrency


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCryptoDataBinding.inflate(inflater,container,false)

        initVariables()
        return binding.root
    }

    private fun initVariables() {

        cryptoCurrency = requireArguments().getSerializable(CryptoData.CRYPTO_KEY) as CryptoCurrency

        _viewmodel.getState().observe(viewLifecycleOwner, Observer {
            when(it){
                CryptoDataState.NoDataError -> showNoData()
                is CryptoDataState.Success -> onSuccess(it.data)
            }
        })

        var divisas = DIVISAS.values()
        var adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,divisas)
        binding.spCryptoDataFiat.adapter = adapter

        _viewmodel.getCrypto(cryptoCurrency.id)
    }



    private fun showNoData() {

    }

    private fun onSuccess(data: CryptoData) {
        initLayout(data)
    }

    private fun initLayout(data: CryptoData) {


        with(binding){
            tvCryptoName.text = data.name
            tvCryptoSymbol.text = cryptoCurrency.symbol.toUpperCase()
            tvCryptoDataRank.text = "#" + cryptoCurrency.market_cap_rank.toString()
            tvCryptoPrice.text = CryptoCurrency.formatPrice(cryptoCurrency.current_price)
            tvCapitalization.text =  CryptoCurrency.getFormattedNumber(cryptoCurrency.market_cap) + "€"


            Picasso.get().load(cryptoCurrency.image).into(ivCryptoImage)
            Picasso.get().load(cryptoCurrency.image).into(ivCryptoDataConverter)

            if (cryptoCurrency.price_change_percentage_24h >= 0) {
                tvCryptoDataLast24h.setTextColor(Color.GREEN)
                ivCryptoDataLast24h.setImageResource(R.drawable.icon_last24h_up)

            } else {
                tvCryptoDataLast24h.setTextColor(Color.RED)
                ivCryptoDataLast24h.setImageResource(R.drawable.icon_last24h_down)
            }
            tvCryptoDataLast24h.text =
                String.format("%.2f", cryptoCurrency.price_change_percentage_24h).replace("-", "") + "%"



            if (cryptoCurrency.market_cap_change_percentage_24h >= 0) {
                tvCapitalizationLast24h.setTextColor(Color.GREEN)
                ivCapitalizationLast24h.setImageResource(R.drawable.icon_last24h_up)

            } else {
                tvCapitalizationLast24h.setTextColor(Color.RED)
                ivCapitalizationLast24h.setImageResource(R.drawable.icon_last24h_down)
            }
            tvCapitalizationLast24h.text =
                String.format("%.2f", cryptoCurrency.market_cap_change_percentage_24h).replace("-", "") + "%"



        }

    }




}
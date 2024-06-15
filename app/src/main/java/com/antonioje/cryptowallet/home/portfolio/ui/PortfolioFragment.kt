package com.antonioje.cryptowallet.home.portfolio.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

class PortfolioFragment : Fragment() {
    private var _binding:FragmentPortfolioBinding? = null
    private val binding get() = _binding!!
    private val _viewmodel:PortfolioViewModel by viewModels()
    private lateinit var _adapter:PortfolioAdapter
    private lateinit var portfolio:Portfolio

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


        binding.imvConfigPortfolio.setOnClickListener {
            showConfigDialog()
        }

        binding.imvSeeGraphic.setOnClickListener {
            showGraphicDialog(portfolio)
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

    private fun showGraphicDialog(portfolio: Portfolio) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.alert_portfolio_graphic, null)
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.uni_sans)

        builder.setView(dialogView)
        val alertDialog = builder.create()

        val pieChart = dialogView.findViewById<PieChart>(R.id.pieChart)
        pieChart.setEntryLabelTypeface(typeface)
        pieChart.setCenterTextTypeface(typeface)

        val entries = portfolio.coinList.map { crypto ->
            PieEntry(crypto.totalValue.toFloat(), crypto.cryptoSymbol.toUpperCase())
        }

        val totalValue = entries.sumByDouble { it.value.toDouble() }
        val dataSet = PieDataSet(entries, "Crypto Distribution")
        dataSet.colors = generateColors()
        dataSet.setDrawValues(false)

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false

        pieChart.setDrawCenterText(true)
        pieChart.centerText = "Selecciona un segmento"
        pieChart.setEntryLabelColor(Color.TRANSPARENT)

        pieChart.isRotationEnabled = false
        pieChart.isHighlightPerTapEnabled = true

        val legend = pieChart.legend
        legend.form = Legend.LegendForm.CIRCLE
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.isEnabled = true


        val legendEntries = entries.mapIndexed { index, entry ->
            val percentage = entry.value / totalValue * 100
            LegendEntry().apply {
                formColor = dataSet.getColor(index)
                label = "${entry.label} (${String.format("%.2f", percentage)}%)"
            }
        }

        legend.setCustom(legendEntries)
        legend.typeface = typeface

        val decimalFormat = DecimalFormat("#.##")

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(entry: Entry?, highlight: Highlight?) {
                if (highlight != null) {
                    val index = highlight.x.toInt()
                    if (index >= 0 && index < entries.size) {
                        val selectedEntry = entries[index]
                        val selectedCrypto = portfolio.coinList[index] // Obtener la criptomoneda seleccionada
                        val totalValue = decimalFormat.format(selectedCrypto.totalValue) // Redondear totalValue a dos decimales

                        pieChart.centerText = "${selectedEntry.label}\n${totalValue}€"
                    }
                }
            }

            override fun onNothingSelected() {
                pieChart.centerText = "Selecciona un segmento"
            }
        })

        alertDialog.show()
    }
    private fun generateColors(): List<Int> {
        return listOf(
            Color.rgb(244, 164, 96),  // Pastel orange
            Color.rgb(176, 224, 230), // Light blue
            Color.rgb(240, 128, 128), // Light coral
            Color.rgb(216, 191, 216), // Thistle
            Color.rgb(255, 182, 193), // Light pink
            Color.rgb(255, 228, 181), // Moccasin
            Color.rgb(221, 160, 221), // Plum
            Color.rgb(144, 238, 144), // Light green
            Color.rgb(173, 216, 230), // Light blue
            Color.rgb(255, 239, 213), // Papaya whip
            Color.rgb(211, 211, 211), // Light gray
            Color.rgb(255, 218, 185), // Peach puff
            Color.rgb(230, 230, 250), // Lavender
            Color.rgb(250, 235, 215), // Antique white
            Color.rgb(240, 255, 240), // Honeydew
            Color.rgb(255, 250, 205), // Lemon chiffon
            Color.rgb(245, 245, 220), // Beige
            Color.rgb(255, 228, 225), // Misty rose
            Color.rgb(245, 222, 179), // Wheat
            Color.rgb(224, 255, 255), // Light cyan
            Color.rgb(255, 228, 196), // Bisque
            Color.rgb(255, 240, 245), // Lavender blush
            Color.rgb(220, 220, 220), // Gainsboro
            Color.rgb(248, 248, 255), // Ghost white
            Color.rgb(255, 235, 205), // Blanched almond
            Color.rgb(240, 248, 255), // Alice blue
            Color.rgb(255, 250, 240), // Floral white
            Color.rgb(253, 245, 230), // Old lace
            Color.rgb(255, 245, 238)  // Seashell
        )
    }

    private fun showConfigDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.alert_config_portfolio, null)
        var public = portfolio.visibilityPublic

        builder.setView(dialogView)
        val alertDialog = builder.create()

        alertDialog.setCancelable(false)

        builder.setView(dialogView)

        val tvCancelAlert = dialogView.findViewById<TextView>(R.id.tvPortfolioConfigCancelAlert)
        val edtName = dialogView.findViewById<EditText>(R.id.edtPortfolioName)
        val rdPrivate = dialogView.findViewById<RadioButton>(R.id.privateRadioButton)
        val rdPublic = dialogView.findViewById<RadioButton>(R.id.publicRadioButton)
        val btnAddNewConfig = dialogView.findViewById<Button>(R.id.btnAddNewConfig)

        val colorAmarillo = ContextCompat.getColor(requireContext(), R.color.yellow)
        rdPrivate.buttonTintList = ColorStateList.valueOf(colorAmarillo)
        rdPublic.buttonTintList = ColorStateList.valueOf(colorAmarillo)

        edtName.setText(portfolio.name)
        if(portfolio.visibilityPublic){
            rdPublic.isChecked = true
            rdPrivate.isChecked = false
        } else {
            rdPrivate.isChecked = true
            rdPublic.isChecked = false
        }

        rdPrivate.setOnClickListener {
            public = false
        }

        rdPublic.setOnClickListener {
            public = true
        }

        tvCancelAlert.setOnClickListener{
            alertDialog.dismiss()
        }


        btnAddNewConfig.setOnClickListener {
            if(public && _viewmodel.portfolioNameAlreadyExist(edtName.text.toString(), portfolio)) {
                edtName.setError(getString(R.string.errNameAlreadyExists))
                edtName.requestFocus()
            } else {
                _viewmodel.addNewPortfolioConfig(edtName.text.toString(), public)
                initVariables()
                alertDialog.dismiss()
            }
        }


        alertDialog.show()
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
        this.portfolio = portfolio
        with(binding){
            tvPortfolioName.text = portfolio.name
            tvPortfolioTotalPrice.text =  String.format("%.2f€", portfolio.totalValue)

            cardViewPortfolio.setOnLongClickListener {
                mostrarDialogoConfirmacion()
                true
            }

            if(portfolio.profitOrLossPorcentage > 0) {
                imvLast24H.visibility = View.VISIBLE
                tvPortfolioAllProfitPorcentage.visibility = View.VISIBLE
                imvLast24H.setImageResource(R.drawable.icon_last24h_up)
                tvPortfolioAllProfitPorcentage.text = String.format("%.2f%%",portfolio.profitOrLossPorcentage).replace("+","")
                tvPortfolioAllProfitPorcentage.setTextColor(Color.GREEN)
            } else {
                imvLast24H.visibility = View.VISIBLE
                tvPortfolioAllProfitPorcentage.visibility = View.VISIBLE
                imvLast24H.setImageResource(R.drawable.icon_last24h_down)
                tvPortfolioAllProfitPorcentage.text = String.format("%.2f%%",portfolio.profitOrLossPorcentage).replace("-","")
                tvPortfolioAllProfitPorcentage.setTextColor(Color.RED)
            }

            if(portfolio.profitOrLossMoney > 0){
                imvSeeGraphic.visibility = View.VISIBLE
                tvPortfolioAllProfit.text = String.format("%.2f€",portfolio.profitOrLossMoney)
                tvPortfolioAllProfit.setTextColor(Color.GREEN)
            } else if(portfolio.totalValue == portfolio.allTimePrice){
                tvPortfolioAllProfit.text = String.format("%.2f€",portfolio.totalValue - portfolio.allTimePrice)
                if (portfolio.allTimePrice == 0.0) {
                    imvSeeGraphic.visibility = View.GONE
                }
                imvLast24H.visibility = View.GONE
                tvPortfolioAllProfitPorcentage.visibility = View.GONE
            } else {
                imvSeeGraphic.visibility = View.VISIBLE
                tvPortfolioAllProfit.text = String.format("%.2f€",portfolio.profitOrLossMoney)
                tvPortfolioAllProfit.setTextColor(Color.RED)

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
        Toast.makeText(requireContext(),"NO DATA",Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoConfirmacion() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.DeleteAlertTitle))
        builder.setMessage(getString(R.string.DeleteAlertMessage))

        builder.setPositiveButton(getString((R.string.DeleteAlertPossitive))) { dialog, which ->
            _viewmodel.resetPortfolio()
        }

        builder.setNegativeButton(getString((R.string.DeleteAlertNegative))) { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
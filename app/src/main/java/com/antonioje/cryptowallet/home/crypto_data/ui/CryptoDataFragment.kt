package com.antonioje.cryptowallet.home.crypto_data.ui

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.enum.DIVISAS
import com.antonioje.cryptowallet.data.enum.TRANSACTIONTYPE
import com.antonioje.cryptowallet.data.model.CryptoTransaction
import com.antonioje.cryptowallet.databinding.FragmentCryptoDataBinding
import com.antonioje.cryptowallet.home.crypto_data.usecase.CryptoDataState
import com.antonioje.cryptowallet.home.crypto_data.usecase.CryptoDataViewModel
import com.github.mikephil.charting.charts.LineChart
import com.squareup.picasso.Picasso
import java.util.Locale
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class CryptoDataFragment : Fragment() {
    private var _binding: FragmentCryptoDataBinding? = null
    private val binding get() = _binding!!
    private val _viewmodel: CryptoDataViewModel by viewModels()
    private lateinit var cryptoData: CryptoData
    private lateinit var lineChart: LineChart
    private var isUpdatingEditText = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCryptoDataBinding.inflate(inflater, container, false)

        initVariables()
        return binding.root
    }

    //region DIBUJO DE LA GRÁFICA
    private fun visualizeCryptoPriceGraphic() {
        val entries = mutableListOf<Entry>()

        val cryptoChange = _viewmodel.getGraphicData(cryptoData)

        lineChart = binding.lineChartCryptoChange

        for (i in cryptoChange.prices) {
            val day = i[0].toLong()
            val value = i[1]
            entries.add(Entry(day.toFloat(), value))
        }

        val dataSet = createDataSet(entries)
        val lineData = LineData(dataSet.dataSets)

        lineChart.apply {
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)

            setDrawGridBackground(false)
            setBackgroundColor(Color.WHITE)
            data = lineData

            val xAxis: XAxis = xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val date = Date(value.toLong())
                    val dateFormat = SimpleDateFormat("dd-MM", Locale.getDefault())
                    return dateFormat.format(date)
                }
            }

            val yAxis: YAxis = axisLeft
            yAxis.setDrawGridLines(true)

            axisRight.isEnabled = false

            invalidate()
        }
    }

    private fun createDataSet(entries: MutableList<Entry>): LineData {
        val dataSet = LineData()
        val lineDataSet = LineDataSet(entries, "").apply {
            setDrawCircles(false)
            setDrawValues(false)
            color = Color.BLUE
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }
        dataSet.addDataSet(lineDataSet)
        return dataSet
    }

    //endregion


    private fun initVariables() {

        val cryptoID: String = requireArguments().getString(CryptoData.CRYPTO_KEY)!!

        binding.ivAddTransaction.setOnClickListener {
            showTransactionDialog()
        }

        _viewmodel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                CryptoDataState.NoDataError -> showNoData()
                is CryptoDataState.Success -> onSuccess(it.data)
            }
        })

        var divisas = DIVISAS.values()
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, divisas)
        binding.spCryptoDataFiat.adapter = adapter

        _viewmodel.getCrypto(cryptoID)

        configEditText()

        initSpinners()


    }

    private fun showTransactionDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.alert_crypto_transaction, null)

        builder.setView(dialogView)
        val alertDialog = builder.create()


        builder.setView(dialogView)

        val edtFecha = dialogView.findViewById<EditText>(R.id.edtFecha)
        val tietPrice = dialogView.findViewById<TextInputEditText>(R.id.precioEditText)
        val tietTotalCoins = dialogView.findViewById<TextInputEditText>(R.id.cantidadEditText)
        val tietTotalValue = dialogView.findViewById<TextInputEditText>(R.id.totalEditText)
        val tilTextBuyOrSell = dialogView.findViewById<TextInputLayout>(R.id.totalTextInputLayout)
        val rdComprar = dialogView.findViewById<RadioButton>(R.id.comprarRadioButton)
        val rdVender = dialogView.findViewById<RadioButton>(R.id.venderRadioButton)
        val btnAddTransaction = dialogView.findViewById<Button>(R.id.btnAddTransaction)
        val colorAmarillo = ContextCompat.getColor(requireContext(), R.color.yellow)
        rdComprar.buttonTintList = ColorStateList.valueOf(colorAmarillo)
        rdVender.buttonTintList = ColorStateList.valueOf(colorAmarillo)

        rdComprar.setOnClickListener {
            tilTextBuyOrSell.hint = "Total gastado"
        }

        rdVender.setOnClickListener {
            tilTextBuyOrSell.hint = "Total recibido"
        }


        tietPrice.setText(CryptoCurrency.formatPrice(cryptoData.market_data.current_price.eur))
        tietTotalCoins.setText("1")
        tietTotalValue.setText(CryptoCurrency.formatPrice(cryptoData.market_data.current_price.eur))


        tietPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                actualizarValorTotal(tietPrice, tietTotalCoins, tietTotalValue)
                tilTextBuyOrSell.isErrorEnabled = false
                tilTextBuyOrSell.setEndIconDrawable(R.drawable.icon_euro)

            }
        })


        tietTotalCoins.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                actualizarValorTotal(tietPrice, tietTotalCoins, tietTotalValue)
                tilTextBuyOrSell.setEndIconDrawable(R.drawable.icon_euro)
                tilTextBuyOrSell.isErrorEnabled = false
            }
        })

        val currentDate = Calendar.getInstance().time

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        edtFecha.setText(formattedDate)


        edtFecha.setOnClickListener {
            showDatapickerDialog(edtFecha)
        }

        btnAddTransaction.setOnClickListener {
            val cost = tietTotalValue.text.toString().replace(',','.').toDoubleOrNull() ?: 0.0
            if (cost == 0.0) {
                tilTextBuyOrSell.endIconDrawable = null
                tilTextBuyOrSell.error = "NO PUEDE SER 0€"
                tilTextBuyOrSell.requestFocus()
            } else {
                val type = if (rdComprar.isChecked) TRANSACTIONTYPE.COMPRAR else TRANSACTIONTYPE.VENDER
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(edtFecha.text.toString())
                val coinPrice = tietPrice.text.toString().replace(',','.').toDoubleOrNull() ?: 0.0
                val coinQuantity = tietTotalCoins.text.toString().replace(',','.').toDoubleOrNull() ?: 0.0


                val transaction = CryptoTransaction(type, date, coinPrice, coinQuantity, cost)

                //AÑADIR A LA FIREBASE
                _viewmodel.addTransaction(cryptoData,transaction)
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }

    fun showDatapickerDialog(edtFecha : EditText){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, yearSelected, monthOfYear, dayOfMonth ->
                val fechaSeleccionada = "$dayOfMonth/${monthOfYear + 1}/$yearSelected"
                edtFecha.setText(fechaSeleccionada)

                edtFecha.clearFocus()
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun actualizarValorTotal(
        tietPrice: TextInputEditText,
        tietTotalCoins: TextInputEditText,
        tietTotalValue: TextInputEditText
    ) {
        val precioText = tietPrice.text.toString().replace(',', '.')
        val cantidadText = tietTotalCoins.text.toString().replace(',', '.')

        val precio = if (precioText.isNotEmpty()) precioText.toDouble() else 0.0
        val cantidad = if (cantidadText.isNotEmpty()) cantidadText.toDouble() else 0.0
        val total = precio * cantidad
        tietTotalValue.setText(CryptoCurrency.formatPrice(total))
    }

    private fun configEditText() {
        binding.etCryptoValueConverter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingEditText) {
                    isUpdatingEditText = true

                    var cantidad = 0.0

                    when (binding.spCryptoDataFiat.selectedItem.toString()) {
                        "USD" -> cantidad = cryptoData.market_data.current_price.usd
                        "EUR" -> cantidad = cryptoData.market_data.current_price.eur
                        "JPY" -> cantidad = cryptoData.market_data.current_price.jpy
                    }

                    var crypto =
                        binding.etCryptoValueConverter.text.toString().toDoubleOrNull() ?: 0.0

                    var total = (cantidad * crypto)

                    binding.etFiatValueConverter.setText(CryptoCurrency.formatPrice(total))

                    if (binding.etCryptoValueConverter.text.isEmpty()) {
                        binding.etFiatValueConverter.setText("")
                    }

                    isUpdatingEditText = false
                }
            }
        })

        binding.etFiatValueConverter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingEditText) {
                    isUpdatingEditText = true

                    var cantidad = 0.0

                    when (binding.spCryptoDataFiat.selectedItem.toString()) {
                        "USD" -> cantidad = cryptoData.market_data.current_price.usd
                        "EUR" -> cantidad = cryptoData.market_data.current_price.eur
                        "JPY" -> cantidad = cryptoData.market_data.current_price.jpy
                    }

                    var fiat = binding.etFiatValueConverter.text.toString().toDoubleOrNull() ?: 0.0

                    var total = (fiat / cantidad)

                    binding.etCryptoValueConverter.setText(CryptoCurrency.formatPrice(total))

                    if (binding.etFiatValueConverter.text.isEmpty()) {
                        binding.etCryptoValueConverter.setText("")
                    }

                    isUpdatingEditText = false
                }

            }
        })
    }

    fun initSpinners() {
        binding.spCryptoDataFiat.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (binding.etCryptoValueConverter.text.isNotEmpty()) {
                        var cantidad = 0.0

                        when (binding.spCryptoDataFiat.selectedItem.toString()) {
                            "USD" -> cantidad = cryptoData.market_data.current_price.usd
                            "EUR" -> cantidad = cryptoData.market_data.current_price.eur
                            "JPY" -> cantidad = cryptoData.market_data.current_price.jpy
                        }

                        var crypto =
                            binding.etCryptoValueConverter.text.toString().replace(",", ".")
                                .toDoubleOrNull() ?: 0.0

                        var total = (cantidad * crypto)

                        if (!isUpdatingEditText) {
                            isUpdatingEditText = true
                            binding.etFiatValueConverter.setText(CryptoCurrency.formatPrice(total))
                            isUpdatingEditText = false
                        }
                    }
                }


                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    private fun showNoData() {

    }

    private fun onSuccess(data: CryptoData) {
        cryptoData = data
        initLayout(data)
        visualizeCryptoPriceGraphic()
    }

    private fun initLayout(data: CryptoData) {
        with(binding) {
            tvCryptoName.text = data.name
            tvCryptoSymbol.text = cryptoData.symbol.toUpperCase()
            if (data.market_cap_rank == 0) {
                tvCryptoDataRank.text = "#" + getString(R.string.CryptoDataNotRank)
            } else {
                tvCryptoDataRank.text = "#" + cryptoData.market_cap_rank.toString()
            }
            tvCryptoPrice.text =
                CryptoCurrency.formatPrice(cryptoData.market_data.current_price.eur) + "€"

            if (cryptoData.market_data.market_cap.eur <= 0) {
                tvCapitalization.text = getString(R.string.CryptoDataNotMarketCap)
            } else {
                tvCapitalization.text =
                    CryptoCurrency.getFormattedNumber(cryptoData.market_data.market_cap.eur.toLong()) + "€"
            }

            if (_viewmodel.isFavorite(data)) {
                ivFavouriteCrytoData.setImageResource(R.drawable.action_favourite_on)
            } else {
                ivFavouriteCrytoData.setImageResource(R.drawable.action_favourite_off)
            }

            ivFavouriteCrytoData.setOnClickListener { onFavoriteCrypto(data) }

            Picasso.get().load(cryptoData.image.large).into(ivCryptoImage)
            Picasso.get().load(cryptoData.image.large).into(ivCryptoDataConverter)

            if (cryptoData.market_data.price_change_percentage_24h >= 0) {
                tvCryptoDataLast24h.setTextColor(Color.GREEN)
                ivCryptoDataLast24h.setImageResource(R.drawable.icon_last24h_up)

            } else {
                tvCryptoDataLast24h.setTextColor(Color.RED)
                ivCryptoDataLast24h.setImageResource(R.drawable.icon_last24h_down)
            }
            tvCryptoDataLast24h.text =
                String.format("%.2f", cryptoData.market_data.price_change_percentage_24h)
                    .replace("-", "") + "%"



            if (cryptoData.market_data.market_cap_change_percentage_24h >= 0) {
                tvCapitalizationLast24h.setTextColor(Color.GREEN)
                ivCapitalizationLast24h.setImageResource(R.drawable.icon_last24h_up)

            } else {
                tvCapitalizationLast24h.setTextColor(Color.RED)
                ivCapitalizationLast24h.setImageResource(R.drawable.icon_last24h_down)
            }
            tvCapitalizationLast24h.text =
                String.format("%.2f", cryptoData.market_data.market_cap_change_percentage_24h)
                    .replace("-", "") + "%"
        }

    }

    private fun onFavoriteCrypto(crypto: CryptoData) {
        if (_viewmodel.isFavorite(crypto)) {
            _viewmodel.deleteFavourite(crypto)
            binding.ivFavouriteCrytoData.setImageResource(R.drawable.action_favourite_off)
        } else {
            _viewmodel.addFavourite(crypto)
            binding.ivFavouriteCrytoData.setImageResource(R.drawable.action_favourite_on)
        }

    }


}
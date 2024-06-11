package com.antonioje.cryptowallet.home.portfolio.ui

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
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.enum.TRANSACTIONTYPE
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoCurrency.Companion.formatPrice
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.model.CryptoTransaction
import com.antonioje.cryptowallet.databinding.FragmentCryptoTransactionBinding
import com.antonioje.cryptowallet.home.crypto_list.usecase.CryptoListState
import com.antonioje.cryptowallet.home.portfolio.adapter.CryptoTransactionAdapter
import com.antonioje.cryptowallet.home.portfolio.usecase.CryptoTransactionViewModel
import com.antonioje.cryptowallet.home.portfolio.usecase.PortfolioListState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CryptoTransactionFragment : Fragment() {
    private var _binding:FragmentCryptoTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var crypto: Crypto
    private lateinit var _adapter: CryptoTransactionAdapter
    private val _viewmodel: CryptoTransactionViewModel by viewModels()


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

            if(crypto.totalCoins > 1000000.0){
                tvTotalCoinsTransaction.text = String.format("%s",
                    CryptoCurrency.formatLargeNumber(crypto.totalCoins.toLong())
                )
            } else if(crypto.totalCoins >= 1.0){
                tvTotalCoinsTransaction.text = String.format("%.2f",
                    crypto.totalCoins)
            } else {
                tvTotalCoinsTransaction.text = String.format("%s",
                    formatPrice(crypto.totalCoins)
                )
            }

            tvTotalCostTransaction.text = String.format("%.2f€", crypto.initialCost)
            tvTotalValueTransaction.text =    String.format("%.2f€", crypto.currentPrice * crypto.totalCoins)
            tvAverageCostTransaction.text =  String.format("%s€", formatPrice(crypto.averageCost))
            tvCoinSymbol.text = crypto.cryptoSymbol.toUpperCase()


            if(crypto.profitOrLossMoney >= 0){
                tvAllTimeProfitLossTransaction.text =  String.format("%.2f€",crypto.profitOrLossMoney)
                tvAllTimeProfitLossTransaction.setTextColor(Color.GREEN)
                imvLast24H.setImageResource(R.drawable.icon_last24h_up)
                tvAllTimeProfitLossTransactionPorcentage.text = String.format("%.2f%%",crypto.profitOrLossPorcentage).replace("+","")
                tvAllTimeProfitLossTransactionPorcentage.setTextColor(Color.GREEN)
            } else {
                tvAllTimeProfitLossTransaction.text =  String.format("%.2f€",crypto.profitOrLossMoney)
                tvAllTimeProfitLossTransaction.setTextColor(Color.RED)
                imvLast24H.setImageResource(R.drawable.icon_last24h_down)
                tvAllTimeProfitLossTransactionPorcentage.text = String.format("%.2f%%",crypto.profitOrLossPorcentage).replace("-","")
                tvAllTimeProfitLossTransactionPorcentage.setTextColor(Color.RED)
            }

            btnAddCryptoTransaction.setOnClickListener {
                showTransactionDialog()
            }
        }
    }

    private fun showTransactionDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.alert_crypto_transaction, null)
        val totalCoins = _viewmodel.getCoinsFromCrypto(crypto.cryptoName)

        builder.setView(dialogView)
        val alertDialog = builder.create()

        alertDialog.setCancelable(false)

        builder.setView(dialogView)

        val tvCancelAlert = dialogView.findViewById<TextView>(R.id.tvCancelAlert)
        val tvAllCoins = dialogView.findViewById<TextView>(R.id.tvAllCoinsTransaction)
        val linLayCoins =dialogView.findViewById<LinearLayout>(R.id.linearLayoutTotalCoins)
        val edtFecha = dialogView.findViewById<EditText>(R.id.edtFecha)
        val tilTotalCoins = dialogView.findViewById<TextInputLayout>(R.id.cantidadTextInputLayout)
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

        if (totalCoins == 0.0){
            rdVender.isEnabled = false
        }

        rdComprar.setOnClickListener {
            tilTextBuyOrSell.hint = "Total gastado"
            linLayCoins.visibility = View.GONE
        }

        rdVender.setOnClickListener {
            tilTextBuyOrSell.hint = "Total recibido"
            linLayCoins.visibility = View.VISIBLE

            if(totalCoins > 1000000.0){
                tvAllCoins.text = String.format("%s %s",
                    CryptoCurrency.formatLargeNumber(totalCoins.toLong()), crypto.cryptoSymbol)
            } else if(totalCoins >= 1.0) {
                tvAllCoins.text = String.format("%.2f %s",
                    totalCoins,crypto.cryptoSymbol)
            } else {
                tvAllCoins.text = String.format("%s %s",
                    formatPrice(totalCoins),crypto.cryptoSymbol)
            }
        }

        tvAllCoins.setOnClickListener {
            tietTotalCoins.setText(totalCoins.toString())
        }


        tvCancelAlert.setOnClickListener{
            alertDialog.dismiss()
        }

        tietPrice.setText(formatPrice(crypto.currentPrice))
        tietTotalCoins.setText("1")
        tietTotalValue.setText(formatPrice(crypto.currentPrice))


        tietPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                actualizarValorTotal(tietPrice, tietTotalCoins, tietTotalValue)
                tilTextBuyOrSell.isErrorEnabled = false
                tilTotalCoins.isErrorEnabled = false
                tilTextBuyOrSell.setEndIconDrawable(R.drawable.icon_euro)

            }
        })


        tietTotalCoins.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                actualizarValorTotal(tietPrice, tietTotalCoins, tietTotalValue)
                tilTextBuyOrSell.setEndIconDrawable(R.drawable.icon_euro)
                tilTotalCoins.isErrorEnabled = false
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
            val type = if (rdComprar.isChecked) TRANSACTIONTYPE.COMPRAR else TRANSACTIONTYPE.VENDER
            if (cost == 0.0) {
                tilTextBuyOrSell.endIconDrawable = null
                tilTextBuyOrSell.error = "NO PUEDE SER 0€"
                tilTextBuyOrSell.requestFocus()
            } else if (type == TRANSACTIONTYPE.VENDER &&  tietTotalCoins.text.toString().toDouble() > totalCoins){
                tilTotalCoins.endIconDrawable = null
                tilTotalCoins.error = "LA CANTIDAD NO PUEDE SER SUPERIOR AL SALDO"
                tilTotalCoins.requestFocus()
            } else {
                val type = if (rdComprar.isChecked) TRANSACTIONTYPE.COMPRAR else TRANSACTIONTYPE.VENDER
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(edtFecha.text.toString())
                val coinPrice = tietPrice.text.toString().replace(',','.').toDoubleOrNull() ?: 0.0
                val coinQuantity = tietTotalCoins.text.toString().replace(',','.').toDoubleOrNull() ?: 0.0

                val transaction = CryptoTransaction(type, date, coinPrice, coinQuantity, cost)

                //AÑADIR A LA FIREBASE
                _viewmodel.addTransaction(crypto,transaction)
                alertDialog.dismiss()
                findNavController().popBackStack()

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


    private fun onClick(cryptoTransaction: CryptoTransaction) {

    }


}
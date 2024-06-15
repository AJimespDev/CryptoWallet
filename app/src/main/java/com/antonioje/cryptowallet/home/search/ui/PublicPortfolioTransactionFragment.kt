package com.antonioje.cryptowallet.home.search.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.enum.TRANSACTIONTYPE
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.model.CryptoTransaction
import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.databinding.FragmentPublicPortfolioTransactionBinding
import com.antonioje.cryptowallet.home.portfolio.adapter.CryptoTransactionAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class PublicPortfolioTransactionFragment : Fragment() {
    private var _binding: FragmentPublicPortfolioTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var _adapter: CryptoTransactionAdapter
    private lateinit var crypto: Crypto

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublicPortfolioTransactionBinding.inflate(inflater, container, false)
        crypto = requireArguments().getSerializable(CryptoData.CRYPTO_KEY) as Crypto
        initVariables()
        return binding.root
    }


    private fun initVariables() {
        with(binding) {
            _adapter = CryptoTransactionAdapter(requireContext(),crypto.cryptoSymbol) { onClick(it) }
            recyclerView.adapter = _adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            _adapter.submitList(crypto.transactions)

            Picasso.get().load(crypto.image.large).into(imCryptoTransaction)
            tvCryptoName.text = crypto.cryptoName

            if (crypto.totalCoins > 1000000.0) {
                tvTotalCoinsTransaction.text = String.format(
                    "%s",
                    CryptoCurrency.formatLargeNumber(crypto.totalCoins.toLong())
                )
            } else if (crypto.totalCoins >= 1.0) {
                tvTotalCoinsTransaction.text = String.format(
                    "%.2f",
                    crypto.totalCoins
                )
            } else {
                tvTotalCoinsTransaction.text = String.format(
                    "%s",
                    CryptoCurrency.formatPrice(crypto.totalCoins)
                )
            }

            tvTotalCostTransaction.text = String.format("%.2f€", crypto.initialCost)
            tvTotalValueTransaction.text =
                String.format("%.2f€", crypto.currentPrice * crypto.totalCoins)
            tvAverageCostTransaction.text = String.format(
                "%s€",
                CryptoCurrency.formatPrice(crypto.transactions.map { it.coinPrice }.average())
            )
            tvCoinSymbol.text = crypto.cryptoSymbol.toUpperCase()

            val profitOrLossMoney = (crypto.currentPrice * crypto.totalCoins) - crypto.initialCost

            if (profitOrLossMoney >= 0) {
                tvAllTimeProfitLossTransaction.text = String.format("%.2f€", profitOrLossMoney)
                tvAllTimeProfitLossTransaction.setTextColor(Color.GREEN)
                imvLast24H.setImageResource(R.drawable.icon_last24h_up)
                tvAllTimeProfitLossTransactionPorcentage.text =
                    String.format("%.2f%%", crypto.profitOrLossPorcentage).replace("+", "")
                tvAllTimeProfitLossTransactionPorcentage.setTextColor(Color.GREEN)
            } else {
                tvAllTimeProfitLossTransaction.text = String.format("%.2f€", profitOrLossMoney)
                tvAllTimeProfitLossTransaction.setTextColor(Color.RED)
                imvLast24H.setImageResource(R.drawable.icon_last24h_down)
                tvAllTimeProfitLossTransactionPorcentage.text =
                    String.format("%.2f%%", crypto.profitOrLossPorcentage).replace("-", "")
                tvAllTimeProfitLossTransactionPorcentage.setTextColor(Color.RED)
            }
        }
    }

    private fun onClick(cryptoTransaction: CryptoTransaction) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.alert_crypto_transaction, null)

        builder.setView(dialogView)
        val alertDialog = builder.create()


        builder.setView(dialogView)

        val tvCancelAlert = dialogView.findViewById<TextView>(R.id.tvCancelAlert)
        val edtFecha = dialogView.findViewById<EditText>(R.id.edtFecha)
        val tietPrice = dialogView.findViewById<TextInputEditText>(R.id.precioEditText)
        val tietTotalCoins = dialogView.findViewById<TextInputEditText>(R.id.cantidadEditText)
        val tietTotalValue = dialogView.findViewById<TextInputEditText>(R.id.totalEditText)
        val rdComprar = dialogView.findViewById<RadioButton>(R.id.comprarRadioButton)
        val rdVender = dialogView.findViewById<RadioButton>(R.id.venderRadioButton)
        val btnAddTransaction = dialogView.findViewById<Button>(R.id.btnAddTransaction)
        val colorAmarillo = ContextCompat.getColor(requireContext(), R.color.yellow)

        btnAddTransaction.visibility = View.GONE
        rdComprar.buttonTintList = ColorStateList.valueOf(colorAmarillo)
        rdVender.buttonTintList = ColorStateList.valueOf(colorAmarillo)

        if (cryptoTransaction.type == TRANSACTIONTYPE.COMPRAR) {
            rdComprar.isChecked = true
            rdVender.isEnabled = false
        } else {
            rdVender.isChecked = true
            rdComprar.isEnabled = false
        }

        tietTotalCoins.setText(cryptoTransaction.coinCuantity.toString())
        tietTotalCoins.isEnabled = false

        tietPrice.setText(CryptoCurrency.formatPrice(cryptoTransaction.coinPrice))
        tietPrice.isEnabled = false

        tietTotalValue.setText(cryptoTransaction.cost.toString())
        tietTotalValue.isEnabled = false

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = sdf.format(cryptoTransaction.date)
        edtFecha.setText(formattedDate)
        edtFecha.isEnabled = false


        tvCancelAlert.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}
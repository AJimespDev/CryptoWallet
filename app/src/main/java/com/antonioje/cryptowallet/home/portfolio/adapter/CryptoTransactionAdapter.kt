package com.antonioje.cryptowallet.home.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.enum.TRANSACTIONTYPE
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoTransaction
import com.antonioje.cryptowallet.databinding.ItemCryptoTransactionBinding
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class CryptoTransactionAdapter(private var cryptoSymbol:String, private val onClick: (transaction: CryptoTransaction) -> Unit): ListAdapter<CryptoTransaction, CryptoTransactionAdapter.CryptoTransactionViewHolder>(CRYPTOTRANSACTION_COMPARATOR) {

    companion object {
        val CRYPTOTRANSACTION_COMPARATOR = object : DiffUtil.ItemCallback<CryptoTransaction>() {
            override fun areItemsTheSame(
                oldItem: CryptoTransaction,
                newItem: CryptoTransaction
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CryptoTransaction,
                newItem: CryptoTransaction
            ): Boolean {
                return  oldItem == newItem
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoTransactionViewHolder {
        val binding = ItemCryptoTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CryptoTransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CryptoTransactionViewHolder, position: Int) {
        holder.render(currentList[position])
    }



    inner class CryptoTransactionViewHolder(private val binding: ItemCryptoTransactionBinding) :  RecyclerView.ViewHolder(binding.root) {
        fun render(transaction: CryptoTransaction){
            itemView.setOnClickListener { onClick(transaction) }
            with(binding){
                tvType.text = transaction.type.toString()
                if(transaction.type == TRANSACTIONTYPE.COMPRAR){
                    tvTypePrice.text = "Pagado:"
                    imvArrow.setImageResource(R.drawable.add_transaction)
                }else{
                    tvTypePrice.text = "Recibido:"
                    imvArrow.setImageResource(R.drawable.sell_transaction)
                }

                val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
                val localDate = transaction.date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                tvDate.text = localDate.format(formatter)
                tvPrice.text = String.format("%s€", transaction.cost.toString())



                if(transaction.coinCuantity > 1000000.0){
                    tvNCoins.text = String.format("%s %s",
                        CryptoCurrency.formatLargeNumber(transaction.coinCuantity.toLong()),cryptoSymbol)
                } else {
                    tvNCoins.text = String.format("%s %s",
                        CryptoCurrency.formatPrice(transaction.coinCuantity),cryptoSymbol)
                }

            }
        }
    }


}
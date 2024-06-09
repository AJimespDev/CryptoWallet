package com.antonioje.cryptowallet.home.portfolio.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.model.Crypto
import com.antonioje.cryptowallet.data.model.CryptoCurrency.Companion.formatLargeNumber
import com.antonioje.cryptowallet.data.model.CryptoCurrency.Companion.formatPrice
import com.antonioje.cryptowallet.databinding.ItemCryptoPortfolioBinding
import com.squareup.picasso.Picasso

class PortfolioAdapter(private val onClick: (crypto: Crypto) -> Unit) :
    ListAdapter<Crypto, PortfolioAdapter.PortfolioViewHolder>(PORTFOLIO_COMPARATOR) {

    companion object {
        val PORTFOLIO_COMPARATOR = object : DiffUtil.ItemCallback<Crypto>() {
            override fun areItemsTheSame(oldItem: Crypto, newItem: Crypto): Boolean {
                return oldItem.cryptoSymbol  == newItem.cryptoSymbol
            }

            override fun areContentsTheSame(oldItem: Crypto, newItem: Crypto): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val binding = ItemCryptoPortfolioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PortfolioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder.render(currentList[position])
    }

    fun sortByName() {
        if (currentList == currentList.sortedBy { it.cryptoName }) {
            submitList(currentList.sortedBy { it.cryptoName }.reversed())
        } else{
            submitList(currentList.sortedBy { it.cryptoName })
        }
    }

    fun sortBy24H() {
        if (currentList == currentList.sortedBy { it.price_change_percentage_24h }) {
            submitList(currentList.sortedBy { it.price_change_percentage_24h }.reversed())
        } else{
            submitList(currentList.sortedBy { it.price_change_percentage_24h })
        }

    }

    fun sortByTotal() {
        if (currentList == currentList.sortedBy { it.totalValue }) {
            submitList(currentList.sortedBy { it.totalValue }.reversed())
        } else {
            submitList(currentList.sortedBy { it.totalValue })
        }
    }

    inner class PortfolioViewHolder(private val binding: ItemCryptoPortfolioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun render(crypto: Crypto) {
            itemView.setOnClickListener { onClick(crypto) }
            with(binding){
                Picasso.get().load(crypto.image.large).into(ivItemCrypto)
                tvItemCryptoSymbol.text = crypto.cryptoName
                if (crypto.price_change_percentage_24h >= 0) {
                    tvItemCryptoLast24h.setTextColor(Color.GREEN)
                    ivItemLast24H.setImageResource(R.drawable.icon_last24h_up)

                } else {
                    tvItemCryptoLast24h.setTextColor(Color.RED)
                    ivItemLast24H.setImageResource(R.drawable.icon_last24h_down)
                }
                tvItemCryptoLast24h.text =
                    String.format("%.2f", crypto.price_change_percentage_24h).replace("-", "") + "%"

                if(crypto.totalCoins > 1000000.0){
                    tvTotalCoins.text = String.format("%s %s", formatLargeNumber(crypto.totalCoins.toLong()),crypto.cryptoSymbol.toUpperCase())
                } else {
                    tvTotalCoins.text = String.format("%s %s", formatPrice(crypto.totalCoins),crypto.cryptoSymbol.toUpperCase())
                }

                 tvTotalPrice.text = String.format("%.2f€", crypto.totalValue)
              }
        }
    }
}
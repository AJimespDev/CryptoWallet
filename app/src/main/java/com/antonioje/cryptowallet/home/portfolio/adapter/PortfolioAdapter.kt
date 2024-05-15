package com.antonioje.cryptowallet.home.portfolio.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.model.Crypto
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

                tvTotalPrice.text = String.format("%.2f€", crypto.totalValue)
                tvTotalCoins.text = String.format("%.2f %s", crypto.totalCoins,crypto.cryptoSymbol.toUpperCase())
            }
        }
    }
}
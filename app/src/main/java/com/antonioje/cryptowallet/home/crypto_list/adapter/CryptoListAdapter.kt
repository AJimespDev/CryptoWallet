package com.antonioje.cryptowallet.home.crypto_list.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.CryptoCurrency
import com.antonioje.cryptowallet.databinding.ItemCryptoBinding
import com.squareup.picasso.Picasso

class CryptoListAdapter(val onClick:(CryptoCurrency) -> Unit) :
    ListAdapter<CryptoCurrency, CryptoListAdapter.HomeListViewHolder>(CRYPTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return HomeListViewHolder(ItemCryptoBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: HomeListViewHolder, position: Int) {
        var item = currentList[position]
        holder.render(item)
    }

    fun sortbyPrice() {
        if (currentList == currentList.sortedBy { it.current_price }) {
            submitList(currentList.sortedBy { it.current_price }.reversed())
        } else {
            submitList(currentList.sortedBy { it.current_price })
        }
    }

    fun sortbyName() {
        if (currentList == currentList.sortedBy { it.name }) {
            submitList(currentList.sortedBy { it.name }.reversed())
        } else {
            submitList(currentList.sortedBy { it.name})
        }
    }


    fun sortbyLast24H() {
        if (currentList == currentList.sortedBy { it.price_change_percentage_24h }) {
            submitList(currentList.sortedBy { it.price_change_percentage_24h }.reversed())
        } else {
            submitList(currentList.sortedBy { it.price_change_percentage_24h })
        }
    }

    fun sortbyMarketCap() {
        if (currentList == currentList.sortedBy { it.market_cap }) {
            submitList(currentList.sortedBy { it.market_cap }.reversed())
        } else {
            submitList(currentList.sortedBy { it.market_cap })
        }
    }

    companion object {
        val CRYPTO_COMPARATOR = object : DiffUtil.ItemCallback<CryptoCurrency>() {
            override fun areItemsTheSame(
                oldItem: CryptoCurrency,
                newItem: CryptoCurrency
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CryptoCurrency,
                newItem: CryptoCurrency
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    inner class HomeListViewHolder(var binding: ItemCryptoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun render(item: CryptoCurrency) {
            itemView.setOnClickListener { onClick(item) }
            with(binding) {
                Picasso.get().load(item.image).into(ivItemCrypto)

                tvItemRank.text = item.market_cap_rank.toString() + "."

                ivFavourite.setOnClickListener {
                    ivFavourite.setImageResource(R.drawable.action_favourite_on)
                }

                tvItemCryptoSymbol.text = item.symbol.toUpperCase()

                tvItemMarketCap.text = "€" + CryptoCurrency.formatLargeNumber(item.market_cap)

                tvItemCryptoPrice.text = CryptoCurrency.formatPrice(item.current_price)

                if (item.price_change_percentage_24h >= 0) {
                    tvItemCryptoLast24h.setTextColor(Color.GREEN)
                    ivItemLast24H.setImageResource(R.drawable.icon_last24h_up)

                } else {
                    tvItemCryptoLast24h.setTextColor(Color.RED)
                    ivItemLast24H.setImageResource(R.drawable.icon_last24h_down)
                }

                tvItemCryptoLast24h.text =
                    String.format("%.2f", item.price_change_percentage_24h).replace("-", "") + "%"

            }
        }


    }
}
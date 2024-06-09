package com.antonioje.cryptowallet.home.search.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.antonioje.cryptowallet.home.search.ui.SearchCryptoFragment
import com.antonioje.cryptowallet.home.search.ui.SearchPortfolioFragment


class SearchPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchCryptoFragment()
            1 -> SearchPortfolioFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
package com.antonioje.cryptowallet.home.search.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.databinding.FragmentSearchBinding
import com.antonioje.cryptowallet.home.search.adapter.SearchPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configTabView()
    }

    private fun configTabView(){
        val viewPager = binding.viewPager
        val customFont = ResourcesCompat.getFont(requireContext(), R.font.humatoheavy)

        viewPager.adapter = SearchPagerAdapter(requireActivity())

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.search_crypto_tab)
                1 -> getString(R.string.search_portfolio_tab)
                else -> null
            }
        }.attach()

        // Método para aplicar la fuente a todas las pestañas
        fun applyCustomFontToTabs() {
            for (i in 0 until tabLayout.tabCount) {
                val tab = tabLayout.getTabAt(i)
                val tabTextView = tab?.view?.getChildAt(1) as? TextView
                tabTextView?.typeface = customFont
            }
        }

        // Aplicar la fuente personalizada inicialmente
        applyCustomFontToTabs()

        // Listener para aplicar la fuente cuando se selecciona/desselecciona una pestaña
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                applyCustomFontToTabs()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                applyCustomFontToTabs()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                applyCustomFontToTabs()
            }
        })

        // Listener para aplicar la fuente cuando se cambia de página mediante deslizamiento
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                applyCustomFontToTabs()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
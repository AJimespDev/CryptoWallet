package com.antonioje.cryptowallet.home.favourites.ui

import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.databinding.FragmentFavouriteListBinding
import com.antonioje.cryptowallet.home.favourites.adapter.FavouriteListAdapter
import com.antonioje.cryptowallet.home.favourites.usecase.FavouriteListState
import com.antonioje.cryptowallet.home.favourites.usecase.FavouriteListViewModel


class FavouriteListFragment : Fragment() {
    private var _binding: FragmentFavouriteListBinding? = null
    private val binding get() = _binding!!
    private lateinit var _adapter:FavouriteListAdapter
    private val _viewModel : FavouriteListViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteListBinding.inflate(inflater,container,false)
        initVariables()
        return binding.root
    }

    private fun initVariables() {
        _adapter = FavouriteListAdapter({onClick(it)},{onFavouriteClick(it)} )

        binding.tvListCapRank.setOnClickListener {
            _adapter.sortbyMarketCap()
            scrollToTop()
        }

        binding.tvListMonedas.setOnClickListener {
            _adapter.sortbyName()
            scrollToTop()
        }

        binding.tvListPrice.setOnClickListener {
            _adapter.sortbyPrice()
            scrollToTop()
        }

        binding.tvListLast24h.setOnClickListener {
            _adapter.sortbyLast24H()
            scrollToTop()
        }

        with(binding.rvFavoriteListFragment){
            adapter = _adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        _viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it){
                FavouriteListState.NoData -> showNoData()
                else -> {
                    _adapter.submitList(_viewModel.favouriteList)
                    onSuccess()
                }
            }
        })
        _viewModel.getFavourites()
    }

    private fun onSuccess() {
        with(binding){
            llTexts.visibility = View.VISIBLE
            rvFavoriteListFragment.visibility = View.VISIBLE
            animNoData.visibility = View.GONE
            tvNoDataFavourite.visibility = View.GONE
        }
    }

    private fun showNoData() {
        with(binding){
            llTexts.visibility = View.GONE
            rvFavoriteListFragment.visibility = View.GONE
            animNoData.visibility = View.VISIBLE
            tvNoDataFavourite.visibility = View.VISIBLE
        }
    }

    fun scrollToTop() {
        Handler().postDelayed({
            kotlin.run {
                binding.rvFavoriteListFragment.scrollToPosition(0)
            }
        }, 300)
    }


    private fun onFavouriteClick(crypto: CryptoCurrency) {
        _viewModel.deleteFavourite(crypto) { onDelete() }
    }

    private fun onDelete() {
        _viewModel.updateList()
        _viewModel.getFavourites()
    }

    private fun onClick(crypto: CryptoCurrency) {
        var bundle = Bundle()
        bundle.putSerializable(CryptoData.CRYPTO_KEY, crypto)
        findNavController().navigate(R.id.action_favoriteListFragment_to_cryptoDataFragment, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
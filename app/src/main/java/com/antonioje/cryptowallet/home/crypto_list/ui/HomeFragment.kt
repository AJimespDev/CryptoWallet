package com.antonioje.cryptowallet.home.crypto_list.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonioje.cryptowallet.MainActivity
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.model.CryptoCurrency
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.repository.CryptoRepository
import com.antonioje.cryptowallet.databinding.FragmentHomeBinding
import com.antonioje.cryptowallet.databinding.ItemCryptoBinding
import com.antonioje.cryptowallet.home.crypto_list.adapter.CryptoListAdapter
import com.antonioje.cryptowallet.home.crypto_list.usecase.CryptoListState
import com.antonioje.cryptowallet.home.crypto_list.usecase.CryptoListViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var provider: String
    private lateinit var _adapter: CryptoListAdapter
    private val _viewmodel: CryptoListViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        ((requireActivity()) as MainActivity).setBottomNavVisible()


        setup()

        initVariables()
        return binding.root
    }

    private fun initVariables() {
        _adapter = CryptoListAdapter({ onClickCrypto(it) }, { onFavoriteCrypto(it) })

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

        with(binding.rvHomeFragment) {
            adapter = _adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }


        _viewmodel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                CryptoListState.NoDataError -> showNoData()
                is CryptoListState.Loading -> showLoading(it.value)
                else -> {
                    _adapter.submitList(_viewmodel.listCrypto)
                    onSuccess()
                }
            }
        })

        _viewmodel.initFavoriteList()
    }

    private fun showLoading(value: Boolean) {
        if (value) {
            with(binding) {
                llTexts.visibility = View.GONE
                rvHomeFragment.visibility = View.GONE
                animLoading.visibility = View.VISIBLE
            }
        } else {
            with(binding) {
                rvHomeFragment.visibility = View.VISIBLE
                llTexts.visibility = View.VISIBLE
                animLoading.visibility = View.GONE
            }
        }
    }

    private fun onFavoriteCrypto(crypto: CryptoCurrency) {
        if (!crypto.favorite) {
            _viewmodel.deleteFavourite(crypto)
        } else {
            _viewmodel.addFavourite(crypto)
        }

    }


    // Scrollea el RecyclerView a la primera posicion tras esperar X ms (1 en este caso)
    // La espera se hace para que al adapter le de tiempo a ordenar la lista previamente
    fun scrollToTop() {
        Handler().postDelayed({
            kotlin.run {
                binding.rvHomeFragment.scrollToPosition(0)
            }
        }, 300)
    }

    private fun setup() {
        try {
            //RECOJO EL EMAIL Y EL TIPO DE PROVEEDOR
            var email = requireArguments().getString(MainActivity.EMAIL).toString()
            var provider = requireArguments().getString(MainActivity.PROVIDER)
            var mantenerSesion = requireArguments().getBoolean(MainActivity.MANTENERSESION)

            if (mantenerSesion) {
                //Obtener la instancia de SharedPreferences
                val sharedPreferences =
                    context?.getSharedPreferences("usu_preferencias", Context.MODE_PRIVATE)

                //Editar las preferencias
                val editor = sharedPreferences?.edit()

                //Guardar los strings en las preferencias
                editor?.putString(MainActivity.EMAIL, email)
                editor?.putString(MainActivity.PROVIDER, provider)
                this.provider = provider.toString()

                //Aplico los cambios
                editor?.apply()
            } else {

            }
        } catch (e: IllegalStateException) {
        }

    }

    private fun onSuccess() {

    }

    private fun showNoData() {

    }

    private fun onClickCrypto(crypto: CryptoCurrency) {
        var bundle = Bundle()
        bundle.putString(CryptoData.CRYPTO_KEY, crypto.id)
        findNavController().navigate(R.id.action_homeFragment_to_cryptoDataFragment, bundle)
    }


}
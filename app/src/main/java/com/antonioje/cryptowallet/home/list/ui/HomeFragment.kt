package com.antonioje.cryptowallet.home.list.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.antonioje.cryptowallet.MainActivity
import com.antonioje.cryptowallet.databinding.FragmentHomeBinding
import com.antonioje.cryptowallet.home.list.adapter.CryptoListAdapter
import com.antonioje.cryptowallet.home.list.usecase.CryptoListState
import com.antonioje.cryptowallet.home.list.usecase.CryptoListViewModel


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var provider: String
    private lateinit var _adapter : CryptoListAdapter
    private val _viewmodel : CryptoListViewModel by viewModels()

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
        _adapter = CryptoListAdapter()

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

        with(binding.rvHomeFragment){
            adapter = _adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        _viewmodel.getState().observe(viewLifecycleOwner, Observer {
            when(it){
                CryptoListState.NoDataError -> showNoData()
                else ->{
                    _adapter.submitList(_viewmodel.listCrypto)
                    onSuccess()
                }
            }
        })

        _viewmodel.getList()
    }

    // Scrollea el RecyclerView a la primera posicion tras esperar X ms (1 en este caso)
    // La espera se hace para que al adapter le de tiempo a ordenar la lista previamente
    fun scrollToTop() {
        Handler().postDelayed({
            kotlin.run {
                binding.rvHomeFragment.scrollToPosition(0)
            }
        }, 100)
    }

    private fun setup() {
        try {
            //RECOJO EL EMAIL Y EL TIPO DE PROVEEDOR
            var email = requireArguments().getString(MainActivity.EMAIL)
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
        }catch(e:IllegalStateException){
        }

    }

    private fun onSuccess(){

    }
    private fun showNoData() {

    }

}
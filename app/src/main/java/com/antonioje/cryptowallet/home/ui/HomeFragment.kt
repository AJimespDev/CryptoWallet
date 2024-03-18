package com.antonioje.cryptowallet.home.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.antonioje.cryptowallet.MainActivity
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var provider: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        ((requireActivity()) as MainActivity).setAppBarVisible()
        setup()
        return binding.root
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
}
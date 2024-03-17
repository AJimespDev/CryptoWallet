package com.antonioje.cryptowallet.home.ui

import android.R
import android.content.Context
import android.os.Bundle
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
import com.antonioje.cryptowallet.MainActivity
import com.antonioje.cryptowallet.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment(), MenuProvider {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var provider:String

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
        }

    }

    fun logOut() {
        //Obtener la instancia de SharedPreferences
        val sharedPreferences =
            context?.getSharedPreferences("usu_preferencias", Context.MODE_PRIVATE)
        //Editar las preferencias
        val editor = sharedPreferences?.edit()
        editor?.clear()
        editor?.apply()

        FirebaseAuth.getInstance().signOut()
        ((requireActivity()) as MainActivity).setAppBarGone()


        //NAVEGO AL LOGIN Y LIMPIO LA PILA DE FRAGMENTS
        val navOptions: NavOptions =
            NavOptions.Builder().setPopUpTo(com.antonioje.cryptowallet.R.id.nav_graph, true).setLaunchSingleTop(true).build()
        findNavController(requireActivity(), com.antonioje.cryptowallet.R.id.nav_host_fragment_content_main).navigate(
            com.antonioje.cryptowallet.R.id.signInFragment,
            null,
            navOptions
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
    }


    private fun setupMenu() {
        var menuhost: MenuHost = requireActivity()
        menuhost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(com.antonioje.cryptowallet.R.menu.menu_home, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            com.antonioje.cryptowallet.R.id.action_signout -> {
                logOut()
                return true
            }

            else -> return false
        }
    }

}
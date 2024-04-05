package com.antonioje.cryptowallet.home.settings.ui


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.antonioje.cryptowallet.MainActivity
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.databinding.FragmentSettingsBinding
import com.antonioje.cryptowallet.login.ui.SignInFragment
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        initVariables()
        return binding.root
    }

    private fun initVariables() {
        binding.btnSettingsLogOut.setOnClickListener {
            (requireActivity() as MainActivity).logOut()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.antonioje.cryptowallet.login.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater,container,false)
        initVariables()

        return binding.root
    }

    private fun initVariables() {
        with(binding){
            tvRegisterAccountSignIn.setOnClickListener{
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }
        }
    }


}
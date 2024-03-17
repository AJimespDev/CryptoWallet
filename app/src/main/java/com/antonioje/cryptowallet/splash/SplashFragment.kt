package com.antonioje.cryptowallet.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.antonioje.cryptowallet.MainActivity
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private var _binding : FragmentSplashBinding? = null
    private val binding get() =  _binding!!

    override fun onStart() {
        super.onStart()

        //(activity as MainActivity).setAppBarGone()

        var r = Runnable {
            findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
        }
        Handler(Looper.getMainLooper()).postDelayed(r, 2000)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
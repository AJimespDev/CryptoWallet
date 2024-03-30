package com.antonioje.cryptowallet.home.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.antonioje.cryptowallet.databinding.FragmentFavoriteListBinding


class FavoriteListFragment : Fragment() {
    private var _binding:FragmentFavoriteListBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteListBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
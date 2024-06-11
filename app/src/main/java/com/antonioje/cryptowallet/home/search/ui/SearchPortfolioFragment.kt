package com.antonioje.cryptowallet.home.search.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.model.CryptoData
import com.antonioje.cryptowallet.data.model.Portfolio
import com.antonioje.cryptowallet.databinding.FragmentSearchPortfolioBinding
import com.antonioje.cryptowallet.home.search.usecase.SearchPortfolioState
import com.antonioje.cryptowallet.home.search.usecase.SearchPortfolioViewModel


class SearchPortfolioFragment : Fragment() {
    private var _binding:FragmentSearchPortfolioBinding? = null
    private val binding get() = _binding!!
    private val _viewmodel: SearchPortfolioViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchPortfolioBinding.inflate(inflater,container, false)
        initVariables()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
    }

    private fun initVariables() {
        _viewmodel.getState().observe(viewLifecycleOwner, Observer {
            when(it){
                SearchPortfolioState.NoData -> showNoData()
                is SearchPortfolioState.Loading -> showLoading(it.value)
                is SearchPortfolioState.Success -> onSuccess(it.data)
            }
        })
        _viewmodel.initPublicPortfolioList()
    }

    private fun showLoading(value: Boolean) {

    }

    private fun showNoData() {
        binding.searchView.visibility = View.GONE
        binding.searchView.visibility = View.GONE
        Toast.makeText(requireContext(),getString(R.string.CryptoSearchNoData), Toast.LENGTH_SHORT).show()
    }

    private fun onSuccess(portfolio: List<Portfolio>) {
        binding.searchView.visibility = View.VISIBLE
        binding.searchView.visibility = View.VISIBLE


        val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,portfolio.map { it.name })
        binding.portfolioSeachList.adapter = adapter

        binding.searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                if(portfolio.map { it.name }.contains(query)){
                    adapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })


        binding.portfolioSeachList.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)
            selectedItem?.let {
                var bundle = Bundle()
                bundle.putSerializable(CryptoData.CRYPTO_KEY,portfolio[position])
                findNavController().navigate(R.id.action_buscarFragment_to_publicPortfolioFragment, bundle)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
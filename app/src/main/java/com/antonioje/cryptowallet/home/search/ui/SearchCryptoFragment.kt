package com.antonioje.cryptowallet.home.search.ui

import android.database.DataSetObserver
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
import com.antonioje.cryptowallet.data.model.CryptoSearch
import com.antonioje.cryptowallet.databinding.FragmentSearchCryptoBinding
import com.antonioje.cryptowallet.home.search.usecase.SearchCryptoState
import com.antonioje.cryptowallet.home.search.usecase.SearchCryptoViewModel


class SearchCryptoFragment : Fragment() {
    private var _binding:FragmentSearchCryptoBinding? = null
    private val binding get() = _binding!!
    private val _viewmodel: SearchCryptoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchCryptoBinding.inflate(inflater,container,false)
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
                SearchCryptoState.NoData -> showNoData()
                is SearchCryptoState.Success -> onSuccess(it.data)
            }
        })
        _viewmodel.getList()
    }

    private fun showNoData() {
        binding.searchView.visibility = View.GONE
        binding.searchView.visibility = View.GONE
        Toast.makeText(requireContext(),getString(R.string.CryptoSearchNoData), Toast.LENGTH_SHORT).show()
    }

    private fun onSuccess(data: ArrayList<CryptoSearch>) {
        binding.searchView.visibility = View.VISIBLE
        binding.searchView.visibility = View.VISIBLE


        val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,data.map { it.name })
        binding.cryptoSeachList.adapter = adapter

        binding.searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                if(data.map { it.name }.contains(query)){
                    adapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })


        binding.cryptoSeachList.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)
            selectedItem?.let {
                var bundle = Bundle()
                val id = data.map { it.id }[data.map { it.name }.indexOf(it)]
                bundle.putString(CryptoData.CRYPTO_KEY,id)
                findNavController().navigate(R.id.action_buscarFragment_to_cryptoDataFragment, bundle)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
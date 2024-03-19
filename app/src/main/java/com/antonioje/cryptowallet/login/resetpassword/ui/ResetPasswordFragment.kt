package com.antonioje.cryptowallet.login.resetpassword.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.databinding.FragmentResetPasswordBinding
import com.antonioje.cryptowallet.login.resetpassword.usecase.ResetPasswordState
import com.antonioje.cryptowallet.login.resetpassword.usecase.ResetPasswordViewModel
import com.google.android.material.textfield.TextInputLayout


class ResetPasswordFragment : Fragment() {
    private var _binding:FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val _viewModel: ResetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(inflater,container,false)
        initVariables()
        _viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it){
                ResetPasswordState.EmailIsEmpty -> showEmailError(getString(R.string.EmailIsEmptyError))
                ResetPasswordState.ErrorEmailFormat -> showEmailError(getString(R.string.EmailFormatError))
                is ResetPasswordState.Error -> showError(it.message)
                else -> onSuccess()
            }
        })
        return binding.root
    }

    private fun onSuccess() {
        Toast.makeText(requireContext(),getString(R.string.ResetPasswordToast),Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun initVariables() {
        with(binding){
            viewmodel = _viewModel
            tietResetPasswordEmail.addTextChangedListener(TextWatcher(tilResetPasswordEmail))
        }
    }

    private fun showError(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun showEmailError(message: String) {
        binding.tietResetPasswordEmail.error = message
        binding.tietResetPasswordEmail.requestFocus()
    }

    inner class TextWatcher(var til: TextInputLayout) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            til.error = null
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
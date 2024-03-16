package com.antonioje.cryptowallet.register.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.databinding.FragmentSignUpBinding
import com.antonioje.cryptowallet.register.ui.usecase.SignUpState
import com.antonioje.cryptowallet.register.ui.usecase.SignUpViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpFragment : Fragment() {
    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val _viewModel :SignUpViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater,container,false)
        initVariables()
        return binding.root
    }


    private fun showFirebaseError(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showEmailError(message: String) {
        binding.tietSignUpEmail.error = message
        binding.tietSignUpEmail.requestFocus()
    }

    private fun showPasswordError(message: String){
        binding.tietSignUpPassword.setError(message,null)
        binding.tietSignUpPassword.requestFocus()
    }

    private fun initVariables() {
        with(binding){
            viewmodel = _viewModel
            tietSignUpEmail.addTextChangedListener(TextWatcher(tilSignUpEmail))
            tietSignUpPassword.addTextChangedListener(TextWatcher(tilSignUpPassword))
        }

        _viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it){
                SignUpState.EmailIsEmpty ->  showEmailError(getString(R.string.EmailIsEmptyError))
                SignUpState.EmailFormatError ->  showEmailError(getString(R.string.EmailFormatError))
                SignUpState.PasswordIsEmpty -> showPasswordError(getString(R.string.PasswordIsEmptyError))
                SignUpState.PasswordFormatError -> showPasswordError(getString(R.string.PasswordFormatError))
                is SignUpState.Error -> showFirebaseError(it.message)
                else -> onSuccess()
            }
        })
    }
    private fun onSuccess(){
        findNavController().popBackStack()
    }


    inner class TextWatcher(var til:TextInputLayout):android.text.TextWatcher{
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
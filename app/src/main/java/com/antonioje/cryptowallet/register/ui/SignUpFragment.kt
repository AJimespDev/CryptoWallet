package com.antonioje.cryptowallet.register.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.antonioje.cryptowallet.MainActivity
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.enum.ProviderType
import com.antonioje.cryptowallet.databinding.FragmentSignUpBinding
import com.antonioje.cryptowallet.register.usecase.SignUpState
import com.antonioje.cryptowallet.register.usecase.SignUpViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpFragment : Fragment() {
    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val _viewModel : SignUpViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater,container,false)
        initVariables()
        return binding.root
    }

    private fun initVariables() {
        with(binding){
            viewmodel = _viewModel
            tietSignUpEmail.addTextChangedListener(TextWatcher(tilSignUpEmail))
            tietSignUpPassword.addTextChangedListener(TextWatcher(tilSignUpPassword))
        }
        configImvGoogleSignUp()

        _viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it){
                SignUpState.EmailIsEmpty ->  showEmailError(getString(R.string.EmailIsEmptyError))
                SignUpState.EmailFormatError ->  showEmailError(getString(R.string.EmailFormatError))
                SignUpState.PasswordIsEmpty -> showPasswordError(getString(R.string.PasswordIsEmptyError))
                SignUpState.PasswordFormatError -> showPasswordError(getString(R.string.PasswordFormatError))
                is SignUpState.Error -> showFirebaseError(it.message)
                SignUpState.SuccessWithGoogle -> showHomeWithGoogle()
                else -> showHome()
            }
        })
    }


    //region MetodosDeLosDiferentesEstados
    private fun showEmailError(message: String) {
        binding.tietSignUpEmail.error = message
        binding.tietSignUpEmail.requestFocus()
    }

    private fun showPasswordError(message: String){
        binding.tietSignUpPassword.setError(message,null)
        binding.tietSignUpPassword.requestFocus()
    }

    private fun showFirebaseError(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(){
        var modoAcceso = Bundle()
        modoAcceso.putString(MainActivity.EMAIL,_viewModel.email.value)
        modoAcceso.putString(MainActivity.PROVIDER,ProviderType.BASIC.name)
        modoAcceso.putBoolean(MainActivity.MANTENERSESION,_viewModel.mantenerSesion.value as Boolean)

        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment,modoAcceso)
    }
//endregion

    //region MétodosDeInicioConGoogle

    private fun configImvGoogleSignUp() {
        binding.imvGoogleLogIn.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleclient = GoogleSignIn.getClient(requireActivity(),googleConf)
            googleclient.signOut()

            startActivityForResult(googleclient.signInIntent,MainActivity.GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == MainActivity.GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                val account = task.getResult(ApiException::class.java)
                if(account != null){
                    _viewModel.signUpWithGoogle(account)
                }
            }catch (e: ApiException){
                showFirebaseError(e.message.toString())
            }

        }
    }
    private fun showHomeWithGoogle() {
        var modoAcceso = Bundle()
        modoAcceso.putString(MainActivity.EMAIL,_viewModel.email.value)
        modoAcceso.putString(MainActivity.PROVIDER, ProviderType.GOOGLE.name)
        modoAcceso.putBoolean(MainActivity.MANTENERSESION,true)

        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment,modoAcceso)
    }
//endregion


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
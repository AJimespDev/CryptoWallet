package com.antonioje.cryptowallet.login.ui

import android.app.AlertDialog
import android.content.Context
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
import com.antonioje.cryptowallet.databinding.FragmentSignInBinding
import com.antonioje.cryptowallet.login.usecase.SignInState
import com.antonioje.cryptowallet.login.usecase.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val _viewModel: SignInViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        binding.linearLayout.visibility = View.VISIBLE
        (requireActivity() as MainActivity).setAppBarGone()
        (requireActivity() as MainActivity).setBottomNavGone()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        initVariables()
        session()
        return binding.root
    }

    private fun session() {
        //Obtener la instancia de SharedPreferences
        val sharedPreferences =
            context?.getSharedPreferences("usu_preferencias", Context.MODE_PRIVATE)
        val email = sharedPreferences?.getString(MainActivity.EMAIL, null)
        val provider = sharedPreferences?.getString(MainActivity.PROVIDER, null)

        if (email != null && provider != null) {
            binding.linearLayout.visibility = View.GONE
            showHome()
        }
    }

    private fun initVariables() {
        with(binding) {
            viewmodel = _viewModel

            tietLogInEmail.addTextChangedListener(TextWatcher(tilLogInEmail))
            tietLogInPassword.addTextChangedListener(TextWatcher(tilLogInPassword))

            tvRegisterAccountSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }

            tvForgotPassword.setOnClickListener{
                findNavController().navigate(R.id.action_signInFragment_to_resetPasswordFragment)
            }
        }
        configImvGoogleSignIn()
        _viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                SignInState.EmailIsMandatory -> showEmailError(getString(R.string.EmailIsEmptyError))
                SignInState.EmailFormatInvalid -> showEmailError(getString(R.string.EmailFormatError))
                SignInState.PasswordIsMandatory -> showPasswordError(getString(R.string.PasswordIsEmptyError))
                SignInState.EmailOrPasswordIncorrect -> showError(getString(R.string.EmailOrPasswordIncorrect))
                is SignInState.Error -> showError(it.message)
                SignInState.SuccessWithGoogle -> showHomeWithGoogle()
                SignInState.Success -> showHome()
                else -> {}
            }
        })

    }




    //region MetodosDeLosDiferentesEstados


    private fun showEmailError(message: String) {
        binding.tietLogInEmail.error = message
        binding.tietLogInEmail.requestFocus()
    }

    private fun showPasswordError(message: String) {
        binding.tietLogInPassword.setError(message, null)
        binding.tietLogInPassword.requestFocus()
    }

    private fun showError(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome() {
        var modoAcceso = Bundle()
        modoAcceso.putString(MainActivity.EMAIL, _viewModel.email.value)
        modoAcceso.putString(MainActivity.PROVIDER, ProviderType.BASIC.name)
        modoAcceso.putBoolean(
            MainActivity.MANTENERSESION,
            _viewModel.mantenerSesion.value as Boolean
        )

        _viewModel.setCompleted()

        findNavController().navigate(R.id.action_signInFragment_to_homeFragment, modoAcceso)
    }

    //endregion


    //region  MétodosDeInicioConGoogle

    private fun configImvGoogleSignIn() {
        binding.imvGoogleLogIn.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleclient = GoogleSignIn.getClient(requireActivity(), googleConf)
            googleclient.signOut()

            startActivityForResult(googleclient.signInIntent, MainActivity.GOOGLE_SIGN_IN)
        }
    }


    private fun showHomeWithGoogle() {
        var modoAcceso = Bundle()
        modoAcceso.putString(MainActivity.EMAIL, _viewModel.email.value)
        modoAcceso.putString(MainActivity.PROVIDER, ProviderType.GOOGLE.name)
        modoAcceso.putBoolean(MainActivity.MANTENERSESION, true)

        _viewModel.setCompleted()

        (requireActivity() as MainActivity).bottomNavegationBar.setItemSelected(R.id.nav_market,true)

        findNavController().navigate(R.id.action_signInFragment_to_homeFragment, modoAcceso)
    }
    //endregion


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MainActivity.GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    _viewModel.signInWithGoogle(account)
                }
            } catch (e: ApiException) {
                showError(e.message.toString())
            }

        }
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
package com.antonioje.cryptowallet.register.usecase

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonioje.cryptowallet.login.usecase.SignInState
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignUpViewModel:ViewModel() {
    private var state = MutableLiveData<SignUpState>()

    fun getState(): MutableLiveData<SignUpState> {
        return state
    }

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var mantenerSesion = MutableLiveData<Boolean>(false)

    fun validateCreateAccount(){
        viewModelScope.launch {
            when{
                TextUtils.isEmpty(email.value) -> state.value = SignUpState.EmailIsEmpty
                !isEmailValid(email.value.toString()) -> state.value = SignUpState.EmailFormatError
                TextUtils.isEmpty(password.value) -> state.value = SignUpState.PasswordIsEmpty
                !isPasswordValid(password.value.toString()) -> state.value =
                    SignUpState.PasswordFormatError
                else ->{
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value.toString(),password.value.toString()).addOnCompleteListener {
                        if(it.isSuccessful){
                            state.value = SignUpState.Success
                        }else {
                            state.value = SignUpState.Error(it.exception?.message.toString())
                        }
                    }
                }
            }
        }

    }

    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$"
        val pattern = Pattern.compile(emailRegex)
        return pattern.matcher(email).matches()
    }


    //La contraseña debe contener letras y números, con una longitud mínima de 8 caracteres.
    fun isPasswordValid(password: String): Boolean {
        val letterRegex = ".*[a-zA-Z].*"
        val digitRegex = ".*\\d.*"
        val minLength = 8

        val containsLetter = password.matches(Regex(letterRegex))
        val containsDigit = password.matches(Regex(digitRegex))
        val isLongEnough = password.length >= minLength

        return containsLetter && containsDigit && isLongEnough
    }

    fun signUpWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            var credential = GoogleAuthProvider.getCredential(account.idToken,null)
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                if(it.isSuccessful){
                    email.value = account.email
                    state.value = SignUpState.SuccessWithGoogle
                }else {
                    state.value = SignUpState.Error(it.exception?.message.toString())
                }
            }
        }
    }


}
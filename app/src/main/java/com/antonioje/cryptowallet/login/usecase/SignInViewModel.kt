package com.antonioje.cryptowallet.login.usecase

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignInViewModel:ViewModel() {
    private var state = MutableLiveData<SignInState>()

    fun getState(): MutableLiveData<SignInState> {
        return state
    }

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var mantenerSesion = MutableLiveData<Boolean>(false)

    fun validateLogIn(){
        viewModelScope.launch {
            when{
                TextUtils.isEmpty(email.value) -> state.value = SignInState.EmailIsMandatory
                !isEmailValid(email.value.toString()) -> state.value =  SignInState.EmailFormatInvalid
                TextUtils.isEmpty(password.value) -> state.value = SignInState.PasswordIsMandatory
                else -> {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value.toString(),password.value.toString()).addOnCompleteListener {
                        if(it.isSuccessful){
                            state.value = SignInState.Success
                        }else {
                            state.value = SignInState.EmailOrPasswordIncorrect
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

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            var credential = GoogleAuthProvider.getCredential(account.idToken,null)
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                if(it.isSuccessful){
                    email.value = account.email
                    state.value = SignInState.SuccessWithGoogle
                }else {
                    state.value = SignInState.Error(it.exception?.message.toString())
                }
            }
        }
    }


    fun setCompleted() {
        state.value = SignInState.Completed
    }
}
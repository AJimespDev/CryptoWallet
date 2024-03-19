package com.antonioje.cryptowallet.login.resetpassword.usecase

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class ResetPasswordViewModel:ViewModel() {
    private var state = MutableLiveData<ResetPasswordState>()

    fun getState(): MutableLiveData<ResetPasswordState> {
        return state
    }

    var email = MutableLiveData<String>()

    fun resetPassword(){
        viewModelScope.launch {
            when{
                TextUtils.isEmpty(email.value) -> state.value = ResetPasswordState.EmailIsEmpty
                !isEmailValid(email.value.toString()) -> state.value =
                    ResetPasswordState.ErrorEmailFormat
                else -> {
                    sendEmail()
                }
            }
        }
    }

    private fun sendEmail() {
        var auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email.value.toString()).addOnCompleteListener {
            if(it.isSuccessful){
                state.value = ResetPasswordState.Success
            }else{
                state.value = ResetPasswordState.Error(it.exception?.message.toString())
            }
        }

    }

    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$"
        val pattern = Pattern.compile(emailRegex)
        return pattern.matcher(email).matches()
    }

}
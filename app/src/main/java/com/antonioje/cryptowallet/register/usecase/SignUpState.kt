package com.antonioje.cryptowallet.register.usecase

import com.antonioje.cryptowallet.login.usecase.SignInState

sealed class SignUpState {
    data object EmailIsEmpty: SignUpState()
    data object EmailFormatError: SignUpState()
    data object PasswordIsEmpty: SignUpState()
    data object PasswordFormatError: SignUpState()
    data class Error(val message:String): SignUpState()
    data object Success: SignUpState()
    data object SuccessWithGoogle: SignUpState()
}
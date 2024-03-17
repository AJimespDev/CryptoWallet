package com.antonioje.cryptowallet.login.usecase

import java.security.MessageDigest

sealed class SignInState {
    data object EmailIsMandatory:SignInState()
    data object EmailFormatInvalid:SignInState()
    data object PasswordIsMandatory:SignInState()
    data object EmailOrPasswordIncorrect:SignInState()
    data object Success:SignInState()
    data object SuccessWithGoogle:SignInState()
    data class Error(val message: String):SignInState()
    data object Completed:SignInState()
}
package com.antonioje.cryptowallet.register.ui.usecase

sealed class SignUpState {
    data object EmailIsEmpty:SignUpState()
    data object EmailFormatError:SignUpState()
    data object PasswordIsEmpty:SignUpState()
    data object PasswordFormatError:SignUpState()
    data class Error(var message:String):SignUpState()
    data object Success:SignUpState()
}
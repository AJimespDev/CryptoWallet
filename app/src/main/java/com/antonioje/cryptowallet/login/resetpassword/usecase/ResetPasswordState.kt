package com.antonioje.cryptowallet.login.resetpassword.usecase

sealed class ResetPasswordState {
    data object EmailIsEmpty: ResetPasswordState()
    data object ErrorEmailFormat: ResetPasswordState()
    data class Error(val message:String): ResetPasswordState()
    data object Success: ResetPasswordState()
}
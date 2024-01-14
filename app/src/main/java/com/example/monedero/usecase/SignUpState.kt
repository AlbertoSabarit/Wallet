package com.example.monedero.usecase

sealed class SignUpState {

    data object NombreEmptyError : SignUpState()
    data object PasswordEmptyError: SignUpState()
    data object PasswordEmptyError2: SignUpState()
    data object PasswordsNotEquals: SignUpState()
    data class UsuarioExiste(var message: String): SignUpState()
    data class Loading(var value: Boolean) : SignUpState()
    data object Success: SignUpState()
}
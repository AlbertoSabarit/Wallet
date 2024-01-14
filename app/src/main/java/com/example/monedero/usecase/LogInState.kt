package com.example.monedero.usecase

import com.example.monedero.data.Usuario

sealed class LogInState {

    data object NombreEmptyError : LogInState()
    data class UsuarioNoExiste(var message: String): LogInState()
    data class Loading(var value: Boolean) : LogInState()
    data class Success(var data : Usuario): LogInState()
}
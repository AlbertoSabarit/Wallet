package com.example.monedero.usecase

sealed class CuentaCreationState {

    data object EntidadEmptyError : CuentaCreationState()
    data object OficinaEmptyError : CuentaCreationState()
    data object DCEmptyError : CuentaCreationState()
    data object NCuentaEmptyError : CuentaCreationState()
    data object EntidadLengthError : CuentaCreationState()
    data object OficinaLengthError : CuentaCreationState()
    data object DCLengthError : CuentaCreationState()
    data object NCuentaLengthError : CuentaCreationState()
    data class cuentaExiste(var message: String): CuentaCreationState()
    data class Loading(var value: Boolean) : CuentaCreationState()
    data object Success: CuentaCreationState()
}
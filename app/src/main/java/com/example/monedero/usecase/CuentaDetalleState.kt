package com.example.monedero.usecase


sealed class CuentaDetalleState {

    data object IngresarEmpty : CuentaDetalleState()
    data object RetirarEmpty : CuentaDetalleState()
    data object FormatErrorIngresar : CuentaDetalleState()
    data object FormatErrorRetirar : CuentaDetalleState()
    data object FormatSobrepasaRetirada : CuentaDetalleState()
    data object IngresoAdvertenciaHacienda : CuentaDetalleState()
    data object RetiradaAdvertenciaHacienda : CuentaDetalleState()
    data object IngresoDenegadoHacienda : CuentaDetalleState()
    data object RetiradaDenegadaHacienda : CuentaDetalleState()
    data class Loading(var value: Boolean) : CuentaDetalleState()
    data object Success : CuentaDetalleState()
}

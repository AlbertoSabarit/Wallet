package com.example.monedero.usecase

import com.example.monedero.data.Cuenta

sealed class CuentaListState {

    data object NoDataList: CuentaListState()
    data class Loading(val value: Boolean): CuentaListState()
    data class Success(var dataset : ArrayList<Cuenta>): CuentaListState()
}
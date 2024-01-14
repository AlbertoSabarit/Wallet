package com.example.monedero.usecase

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monedero.data.Cuenta
import com.example.monedero.network.Resource
import com.example.monedero.repository.CuentasRepositorio
import kotlinx.coroutines.launch

class CuentaDetalleViewModel: ViewModel() {

    var ingresar = MutableLiveData<String>()
    var retirar = MutableLiveData<String>()

    var state = MutableLiveData<CuentaDetalleState>()

    fun getState(): LiveData<CuentaDetalleState> {
        return state
    }

    fun ingresarDineroCuenta(cuenta: Cuenta) {

        when {
            TextUtils.isEmpty(ingresar.value) -> state.value = CuentaDetalleState.IngresarEmpty
            !validarFormato(ingresar.value!!) -> state.value = CuentaDetalleState.FormatErrorIngresar

            else -> {
                viewModelScope.launch {
                    state.value = CuentaDetalleState.Loading(true)

                    val cantidad: Double? = ingresar.value!!.toDoubleOrNull()

                    if (cantidad != null) {
                        val result = CuentasRepositorio.ingresarDineroCuenta(cuenta)
                        state.value = CuentaDetalleState.Loading(false)

                        when (result) {
                            is Resource.Error -> {
                                when {
                                    cantidad <= Cuenta.LIMITE_FRAUDE * 2 -> {

                                        state.value = CuentaDetalleState.IngresoAdvertenciaHacienda
                                    }
                                    cantidad > Cuenta.LIMITE_FRAUDE * 2 -> {

                                        state.value = CuentaDetalleState.IngresoDenegadoHacienda
                                    }
                                }
                            }
                            is Resource.Success<*> -> state.value = CuentaDetalleState.Success
                        }
                    } else {
                        state.value = CuentaDetalleState.FormatErrorIngresar
                    }
                }
            }
        }
    }

    fun retirarDineroCuenta(cuenta: Cuenta) {

        when {
            TextUtils.isEmpty(retirar.value) -> state.value = CuentaDetalleState.RetirarEmpty
            !validarFormato(retirar.value!!) -> state.value = CuentaDetalleState.FormatErrorRetirar

            else -> {
                viewModelScope.launch {
                    state.value = CuentaDetalleState.Loading(true)

                    val cantidad: Double? = retirar.value!!.toDoubleOrNull()

                    if (cantidad != null) {
                        val result = CuentasRepositorio.retirarDineroCuenta(cuenta)
                        state.value = CuentaDetalleState.Loading(false)

                        when (result) {
                            is Resource.Error -> {
                                when {
                                    cantidad > cuenta.saldo!! -> {
                                        state.value = CuentaDetalleState.FormatSobrepasaRetirada
                                    }
                                    cantidad <= Cuenta.LIMITE_FRAUDE * 2 -> {
                                        state.value = CuentaDetalleState.RetiradaAdvertenciaHacienda
                                    }
                                    cantidad > Cuenta.LIMITE_FRAUDE * 2 -> {
                                        state.value = CuentaDetalleState.RetiradaDenegadaHacienda
                                    }
                                }
                            }
                            is Resource.Success<*> -> state.value = CuentaDetalleState.Success
                        }
                    } else {
                        state.value = CuentaDetalleState.FormatErrorRetirar
                    }
                }
            }
        }
    }


    private fun validarFormato(saldo: String): Boolean {
        return try {
            val intSaldo = saldo.toDouble()
            intSaldo > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

}
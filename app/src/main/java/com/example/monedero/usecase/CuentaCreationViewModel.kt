package com.example.monedero.usecase

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monedero.data.Cuenta
import com.example.monedero.network.Resource
import com.example.monedero.repository.CuentasRepositorio
import kotlinx.coroutines.launch

class CuentaCreationViewModel: ViewModel() {

    var entidad = MutableLiveData<String>()
    var oficina = MutableLiveData<String>()
    var dc = MutableLiveData<String>()
    var numeroCuenta = MutableLiveData<String>()

    var state = MutableLiveData<CuentaCreationState>()

    fun getState(): LiveData<CuentaCreationState> {
        return state
    }

    fun crearCuenta(cuenta: Cuenta) {

        when {
            TextUtils.isEmpty(entidad.value) -> state.value = CuentaCreationState.EntidadEmptyError
            TextUtils.isEmpty(oficina.value) -> state.value = CuentaCreationState.OficinaEmptyError
            TextUtils.isEmpty(dc.value) -> state.value = CuentaCreationState.DCEmptyError
            TextUtils.isEmpty(numeroCuenta.value) -> state.value = CuentaCreationState.NCuentaEmptyError
            !validarLongitud4(entidad.value!!) -> state.value = CuentaCreationState.EntidadLengthError
            !validarLongitud4(oficina.value!!) -> state.value = CuentaCreationState.OficinaLengthError
            !validarLongitud2(dc.value!!) -> state.value = CuentaCreationState.DCLengthError
            !validarLongitud10(numeroCuenta.value!!) -> state.value = CuentaCreationState.NCuentaLengthError

            else -> {
                viewModelScope.launch {
                    state.value = CuentaCreationState.Loading(true)
                    val result = CuentasRepositorio.crearCuenta(cuenta)
                    state.value = CuentaCreationState.Loading(false)

                    when (result) {
                        is Resource.Error -> state.value =
                            CuentaCreationState.cuentaExiste(result.exception.message!!)

                        is Resource.Success<*> -> state.value = CuentaCreationState.Success
                    }
                }
            }
        }
    }

    fun validarLongitud4(numero : String): Boolean{
        return numero.length == 4
    }
    fun validarLongitud2(numero : String): Boolean{
        return numero.length == 2
    }
    fun validarLongitud10(numero : String): Boolean{
        return numero.length == 10
    }
}
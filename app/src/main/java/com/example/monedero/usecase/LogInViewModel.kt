package com.example.monedero.usecase

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monedero.data.Usuario
import com.example.monedero.network.Resource
import com.example.monedero.repository.UsuarioRepositorio
import kotlinx.coroutines.launch

class LogInViewModel: ViewModel() {

    var nombre = MutableLiveData<String>()

    var state = MutableLiveData<LogInState>()

    fun getState(): LiveData<LogInState> {
        return state
    }

    fun validarUsuario(usuario: Usuario) {

        when {
            TextUtils.isEmpty(nombre.value) -> state.value = LogInState.NombreEmptyError

            else -> {
                viewModelScope.launch {
                    state.value = LogInState.Loading(true)
                    val result = UsuarioRepositorio.verificarUsuario(usuario)
                    state.value = LogInState.Loading(false)

                    when (result) {
                        is Resource.Error -> state.value =
                            LogInState.UsuarioNoExiste(result.exception.message!!)

                        is Resource.Success<*> -> state.value = LogInState.Success(usuario)
                    }
                }
            }
        }
    }

}
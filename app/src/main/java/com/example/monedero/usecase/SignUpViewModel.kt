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

class SignUpViewModel : ViewModel() {

    var nombre = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var password2 = MutableLiveData<String>()

    var state = MutableLiveData<SignUpState>()

    fun getState(): LiveData<SignUpState> {
        return state
    }

    fun validarCredenciales(usuario: Usuario) {

        when {
            TextUtils.isEmpty(nombre.value) -> state.value = SignUpState.NombreEmptyError
            TextUtils.isEmpty(password.value) -> state.value = SignUpState.PasswordEmptyError
            TextUtils.isEmpty(password2.value) -> state.value = SignUpState.PasswordEmptyError2
            !isEqualPasswords(password.value!!, password2.value!!) -> state.value =
                SignUpState.PasswordsNotEquals

            else -> {
                viewModelScope.launch {
                    state.value = SignUpState.Loading(true)
                    val result = UsuarioRepositorio.crearUsuario(usuario)
                    state.value = SignUpState.Loading(false)

                    when (result) {
                        is Resource.Error -> state.value =
                            SignUpState.UsuarioExiste(result.exception.message!!)

                        is Resource.Success<*> -> state.value = SignUpState.Success
                    }
                }
            }
        }
    }

    private fun isEqualPasswords(pass1: String, pass2: String): Boolean {
        return pass1 == pass2
    }
}
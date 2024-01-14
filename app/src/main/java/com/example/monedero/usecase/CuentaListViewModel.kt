package com.example.monedero.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monedero.data.Cuenta
import com.example.monedero.data.Usuario
import com.example.monedero.network.ResourceList
import com.example.monedero.repository.CuentasRepositorio
import kotlinx.coroutines.launch

class CuentaListViewModel: ViewModel() {

    private var state = MutableLiveData<CuentaListState>()

    fun getState() : LiveData<CuentaListState>{
        return state
    }

    fun getCuentaList(usuario: Usuario) {

        viewModelScope.launch {

            state.value = CuentaListState.Loading(true)
            val result = CuentasRepositorio.getCuentaList(usuario)
            state.value = CuentaListState.Loading(false)

            when(result){
                is ResourceList.NoData -> state.value = CuentaListState.NoDataList
                is ResourceList.Success<*> -> {
                    (result.data as ArrayList<Cuenta>).sort()
                    state.value = CuentaListState.Success(result.data as ArrayList<Cuenta>)
                }
            }
        }
    }
}
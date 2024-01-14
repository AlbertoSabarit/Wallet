package com.example.monedero.repository

import com.example.monedero.data.Usuario
import com.example.monedero.network.Resource
import com.example.monedero.network.ResourceList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.Exception

class UsuarioRepositorio private constructor() {

    companion object {

        var dataSet = mutableListOf<Usuario>()

        init {
            initUsuarioRepository()
        }

        fun initUsuarioRepository() {
            dataSet.add(Usuario("Alberto", "9999"))
            dataSet.add(Usuario("Lourdes", "1818"))
        }

        suspend fun crearUsuario(usuario: Usuario): Resource {
            return withContext(Dispatchers.IO) {
                delay(1000)
                if (dataSet.any { it.nombre!!.equals(usuario.nombre!!, ignoreCase = true) }) {
                    Resource.Error(Exception("El usuario ya existe en la base de datos"))
                } else {
                    dataSet.add(usuario)
                    Resource.Success(dataSet)
                }
            }
        }

        suspend fun verificarUsuario(usuario: Usuario): Resource {
            return withContext(Dispatchers.IO) {
                delay(1000)
                if (dataSet.isEmpty() || dataSet.none { it.nombre!!.equals(usuario.nombre!!, ignoreCase = true) } || dataSet.none { it.password == usuario.password }) {
                    Resource.Error(Exception("El usuario no existe"))
                } else {
                    Resource.Success(usuario)
                }
            }
        }

    }
}
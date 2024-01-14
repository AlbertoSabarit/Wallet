package com.example.monedero.network

import java.lang.Exception

sealed class Resource {

    data class Success<T>(var data: T?) : Resource()
    data class Error(var exception: Exception) : Resource()

}
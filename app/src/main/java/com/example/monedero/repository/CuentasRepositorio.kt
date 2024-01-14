package com.example.monedero.repository

import com.example.monedero.data.Cuenta
import com.example.monedero.data.Usuario
import com.example.monedero.network.Resource
import com.example.monedero.network.ResourceList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.ArrayList

class CuentasRepositorio private constructor(){

    companion object {

        var dataSet = mutableListOf<Cuenta>()

        init {
            initCuentasRepository()
        }

        fun initCuentasRepository() {
            dataSet.add(Cuenta("ES6677921234661237856686", 7000.15, Usuario("Alberto", "9999")))
            dataSet.add(Cuenta("ES2345961234231234532411", 8160.90, Usuario("Lourdes", "1818")))
            dataSet.add(Cuenta("ES1452341234149056576190", 16450.02, Usuario("Alberto", "9999")))
            dataSet.add(Cuenta("ES2345961234231234532443", 9552.75, Usuario("Lourdes", "1818")))
            dataSet.add(Cuenta("ES1177921234111724284936", 3460.05, Usuario("Alberto", "9999")))
            dataSet.add(Cuenta("ES0045961234001234532324", 127.90, Usuario("Lourdes", "1818")))
            dataSet.add(Cuenta("ES9867341234989011092827", 1346.02, Usuario("Alberto", "9999")))
            dataSet.add(Cuenta("ES5245961234521230977353", 0.75, Usuario("Lourdes", "1818")))
        }

        suspend fun getCuentaList(usuario: Usuario): ResourceList {
            return withContext(Dispatchers.IO) {
                delay(1000)
                val cuentasFiltradas = dataSet.filter { it.usuario == usuario || it.usuario?.nombre.equals(usuario.nombre, ignoreCase = true) }

                when {
                    cuentasFiltradas.isEmpty() -> ResourceList.NoData(Exception("No hay cuentas que mostrar para este usuario"))
                    else -> ResourceList.Success(cuentasFiltradas as ArrayList<Cuenta>)
                }
            }
        }

        suspend fun crearCuenta(cuenta: Cuenta): Resource {
            return withContext(Dispatchers.IO) {
                delay(1000)
                if (dataSet.any { it.numeroCuenta == cuenta.numeroCuenta && it.usuario.nombre == cuenta.usuario.nombre}) {
                    Resource.Error(Exception("La cuenta ya existe"))
                } else {
                    dataSet.add(cuenta)
                    Resource.Success(dataSet)
                }
            }
        }

        suspend fun ingresarDineroCuenta(cuenta: Cuenta): Resource {
            return withContext(Dispatchers.IO) {
                delay(1000)
                val nuevaCantidad = cuenta.nuevaCantidad
                if (nuevaCantidad <= Cuenta.LIMITE_FRAUDE) {
                    cuenta.ingresarSaldo(nuevaCantidad)
                    Resource.Success(dataSet)
                } else if (nuevaCantidad <= Cuenta.LIMITE_FRAUDE * 2) {
                    cuenta.ingresarSaldo(nuevaCantidad)
                    Resource.Error(Exception("Se informará a Hacienda y se almacenará como movimiento de control de fraude"))
                } else {
                    Resource.Error(Exception("No se permite el movimiento. Cantidad superior al doble del límite"))
                }
            }
        }
        suspend fun retirarDineroCuenta(cuenta: Cuenta): Resource {
            return withContext(Dispatchers.IO) {
                delay(1000)
                val nuevaCantidad = cuenta.nuevaCantidad
                val saldoActual = cuenta.saldo ?: 0.0

                if (nuevaCantidad <= 0) {
                    Resource.Error(Exception("La cantidad a retirar debe ser mayor a cero"))
                } else if (nuevaCantidad > saldoActual) {
                    Resource.Error(Exception("No se puede retirar. Cantidad superior al saldo actual"))
                } else if (nuevaCantidad <= Cuenta.LIMITE_FRAUDE) {
                    cuenta.retirarSaldo(nuevaCantidad)
                    Resource.Success(dataSet)
                } else if (nuevaCantidad <= Cuenta.LIMITE_FRAUDE * 2) {
                    cuenta.retirarSaldo(nuevaCantidad)
                    Resource.Error(Exception("Movimiento de control de fraude. Se informará a hacienda"))
                } else {
                    Resource.Error(Exception("No se permite el movimiento. Cantidad superior al doble del límite"))
                }
            }
        }



        suspend fun retirarDineroCuenta2(cuenta: Cuenta): Resource {
            return withContext(Dispatchers.IO) {
                delay(1000)
                if (!cuenta.retirarSaldo(cuenta.nuevaCantidad)) {
                    Resource.Error(Exception("No se puede retirar"))
                } else {
                    Resource.Success(dataSet)
                }
            }
        }

    }
}
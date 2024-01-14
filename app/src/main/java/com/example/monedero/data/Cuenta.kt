package com.example.monedero.data

import android.os.Parcel
import android.os.Parcelable

class Cuenta(val numeroCuenta: String?, var saldo: Double?, var usuario: Usuario) :
    Comparable<Cuenta>, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readParcelable(Usuario::class.java.classLoader)!!
    ) {
    }

    var nuevaCantidad: Double = 0.0

    fun ingresarSaldo(cantidad: Double?): Boolean {
        saldo = saldo?.plus(cantidad!!)
        return true
    }

    fun retirarSaldo(cantidad: Double?): Boolean {
        return when {
            cantidad == null || cantidad <= 0 -> false
            cantidad > saldo!! -> {
                false
            }

            else -> {
                saldo = saldo?.minus(cantidad)
                true
            }
        }
    }

    override fun compareTo(other: Cuenta): Int {
        return numeroCuenta!!.compareTo(other.numeroCuenta!!)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(numeroCuenta)
        parcel.writeValue(saldo)
        parcel.writeParcelable(usuario, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cuenta> {

        const val LIMITE_FRAUDE = 1000.0

        val TAG = "Cuenta"

        fun create(numeroCuenta: String?, saldo: Double?, usuario: Usuario): Cuenta {
            val cuenta = Cuenta(
                numeroCuenta = numeroCuenta,
                saldo = saldo,
                usuario = usuario
            )
            return cuenta
        }

        override fun createFromParcel(parcel: Parcel): Cuenta {
            return Cuenta(parcel)
        }

        override fun newArray(size: Int): Array<Cuenta?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.monedero.data

import android.os.Parcel
import android.os.Parcelable

class Usuario(
    val nombre: String?,
    val password: String?
) : Comparable<Usuario>, Parcelable {

    var id: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
        id = parcel.readInt()
    }

    override fun compareTo(other: Usuario): Int {
        return nombre!!.compareTo(other.nombre!!)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(nombre)
        dest.writeString(password)
        dest.writeInt(id)
    }

    companion object CREATOR : Parcelable.Creator<Usuario> {

        val TAG = "usuario"
        private var idUsuario: Int = 0

        fun create(nombre: String?, password: String?): Usuario {
            val usuario = Usuario(
                nombre = nombre,
                password = password
            )
            usuario.id = ++idUsuario
            return usuario
        }


        override fun createFromParcel(parcel: Parcel): Usuario {
            return Usuario(parcel)
        }

        override fun newArray(size: Int): Array<Usuario?> {
            return arrayOfNulls(size)
        }
    }
}

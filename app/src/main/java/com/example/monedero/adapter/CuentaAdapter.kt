package com.example.monedero.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monedero.data.Cuenta
import com.example.monedero.databinding.CuentaListBinding

class CuentaAdapter(val listener: onClickCuenta) :
    RecyclerView.Adapter<CuentaAdapter.CuentaViewHolder>() {

    var dataset = arrayListOf<Cuenta>()

    interface onClickCuenta {
        fun onClickDetallesCuenta(cuenta: Cuenta)
    }

    inner class CuentaViewHolder(val binding: CuentaListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cuenta: Cuenta) {
            with(binding) {
                val saldoRedondeado = String.format("%.2f", cuenta.saldo)
                tvNumeroCuenta.text = cuenta.numeroCuenta
                tvSaldo.text = "$saldoRedondeado€"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuentaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CuentaViewHolder(CuentaListBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: CuentaViewHolder, position: Int) {
        var cuenta = dataset[position]
        holder.bind(cuenta)

        holder.binding.root.setOnClickListener {
            listener.onClickDetallesCuenta(cuenta)
        }
    }

    fun update(newDataSet: ArrayList<Cuenta>) {
        dataset = newDataSet
        notifyDataSetChanged()
    }

    fun sort() {
        dataset.sortBy { it.saldo }
        notifyDataSetChanged()
    }
}
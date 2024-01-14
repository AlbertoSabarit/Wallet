package com.example.monedero.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.monedero.BaseFragmentDialog
import com.example.monedero.R
import com.example.monedero.data.Cuenta
import com.example.monedero.data.Usuario.CREATOR.TAG
import com.example.monedero.databinding.FragmentDetallesCuentaBinding
import com.example.monedero.usecase.CuentaDetalleState
import com.example.monedero.usecase.CuentaDetalleViewModel
import com.google.android.material.textfield.TextInputLayout

class DetallesCuentaFragment : Fragment() {

    private var _binding: FragmentDetallesCuentaBinding? = null

    private val binding get() = _binding!!

    private val viewModel: CuentaDetalleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetallesCuentaBinding.inflate(inflater, container, false)
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tieIngresar.addTextChangedListener(ErrorWatchers(binding.tilIngresar))
        binding.tieRetirar.addTextChangedListener(ErrorWatchers(binding.tilRetirar))

        val cuenta = requireArguments().getParcelable<Cuenta>(Cuenta.TAG)

        with(binding) {
            tvNumeroIBAN.text = cuenta!!.numeroCuenta.toString()
            tvSaldo.text = cuenta!!.saldo.toString() + "€"
        }

        binding.btnIngresar.setOnClickListener {
            val cantidad: Double? = binding.tieIngresar.text.toString().toDoubleOrNull()

            if (cantidad != null) {
                cuenta!!.nuevaCantidad = cantidad
            } else {
                Toast.makeText(context, "Entrada inválida", Toast.LENGTH_SHORT).show()
            }
            viewModel.ingresarDineroCuenta(cuenta!!)
        }

        binding.btnRetirar.setOnClickListener {
            val cantidad: Double? = binding.tieRetirar.text.toString().toDoubleOrNull()

            if (cantidad != null) {
                cuenta!!.nuevaCantidad = cantidad

            } else {
                Toast.makeText(context, "Entrada inválida", Toast.LENGTH_SHORT).show()
            }
            viewModel.retirarDineroCuenta(cuenta!!)

        }

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                CuentaDetalleState.IngresarEmpty -> setIngresarEmpty()
                CuentaDetalleState.RetirarEmpty -> setRetirarEmpty()
                CuentaDetalleState.FormatErrorIngresar -> setFormatErrorIngresar()
                CuentaDetalleState.FormatErrorRetirar -> setFormatErrorRetirar()
                CuentaDetalleState.FormatSobrepasaRetirada -> setFormatSobrepasaRetirada()
                CuentaDetalleState.IngresoAdvertenciaHacienda -> setIngresoAdvertenciaHacienda()
                CuentaDetalleState.RetiradaAdvertenciaHacienda -> setRetiradaAdvertenciaHacienda()
                CuentaDetalleState.IngresoDenegadoHacienda -> setIngresoDenegadoHacienda()
                CuentaDetalleState.RetiradaDenegadaHacienda -> setRetiradaDenegadaHacienda()
                is CuentaDetalleState.Loading -> onLoading(it.value)
                CuentaDetalleState.Success -> onSuccess()
            }
        })
    }




    private fun onSuccess() {
        Toast.makeText(context, "Operación realizada", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun onLoading(value: Boolean) {
        if (value) {
            findNavController().navigate(R.id.action_detallesCuentaFragment_to_fragmentProgressDialog)
        } else {
            findNavController().popBackStack()
        }
    }
    private fun setRetiradaDenegadaHacienda() {
        binding.tilRetirar.error = "Denegada"
        binding.tilRetirar.requestFocus()

        val dialog = BaseFragmentDialog.newInstance("Atención", "Retirada denegada")

        dialog.show((context as AppCompatActivity).supportFragmentManager, TAG)
        dialog.parentFragmentManager.setFragmentResultListener(
            BaseFragmentDialog.request,
            viewLifecycleOwner
        ) { _, _ ->

        }
    }

    private fun setIngresoDenegadoHacienda() {
        binding.tilIngresar.error = "Denegado"
        binding.tilIngresar.requestFocus()

        val dialog = BaseFragmentDialog.newInstance("Atención", "Ingreso denegado")

        dialog.show((context as AppCompatActivity).supportFragmentManager, TAG)
        dialog.parentFragmentManager.setFragmentResultListener(
            BaseFragmentDialog.request,
            viewLifecycleOwner
        ) { _, _ ->

        }
    }

    private fun setIngresoAdvertenciaHacienda() {

        val dialog = BaseFragmentDialog.newInstance("Advertencia", "Se informará a Hacienda sobre el ingreso.")
        dialog.show((context as AppCompatActivity).supportFragmentManager, TAG)
        dialog.parentFragmentManager.setFragmentResultListener(
            BaseFragmentDialog.request,
            viewLifecycleOwner
        ) { _, _ ->

        }
        findNavController().popBackStack()
    }

    private fun setRetiradaAdvertenciaHacienda() {

        val dialog = BaseFragmentDialog.newInstance("Advertencia", "Se informará a Hacienda sobre la retirada.")
        dialog.show((context as AppCompatActivity).supportFragmentManager, TAG)
        dialog.parentFragmentManager.setFragmentResultListener(
            BaseFragmentDialog.request,
            viewLifecycleOwner
        ) { _, _ ->

        }
        findNavController().popBackStack()
    }

    private fun setFormatSobrepasaRetirada() {
        binding.tilRetirar.error = "Saldo menor"
        binding.tilRetirar.requestFocus()
    }

    private fun setFormatErrorRetirar() {
        binding.tilRetirar.error = "Formato inválido"
        binding.tilRetirar.requestFocus()
    }

    private fun setFormatErrorIngresar() {
        binding.tilIngresar.error = "Formato inválido"
        binding.tilIngresar.requestFocus()
    }

    private fun setRetirarEmpty() {
        binding.tilRetirar.error = "Vacío"
        binding.tilRetirar.requestFocus()
    }

    private fun setIngresarEmpty() {
        binding.tilIngresar.error = "Vacío"
        binding.tilIngresar.requestFocus()
    }

    inner class ErrorWatchers(val til: TextInputLayout) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            til.error = null
        }
    }
}
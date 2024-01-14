package com.example.monedero.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.monedero.R
import com.example.monedero.data.Cuenta
import com.example.monedero.data.Usuario
import com.example.monedero.databinding.FragmentAccountCreationBinding
import com.example.monedero.usecase.CuentaCreationState
import com.example.monedero.usecase.CuentaCreationViewModel
import com.google.android.material.textfield.TextInputLayout

class AccountCreationFragment : Fragment() {

    private var _binding: FragmentAccountCreationBinding? = null

    private val binding get() = _binding!!

    private val viewModel: CuentaCreationViewModel by viewModels()

    val IBAN = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAccountCreationBinding.inflate(inflater, container, false)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tieEntidad.addTextChangedListener(ErrorWatchers(binding.tilEntidad))
        binding.tieOficina.addTextChangedListener(ErrorWatchers(binding.tilOficina))
        binding.tieDC.addTextChangedListener(ErrorWatchers(binding.tilDC))
        binding.tieEntidad.addTextChangedListener(ErrorWatchers(binding.tilNCuenta))

        val usuario = requireArguments().getParcelable<Usuario>(Usuario.TAG)

        binding.btnCrearCuenta.setOnClickListener {
            val pais = binding.spCodigo.selectedItem.toString()
            val entidad = binding.tieEntidad.text.toString()
            val oficina = binding.tieOficina.text.toString()
            val dc = binding.tieDC.text.toString()
            val nCuenta = binding.tieNCuenta.text.toString()

            val IBAN = StringBuilder().apply {
                append(pais)
                append(dc)
                append(entidad)
                append(oficina)
                append(dc)
                append(nCuenta)
            }
            val cuenta = Cuenta.create(IBAN.toString(), 0.0, usuario!!)
            viewModel.crearCuenta(cuenta)
        }

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                CuentaCreationState.EntidadEmptyError -> setEntidadEmptyError()
                CuentaCreationState.OficinaEmptyError -> setOficinaEmptyError()
                CuentaCreationState.DCEmptyError -> setDCEmptyError()
                CuentaCreationState.NCuentaEmptyError -> setNCuentaEmptyError()
                CuentaCreationState.EntidadLengthError -> setEntidadLengthError()
                CuentaCreationState.OficinaLengthError -> setOficinaLengthError()
                CuentaCreationState.DCLengthError -> setDCLengthError()
                CuentaCreationState.NCuentaLengthError -> setNCuentaLengthError()
                is CuentaCreationState.Loading -> onLoading(it.value)
                is CuentaCreationState.cuentaExiste -> setCuentaExiste(it.message)
                CuentaCreationState.Success -> onSuccess()
            }
        })
    }

    private fun setNCuentaLengthError() {
        binding.tilNCuenta.error = "Error"
        binding.tilNCuenta.requestFocus()
    }

    private fun setDCLengthError() {
        binding.tilDC.error = "Error"
        binding.tilDC.requestFocus()
    }

    private fun setOficinaLengthError() {
        binding.tilOficina.error = "Error"
        binding.tilOficina.requestFocus()
    }

    private fun setEntidadLengthError() {
        binding.tilEntidad.error = "Error"
        binding.tilEntidad.requestFocus()
    }

    private fun onSuccess() {
        Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun setCuentaExiste(message: String) {
        binding.tvErrorCuentaExiste.visibility = View.VISIBLE
    }

    private fun onLoading(value: Boolean) {
        if (value) {
            findNavController().navigate(R.id.action_accountCreationFragment_to_fragmentProgressDialog)
        } else {
            findNavController().popBackStack()
        }
    }

    private fun setNCuentaEmptyError() {
        binding.tilNCuenta.error = "Vacío"
        binding.tilNCuenta.requestFocus()
    }

    private fun setDCEmptyError() {
        binding.tilDC.error = "Vacío"
        binding.tilDC.requestFocus()
    }

    private fun setOficinaEmptyError() {
        binding.tilOficina.error = "Vacío"
        binding.tilOficina.requestFocus()
    }

    private fun setEntidadEmptyError() {
        binding.tilEntidad.error = "Vacío"
        binding.tilEntidad.requestFocus()
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


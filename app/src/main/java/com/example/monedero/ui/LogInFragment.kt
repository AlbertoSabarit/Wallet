package com.example.monedero.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.monedero.R
import com.example.monedero.data.Usuario
import com.example.monedero.databinding.FragmentLogInBinding
import com.example.monedero.usecase.LogInState
import com.example.monedero.usecase.LogInViewModel
import com.google.android.material.textfield.TextInputLayout


class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null

    private val binding get() = _binding!!

    private val viewModel: LogInViewModel by viewModels()

    val passwordBuilder = StringBuilder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tieNombre.addTextChangedListener(ErrorWatchers(binding.tilNombre))

        addPasword()

        binding.btnRegistrar.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }

        binding.btnAceptar.setOnClickListener {
            val usuario = Usuario.create(binding.tieNombre.text.toString().lowercase(), passwordBuilder.toString())
            viewModel.validarUsuario(usuario)
        }

        binding.btnBorrar.setOnClickListener {
            reiniciarPassword()
        }

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                LogInState.NombreEmptyError -> setNombreEmptyError()
                is LogInState.UsuarioNoExiste -> setUsuarioNoExiste(it.message)
                is LogInState.Loading -> onLoading(it.value)
                is LogInState.Success -> onSuccess(it.data)
            }
        })
    }

    private fun setUsuarioNoExiste(message: String) {
        binding.tilNombre.error = "Usuario/contraseña no válidos"
        binding.tilNombre.requestFocus()
    }

    fun addPasword(){

        binding.button1.setOnClickListener { crearPassword("1") }
        binding.button2.setOnClickListener { crearPassword("2") }
        binding.button3.setOnClickListener { crearPassword("3") }
        binding.button4.setOnClickListener { crearPassword("4") }
        binding.button5.setOnClickListener { crearPassword("5") }
        binding.button6.setOnClickListener { crearPassword("6") }
        binding.button7.setOnClickListener { crearPassword("7") }
        binding.button8.setOnClickListener { crearPassword("8") }
        binding.button9.setOnClickListener { crearPassword("9") }
        binding.button0.setOnClickListener { crearPassword("0") }
    }

    private fun crearPassword(numero: String) {
        passwordBuilder.append(numero)
        actualizarAsteriscos()
    }

    private fun reiniciarPassword() {
        passwordBuilder.clear()
        actualizarAsteriscos()
    }

    private fun actualizarAsteriscos() {
        val asteriscos = "*".repeat(passwordBuilder.length)
        binding.tvAsteristcos.text = asteriscos
    }

    private fun onSuccess(usuario : Usuario) {

        val bundle = bundleOf(Usuario.TAG to usuario)

        findNavController().navigate(R.id.action_logInFragment_to_accountListFragment, bundle)
        Toast.makeText(context, "Sesión iniciada", Toast.LENGTH_SHORT).show()
    }

    private fun onLoading(value: Boolean) {
        if (value) {

            findNavController().navigate(R.id.action_logInFragment_to_fragmentProgressDialog)
        } else {
            findNavController().popBackStack()
        }
    }


    private fun setNombreEmptyError() {
        binding.tilNombre.error = "Nombre vacío"
        binding.tilNombre.requestFocus()
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
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
import com.example.monedero.data.Usuario
import com.example.monedero.databinding.FragmentSignUpBinding
import com.example.monedero.usecase.SignUpState
import com.example.monedero.usecase.SignUpViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null

    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tieNombre.addTextChangedListener(ErrorWatchers(binding.tilNombre))
        binding.tiePasswordSignUp.addTextChangedListener(ErrorWatchers(binding.tilPasswordSignUp))
        binding.tieConfirmsPasswordsSignUp.addTextChangedListener(ErrorWatchers(binding.tilConfirmsPasswordsSignUp))



        binding.btnCrearUsuario.setOnClickListener{

            val usuario = Usuario.create(binding.tieNombre.text.toString().lowercase(), binding.tiePasswordSignUp.text.toString())
            viewModel.validarCredenciales(usuario)
        }

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it){
                SignUpState.NombreEmptyError -> setNombreEmptyError()
                SignUpState.PasswordEmptyError -> setPasswordEmptyError()
                SignUpState.PasswordEmptyError2 -> setPasswordEmptyError2()
                SignUpState.PasswordsNotEquals -> setPasswordsNotEquals()
                is SignUpState.UsuarioExiste -> setUsuarioExiste(it.message)
                is SignUpState.Loading -> onLoading(it.value)
                SignUpState.Success -> onSuccess()

            }
        })

    }

    private fun onSuccess() {
        Toast.makeText(context, "Usuario creado con éxito", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun onLoading(value: Boolean) {
        if(value){
            findNavController().navigate(R.id.action_signUpFragment_to_fragmentProgressDialog)
        }else{
            findNavController().popBackStack()
        }
    }

    private fun setUsuarioExiste(message: String) {
        binding.tilNombre.error= "El usuario ya existe"
        binding.tilNombre.requestFocus()
        Snackbar.make(requireView(), "El usuario ya existe", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun setPasswordsNotEquals() {
        binding.tilConfirmsPasswordsSignUp.error= "Las contraseñas deben coincidir"
        binding.tilConfirmsPasswordsSignUp.requestFocus()
    }

    private fun setPasswordEmptyError2() {
        binding.tilConfirmsPasswordsSignUp.error= "Contraseña vacía"
        binding.tilConfirmsPasswordsSignUp.requestFocus()
    }

    private fun setPasswordEmptyError() {
        binding.tilPasswordSignUp.error= "Contraseña vacía"
        binding.tilPasswordSignUp.requestFocus()
    }

    private fun setNombreEmptyError() {
        binding.tilNombre.error= "Nombre vacío"
        binding.tilNombre.requestFocus()
    }

    inner class ErrorWatchers(val til : TextInputLayout) : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            til.error = null
        }

    }
}
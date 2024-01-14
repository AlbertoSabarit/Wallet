package com.example.monedero.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monedero.MainActivity
import com.example.monedero.R
import com.example.monedero.adapter.CuentaAdapter
import com.example.monedero.data.Cuenta
import com.example.monedero.data.Usuario
import com.example.monedero.databinding.FragmentAccountListBinding
import com.example.monedero.usecase.CuentaListState
import com.example.monedero.usecase.CuentaListViewModel
import com.google.android.material.snackbar.Snackbar


class AccountListFragment : Fragment(), CuentaAdapter.onClickCuenta, MenuProvider {

    private var _binding: FragmentAccountListBinding? = null

    private val binding get() = _binding!!

    private val viewModel: CuentaListViewModel by viewModels()

    private lateinit var cuentaAdapter: CuentaAdapter

    lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountListBinding.inflate(inflater, container, false)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuario = requireArguments().getParcelable<Usuario>(Usuario.TAG)
        viewModel.getCuentaList(usuario!!)

        binding.fabCreateCuenta.setOnClickListener {
            val bundle = bundleOf(Usuario.TAG to usuario)
            findNavController().navigate(R.id.action_accountListFragment_to_accountCreationFragment, bundle)
        }

        setUpCuentaRecycler()

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                CuentaListState.NoDataList -> setNoDataList()
                is CuentaListState.Loading -> onLoading(it.value)
                is CuentaListState.Success -> onSuccess(it.dataset)
            }
        })
    }

    private fun setUpCuentaRecycler() {

        cuentaAdapter = CuentaAdapter(this)

        with(binding.CuentasRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = cuentaAdapter
        }
    }

    private fun onSuccess(dataset: ArrayList<Cuenta>) {

        with(binding) {
            imgNoData.visibility = View.GONE
            tvNoData.visibility = View.GONE
            CuentasRecyclerView.visibility = View.VISIBLE
        }

        cuentaAdapter.update(dataset)
    }

    private fun onLoading(value: Boolean) {
        if (value) {
            findNavController().navigate(R.id.action_accountListFragment_to_fragmentProgressDialog)
        } else {
            findNavController().popBackStack()
        }
    }

    private fun setNoDataList() {
        with(binding) {
            imgNoData.visibility = View.VISIBLE
            tvNoData.visibility = View.VISIBLE
            CuentasRecyclerView.visibility = View.GONE
        }
    }

    override fun onClickDetallesCuenta(cuenta: Cuenta) {

        var bundle = bundleOf(Cuenta.TAG to cuenta)

        findNavController().navigate(
            R.id.action_accountListFragment_to_detallesCuentaFragment,bundle)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_list_cuentas, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val usuario = requireArguments().getParcelable<Usuario>(Usuario.TAG)
        return when (menuItem.itemId) {
            R.id.action_refresh -> {
                Snackbar.make(requireView(),"Lista ordenada por IBAN", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                viewModel.getCuentaList(usuario!!)
                true
            }

            R.id.action_money -> {
                Snackbar.make(requireView(),"Lista ordenada por saldo", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                cuentaAdapter.sort()
                true
            }

            else -> false
        }
    }
    private fun setUpToolbar() {

        (requireActivity() as? MainActivity)?.toolbar?.apply {
            visibility = View.VISIBLE
        }

        val menuhost: MenuHost = requireActivity()
        menuhost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        setUpToolbar()
    }
}
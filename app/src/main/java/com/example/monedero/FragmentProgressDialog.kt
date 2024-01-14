package com.example.monedero


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment


class FragmentProgressDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.fragment_dialog_progress, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        builder.setTitle("Esperando...")

        return builder.create()
    }
}

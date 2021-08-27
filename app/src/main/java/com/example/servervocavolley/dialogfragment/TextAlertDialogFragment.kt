package com.example.servervocavolley.dialogfragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class TextAlertDialogFragment(private val text: String): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(text)
                .setPositiveButton("확인"
                ) { _, _ ->
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
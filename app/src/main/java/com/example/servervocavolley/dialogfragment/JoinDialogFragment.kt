package com.example.servervocavolley.dialogfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.servervocavolley.databinding.DialogJoinBinding

class JoinDialogFragment(private val title: String, private val firstText: String, private val secondText: String) :
    DialogFragment() {
    lateinit var binding: DialogJoinBinding
    internal lateinit var listener: JoinDialogListener

    interface JoinDialogListener {
        fun joinClickedPassButton(dialog: JoinDialogFragment, first: String, second: String) {
            dialog.binding.idEditText.text.clear()
            dialog.binding.passwordEditText.text.clear()
        }

        fun joinRequestData(dialog: DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        clickedButton()
    }

    private fun initView() {
        binding.joinTextView.text = title
        binding.idEditText.hint = firstText
        binding.passwordEditText.hint = secondText
    }

    private fun clickedButton() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.passButton.setOnClickListener {
            if (binding.idEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {
                listener.joinClickedPassButton(
                    this,
                    binding.idEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
            } else {
                listener.joinRequestData(this)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as JoinDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

}
package com.example.servervocavolley.dialogfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.servervocavolley.R
import com.example.servervocavolley.dataclass.Voca
import com.example.servervocavolley.databinding.DialogSpinnerBinding

class SpinnerDialogFragment(
    private var vocaActivity: Context,
    private val vocaList: MutableList<Voca>,
    private val isDelete: Boolean
) : DialogFragment() {
    private lateinit var binding: DialogSpinnerBinding
    private var strList = mutableListOf<String>()
    internal lateinit var listener: SpinnerDialogListener

    interface SpinnerDialogListener {
        fun spinnerClickedPassButton(dialog: DialogFragment, eng: String, kor: String, idx: Int)
        fun spinnerClickedCancelButton(dialog: DialogFragment)
        fun spinnerIsEmptyEditText(dialog: DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSpinnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        clickedButton()
    }

    private fun initView() {
        if (isDelete) {
            binding.editTextLayout.visibility = GONE
            binding.spinnerTitleTextView.text = "삭제하기"
        } else {
            binding.spinnerTitleTextView.text = "수정하기"
        }

        strList.clear()
        vocaList.forEach {
            strList.add("${it.eng} - ${it.kor}")
        }

        var adapter = ArrayAdapter(
            vocaActivity,
            R.layout.support_simple_spinner_dropdown_item,
            strList
        )

        binding.spinner.adapter = adapter
        binding.spinner.setSelection(0)
    }

    private fun clickedButton() {
        binding.passButton.setOnClickListener {
            var position = binding.spinner.selectedItemPosition
            if (isDelete) {
                listener.spinnerClickedPassButton(
                    this,
                    vocaList[position].eng,
                    vocaList[position].kor,
                    vocaList[position].idx
                )
            } else {
                if (binding.engEditText.text.isNotEmpty() && binding.korEditText.text.isNotEmpty()) {
                    listener.spinnerClickedPassButton(
                        this,
                        binding.engEditText.text.toString(),
                        binding.korEditText.text.toString(),
                        vocaList[position].idx
                    )
                } else {
                    listener.spinnerIsEmptyEditText(this)
                }
            }
        }

        binding.cancelButton.setOnClickListener {
            listener.spinnerClickedCancelButton(this)
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SpinnerDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }
}


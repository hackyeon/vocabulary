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
) : DialogFragment(),
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: DialogSpinnerBinding
    private var engList = mutableListOf<String>()
    private var korList = mutableListOf<String>()
    private var idxList = mutableListOf<Int>()
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


        // 혹시 몰라서 클리어하고 시작하자
        engList.clear()
        korList.clear()
        idxList.clear()
        for (i in vocaList) {
            engList.add(i.eng)
            korList.add(i.kor)
            idxList.add(i.idx)
        }

        var engAdapter = ArrayAdapter(
            vocaActivity,
            R.layout.support_simple_spinner_dropdown_item,
            engList
        )

        var korAdapter = ArrayAdapter(
            vocaActivity,
            R.layout.support_simple_spinner_dropdown_item,
            korList
        )

        binding.engSpinner.adapter = engAdapter
        binding.korSpinner.adapter = korAdapter
        binding.engSpinner.setSelection(0)
        binding.korSpinner.setSelection(0)
    }

    private fun clickedButton() {
        binding.engSpinner.onItemSelectedListener = this
        binding.korSpinner.onItemSelectedListener = this

        binding.passButton.setOnClickListener {
            var position = binding.engSpinner.selectedItemPosition
            if (isDelete) {
                listener.spinnerClickedPassButton(
                    this,
                    engList[position],
                    korList[position],
                    idxList[position]
                )
            } else {
                if (binding.engEditText.text.isNotEmpty() && binding.korEditText.text.isNotEmpty()) {
                    listener.spinnerClickedPassButton(
                        this,
                        binding.engEditText.text.toString(),
                        binding.korEditText.text.toString(),
                        idxList[position]
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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        binding.engSpinner.setSelection(p2)
        binding.korSpinner.setSelection(p2)

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
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


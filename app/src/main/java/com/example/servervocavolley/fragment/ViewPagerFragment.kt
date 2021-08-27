package com.example.servervocavolley.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.servervocavolley.dataclass.Voca
import com.example.servervocavolley.adapter.VocaRecyclerViewAdapter
import com.example.servervocavolley.databinding.FragmentViewPagerBinding

class ViewPagerFragment(private val vocaList: MutableList<Voca>) : Fragment() {
    private lateinit var binding: FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = VocaRecyclerViewAdapter(vocaList)
    }

    fun dataChange() {
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

}
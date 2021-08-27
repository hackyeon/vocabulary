package com.example.servervocavolley.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.servervocavolley.dataclass.Voca
import com.example.servervocavolley.databinding.ItemVocaBinding

class VocaRecyclerViewAdapter(private val dataSet:MutableList<Voca>): RecyclerView.Adapter<VocaRecyclerViewAdapter.ViewHolder>() {
    private lateinit var binding: ItemVocaBinding

    class ViewHolder(val binding: ItemVocaBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(eng: String, kor: String){
            binding.korTextView.text = kor
            binding.engTextView.text = eng
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemVocaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var eng = dataSet[position].eng
        var kor = dataSet[position].kor
        holder.bind(eng, kor)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
package com.example.servervocavolley.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.servervocavolley.fragment.ViewPagerFragment
import com.example.servervocavolley.dataclass.Voca

class ViewPagerAdapter(
    fa: FragmentActivity,
    private val myVocaList: MutableList<Voca>,
    private val allVocaList: MutableList<Voca>
) : FragmentStateAdapter(fa) {
    private var allList = ViewPagerFragment(allVocaList)
    private var myList = ViewPagerFragment(myVocaList)
    private var createdAllList = false
    private var createdMyList = false

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                createdAllList = true
                allList
            }
            else -> {
                createdMyList = true
                myList
            }
        }
    }

    fun allDataChange(){
        if(createdAllList) allList.dataChange()
    }
    fun myDataChange(){
        if(createdMyList) myList.dataChange()
    }

}
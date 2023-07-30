package com.woojugoing.android74_memoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woojugoing.android74_memoapp.databinding.FragmentMemoMainBinding
import com.woojugoing.android74_memoapp.databinding.RowMainBinding

class MemoMainFragment : Fragment() {

    lateinit var fragmentMemoMainBinding: FragmentMemoMainBinding
    lateinit var mainActivity: MainActivity
    lateinit var memoDataList: MutableList<MemoClass>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMemoMainBinding = FragmentMemoMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        val categoryIdx = arguments?.getInt("category_idx")
        memoDataList = MemoDAO.selectAll(mainActivity, categoryIdx!!)
        val categoryClass = CategoryDAO.select(mainActivity, categoryIdx)

        fragmentMemoMainBinding.run {
            toolbarMemoMain.run {
                title = categoryClass?.categoryName
                inflateMenu(R.menu.menu_main)
                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_main_add -> {
                            val newBundle = Bundle()
                            newBundle.putInt("category_idx", categoryIdx)
                            mainActivity.replaceFragment(MainActivity.MEMO_ADD_FRAGMENT, addToBackStack = true, animate = true, newBundle)
                        }
                    }
                    false
                }
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MEMO_MAIN_FRAGMENT)
                }
            }

            recyclerViewMemo.run {
                adapter = MemoAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
                addItemDecoration(DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL))
            }
        }
        return fragmentMemoMainBinding.root
    }

    inner class MemoAdapter: RecyclerView.Adapter<MemoAdapter.MemoViewHolder>(){

        inner class MemoViewHolder(rowMainBinding: RowMainBinding): RecyclerView.ViewHolder(rowMainBinding.root){
            val textViewRow: TextView
            init {
                textViewRow = rowMainBinding.textViewRow
                rowMainBinding.root.setOnClickListener {
                    val selectedMemoIdx = memoDataList[adapterPosition].memoIdx
                    val newBundle = Bundle()
                    newBundle.putInt("memo_idx", selectedMemoIdx)
                    mainActivity.replaceFragment(MainActivity.MEMO_READ_FRAGMENT, addToBackStack = true, animate = true, newBundle)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
            val rowMainBinding = RowMainBinding.inflate(layoutInflater)
            val memoViewHolder = MemoViewHolder(rowMainBinding)

            rowMainBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return memoViewHolder
        }

        override fun getItemCount(): Int {
            return memoDataList.size
        }

        override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
            holder.textViewRow.text = memoDataList[position].memoTitle
        }
    }
}
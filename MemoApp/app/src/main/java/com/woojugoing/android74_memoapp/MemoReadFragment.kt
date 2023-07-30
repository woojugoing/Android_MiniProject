package com.woojugoing.android74_memoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.woojugoing.android74_memoapp.databinding.FragmentMemoReadBinding

class MemoReadFragment : Fragment() {

    lateinit var fragmentMemoReadBinding: FragmentMemoReadBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivity = activity as MainActivity
        fragmentMemoReadBinding = FragmentMemoReadBinding.inflate(layoutInflater)

        val memoIdx = arguments?.getInt("memo_idx")
        val memoClass = MemoDAO.select(mainActivity, memoIdx!!)

        fragmentMemoReadBinding.run {
            toolbarMemoRead.run {
                title = "메모 읽기"
                inflateMenu(R.menu.menu_read)
                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_read_edit -> {
                            val newBundle = Bundle()
                            newBundle.putInt("memo_idx", memoIdx)
                            mainActivity.replaceFragment(MainActivity.MEMO_EDIT_FRAGMENT, addToBackStack = true, animate = true, newBundle)
                        }
                        R.id.menu_read_delete -> {
                            MemoDAO.delete(mainActivity, memoIdx)
                            mainActivity.removeFragment(MainActivity.MEMO_READ_FRAGMENT)
                        }
                    }
                    false
                }
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MEMO_READ_FRAGMENT)
                }
            }
            textViewMemoTitle.text = memoClass?.memoTitle
            textViewMemoDate.text = memoClass?.memoDate
            textViewMemoContent.text = memoClass?.memoContent
        }
        return fragmentMemoReadBinding.root
    }
}
package com.woojugoing.android74_memoapp

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.woojugoing.android74_memoapp.databinding.FragmentMemoEditBinding

class MemoEditFragment : Fragment() {

    lateinit var fragmentMemoEditBinding: FragmentMemoEditBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentMemoEditBinding = FragmentMemoEditBinding.inflate(inflater)

        val memoIdx = arguments?.getInt("memo_idx")
        val memoClass = MemoDAO.select(mainActivity, memoIdx!!)

        fragmentMemoEditBinding.run {
            toolbarMemoEdit.run {
                title = "메모 수정"
                inflateMenu(R.menu.menu_save)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_memo_save -> {
                            var editTitle = editTextEditTitle.text.toString()
                            var editContent = editTextEditContent.text.toString()

                            if (editTitle.length == 0) {
                                val builder = AlertDialog.Builder(mainActivity)
                                builder.setTitle("제목 입력 오류")
                                builder.setMessage("제목을 입력해주세요.")
                                builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                                    mainActivity.showSoftInput(editTextEditTitle, 150)
                                }
                                builder.show()
                                return@setOnMenuItemClickListener false
                            }

                            if (editContent.isEmpty()) {
                                val builder = AlertDialog.Builder(mainActivity)
                                builder.setTitle("내용 입력 오류")
                                builder.setMessage("내용을 입력해주세요.")
                                builder.setPositiveButton("확인") { _: DialogInterface, i: Int ->
                                    mainActivity.showSoftInput(editTextEditContent, 150)
                                }
                                builder.show()
                                return@setOnMenuItemClickListener false
                            }

                            memoClass?.memoTitle = editTitle
                            memoClass?.memoContent = editContent

                            MemoDAO.update(mainActivity, memoClass!!)
                            mainActivity.removeFragment(MainActivity.MEMO_EDIT_FRAGMENT)
                        }
                    }
                    false
                }
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MEMO_EDIT_FRAGMENT)
                }
            }

            editTextEditTitle.setText(memoClass?.memoTitle)
            editTextEditContent.setText(memoClass?.memoContent)
        }
        return fragmentMemoEditBinding.root
    }
}
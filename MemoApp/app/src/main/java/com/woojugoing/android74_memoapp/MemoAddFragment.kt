package com.woojugoing.android74_memoapp

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.woojugoing.android74_memoapp.databinding.FragmentMemoAddBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemoAddFragment : Fragment() {

    lateinit var fragmentMemoAddBinding: FragmentMemoAddBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivity = activity as MainActivity
        fragmentMemoAddBinding = FragmentMemoAddBinding.inflate(inflater)
        fragmentMemoAddBinding.run {

            mainActivity.showSoftInput(editTextMemoTitle, 150)

            toolbarMemoAdd.run {
                title = "메모 등록"
                inflateMenu(R.menu.menu_save)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_memo_save -> {
                            val title = editTextMemoTitle.text.toString()
                            val content = editTextMemoContent.text.toString()
                            val categoryIdx = arguments?.getInt("category_idx")
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val now = sdf.format(Date())

                            if (title.isEmpty()) {
                                val builder = AlertDialog.Builder(mainActivity)
                                builder.setTitle("제목 입력 오류")
                                builder.setMessage("제목을 입력해주세요.")
                                builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                                    mainActivity.showSoftInput(editTextMemoTitle, 150)
                                }
                                builder.show()
                                return@setOnMenuItemClickListener false
                            }

                            if (content.isEmpty()) {
                                val builder = AlertDialog.Builder(mainActivity)
                                builder.setTitle("내용 입력 오류")
                                builder.setMessage("내용을 입력해주세요.")
                                builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                                    mainActivity.showSoftInput(editTextMemoContent, 150)
                                }
                                builder.show()
                                return@setOnMenuItemClickListener false
                            }

                            val memoClass = MemoClass(0, title, content, now, categoryIdx!!)
                            MemoDAO.insert(mainActivity, memoClass)

                            mainActivity.hideSoftInput()
                            mainActivity.removeFragment(MainActivity.MEMO_ADD_FRAGMENT)
                        }
                    }
                    false
                }
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.hideSoftInput()
                    mainActivity.removeFragment(MainActivity.MEMO_ADD_FRAGMENT)
                }
            }
        }
        return fragmentMemoAddBinding.root
    }
}
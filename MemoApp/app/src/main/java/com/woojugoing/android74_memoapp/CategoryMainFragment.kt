package com.woojugoing.android74_memoapp

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woojugoing.android74_memoapp.databinding.DialogCategoryMainBinding
import com.woojugoing.android74_memoapp.databinding.FragmentCategoryMainBinding
import com.woojugoing.android74_memoapp.databinding.RowMainBinding

class CategoryMainFragment : Fragment() {

    lateinit var fragmentCategoryMainBinding: FragmentCategoryMainBinding
    lateinit var mainActivity: MainActivity
    lateinit var categoryDataList: MutableList<CategoryClass>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivity = activity as MainActivity
        fragmentCategoryMainBinding = FragmentCategoryMainBinding.inflate(inflater)
        categoryDataList = CategoryDAO.selectAll(mainActivity)

        fragmentCategoryMainBinding.run {
            toolbarCategoryMain.run {
                title = "카테고리 목록"
                inflateMenu(R.menu.menu_main)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_main_add -> {
                            val builder = AlertDialog.Builder(mainActivity)
                            builder.setTitle("새로운 카테고리 등록")
                            val dialogCategoryMainBinding =
                                DialogCategoryMainBinding.inflate(layoutInflater)
                            builder.setView(dialogCategoryMainBinding.root)
                            builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                                val newCategoryName =
                                    dialogCategoryMainBinding.editTextDialogCategory.text.toString()
                                val newCategoryClass = CategoryClass(0, newCategoryName)
                                CategoryDAO.insert(mainActivity, newCategoryClass)
                                categoryDataList = CategoryDAO.selectAll(mainActivity)
                                recyclerViewCategory.adapter?.notifyDataSetChanged()
                            }
                            builder.setNegativeButton("취소", null)
                            builder.show()
                            mainActivity.showSoftInput(
                                dialogCategoryMainBinding.editTextDialogCategory,
                                500
                            )
                        }
                    }
                    false
                }
            }

            recyclerViewCategory.run {
                adapter = CategoryAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
                addItemDecoration(
                    DividerItemDecoration(
                        mainActivity,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }

        }

        return fragmentCategoryMainBinding.root
    }

    inner class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

        inner class CategoryViewHolder(rowMainBinding: RowMainBinding) :
            RecyclerView.ViewHolder(rowMainBinding.root) {
            val textViewRow: TextView

            init {
                textViewRow = rowMainBinding.textViewRow
                rowMainBinding.root.setOnCreateContextMenuListener { menu, _, _ ->
                    menu.setHeaderTitle(categoryDataList[adapterPosition].categoryName + " - 관리")
                    mainActivity.menuInflater.inflate(R.menu.menu_category_context, menu)
                    // edit
                    menu[0].setOnMenuItemClickListener {
                        val dialogCategoryMainBinding =
                            DialogCategoryMainBinding.inflate(layoutInflater)
                        val builder = AlertDialog.Builder(mainActivity)
                        builder.setTitle("카테고리 수정")
                        builder.setView(dialogCategoryMainBinding.root)
                        dialogCategoryMainBinding.editTextDialogCategory.setText(categoryDataList[adapterPosition].categoryName)
                        builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                            val editCategoryName =
                                dialogCategoryMainBinding.editTextDialogCategory.text.toString()
                            categoryDataList[adapterPosition].categoryName = editCategoryName
                            CategoryDAO.update(mainActivity, categoryDataList[adapterPosition])
                            categoryDataList = CategoryDAO.selectAll(mainActivity)
                            fragmentCategoryMainBinding.recyclerViewCategory.adapter?.notifyDataSetChanged()
                        }
                        builder.setNegativeButton("취소", null)
                        builder.show()
                        false
                    }

                    menu[1].setOnMenuItemClickListener {
                        val deleteCategoryIdx = categoryDataList[adapterPosition].categoryIdx
                        MemoDAO.deleteMemoInCat(mainActivity, deleteCategoryIdx)
                        CategoryDAO.delete(mainActivity, deleteCategoryIdx)
                        categoryDataList = CategoryDAO.selectAll(mainActivity)
                        fragmentCategoryMainBinding.recyclerViewCategory.adapter?.notifyDataSetChanged()
                        false
                    }
                }

                rowMainBinding.root.setOnClickListener {
                    val selectedCatIdx = categoryDataList[adapterPosition].categoryIdx
                    val newBundle = Bundle()
                    newBundle.putInt("category_idx", selectedCatIdx)
                    mainActivity.replaceFragment(
                        MainActivity.MEMO_MAIN_FRAGMENT,
                        addToBackStack = true,
                        animate = true,
                        newBundle
                    )
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val rowMainBinding = RowMainBinding.inflate(layoutInflater)
            val categoryViewHolder = CategoryViewHolder(rowMainBinding)

            rowMainBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return categoryViewHolder
        }

        override fun getItemCount(): Int {
            return categoryDataList.size
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            holder.textViewRow.text = categoryDataList[position].categoryName
        }
    }
}
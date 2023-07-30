package com.woojugoing.android74_memoapp

import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.woojugoing.android74_memoapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentLoginBinding.run {
            toolbarLogin.title = "로그인"
            mainActivity.showSoftInput(textLayoutLoginInput, 150)

            buttonLoginSubmit.setOnClickListener {
                clickLoginSubmitButton()
            }

            textLayoutLoginInput.setOnEditorActionListener { _, _, _ ->
                clickLoginSubmitButton()
                false
            }
        }
        return fragmentLoginBinding.root
    }

    fun clickLoginSubmitButton() {
        fragmentLoginBinding.run {
            val str1 = textLayoutLoginInput.text.toString()


            val pref = mainActivity.getSharedPreferences("data", MODE_PRIVATE)
            val password = pref.getString("password", null)

            if (str1.length == 0) {
                val builder = AlertDialog.Builder(mainActivity)
                builder.setTitle("로그인 오류")
                builder.setMessage("비밀번호를 입력해주세요.")
                builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                    mainActivity.showSoftInput(textLayoutLoginInput, 150)
                }
                builder.show()
                return
            }

            if (str1 != password) {
                val builder = AlertDialog.Builder(mainActivity)
                builder.setTitle("로그인 오류")
                builder.setMessage("비밀번호를 잘못 입력하였습니다.")
                builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                    textLayoutLoginInput.text?.clear()
                    mainActivity.showSoftInput(textLayoutLoginInput, 150)
                }
                builder.show()
                return
            }

            val builder = AlertDialog.Builder(mainActivity)
            builder.setTitle("로그인 성공")
            builder.setMessage("로그인에 성공하였습니다.")
            builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                mainActivity.replaceFragment(
                    MainActivity.CATEGORY_MAIN_FRAGMENT,
                    addToBackStack = false,
                    animate = true
                )
            }
            builder.show()
        }
    }
}
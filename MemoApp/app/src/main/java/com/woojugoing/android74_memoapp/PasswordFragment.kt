package com.woojugoing.android74_memoapp

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.woojugoing.android74_memoapp.databinding.FragmentPasswordBinding

class PasswordFragment : Fragment() {

    lateinit var fragmentPasswordBinding: FragmentPasswordBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPasswordBinding = FragmentPasswordBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentPasswordBinding.run {
            mainActivity.showSoftInput(textLayoutPasswordInput, 150)
            toolbarPassword.title = "비밀번호 설정"

            buttonPasswordSubmit.setOnClickListener {
                clickSubmitBtn()
            }

            textLayoutPasswordCheck.run {
                setOnEditorActionListener { _, _, _ ->
                    clickSubmitBtn()
                    false
                }
            }
        }

        return fragmentPasswordBinding.root
    }

    private fun clickSubmitBtn() {
        fragmentPasswordBinding.run {
            val str1 = textLayoutPasswordInput.text.toString()
            val str2 = textLayoutPasswordCheck.text.toString()

            if (str1.isEmpty()) {
                val builder = AlertDialog.Builder(mainActivity)
                builder.setTitle("비밀번호 오류")
                builder.setMessage("비밀번호를 입력해주세요.")
                builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                    mainActivity.showSoftInput(textLayoutPasswordInput, 150)
                }
                builder.show()
                return
            }

            if (str2.isEmpty()) {
                val builder = AlertDialog.Builder(mainActivity)
                builder.setTitle("비밀번호 오류")
                builder.setMessage("비밀번호를 입력해주세요.")
                builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                    mainActivity.showSoftInput(textLayoutPasswordCheck, 150)
                }
                builder.show()
                return
            }

            if (str1 != str2) {
                val builder = AlertDialog.Builder(mainActivity)
                builder.setTitle("비밀번호 오류")
                builder.setMessage("비밀번호가 일치하지 않습니다.")
                builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                    textLayoutPasswordInput.setText("")
                    textLayoutPasswordCheck.setText("")
                    mainActivity.showSoftInput(textLayoutPasswordInput, 150)
                }
                builder.show()
                return
            }

            val builder = AlertDialog.Builder(mainActivity)
            builder.setTitle("설정 완료")
            builder.setMessage("비밀번호 설정이 완료되었습니다.")
            builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                var pref = mainActivity.getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)
                var editor = pref.edit()
                editor.putString("password", str1)
                editor.commit()
                mainActivity.replaceFragment(
                    MainActivity.LOGIN_FRAGMENT,
                    addToBackStack = false,
                    animate = true
                )
            }
            builder.show()
        }
    }
}
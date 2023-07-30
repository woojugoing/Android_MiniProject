package com.woojugoing.android74_memoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.woojugoing.android74_memoapp.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    companion object {
        const val PASSWORD_FRAGMENT = "PasswordFragment"
        const val LOGIN_FRAGMENT = "LoginFragment"
        const val CATEGORY_MAIN_FRAGMENT = "CategoryMainFragment"
        const val MEMO_MAIN_FRAGMENT = "MemoMainFragment"
        const val MEMO_ADD_FRAGMENT = "MemoAddFragment"
        const val MEMO_READ_FRAGMENT = "MemoReadFragment"
        const val MEMO_EDIT_FRAGMENT = "MemoEditFragment"
    }

    lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        val pref = getSharedPreferences("data", MODE_PRIVATE)
        val password = pref.getString("password", null)
        if (password == null) {
            replaceFragment(PASSWORD_FRAGMENT, addToBackStack = false, animate = false)
        } else {
            replaceFragment(LOGIN_FRAGMENT, addToBackStack = false, animate = false)
        }

        val dbHelper = DBHelper(this)
        dbHelper.writableDatabase
        dbHelper.close()
    }

    fun replaceFragment(
        name: String,
        addToBackStack: Boolean,
        animate: Boolean,
        arguments: Bundle? = null
    ) {
        SystemClock.sleep(200)
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val newFragment = when (name) {
            PASSWORD_FRAGMENT -> PasswordFragment()
            LOGIN_FRAGMENT -> LoginFragment()
            CATEGORY_MAIN_FRAGMENT -> CategoryMainFragment()
            MEMO_MAIN_FRAGMENT -> MemoMainFragment()
            MEMO_ADD_FRAGMENT -> MemoAddFragment()
            MEMO_READ_FRAGMENT -> MemoReadFragment()
            MEMO_EDIT_FRAGMENT -> MemoEditFragment()
            else -> Fragment()
        }

        newFragment.arguments = arguments

        if (newFragment != null) {
            if (animate) {
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
            }

            fragmentTransaction.replace(R.id.container_main, newFragment)

            if (addToBackStack) {
                fragmentTransaction.addToBackStack(name)
            }
            fragmentTransaction.commit()
        }
    }

    fun removeFragment(name: String) {
        SystemClock.sleep(200)
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun showSoftInput(view: View, delay: Long) {
        view.requestFocus()
        thread {
            SystemClock.sleep(delay)
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideSoftInput() {
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

data class CategoryClass(var categoryIdx: Int, var categoryName: String)
data class MemoClass(
    var memoIdx: Int,
    var memoTitle: String,
    var memoContent: String,
    var memoDate: String,
    var memoCatIdx: Int
)
package com.woojugoing.webtoonapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.woojugoing.webtoonapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.run {
            btn1.setOnClickListener {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainer, WebViewFragment())
                    commit()
                }
            }

            btn2.setOnClickListener {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainer, SecondFragment())
                    commit()
                }
            }
        }
    }
}
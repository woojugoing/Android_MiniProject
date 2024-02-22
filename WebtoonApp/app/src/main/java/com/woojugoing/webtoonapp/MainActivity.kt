package com.woojugoing.webtoonapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.google.android.material.tabs.TabLayoutMediator
import com.woojugoing.webtoonapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnTabLayoutNameChanged {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val sharedPreferences = getSharedPreferences(WebViewFragment.Companion.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val tab0 = sharedPreferences?.getString("tab0_name", "월요 웹툰")
        val tab1 = sharedPreferences?.getString("tab1_name", "화요 웹툰")
        val tab2 = sharedPreferences?.getString("tab2_name", "수요 웹툰")

        activityMainBinding.run {
            viewPagerMain.adapter = ViewPagerAdapter(this@MainActivity)
            TabLayoutMediator(tabLayoutMain, viewPagerMain) { tab, position ->
                tab.text = when(position) {
                    0 -> tab0
                    1 -> tab1
                    else -> tab2
                }
            }.attach()
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.fragments[activityMainBinding.viewPagerMain.currentItem]
        if(currentFragment is WebViewFragment) {
            if(currentFragment.canGoBack()) currentFragment.goBack() else super.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun nameChanged(position: Int, name: String) {
        val tab = activityMainBinding.tabLayoutMain.getTabAt(position)
        tab?.text = name
    }
}
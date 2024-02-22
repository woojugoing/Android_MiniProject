package com.woojugoing.webtoonapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.woojugoing.webtoonapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.run {
            webView.run {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true   // 앱 내 js 사용 허용
                loadUrl("https://google.com")
            }
        }
    }
}
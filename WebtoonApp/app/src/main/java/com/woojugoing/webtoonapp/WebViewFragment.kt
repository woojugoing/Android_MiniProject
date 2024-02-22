package com.woojugoing.webtoonapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.woojugoing.webtoonapp.databinding.FragmentWebviewBinding

class WebViewFragment: Fragment() {

    private lateinit var fragmentWebViewBinding: FragmentWebviewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentWebViewBinding = FragmentWebviewBinding.inflate(inflater)
        return fragmentWebViewBinding.root
    }
}
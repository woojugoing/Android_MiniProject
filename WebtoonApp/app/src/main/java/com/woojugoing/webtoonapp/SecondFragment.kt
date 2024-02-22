package com.woojugoing.webtoonapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.woojugoing.webtoonapp.databinding.FragmentSecondBinding

class SecondFragment: Fragment() {

    private lateinit var fragmentSecondBinding: FragmentSecondBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentSecondBinding = FragmentSecondBinding.inflate(inflater)
        return fragmentSecondBinding.root
    }
}
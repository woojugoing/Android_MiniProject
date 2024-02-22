package com.woojugoing.webtoonapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.woojugoing.webtoonapp.databinding.FragmentWebviewBinding

class WebViewFragment(private val position: Int, private val webViewURL: String): Fragment() {

    var listener: OnTabLayoutNameChanged? = null
    private lateinit var fragmentWebViewBinding: FragmentWebviewBinding
    companion object { const val SHARED_PREFERENCE = "WEB_HISTORY" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentWebViewBinding = FragmentWebviewBinding.inflate(inflater)
        return fragmentWebViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentWebViewBinding.run {
            webview.run {
                webViewClient = WebtoonViewClient(fragmentWebViewBinding.progressWebView) { url -> activity?.getSharedPreferences("WEB_HISTORY", Context.MODE_PRIVATE)?.edit { putString("tab$position", url) } }
                settings.javaScriptEnabled = true
                loadUrl(webViewURL)
            }
            btnWebViewBackToLast.setOnClickListener {
                val sharedPreferences = activity?.getSharedPreferences("WEB_HISTORY", Context.MODE_PRIVATE)
                val url = sharedPreferences?.getString("tab $position", "")
                if(url.isNullOrEmpty()) Toast.makeText(context, getString(R.string.back_to_history_error_message), Toast.LENGTH_SHORT).show() else fragmentWebViewBinding.webview.loadUrl(url)
            }
            btnWebViewChangeTab.setOnClickListener {
                val dialog = AlertDialog.Builder(context)
                val editText = EditText(context)

                dialog.run {
                    setView(editText)
                    setPositiveButton(getString(R.string.save)) { _, _ ->
                        activity?.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)?.edit {
                            putString("tab${position}_name", editText.text.toString())
                            listener?.nameChanged(position, editText.text.toString())
                        }
                    }
                    setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ -> dialogInterface.cancel() }
                    show()
                }
            }
        }
    }

    fun canGoBack(): Boolean { return fragmentWebViewBinding.webview.canGoBack() }
    fun goBack() { fragmentWebViewBinding.webview.goBack() }
}

interface OnTabLayoutNameChanged { fun nameChanged(position: Int, name: String) }
package com.duoduo.mark2.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.duoduo.mark2.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditorFragment(val listener: Listener) : Fragment() {

    interface Listener {
        fun onLoaded()
    }

    private lateinit var webView: WebView

    private lateinit var btnBold: MaterialButton
    private lateinit var btnHeader: MaterialButton
    private lateinit var btnStrikethrough: MaterialButton
    private lateinit var btnImage: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_editor, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView = view.findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.requestFocus(View.FOCUS_DOWN or View.FOCUS_UP)
        webView.loadUrl("file:///android_asset/index.html")
        WebView.setWebContentsDebuggingEnabled(true)

        btnBold = view.findViewById(R.id.btn_bold)
        btnBold.setOnClickListener {
            formatText("bold")
        }
        btnHeader = view.findViewById(R.id.btn_header)
        btnHeader.setOnClickListener {
            formatHeading("h1")
        }
        btnStrikethrough = view.findViewById(R.id.btn_strikethrough)
        btnStrikethrough.setOnClickListener {
            formatText("strikethrough")
        }
        btnImage = view.findViewById(R.id.btn_image)
        btnImage.setOnClickListener {

        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                listener.onLoaded()
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                val alertDialog = MaterialAlertDialogBuilder(context!!)
                alertDialog.setTitle("Editor")
                alertDialog.setMessage(message)
                alertDialog.setPositiveButton("OK", null)
                alertDialog.show()
                result?.confirm()
                return true
            }
        }
    }

    private fun executeJavaScript(js: String) {
        webView.evaluateJavascript(js, null)
    }

    fun setMarkdownContent(content: String) {
        executeJavaScript("window.setMarkdownContent(\"${content.replace("\"", "\\\"")}\\n\")")
    }

    fun formatText(payload: String) {
        executeJavaScript("window.executeFormatText(\"$payload\")")
    }

    fun formatHeading(payload: String) {
        executeJavaScript("window.formatHeading(\"$payload\")")
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: Listener) =
            EditorFragment(listener).apply {
                arguments = Bundle().apply {

                }
            }
    }
}
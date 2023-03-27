package com.example.williamhybrid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {
    lateinit var webView:WebView
    val samplePageUrl = "https://web-sdk-dev-sec-dir.public.sre.dfinery.io"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.williamhybridtest)
        settingWebView(webView)
        webView.loadUrl(samplePageUrl)
    }

    private fun settingWebView(webView: WebView){
        webView.settings.apply {
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
            useWideViewPort = true
            loadWithOverviewMode = true
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = false
            cacheMode = WebSettings.LOAD_NO_CACHE
            pluginState = WebSettings.PluginState.ON
            loadWithOverviewMode = true
            setSupportMultipleWindows(true)
            loadsImagesAutomatically = true
            setAppCacheEnabled(false)
            domStorageEnabled = true
            setGeolocationEnabled(false)
            defaultTextEncodingName = "utf-8"
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView.apply {
            addJavascriptInterface(AdbrixJavascriptInterface(applicationContext), "adbrixBridge")
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
        }
    }
}
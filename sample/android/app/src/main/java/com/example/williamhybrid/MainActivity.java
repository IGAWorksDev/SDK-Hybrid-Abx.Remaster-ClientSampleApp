package com.example.williamhybrid;

import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private WebView mWebView;
    // 웹뷰 url 셋팅
    private final String samplePageUrl = "https://web-sdk-dev-sec-dir.public.sre.dfinery.io";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.williamhybridtest);
        settingWebView(mWebView);
        mWebView.loadUrl(samplePageUrl);
    }

    private void settingWebView(WebView webView){
        // 웹뷰 세팅
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(new AdbrixJavascriptInterface(getApplicationContext()), "adbrixBridge");
        webView.setWebViewClient(new WebViewClient(){});
        webView.setWebChromeClient(new WebChromeClient());
    }
}

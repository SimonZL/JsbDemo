package com.example.jsbdemo

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.github.lzyzsd.jsbridge.DefaultHandler


class MainActivity : AppCompatActivity() {

    private var mWebView: BridgeWebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWebView = findViewById(R.id.webview)
        mWebView?.apply {
            setDefaultHandler(DefaultHandler())
            settings.allowFileAccess = true
            settings.databaseEnabled = true
            // 允许网页定位
            settings.setGeolocationEnabled(true)
            // 允许网页弹对话框
            settings.javaScriptCanOpenWindowsAutomatically = true
            // 加快网页加载完成的速度，等页面完成再加载图片
            settings.loadsImagesAutomatically = true
            // 开启 localStorage
            settings.domStorageEnabled = true
            // 设置支持javascript// 本地 DOM 存储（解决加载某些网页出现白板现象）
            settings.javaScriptEnabled = true
            // 进行缩放
            settings.builtInZoomControls = true
            // 设置UserAgent
            settings.userAgentString = settings.userAgentString + "app"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 解决 Android 5.0 上 WebView 默认不允许加载 Http 与 Https 混合内容
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webViewClient = object : BridgeWebViewClient(this) {
                // 修复 页面还没加载完成，注册代码还没初始完成，就调用了callHandle
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }

                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
                    handler.proceed() // 接受所有网站的证书
                }
            }
            loadUrl("http://192.168.0.106:7456/web-mobile/web-mobile/index.html")
        }
        // 注册Native方法供JS调用
        mWebView?.registerHandler(
            "rechargeBlueDiamond"
        ) { data, function ->
            Log.i(
                "MainActivity",
                "functionToAndroid2 handler = callNativeHandler, data from web = $data"
            )
            function?.onCallBack("reponse data from Android 中文 from Java")
        }


        findViewById<TextView>(R.id.btn).setOnClickListener {
            //调用js
            mWebView?.callHandler(
                "nativeToJs",
                "data"
            ) { data ->
                Log.i("MainActivity", "reponse data from JS $data")
            }
        }
    }
}
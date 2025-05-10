package com.example.githubdemo.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.example.githubdemo.R
import com.example.githubdemo.databinding.ActivityWebViewAuthBinding
import com.example.githubdemo.util.Constants

/**
 * 用于GitHub OAuth授权的WebView活动
 */
class WebViewAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewAuthBinding

    companion object {
        private const val EXTRA_AUTH_URL = "extra_auth_url"
        private const val EXTRA_REDIRECT_URL = "extra_redirect_url"

        /**
         * 创建WebViewAuthActivity的Intent
         */
        fun createIntent(context: Context, authUrl: String, redirectUrl: String): Intent {
            return Intent(context, WebViewAuthActivity::class.java).apply {
                putExtra(EXTRA_AUTH_URL, authUrl)
                putExtra(EXTRA_REDIRECT_URL, redirectUrl)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authUrl = intent.getStringExtra(EXTRA_AUTH_URL)
        val redirectUrl = intent.getStringExtra(EXTRA_REDIRECT_URL) ?: Constants.REDIRECT_URI

        if (authUrl.isNullOrEmpty()) {
            finish()
            return
        }

        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val url = request.url.toString()
                    LogUtils.d("WebView URL: $url")
                    if (url.startsWith(redirectUrl)) {
                        val uri = Uri.parse(url)
                        val code = uri.getQueryParameter("code")

                        val resultIntent = Intent().apply {
                            putExtra("auth_code", code)
                        }

                        setResult(RESULT_OK, resultIntent)
                        finish()
                        return true
                    }
                    return false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    LogUtils.d("WebView onReceivedError URL: $url")
                    super.onReceivedError(view, request, error)
                }
            }

            // 加载授权URL
            loadUrl(authUrl)
        }

        // 设置工具栏
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.github_login)
        }

        // 设置工具栏返回按钮点击事件
        binding.toolbar.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            setResult(RESULT_CANCELED)
            super.onBackPressed()
        }
    }
}
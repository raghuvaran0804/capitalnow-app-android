package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.ActivityBannerwebViewBinding


class BannerWebViewActivity : BaseActivity() {
    private val binding by lazy { ActivityBannerwebViewBinding.inflate(layoutInflater)}
    private val swipeRefreshLayout: SwipeRefreshLayout? = null
    private var url: String? = null
    private var shareMsg: String? = null
    private var activity: AppCompatActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        activity = this
        initView()
    }

    private fun initView() {
        try {
            binding.tvBack.setOnClickListener {
                onBackPressed()
            }
            if (intent.extras != null) {
                if (intent.hasExtra("url")) {
                    url = intent.getStringExtra("url")
                }
                if (intent.hasExtra("shareMsg")) {
                    shareMsg = intent.getStringExtra("shareMsg")
                }
            }
            loadWebView(url)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(url: String?) {
        try {
            binding.wvWebView.settings.javaScriptEnabled = true
            binding.wvWebView.settings.allowContentAccess = true
            binding.wvWebView.settings.domStorageEnabled = true
            binding.wvWebView.settings.useWideViewPort = true
            binding.wvWebView.settings.domStorageEnabled = true
            binding.wvWebView.loadUrl(url.toString())
            binding.wvWebView.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    try {
                        if (url?.contains("https://www.capitalnow.in/share") == true) {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_TITLE, "CapitalNow App")
                            shareIntent.putExtra(
                                Intent.EXTRA_SUBJECT,
                                "CapitalNow App - Refer & Earn"
                            )
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg)
                            shareIntent.putExtra(Constants.BUNDLE_REFER_CODE_SMS_MSG, shareMsg)
                            shareIntent.putExtra(Constants.BUNDLE_REFER_CODE_EMAIL_MSG, shareMsg)
                            shareIntent.putExtra(Constants.BUNDLE_REFER_CODE_SOCIAL_MSG, shareMsg)
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg)
                            startActivity(Intent.createChooser(shareIntent, "choose one"))

                        } else{
                            view?.loadUrl(url.toString())
                        }
                    } catch (e: Exception) {
                        view?.loadUrl(url.toString())
                        e.printStackTrace()
                    }

                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }

            binding.swipeRefreshLayout.setOnRefreshListener { binding.wvWebView.reload() }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
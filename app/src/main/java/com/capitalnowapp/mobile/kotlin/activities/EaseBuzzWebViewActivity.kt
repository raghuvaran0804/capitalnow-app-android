package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.databinding.ActivityEaseBuzzWebViewBinding
import java.lang.ref.WeakReference

class EaseBuzzWebViewActivity : AppCompatActivity() {
    private var furl: String? = ""
    private var surl: String? = ""
    private var binding: ActivityEaseBuzzWebViewBinding? = null
    private var activity: AppCompatActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEaseBuzzWebViewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        try{
            if(intent.extras != null){
                 surl = intent.getStringExtra("surl")
                furl = intent.getStringExtra("furl")
                if(surl != null){
                    loadWebView(surl!!)
                }
                if(furl != null){
                    loadWebView(furl!!)
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(weblink: String) {

        binding!!.wvWebView.settings.javaScriptEnabled = true
        if(surl != null) {
            binding!!.wvWebView.loadUrl(surl!!)
        }
        if(furl != null){
            binding!!.wvWebView.loadUrl(furl!!)
        }
        val mActivityRef = WeakReference<Activity>(this)
        binding!!.wvWebView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                when {
                    url.startsWith("mailto:") -> {
                        val activity: Activity = mActivityRef.get()!!
                        val mt = MailTo.parse(url)
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(mt.to))
                        intent.putExtra(Intent.EXTRA_TEXT, mt.body)
                        intent.putExtra(Intent.EXTRA_SUBJECT, mt.subject)
                        intent.putExtra(Intent.EXTRA_CC, mt.cc)
                        intent.type = "message/rfc822"
                        activity.startActivity(intent)
                        view.reload()
                        return true
                    }
                    url.contains("/dashboard/home") -> {
                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()

                    }


                    else -> {
                        view.loadUrl(url)
                    }
                }
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                //view.visibility = View.GONE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }

        }
        binding!!.wvWebView.settings.builtInZoomControls = true
    }
    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}


package com.capitalnowapp.mobile.kotlin.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.MailTo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.R
import kotlinx.android.synthetic.main.activity_docs_guidelines.pb
import kotlinx.android.synthetic.main.activity_docs_guidelines.webView
import kotlinx.android.synthetic.main.toolbar_basic.iv
import kotlinx.android.synthetic.main.toolbar_basic.tvAction
import kotlinx.android.synthetic.main.toolbar_basic.tvToolbarTitle
import java.lang.ref.WeakReference

class DocsGuidelinesActivity : AppCompatActivity() {

    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Dialog)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docs_guidelines)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.setFinishOnTouchOutside(true)

        if (intent.extras != null) {
            url = intent.extras!!.getString("url").toString()
        }

        //android O fix bug orientation
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true

        val mActivityRef = WeakReference<Activity>(this)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("mailto:")) {
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
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                pb.visibility = View.VISIBLE
                view.visibility = View.GONE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                pb.visibility = View.GONE
                view.visibility = View.VISIBLE
                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl(url)
        webView.settings.builtInZoomControls = true

        tvToolbarTitle.text = getString(R.string.upload_docs_guidelines_title)
        iv.visibility = VISIBLE
        iv.setImageResource(R.drawable.ic_left_arrow)
        tvAction.visibility = GONE

        iv.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        //  overridePendingTransition(R.anim.slide_out_up, R.anim.slide_in_up);
    }
}
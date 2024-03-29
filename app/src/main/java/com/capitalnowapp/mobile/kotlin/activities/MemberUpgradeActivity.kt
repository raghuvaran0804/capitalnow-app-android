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
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.capitalnowapp.mobile.databinding.ActivityMemberUpgradeBinding
import com.capitalnowapp.mobile.models.MemberUpgradeConsentResponse
import java.lang.ref.WeakReference

class MemberUpgradeActivity : AppCompatActivity(), LifecycleObserver {
    private var memberUpgradeConsentResponse: MemberUpgradeConsentResponse? = null
    private var binding: ActivityMemberUpgradeBinding? = null
    private var activity: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberUpgradeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        try {
            val bundle = intent.extras
            if (bundle != null) {
                memberUpgradeConsentResponse = bundle["link"] as MemberUpgradeConsentResponse
                val link = memberUpgradeConsentResponse?.data?.link.toString()
                binding!!.wvWebView.settings.javaScriptEnabled = true
                binding!!.wvWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                binding!!.wvWebView.settings.domStorageEnabled = true
                binding!!.wvWebView.settings.allowContentAccess = true
                binding!!.wvWebView.settings.allowUniversalAccessFromFileURLs = true
                //binding?.wvWebView?.loadUrl("https://api.capitalnow.in/index.php/Limitenhancement/statusUpdate/os")
                binding?.wvWebView?.loadUrl(link)
                val mActivityRef = WeakReference<Activity>(this)
                binding!!.wvWebView.webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                            if(url.startsWith("mailto:")) {
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
                            } else if
                            (url.contains("cnmobile/dashboard")) {
                                val intent =
                                    Intent(applicationContext, DashboardActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.putExtra("refresh",true)
                                startActivity(intent)
                            } else {
                                view.loadUrl(url)
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

            }

            binding?.ivBack?.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
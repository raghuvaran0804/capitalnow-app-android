package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.DownloadListener
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityBbpswebViewBinding
import com.capitalnowapp.mobile.models.GetBbpsLinkGeneratorReq
import com.capitalnowapp.mobile.models.GetBbpsLinkGeneratorResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson


class BBPSWebViewActivity : BaseActivity() {
    private val binding by lazy { ActivityBbpswebViewBinding.inflate(layoutInflater) }
    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1
    }

    private var getBbpsLinkGeneratorResponse: GetBbpsLinkGeneratorResponse? = null
    private var pbKey: String = ""
    private var activity: AppCompatActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        activity = this
        initView()
    }

    private fun initView() {
        try {
            if (intent.extras != null) {
                if (intent.hasExtra("pbKey1")) {
                    pbKey = intent.getStringExtra("pbKey1").toString()
                } else if (intent.hasExtra("pbKey2")) {
                    pbKey = intent.getStringExtra("pbKey2").toString()
                } else if (intent.hasExtra("pbKey3")) {
                    pbKey = intent.getStringExtra("pbKey3").toString()
                } else if (intent.hasExtra("pbKey4")) {
                    pbKey = intent.getStringExtra("pbKey4").toString()
                } else if (intent.hasExtra("pbKey5")) {
                    pbKey = intent.getStringExtra("pbKey5").toString()
                } else if (intent.hasExtra("pbKey6")) {
                    pbKey = intent.getStringExtra("pbKey6").toString()
                } else if (intent.hasExtra("pbKey7")) {
                    pbKey = intent.getStringExtra("pbKey7").toString()
                } else if (intent.hasExtra("pbKey8")) {
                    pbKey = intent.getStringExtra("pbKey8").toString()
                } else if (pbKey == null || pbKey == "") {
                    pbKey = ""
                }
            }
            bbpsLinkGenerator()
            binding.tvBack.setOnClickListener {
                onBackPressed()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun bbpsLinkGenerator() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getBbpsLinkGeneratorReq = GetBbpsLinkGeneratorReq()
            val token = userToken
            if (pbKey.contains("\r\n")) {
                pbKey = pbKey.replace("\r\n", "")
            }
            getBbpsLinkGeneratorReq.billerType = pbKey
            genericAPIService.bbpsLinkGenerator(getBbpsLinkGeneratorReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getBbpsLinkGeneratorResponse = Gson().fromJson(
                    responseBody,
                    GetBbpsLinkGeneratorResponse::class.java
                )
                if (getBbpsLinkGeneratorResponse != null && getBbpsLinkGeneratorResponse!!.status == true) {
                    if (!getBbpsLinkGeneratorResponse!!.url.isNullOrEmpty()) {
                        loadWebView(getBbpsLinkGeneratorResponse?.url)
                    }
                } else {

                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        getBbpsLinkGeneratorResponse!!.message
                    )
                }
            }

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

                        if (url?.contains("gpay:") == true || url?.contains("phonepe:") == true) {    //To allow link which starts with upi://
                            val intent = Intent(Intent.ACTION_VIEW) // To show app chooser
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                            return true
                        } else if (url?.startsWith("upi:") == true) {
                            val intent = Intent(Intent.ACTION_VIEW) // To show app chooser
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                            return true
                        }

                        view?.loadUrl(url.toString())
                        return true

                    } catch (e: ActivityNotFoundException) {
                        view?.stopLoading();
                        Toast.makeText(
                            activity,
                            "UPI supported applications not found",
                            Toast.LENGTH_SHORT
                        ).show();
                    } catch (e: Exception) {
                        Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    return true
                }
            }


            binding.wvWebView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                val i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(url))
                startActivity(i)
            })


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
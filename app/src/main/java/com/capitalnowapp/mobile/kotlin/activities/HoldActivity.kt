package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityHoldBinding
import com.capitalnowapp.mobile.models.GetHoldStatusReq
import com.capitalnowapp.mobile.models.GetHoldStatusResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import java.net.URLDecoder


class HoldActivity : BaseActivity() {
    private var deocdedUrl: String? = null
    private var url: String? = null
    private var getHoldStatusResponse: GetHoldStatusResponse? = null
    private var binding: ActivityHoldBinding? = null
    private var activity: AppCompatActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHoldBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        try{
            getHoldStatus()
            binding?.tvProceed?.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            binding?.wvWebView?.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(deocdedUrl!!)
                    return true
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun getHoldStatus() {
        try{
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity)
            val getHoldStatusReq = GetHoldStatusReq()
            val token = userToken
            genericAPIService.getHoldStatus(getHoldStatusReq, token)
            Log.d("req", Gson().toJson(getHoldStatusReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getHoldStatusResponse = Gson().fromJson(
                    responseBody,
                    GetHoldStatusResponse::class.java
                )
                if (getHoldStatusResponse != null && getHoldStatusResponse!!.status == true) {
                    url = getHoldStatusResponse?.data?.link
                    deocdedUrl = URLDecoder.decode(url, "UTF-8")
                    Log.d("decodedurl", deocdedUrl.toString())
                    binding?.wvWebView!!.loadUrl(deocdedUrl!!)
                    binding?.wvWebView!!.settings.javaScriptEnabled = true
                    binding?.wvWebView!!.settings.allowContentAccess = true
                    binding?.wvWebView!!.settings.domStorageEnabled = true
                    binding?.wvWebView!!.settings.useWideViewPort = true
                } else {
                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        getHoldStatusResponse!!.message
                    )
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }


}
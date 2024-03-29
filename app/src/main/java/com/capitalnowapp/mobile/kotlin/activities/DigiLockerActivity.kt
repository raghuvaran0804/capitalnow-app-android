package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityDigiLockerBinding
import com.capitalnowapp.mobile.models.OpenDigiLockerReq
import com.capitalnowapp.mobile.models.OpenDigiLockerResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class DigiLockerActivity : BaseActivity() {
    private var binding: ActivityDigiLockerBinding? = null
    private var activity: AppCompatActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDigiLockerBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        try {
            binding?.tvContinue?.setOnClickListener {
                openDigiLocker()
            }
            binding?.tvBack?.setOnClickListener {
                val intent = Intent(activity , DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun openDigiLocker() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity,0)
            val openDigiLockerReq = OpenDigiLockerReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.openDigiLocker(openDigiLockerReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val openDigiLockerResponse = Gson().fromJson(responseBody, OpenDigiLockerResponse::class.java)
                //   couponsResponse.status = false
                if (openDigiLockerResponse != null && openDigiLockerResponse.status == true) {
                    loadDigiLocker(openDigiLockerResponse)
                } else {
                    Toast.makeText(this, openDigiLockerResponse.message, Toast.LENGTH_SHORT)
                        .show()
                }

            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                Toast.makeText(this, getString(R.string.error_failure), Toast.LENGTH_SHORT)
                    .show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadDigiLocker(openDigiLockerResponse: OpenDigiLockerResponse) {
        try{
            binding?.llMainView?.visibility = View.GONE
            binding?.wvWebView?.visibility = View.VISIBLE
            val url = openDigiLockerResponse.data?.signzyUrl

            binding?.wvWebView!!.settings.javaScriptEnabled = true
            binding?.wvWebView!!.settings.allowContentAccess = true
            binding?.wvWebView!!.settings.domStorageEnabled = true
            binding?.wvWebView!!.settings.useWideViewPort = true
            binding?.wvWebView!!.settings.domStorageEnabled = true
            binding!!.wvWebView.loadUrl(url.toString())
            binding?.wvWebView?.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    try {

                        if (url?.contains("https://www.capitalnow.in/clsoeappsuccess") == true ) {
                            val intent = Intent(activity , DashboardActivity::class.java)
                            intent.putExtra("from", "digiLockersuccess")
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            return true
                        }else if(url?.contains("https://www.capitalnow.in/clsoeappfailure") == true ){
                            val intent = Intent(activity , DashboardActivity::class.java)
                            intent.putExtra("from", "digiLockerfailure")
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            return true
                        }

                        view?.loadUrl(url!!)
                        return true

                    }  catch (e: Exception) {
                        Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    return true
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}
package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityEnachBinding
import com.capitalnowapp.mobile.models.CreateMandateReq
import com.capitalnowapp.mobile.models.CreateMandateResponse
import com.capitalnowapp.mobile.models.NachData
import com.capitalnowapp.mobile.models.RazorPayCreateMandateReq
import com.capitalnowapp.mobile.models.RazorPayCreateMandateResponse
import com.capitalnowapp.mobile.models.RazorPaySubmitMandateReq
import com.capitalnowapp.mobile.models.RazorPaySubmitMandateResponse
import com.capitalnowapp.mobile.models.SubmitMandateReq
import com.capitalnowapp.mobile.models.SubmitMandateResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference


class ENachActivity : BaseActivity(), LifecycleObserver {
    private var nachUrl: String? = null
    var binding: ActivityEnachBinding? = null
    private var activity: AppCompatActivity? = null
    private var firstTimeResume = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnachBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {

        val obj = JSONObject()
        try {
            obj.put("cnid",userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.eNach_mandate_page_landed))

        binding?.tvBack?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding!!.flMain.visibility = View.VISIBLE
        binding!!.wvNote.visibility = View.VISIBLE
        binding!!.wvNote.settings.javaScriptEnabled = true
        binding!!.wvNote.loadUrl("https://app.capitalnow.in/mpage/nach_information")
        binding!!.tvContinue.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid",userDetails.qcId)
                obj.put(getString(R.string.interaction_type),"CONTINUE Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.eNach_mandate_page_interacted))

            createMandate()
            //razorPayCreateMandate()

        }
        binding!!.tvOk.setOnClickListener {


            val obj = JSONObject()
            try {
                obj.put("cnid",userDetails.qcId)
                obj.put(getString(R.string.interaction_type),"OK Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.eNach_mandate_success_page_interacted))

            startActivity(Intent(this@ENachActivity, DashboardActivity::class.java))
            finishAffinity()
        }
        binding!!.tvRetry.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid",userDetails.qcId)
                obj.put(getString(R.string.interaction_type),"Retry Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.eNach_mandate_failure_page_interacted))

            createMandate()
            //razorPayCreateMandate()
        }
        if (intent.extras != null) {
            val nachData = intent.getSerializableExtra("nachData") as NachData
            if (nachData != null) {
                binding!!.tvBankName.text = nachData.bankName
                binding!!.tvAccountNumber.text = nachData.bankAcNumber
            }
        }
    }

    private fun createMandate() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity,0)
        val createMandateReq = CreateMandateReq()
        createMandateReq.platform = "Android"
        createMandateReq.deviceId = Constants.SP_DEVICE_UNIQUE_ID
        val token = userToken
        genericAPIService.createMandate(createMandateReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val createMandateResponse =
                Gson().fromJson(responseBody, CreateMandateResponse::class.java)
            if (createMandateResponse != null && createMandateResponse.status == Constants.STATUS_SUCCESS && createMandateResponse.data?.webviewUrl != null) {
                nachUrl = createMandateResponse.data.webviewUrl
                if (nachUrl != null && nachUrl != "") {
                    if (createMandateResponse?.data.mandateProvider == 1 || createMandateResponse?.data.mandateProvider == 3) {
                        loadNach(nachUrl!!)
                    }else if (createMandateResponse?.data.mandateProvider == 2){
                        loadRazorpay(nachUrl!!)
                    }

                } else {
                    binding!!.wvNach.visibility = View.GONE
                    binding!!.flMain.visibility = View.VISIBLE
                }
            } else {
                CNAlertDialog.showAlertDialog(
                    activity,
                    resources.getString(R.string.title_alert),
                    createMandateResponse.message
                )
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNProgressDialog.hideProgressDialog()

            }
        }
    }

    private fun razorPayCreateMandate() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity, 0)
        val razorPayCreateMandateReq = RazorPayCreateMandateReq()
        razorPayCreateMandateReq.platform = "Android"
        razorPayCreateMandateReq.deviceId = Utility.getInstance().getDeviceUniqueId(this)
        val token = userToken
        genericAPIService.razorPayCreateMandate(razorPayCreateMandateReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val razorPayCreateMandateResponse =
                Gson().fromJson(responseBody, RazorPayCreateMandateResponse::class.java)
            if (razorPayCreateMandateResponse.status == true && razorPayCreateMandateResponse.razorpaydata?.webviewUrl != null) {
                nachUrl = razorPayCreateMandateResponse.razorpaydata?.webviewUrl
                if (nachUrl != null && nachUrl != "") {
                    loadNach(nachUrl!!)
                    //loadRazorpay(nachUrl!!)
                } else {
                    binding!!.wvNach.visibility = View.GONE
                    binding!!.flMain.visibility = View.VISIBLE
                }
            } else {
                CNAlertDialog.showAlertDialog(
                    activity,
                    resources.getString(R.string.title_alert),
                    razorPayCreateMandateResponse.message
                )
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNProgressDialog.hideProgressDialog()

            }
        }
    }

    private fun submitMandate(req: SubmitMandateReq) {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity)
        val token = userToken
        genericAPIService.submitMandate(req, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val submitMandateResponse = Gson().fromJson(
                responseBody,
                SubmitMandateResponse::class.java
            )
            if (submitMandateResponse != null && submitMandateResponse.status == Constants.STATUS_SUCCESS) {
                binding!!.wvNach.visibility = View.GONE
                binding!!.flMain.visibility = View.VISIBLE
                binding!!.llSuccess.visibility = View.VISIBLE
                binding!!.llFailure.visibility = View.GONE
                binding!!.tvSuccessTitle.text = submitMandateResponse.title
                binding!!.tvSuccessText.text = submitMandateResponse.message
                binding!!.tvOk.visibility = View.VISIBLE
                binding!!.tvContinue.visibility = View.GONE
                binding!!.wvNote.visibility = View.GONE

            } else {
                binding!!.wvNach.visibility = View.GONE
                binding!!.flMain.visibility = View.VISIBLE
                binding!!.llFailure.visibility = View.VISIBLE
                binding!!.llSuccess.visibility = View.GONE
                binding!!.tvFailureTitle.text = submitMandateResponse.title
                binding!!.tvFailureText.text = submitMandateResponse.message
                binding!!.tvRetry.visibility = View.VISIBLE
                binding!!.tvContinue.visibility = View.GONE
                binding!!.wvNote.visibility = View.GONE
            }

        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                CNProgressDialog.hideProgressDialog()
                CNAlertDialog.showAlertDialog(
                    activity,
                    resources.getString(R.string.title_alert),
                    getString(R.string.error_failure)
                )

            }
        }
    }

    private fun razorPaySubmitMandate(req: RazorPaySubmitMandateReq) {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity, 0)
        val token = userToken
        genericAPIService.razorPaySubmitMandate(req, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val razorPaySubmitMandateResponse = Gson().fromJson(
                responseBody,
                RazorPaySubmitMandateResponse::class.java
            )
            if (razorPaySubmitMandateResponse != null && razorPaySubmitMandateResponse.status == true) {
                binding!!.wvNach.visibility = View.GONE
                binding!!.flMain.visibility = View.VISIBLE
                binding!!.llSuccess.visibility = View.VISIBLE
                binding!!.llFailure.visibility = View.GONE
                //binding!!.tvSuccessTitle.text = razorPaySubmitMandateResponse.title
                binding!!.tvSuccessText.text = razorPaySubmitMandateResponse.message
                binding!!.tvOk.visibility = View.VISIBLE
                binding!!.tvContinue.visibility = View.GONE
                binding!!.wvNote.visibility = View.GONE

            } else {
                binding!!.wvNach.visibility = View.GONE
                binding!!.flMain.visibility = View.VISIBLE
                binding!!.llFailure.visibility = View.VISIBLE
                binding!!.llSuccess.visibility = View.GONE
                //binding!!.tvFailureTitle.text = submitMandateResponse.title
                binding!!.tvFailureText.text = razorPaySubmitMandateResponse.message
                binding!!.tvRetry.visibility = View.VISIBLE
                binding!!.tvContinue.visibility = View.GONE
                binding!!.wvNote.visibility = View.GONE
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                CNProgressDialog.hideProgressDialog()
                CNAlertDialog.showAlertDialog(
                    activity,
                    resources.getString(R.string.title_alert),
                    getString(R.string.error_failure)
                )

            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadNach(weblink: String) {
        binding!!.wvNach.visibility = View.VISIBLE
        binding!!.flMain.visibility = View.GONE
        binding!!.wvNach.settings.javaScriptEnabled = true
        binding!!.wvNach.loadUrl(weblink)
        val mActivityRef = WeakReference<Activity>(this)
        binding!!.wvNach.webViewClient = object : WebViewClient() {
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
                    url.contains("cca_mobile_handler/digio_halnder.php?status=success") -> {

                        val sanitizer = UrlQuerySanitizer(url)
                        val req = SubmitMandateReq()
                        req.digioDocId = sanitizer.getValue("digio_doc_id")
                        req.digioMessage = sanitizer.getValue("message")
                        req.digioStatus = sanitizer.getValue("status")
                        submitMandate(req)
                        //BankProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
                        //startTimer()
                        binding!!.wvNach.visibility = View.GONE
                    }
                    url.contains("cca_mobile_handler/digio_halnder.php?status=failure") -> {

                        val sanitizer = UrlQuerySanitizer(url)
                        val req = SubmitMandateReq()
                        req.digioDocId = sanitizer.getValue("digio_doc_id")
                        req.digioMessage = sanitizer.getValue("message")
                        req.digioStatus = sanitizer.getValue("status")
                        submitMandate(req)
                        //BankProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
                        //startTimer()
                        binding!!.wvNach.visibility = View.GONE
                    }
                    url.contains("cca_mobile_handler/digio_halnder.php?status=cancel") -> {
                        val sanitizer = UrlQuerySanitizer(url)
                        val req = SubmitMandateReq()
                        req.digioDocId = sanitizer.getValue("digio_doc_id")
                        req.digioMessage = sanitizer.getValue("message")
                        req.digioStatus = sanitizer.getValue("status")
                        submitMandate(req)
                        binding!!.wvNach.visibility = View.GONE
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
        binding!!.wvNach.settings.builtInZoomControls = true
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadRazorpay(weblink: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(weblink)
        startActivity(openURL)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (!firstTimeResume) {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("from", "fromEnach")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            firstTimeResume = false
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}
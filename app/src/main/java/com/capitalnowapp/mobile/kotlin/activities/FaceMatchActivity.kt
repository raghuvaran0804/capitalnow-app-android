package com.capitalnowapp.mobile.kotlin.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.MailTo
import android.net.UrlQuerySanitizer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityFaceMatchBinding
import com.capitalnowapp.mobile.models.CreateKycReq
import com.capitalnowapp.mobile.models.CreateVKYCResponse
import com.capitalnowapp.mobile.models.SubmitVKYCReq
import com.capitalnowapp.mobile.models.SubmitVKYCResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.TrackingUtil
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.Timer
import kotlin.concurrent.schedule


class FaceMatchActivity : BaseActivity() {
    private var submitVKYCResponse: SubmitVKYCResponse? = null
    private var vKYCUrl: String? = null
    var binding: ActivityFaceMatchBinding? = null
    private var activity: AppCompatActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceMatchBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {

        val obj = JSONObject()
        try {
            obj.put("cnid", userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.video_KYC_page_landed))

        binding?.tvBack?.setOnClickListener {
            onBackPressed()

        }

        binding!!.tvStartVideoKyc.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "START VIDEO KYC Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.video_KYC_page_interacted))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this@FaceMatchActivity,
                        android.Manifest.permission.RECORD_AUDIO,
                    ) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(
                        this@FaceMatchActivity,
                        android.Manifest.permission.CAMERA,
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@FaceMatchActivity,
                        arrayOf(
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.CAMERA
                        ),
                        10001
                    )

                } else {
                    createVKYC()
                }
            } else {
                createVKYC()
            }
        }
        binding!!.tvSubmit.setOnClickListener {


            val obj = JSONObject()
            try {
                obj.put("cnid", userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "DONE Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.video_KYC_success_page_interacted))
            if (submitVKYCResponse?.statusRedirect == 8) {
                if (sharedPreferences.getBoolean("fromDocs")) {
                    onBackPressed()
                } else {
                    startActivity(
                        Intent(
                            this@FaceMatchActivity,
                            UploadBankDetailsActivity::class.java
                        )
                    )
                    finishAffinity()
                }
            } else {
                if (sharedPreferences.getBoolean("fromDocs")) {
                    onBackPressed()
                } else {
                    startActivity(Intent(this@FaceMatchActivity, DashboardActivity::class.java))
                    finishAffinity()
                }
            }

        }
        binding!!.tvRetry.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "RETRY Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.video_KYC_failure_page_interacted))

            binding!!.llFaiilure.visibility = View.GONE
            binding!!.tvRetry.visibility = View.GONE
            binding!!.wvFaceMatch.visibility = View.VISIBLE
            createVKYC()
        }
    }

    private fun createVKYC() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity, 0)
        val createKycReq = CreateKycReq()
        createKycReq.userId = userDetails.userId
        val token = userToken
        genericAPIService.createVKYC(createKycReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val createVKYCResponse =
                Gson().fromJson(responseBody, CreateVKYCResponse::class.java)
            if (createVKYCResponse != null && createVKYCResponse.status == Constants.STATUS_SUCCESS && createVKYCResponse.vKycdata?.webviewUrl != null) {
                vKYCUrl = createVKYCResponse.vKycdata!!.webviewUrl
                if (vKYCUrl != null && vKYCUrl != "") {
                    loadVKYC(vKYCUrl!!)
                } else {
                    binding!!.wvFaceMatch.visibility = View.GONE
                    binding!!.llInstructions.visibility = View.VISIBLE
                    binding!!.tvStartVideoKyc.visibility = View.VISIBLE
                }
            } else {
                CNAlertDialog.showAlertDialog(
                    activity,
                    resources.getString(R.string.title_alert),
                    createVKYCResponse.message
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

    private fun submitVKYC(req: SubmitVKYCReq) {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity, 0)
        val token = userToken
        genericAPIService.submitVKYC(req, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            submitVKYCResponse = Gson().fromJson(
                responseBody,
                SubmitVKYCResponse::class.java
            )
            if (submitVKYCResponse != null && submitVKYCResponse!!.status == Constants.STATUS_SUCCESS) {
                binding!!.wvFaceMatch.visibility = View.GONE
                binding!!.llInstructions.visibility = View.GONE
                binding!!.tvStartVideoKyc.visibility = View.GONE
                binding!!.llFaiilure.visibility = View.GONE
                binding!!.tvRetry.visibility = View.GONE
                binding!!.llSuccess.visibility = View.VISIBLE
                binding!!.llInfo.visibility = View.VISIBLE
                binding!!.tvSubmit.visibility = View.VISIBLE

            } else {
                binding!!.wvFaceMatch.visibility = View.GONE
                binding!!.llInstructions.visibility = View.GONE
                binding!!.tvStartVideoKyc.visibility = View.GONE
                binding!!.llInfo.visibility = View.VISIBLE
                binding!!.llFaiilure.visibility = View.VISIBLE
                binding!!.tvRetry.visibility = View.VISIBLE
                binding!!.llSuccess.visibility = View.GONE
                binding!!.tvSubmit.visibility = View.GONE
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

    private fun loadVKYC(weblink: String) {
        binding!!.wvFaceMatch.visibility = View.VISIBLE
        binding!!.llInfo.visibility = View.GONE
        binding!!.llInstructions.visibility = View.GONE
        binding!!.tvStartVideoKyc.visibility = View.GONE
        binding!!.wvFaceMatch.settings.javaScriptEnabled = true
        binding!!.wvFaceMatch.loadUrl(weblink)
        val mActivityRef = WeakReference<Activity>(this)
        binding!!.wvFaceMatch.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    request.grant(request.resources)
                }

            }
        }
        binding!!.wvFaceMatch.webViewClient = object : WebViewClient() {
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
                        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
                        val sanitizer = UrlQuerySanitizer(url)
                        val req = SubmitVKYCReq()
                        req.digioDocId = sanitizer.getValue("digio_doc_id")
                        Timer().schedule(5000) {
                            submitVKYC(req)
                            CNProgressDialog.hideProgressDialog()
                        }
                        binding!!.wvFaceMatch.visibility = View.GONE
                    }

                    url.contains("cca_mobile_handler/digio_halnder.php?status=failure") -> {

                        val sanitizer = UrlQuerySanitizer(url)
                        val req = SubmitVKYCReq()
                        req.digioDocId = sanitizer.getValue("digio_doc_id")

                        submitVKYC(req)


                        binding!!.wvFaceMatch.visibility = View.GONE
                    }

                    url.contains("cca_mobile_handler/digio_halnder.php?status=cancel") -> {
                        val sanitizer = UrlQuerySanitizer(url)
                        val req = SubmitVKYCReq()
                        req.digioDocId = sanitizer.getValue("digio_doc_id")
                        submitVKYC(req)

                        binding!!.wvFaceMatch.visibility = View.GONE
                        binding!!.llInfo.visibility = View.VISIBLE
                        binding!!.llInstructions.visibility = View.VISIBLE
                        binding!!.tvStartVideoKyc.visibility = View.VISIBLE
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
        binding!!.wvFaceMatch.settings.builtInZoomControls = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10001 && grantResults.isNotEmpty()) {
            var canStart = false;
            if (grantResults.size == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canStart = true;
                }
            } else if (grantResults.size == 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    canStart = true;
                }
            }
            if (canStart) {
                createVKYC()
            } else {
                Toast.makeText(
                    this,
                    "Please allow the Required permissions for verifying video KYC. ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }
}



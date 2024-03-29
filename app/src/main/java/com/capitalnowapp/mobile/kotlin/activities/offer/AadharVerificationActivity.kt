package com.capitalnowapp.mobile.kotlin.activities.offer

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.MailTo
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityAadharVerification2Binding
import com.capitalnowapp.mobile.models.AadharOtpReq
import com.capitalnowapp.mobile.models.AadharOtpResponse
import com.capitalnowapp.mobile.models.GenericResponse
import com.capitalnowapp.mobile.models.VerifyAadharOtpReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.TancText
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.chaos.view.PinView
import com.google.gson.Gson
import java.lang.ref.WeakReference

class AadharVerificationActivity : BaseActivity() {
    private var aadharNum: String = ""
    private var accessKey = ""
    private var activity: AppCompatActivity? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var aadharOtpResponse = AadharOtpResponse()
    private var binding: ActivityAadharVerification2Binding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAadharVerification2Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }
    private fun initView() {
        profileFormData()
        binding!!.etAadhar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                when (s?.length) {
                    12 -> {
                        aadharNum = s.toString()
                    }
                }
                binding!!.etAadhar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        })

        binding!!.tvSendOtp.setOnClickListener {
            if ( binding!!.cbConfirmAadhaar.isChecked) {
                sendAadharOtp()
            } else {
                if(binding!!.etAadhar.length() <= 11) {
                    Toast.makeText(this, "Enter Valid Aadhar Number", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Please select the consent", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 21
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                    setData()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setData() {
        try {
            if(profileFormDataResponse.offerHelp?.icon !=null){
                Glide.with(this).load(profileFormDataResponse.offerHelp!!.icon)
                    .into(binding?.ivHelp!!)
            }
            binding?.etAadhar?.setText(profileFormDataResponse.profileformData?.paadharNumber)

            var kfsLinkData = profileFormDataResponse.profileformData?.tancText?.get(0)
            setBorrowTerms(kfsLinkData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBorrowTerms(kfsLinkData: TancText?) {
        try {

            var startIndex: Int
            var endIndex: Int
            val ss: SpannableString?
            if (kfsLinkData?.message != null) {
                val words: List<String> = kfsLinkData.findText!!.split("||")
                val links: List<String> = kfsLinkData.replaceLinks!!.split("||")
                ss = SpannableString(kfsLinkData.message)
                for (w in words.withIndex()) {
                    val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(textView: View) {
                            var url = links[w.index]
                            showTermsPolicyDialog(w.value, url)

                        }
                    }
                    startIndex = ss.indexOf(w.value, 0)
                    endIndex = startIndex + w.value.length
                    ss.setSpan(
                        termsAndCondition,
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    binding?.tvAadhaarConfirm?.text = ss
                    binding?.tvAadhaarConfirm?.movementMethod = LinkMovementMethod.getInstance()
                }
            }

        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder(
            (activity as BaseActivity).activityContext,
            R.style.RulesAlertDialogStyle
        )
        val inflater: LayoutInflater = this@AadharVerificationActivity.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.cs_dialog_terms_conditions, null)
        alert.setView(dialogView)
        val tvTitle: CNTextView = dialogView.findViewById(R.id.et_title)
        tvTitle.text = title
        val pb = dialogView.findViewById<ProgressBar>(R.id.pb)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        val dialog: Dialog = alert.create()
        val mActivityRef = WeakReference<Activity>(activity)
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
                }else if(url.contains("https://www.capitalnow.in/clsoeapp")) {
                    webView.visibility = View.GONE
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
        webView.loadUrl(link)
        dialog.show()
    }

    private fun sendAadharOtp() {
        CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this)
        val aadharOtpReq = AadharOtpReq()
        aadharOtpReq.userId = userDetails.userId
        aadharOtpReq.aadharno = aadharNum
        val token = userToken
        genericAPIService.sendCsAadharOtp(aadharOtpReq,token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            aadharOtpResponse = Gson().fromJson(responseBody, AadharOtpResponse::class.java)
            if (aadharOtpResponse != null && aadharOtpResponse.status == true) {
                val bundle = Bundle()
                bundle.putString("accessKey", aadharOtpResponse.accessKey.toString())
                bundle.putString("aadharNum", aadharNum)
                //(activity as DashboardActivity).replaceAadharOtp(bundle)
                showPopup(bundle)
            } else {
                //Failure
                Toast.makeText(this, "OTP for Aadhar verification Failed", Toast.LENGTH_SHORT).show()
                //binding!!.tvSkip.visibility = View.VISIBLE
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNProgressDialog.hideProgressDialog()
            }
        }
    }

    private fun showPopup(bundle: Bundle) {
        var otp = ""
        val alertDialog = Dialog(this)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.verify_aadhar_otp_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        val tvVerifyOtp = alertDialog.findViewById<TextView>(R.id.tvVerifyOtp)
        val ivCancel = alertDialog.findViewById<ImageView>(R.id.ivCancel)
        val tvOtp = alertDialog.findViewById<PinView>(R.id.tvOtp)
        //tvOtp.text = otpToSend
        tvOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                otp = s.toString()
                if (otp.length == 6) {
                    hideKeyboard(this@AadharVerificationActivity)
                    tvVerifyOtp.isEnabled = true
                } else {
                    tvVerifyOtp.isEnabled = false
                }
            }
        })
        tvVerifyOtp.setOnClickListener {
            verifyAadharOtp(otp, alertDialog)
        }
        ivCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun verifyAadharOtp(otp: String, alertDialog: Dialog) {
        CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity)
        val verifyAadharOtpReq = VerifyAadharOtpReq()
        verifyAadharOtpReq.accesscode = aadharOtpResponse.accessKey.toString()
        verifyAadharOtpReq.otp = otp
        verifyAadharOtpReq.userId = userDetails.userId
        val token = userToken
        genericAPIService.verifyCsAadharOtp(verifyAadharOtpReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val genericResponse = Gson().fromJson(responseBody, GenericResponse::class.java)
            if (genericResponse != null && genericResponse.status) {
                //Success
                alertDialog.dismiss()
                getApplyLoanDataBase(true)

            } else {
                CNAlertDialog.showAlertDialog(
                    this,
                    resources.getString(R.string.title_alert),
                    genericResponse.message
                )
                alertDialog.dismiss()
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNProgressDialog.hideProgressDialog()
                alertDialog.dismiss()
            }
        }

    }
}
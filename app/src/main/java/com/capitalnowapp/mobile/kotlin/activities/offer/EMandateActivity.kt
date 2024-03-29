package com.capitalnowapp.mobile.kotlin.activities.offer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityEmandateBinding
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.TancText
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import java.lang.ref.WeakReference

class EMandateActivity : BaseActivity() {
    private var activity: AppCompatActivity? = null
    private var nachUrl: String? = null
    private var firstTimeResume = true;
    private var binding:ActivityEmandateBinding? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmandateBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        profileFormData()
        binding!!.flMain.visibility = View.VISIBLE
        binding!!.wvNote.visibility = View.VISIBLE
        binding!!.wvNote.settings.javaScriptEnabled = true
        binding!!.wvNote.loadUrl("https://app.capitalnow.in/mpage/nach_information")
        binding!!.tvContinue.setOnClickListener {
            if(binding?.cbAgreeTerms?.isChecked!!) {
                razorPayCreateMandate()
            }else{
                Toast.makeText(this, "Please Check the Consent", Toast.LENGTH_SHORT).show()
            }

        }
        /*binding!!.tvContinue.setOnClickListener {
            startActivity(Intent(this@EMandateActivity, DashboardActivity::class.java))
            finishAffinity()
        }*/
        binding!!.tvRetry.setOnClickListener {
            razorPayCreateMandate()
        }
        /*if (intent.extras != null) {
            val nachData = intent.getSerializableExtra("nachData") as NachData
            if (nachData != null) {
                binding!!.tvBankName.text = nachData.bankName
                binding!!.tvAccountNumber.text = nachData.bankAcNumber
            }
        }*/
    }
    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 25
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
        try{
            binding?.tvBankName?.text = profileFormDataResponse.profileformData?.pSalaryBankAccount
            binding?.tvAccountNumber?.text = profileFormDataResponse.profileformData?.pAccountNo
            if(profileFormDataResponse.partnerInfo?.lpLogo !=null){
                Glide.with(this).load(profileFormDataResponse.partnerInfo?.lpLogo)
                    .into(binding?.ivCsLogo!!)
            }
            binding?.tvLoanPartnerText?.text = profileFormDataResponse.partnerInfo?.lpName
            var kfsLinkData = profileFormDataResponse.profileformData?.tancText?.get(0)
            setBorrowTerms(kfsLinkData!!)


        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun setBorrowTerms(kfsLinkData: TancText) {
        var startIndex: Int
        var endIndex: Int
        val ss: SpannableString?
        if (kfsLinkData.message != null) {
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

                binding?.tvBorrowerTerms?.text = ss
                binding?.tvBorrowerTerms?.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder(
            (activity as BaseActivity).activityContext,
            R.style.RulesAlertDialogStyle
        )
        val inflater: LayoutInflater = this@EMandateActivity.layoutInflater
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

    private fun razorPayCreateMandate() {
        CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this, 0)
        val profileFormDataReq = ProfileFormDataReq()
        profileFormDataReq.pageNo = 25
        var map = HashMap<String, String>()
        map.put(profileFormDataResponse.profileformData?.tancText?.get(0)?.consentKey!!,"1")
        val token = userToken
        genericAPIService.razorPayCSCreateMandate(profileFormDataReq, token)
        genericAPIService.setOnDataListener { responseBody -> CNProgressDialog.hideProgressDialog()
            val profileFormDataResponse =
                Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
            if (profileFormDataResponse.status == true) {
                nachUrl = profileFormDataResponse.profileformData?.webViewUrl
                if (nachUrl != null && nachUrl != "") {
                    //loadNach(nachUrl!!)
                    loadRazorpay(nachUrl!!)
                } else {
                    loadRazorpay(nachUrl!!)
                    /*binding!!.wvNach.visibility = View.GONE
                    binding!!.flMain.visibility = View.VISIBLE*/
                }
            } else {
                CNAlertDialog.showAlertDialog(activity, resources.getString(R.string.title_alert), profileFormDataResponse.message)
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNProgressDialog.hideProgressDialog()

            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadRazorpay(weblink: String) {
        val openURL = Intent(android.content.Intent.ACTION_VIEW)
        openURL.data = Uri.parse(weblink)
        startActivity(openURL)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (!firstTimeResume) {
            getApplyLoanDataBase(true)
        } else {
            firstTimeResume = false
        }
    }
}


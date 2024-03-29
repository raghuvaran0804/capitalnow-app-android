package com.capitalnowapp.mobile.kotlin.activities.offer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityFinalOfferBinding
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.TancText
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import java.lang.ref.WeakReference

class FinalOfferActivity : BaseActivity() {
    private lateinit var value: String
    private lateinit var url: String
    private var activity: AppCompatActivity? = null
    private var binding: ActivityFinalOfferBinding? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var csGenericResponse = CSGenericResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinalOfferBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()

        binding?.tvAgree?.setOnClickListener {
            if (binding?.cbAgreeTerms?.isChecked!!) {
                acceptLoan()
            } else {
                Toast.makeText(this, "Please Check the Consent", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.cbAgreeTerms?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
               showTermsPolicyDialog(value,url)
            }
        }
    }

    private fun acceptLoan() {
        try{
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 24
            var map = HashMap<String, String>()
            map.put(profileFormDataResponse.profileformData?.tancText?.get(0)?.consentKey!!,"1")
            val token = userToken
            genericAPIService.agreeOffer(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                csGenericResponse = Gson().fromJson(responseBody, CSGenericResponse::class.java)
                if(csGenericResponse.status == true){
                    getApplyLoanDataBase(true)
                } else {
                    Toast.makeText(this, csGenericResponse.message, Toast.LENGTH_SHORT).show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }

        }catch(e: Exception){
            e.printStackTrace()
        }
    }

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 24
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                setFinalOfferData()
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

    @SuppressLint("SetTextI18n")
    private fun setFinalOfferData() {
        try{
            binding?.tvLoanAmount?.text = ": "+this.profileFormDataResponse.profileformData?.kfsData?.loanAmount.toString()
            binding?.tvRateOfInterest?.text =": "+ this.profileFormDataResponse.profileformData?.kfsData?.rateOfInterest.toString()
            binding?.tvProcessingFee?.text =": "+ this.profileFormDataResponse.profileformData?.kfsData?.processingFee.toString()
            binding?.tvTenure?.text =": "+ this.profileFormDataResponse.profileformData?.kfsData?.tenure.toString()
            binding?.tvEmiAmount?.text =": "+ this.profileFormDataResponse.profileformData?.kfsData?.emiAmount.toString()
            binding?.tvAPR?.text =": "+ this.profileFormDataResponse.profileformData?.kfsData?.apr.toString()
            if(profileFormDataResponse.partnerInfo?.lpLogo !=null){
                Glide.with(this).load(profileFormDataResponse.partnerInfo?.lpLogo)
                    .into(binding?.ivCsLogo!!)
            }
            binding?.tvLoanPartnerText?.text = profileFormDataResponse.partnerInfo?.lpName
            //
            // binding?.llTop?.setBackgroundColor(profileFormDataResponse.partnerInfo?.lpThemeColor!!.toInt())
            var kfsLinkData = profileFormDataResponse.profileformData?.tancText?.get(0)
            setBorrowTerms(kfsLinkData!!)

        }catch (e : Exception){
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
            url = links[0]
            value = words[0]
            for (w in words.withIndex()) {
                val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        url = links[w.index]
                        value = w.value
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
        val inflater: LayoutInflater = this@FinalOfferActivity.layoutInflater
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
                    //webView.visibility = View.GONE
                    dialog.dismiss()
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
}
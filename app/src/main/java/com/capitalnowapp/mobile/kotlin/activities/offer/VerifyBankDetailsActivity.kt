package com.capitalnowapp.mobile.kotlin.activities.offer

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityVerifyBankDetailsBinding
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback
import com.capitalnowapp.mobile.kotlin.adapters.ListFilterAdapter
import com.capitalnowapp.mobile.models.ContactUsResponse
import com.capitalnowapp.mobile.models.offerModel.OfferBankLinkReq
import com.capitalnowapp.mobile.models.offerModel.OfferBankLinkResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.SupportedBankList
import com.capitalnowapp.mobile.models.offerModel.TancText
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import java.lang.ref.WeakReference

class VerifyBankDetailsActivity : BaseActivity() {
    private var binding: ActivityVerifyBankDetailsBinding? = null
    private lateinit var adapter: ListFilterAdapter
    private var activity: AppCompatActivity? = null
    private var selectedBankId: String = ""
    private var webUrl: String = ""
    private var selectedBankName: String = ""
    private var bankList: ArrayList<SupportedBankList>? = ArrayList()
    var dialog: AlertDialog? = null
    var accountTypeListMap: LinkedHashMap<String, String>? = null
    var accountTypeKeys: Array<String>? = null
    private var accountType: String? = ""
    private var firstTimeResume = true;
    private var isBankSelected: Boolean = false
    private var isBankTypeSelected: Boolean = false
    private var bankMasterList: ArrayList<MasterData>? = ArrayList()
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var genericResponse: ContactUsResponse = ContactUsResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        profileFormData()
        binding!!.etSelectBank.setOnClickListener {
            getBanksList()
            if (bankMasterList != null && bankMasterList?.isNotEmpty()!!) {
                showCodesDialog(bankMasterList!!, 1)
            }else{
                getBanksList()
            }
        }

        accountTypeListMap = LinkedHashMap<String, String>()
        accountTypeListMap!!["NETBANKING"] = "NETBANKING"
        accountTypeListMap!!["STATEMENT"] = "STATEMENT"
        accountTypeKeys = accountTypeListMap!!.keys.toTypedArray()

        binding?.etBankType?.setOnClickListener {
            if (accountTypeKeys != null && accountTypeKeys!!.isNotEmpty()) {
                accountTypeDialog()
            }
        }
        binding!!.tvNext.setOnClickListener {
            if(binding?.cbAgreeTerms?.isChecked!!) {
                getBankLink()
            } else{
                Toast.makeText(this, "Please Check the Consent", Toast.LENGTH_SHORT).show()
            }
            /*if (isBankSelected && isBankTypeSelected && cbConfrimBank.isChecked) {

            } else {
                if (!isBankSelected) {
                    displayToast("Select Bank")
                }else if(!isBankTypeSelected){
                    displayToast("Select Bank Type")
                }  else{
                    displayToast("Please Check the concent")
                }
            }*/
        }
        binding?.ivHelp?.setOnClickListener {
            showHelpPopup()
        }
    }

    private fun showHelpPopup() {
        val alertDialog = Dialog(this)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.cn_help_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(true)
        val tvEmailText = alertDialog.findViewById<TextView>(R.id.tvEmailText)
        val tvCall = alertDialog.findViewById<TextView>(R.id.tvCall)
        val ivCancel = alertDialog.findViewById<ImageView>(R.id.ivCancel)
        tvEmailText.text = profileFormDataResponse.offerHelp?.email.toString()
        tvCall.text = profileFormDataResponse.offerHelp?.phone.toString()
        tvEmailText.setOnClickListener {
            alertDialog.dismiss()
            composeEmail()
        }
        tvCall.setOnClickListener {
            callToNum()
            alertDialog.dismiss()
        }
        ivCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun callToNum() {
        try {
            if (genericResponse.phone != "") {
                val num = genericResponse.phone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$num")
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun composeEmail() {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", genericResponse.email, null))
            startActivity(Intent.createChooser(emailIntent, "Choose to send email..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBankLink() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val offerBankLinkReq = OfferBankLinkReq()
            offerBankLinkReq.pageNo = "23"
            offerBankLinkReq.type = accountType
            offerBankLinkReq.institutionId = selectedBankId
            val token = userToken
            genericAPIService.getOfferBankWeblink(offerBankLinkReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val offerWebLinkResponse = Gson().fromJson(
                    responseBody,
                    OfferBankLinkResponse::class.java
                )
                if (offerWebLinkResponse.status == true && offerWebLinkResponse.offerWebLinkdata?.webviewUrl?.isNotEmpty() == true) {
                    webUrl = offerWebLinkResponse.offerWebLinkdata?.webviewUrl!!
                    loadBankStatement()
                } else {
                    Toast.makeText(this, offerWebLinkResponse.message, Toast.LENGTH_SHORT).show()
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                    CNProgressDialog.hideProgressDialog()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun loadBankStatement() {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(webUrl)
        startActivity(openURL)
    }

    private fun accountTypeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(accountTypeKeys) { _, which ->
            binding?.etBankType?.setText(accountTypeKeys?.get(which))
            accountType = accountTypeListMap?.get(accountTypeKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun showCodesDialog(codeArrayList: java.util.ArrayList<MasterData>, flag: Int) {
        try {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(com.capitalnowapp.mobile.R.layout.filter_dialog, null)
            val rvData: RecyclerView = view.findViewById(com.capitalnowapp.mobile.R.id.rvData)
            val tvCustom: TextView = view.findViewById(com.capitalnowapp.mobile.R.id.tvCustom)
            tvCustom.visibility = View.GONE
            rvData.layoutManager = LinearLayoutManager(this)
            val etSearchCode = view.findViewById<EditText>(com.capitalnowapp.mobile.R.id.etSearch)
            if (flag == 1) {
                etSearchCode.hint = getString(com.capitalnowapp.mobile.R.string.select_bank)
            }

            adapter = ListFilterAdapter(this, codeArrayList, SelectedIdCallback { selectedId ->
                try {
                    if (flag == 1) {
                        //setEmptyData(2)
                        for (item in 0 until bankList?.size!!) {
                            if (bankList!![item].perfiosInstitutionId.toString() == selectedId) {
                                binding!!.etSelectBank.setText(bankList!![item].razorpayBankName)
                                selectedBankId = bankList!![item].perfiosInstitutionId.toString()
                                selectedBankName = bankList!![item].razorpayBankName.toString()
                                break
                            }
                        }
                    }
                    dialog?.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            rvData.adapter = adapter

            builder.setView(view)
            builder.setCancelable(true)
            dialog = builder.create()
            dialog?.show()

            val displayMetrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val displayWidth: Int = displayMetrics.widthPixels
            val displayHeight: Int = displayMetrics.heightPixels
            val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog?.window!!.attributes)
            val dialogWindowWidth = (displayWidth * 0.8f).toInt()
            val dialogWindowHeight = (displayHeight * 0.6f).toInt()
            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window!!.attributes = layoutParams
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBanksList() {
        if (profileFormDataResponse.profileformData?.banklist?.isNotEmpty()!!) {
            bankList = ArrayList()
            bankMasterList = ArrayList()
            val bankTableData = profileFormDataResponse.profileformData?.banklist
            if (bankTableData != null) {
                for (item in bankTableData) {
                    val masterData = MasterData()
                    masterData.id = item.perfiosInstitutionId.toString()
                    masterData.name = item.razorpayBankName
                    bankMasterList?.add(masterData)
                    bankList?.add(item!!)
                }
            }
        }
    }
    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 23
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                    helpData()
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

    private fun helpData() {
        try {
            if(profileFormDataResponse.offerHelp?.icon !=null){
                Glide.with(this).load(profileFormDataResponse.offerHelp!!.icon)
                    .into(binding?.ivHelp!!)
            }
            var kfsLinkData = profileFormDataResponse.profileformData?.tancText?.get(0)
            setBorrowTerms(kfsLinkData)
        }catch (e:Exception){
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

                    binding?.tvConsentText?.text = ss
                    binding?.tvConsentText?.movementMethod = LinkMovementMethod.getInstance()
                }
            }

        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = androidx.appcompat.app.AlertDialog.Builder(
            (activity as BaseActivity).activityContext,
            R.style.RulesAlertDialogStyle
        )
        val inflater: LayoutInflater = this@VerifyBankDetailsActivity.layoutInflater
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (!firstTimeResume) {
            //getApplyLoanDataBase()
        } else {
            firstTimeResume = false
        }
    }
}
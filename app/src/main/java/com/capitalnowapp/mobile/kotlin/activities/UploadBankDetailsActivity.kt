package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.MailTo
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.BankProgressDialog
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityUploadBankDetailsBinding
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback
import com.capitalnowapp.mobile.kotlin.adapters.AdapterItemClickListener
import com.capitalnowapp.mobile.kotlin.adapters.BankStatementTypeAdapter
import com.capitalnowapp.mobile.kotlin.adapters.ListFilterAdapter
import com.capitalnowapp.mobile.kotlin.adapters.LoanOptionsAdapter
import com.capitalnowapp.mobile.models.AnalyseCapabilityReq
import com.capitalnowapp.mobile.models.AnalysisListData
import com.capitalnowapp.mobile.models.BankDetails
import com.capitalnowapp.mobile.models.BankListRes
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.GenericRequest
import com.capitalnowapp.mobile.models.GetAnalysisListResponse
import com.capitalnowapp.mobile.models.GetAnalysisTypeReq
import com.capitalnowapp.mobile.models.GetAnalysisTypeResponse
import com.capitalnowapp.mobile.models.GetBankLinkReq
import com.capitalnowapp.mobile.models.IdTextData
import com.capitalnowapp.mobile.models.SaveLoanConsentResponse
import com.capitalnowapp.mobile.models.SubmitBankChangeReq
import com.capitalnowapp.mobile.models.SubmitBankChangeResponse
import com.capitalnowapp.mobile.models.WebLinkRes
import com.capitalnowapp.mobile.models.login.RegisterDeviceResponse
import com.capitalnowapp.mobile.models.userdetails.RegisterUserReq
import com.capitalnowapp.mobile.models.userdetails.UserDetails
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.TrackingUtil
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.Locale


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class UploadBankDetailsActivity : BaseActivity(), AdapterItemClickListener {
    private var typeList: List<AnalysisListData>? = null
    private var selectedType: String? = null
    private var otherMobileNumber: String? = null
    private var getAnalysisTypeResponse: GetAnalysisTypeResponse? = null
    private var getAnalysisListResponse: GetAnalysisListResponse? = null
    private lateinit var cnModel: CNModel
    private var activity: AppCompatActivity? = null

    private var userDetails: UserDetails? = null

    private lateinit var selectedbank: BankDetails
    var binding: ActivityUploadBankDetailsBinding? = null
    var dialog: AlertDialog? = null
    private lateinit var adapter: ListFilterAdapter
    private var isRedirect: String? = ""
    private var isBankSelected: Boolean = false
    private var dynamicText = ""
    private var latestDocs = ""
    private var loansResponse: JSONObject? = null
    private var referrer: String? = ""
    private var monitType: String? = ""
    private var bankName: String? = ""
    private var bankCode: String? = ""
    private var readonly: Boolean = false
    private var monitoring: String? = null
    private var selectedMobileNumber: String? = null
    private var mobileVisible: Boolean = false
    private lateinit var bankStatementTypeadapter: BankStatementTypeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()

    }

    private fun initView() {

        val obj = JSONObject()
        try {
            obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.select_bank_page_landed))



        if (intent.extras != null) {
            if (intent.hasExtra("loansResponse")) {
                val loansResponse = intent.getStringExtra("loansResponse")
                this.loansResponse = JSONObject(loansResponse)
                if (this.loansResponse != null && this.loansResponse!!.has("loan_options")) {
                    updateStep2Response(
                        (activity as BaseActivity).userDetails.userId,
                        this.loansResponse!!
                    )
                    //this.loansResponse!!.get()
                }
                if (this.loansResponse != null && this.loansResponse!!.has("show_manual_statement")) {
                    val showManualUpload = this.loansResponse!!.getBoolean("show_manual_statement")
                    if (showManualUpload) {
                        //binding!!.llUploadBankStatement.visibility = View.VISIBLE
                    } else {
                        //binding!!.llUploadBankStatement.visibility = View.GONE
                    }
                }
                if (this.loansResponse != null && this.loansResponse!!.has("monitoring_card_data")) {
                    bankName = intent.getStringExtra("bank_name")
                    bankCode = intent.getStringExtra("bank_code")
                    monitoring = intent.getStringExtra("type")
                    referrer = monitoring
                    readonly = intent.getBooleanExtra("readonly", false)
                    binding?.etSelectBank!!.setText(bankName)
                    isBankSelected = true
                    //getAnalysisType()
                    getAnalysisList(false)
                }
            }
            //dynamicText = intent.getStringExtra("bank_statement_upload_text")!!
            if (intent.hasExtra("referrer")) {
                referrer = intent.getStringExtra("referrer")!!
            } else if (sharedPreferences.getBoolean("fromDocs")) {
                referrer = ""
                (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", false)
            }
            if (intent.hasExtra("type")) {
                monitoring = intent.getStringExtra("type")
            }
            if (!(this.loansResponse != null && this.loansResponse!!.has("monitoring_card_data")) && intent.hasExtra(
                    "bank_name"
                )
            ) {
                bankName = intent.getStringExtra("bank_name")
                bankCode = intent.getStringExtra("bank_code")
                monitoring = intent.getStringExtra("type")
                readonly = intent.getBooleanExtra("readonly", false)
                referrer = monitoring
                binding?.etSelectBank!!.setText(bankName)
                isBankSelected = true
                //getAnalysisType()
                getAnalysisList(false)
            }
            if (readonly) {
                !binding?.etSelectBank!!.isEnabled
                !binding?.etSelectBank!!.isFocusable
            } else {
                binding?.etSelectBank!!.isEnabled
                binding?.etSelectBank!!.isFocusable
            }

        }
        cnModel = CNModel(this, this, Constants.RequestFrom.HOME_PAGE)
        //(activity as DashboardActivity).isFinBit = true
        binding!!.etSelectBank.setOnClickListener {
            if (readonly) {
                !binding?.etSelectBank!!.isEnabled
                !binding?.etSelectBank!!.isFocusable
            } else {
                binding?.etSelectBank!!.isEnabled
                binding?.etSelectBank!!.isFocusable
                getBanksList()

            }
        }
        binding?.tvBack?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding?.tvCancel!!.setOnClickListener {
            binding?.etMobileNumber?.setText("")
            selectedMobileNumber = ""
        }

        binding!!.tvInternetBanking.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Checkbox Ticked")
                obj.put(getString(R.string.interaction_type), "PROCEED Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.select_bank_page_interacted))
            if (isBankSelected) {
                typeList = getAnalysisListResponse?.data
                if(selectedType == null) {
                    Toast.makeText(
                        activity, "Choose bank statement verification option.",
                        Toast.LENGTH_SHORT
                    ).show()
                }else {
                    if(mobileVisible){
                        if(selectedMobileNumber?.length == 10){
                            if (binding?.tilMobileNumber!!.visibility === VISIBLE) {
                                if (selectedMobileNumber != null && selectedMobileNumber!!.isNotEmpty() && selectedMobileNumber?.length == 10) {
                                    if (binding?.cbConfrimBank!!.isChecked) {
                                        //getBankLink()
                                        getBankWebLinkType()
                                    } else {
                                        displayToast("Please Check the consent")
                                    }
                                } else {
                                    displayToast("Please Enter Valid Mobile Number")
                                }
                            } else {
                                if(selectedType != null) {
                                    if (binding?.cbConfrimBank!!.isChecked) {
                                        //getBankLink()
                                        getBankWebLinkType()
                                    } else {
                                        displayToast("Please Check the consent")
                                    }
                                }else {
                                    Toast.makeText(
                                        activity, "Choose bank statement verification option.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }else{
                            Toast.makeText(
                                activity, "Please enter valid Mobile Number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }else {
                        if (binding?.tilMobileNumber!!.visibility === VISIBLE) {
                            if (selectedMobileNumber != null && selectedMobileNumber!!.isNotEmpty() && selectedMobileNumber?.length == 10) {
                                if (binding?.cbConfrimBank!!.isChecked) {
                                    //getBankLink()
                                    getBankWebLinkType()
                                } else {
                                    displayToast("Please Check the consent")
                                }
                            } else {
                                displayToast("Please Enter Valid Mobile Number")
                            }
                        } else {
                            if (binding?.cbConfrimBank!!.isChecked) {
                                //getBankLink()
                                getBankWebLinkType()
                            } else {
                                displayToast("Please Check the consent")
                            }
                        }
                    }
                }

            } else {
                displayToast("Select Bank")
            }
            /*if (isBankSelected && cbConfrimBank.isChecked && selectedMobileNumber != "" && selectedMobileNumber?.length == 10) {
                getBankLink()
            } else {
                if (!isBankSelected) {

                }

                else if (selectedMobileNumber == "") {
                    displayToast("Please Enter Valid Mobile Number")
                }

                else if (!cbConfrimBank.isChecked) {
                    displayToast("Please Check the consent")
                }
            }*/
        }


    }

    private fun getAnalysisList(failedCase: Boolean) {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getAnalysisTypeReq = GetAnalysisTypeReq()
            val token = userToken
            getAnalysisTypeReq.bankCode = bankCode
            getAnalysisTypeReq.referrer = referrer
            genericAPIService.getAnalysisList(getAnalysisTypeReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getAnalysisListResponse = Gson().fromJson(
                    responseBody,
                    GetAnalysisListResponse::class.java

                )
                if (getAnalysisListResponse != null && getAnalysisListResponse!!.status == true) {
                    val typeList = getAnalysisListResponse!!.data
                    setSelectedTypeData(typeList)
                    if (failedCase) {
                        CNAlertDialog.showAlertDialog(
                            this,
                            resources.getString(R.string.title_alert),
                            "Bank Statement verification failed. Kindly retry."
                        )
                    }
                } else {
                    if (getAnalysisListResponse?.code == 5091) {
                        showCustomAlertDialog5091(getAnalysisListResponse!!.message)
                    } else if (getAnalysisListResponse?.code == 5092) {
                        showCustomAlertDialog5092(getAnalysisListResponse!!.message)
                    }

                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                    CNProgressDialog.hideProgressDialog()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showCustomAlertDialog5091(message: String?) {
        try {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.custom_alert, null)
            builder.setView(view)
            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
            val tvOk = view.findViewById<TextView>(R.id.tvOk)
            val dialog = builder.create()
            /*if (intent.extras != null) {
                val finalErrorMessage = intent.getStringExtra("errorMessage")!!
                FirebaseCrashlytics.getInstance().recordException(Exception(finalErrorMessage))

            }*/
            tvTitle.text = "Alert"
            tvMessage.text = message

            tvOk.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            builder.setCancelable(true)
            dialog?.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showCustomAlertDialog5092(message: String?) {
        try {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.custom_alert, null)
            builder.setView(view)
            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
            val tvOk = view.findViewById<TextView>(R.id.tvOk)
            val dialog = builder.create()
            /*if (intent.extras != null) {
                val finalErrorMessage = intent.getStringExtra("errorMessage")!!
                FirebaseCrashlytics.getInstance().recordException(Exception(finalErrorMessage))

            }*/
            tvTitle.text = "Alert"
            tvMessage.text = message

            tvOk.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("from", "fromuploadfinbit")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            builder.setCancelable(true)
            dialog?.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAnalysisType() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getAnalysisTypeReq = GetAnalysisTypeReq()
            val token = userToken
            getAnalysisTypeReq.bankCode = bankCode
            genericAPIService.getAnalysisType(getAnalysisTypeReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getAnalysisTypeResponse = Gson().fromJson(
                    responseBody,
                    GetAnalysisTypeResponse::class.java
                )
                if (getAnalysisTypeResponse != null && getAnalysisTypeResponse!!.status == true) {

                    if (getAnalysisTypeResponse!!.data!!.vendorType == "AA") {
                        binding?.tilMobileNumber!!.visibility = VISIBLE
                        binding?.tvCancel!!.visibility = VISIBLE
                        setMobileNumberList(getAnalysisTypeResponse!!.data!!.mobNumbers)
                    } else {
                        binding?.tilMobileNumber!!.visibility = GONE
                        binding?.tvCancel!!.visibility = GONE
                    }
                } else {
                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        getAnalysisTypeResponse!!.message
                    )
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                    CNProgressDialog.hideProgressDialog()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSelectedTypeData(typeList: List<AnalysisListData>?) {
        try {
            binding?.rvSelectType?.layoutManager = LinearLayoutManager(this)
            bankStatementTypeadapter = BankStatementTypeAdapter(this, typeList, this)
            binding?.rvSelectType?.adapter = bankStatementTypeadapter

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onItemClicked(value: String?, selectedMobileNo: String?, mobileVisible : Boolean) {
        // Handle the value received from the adapter
        selectedType = value
        selectedMobileNumber = selectedMobileNo
        this.mobileVisible = mobileVisible
    }

    private fun setMobileNumberList(mobNumbers: List<String>?) {
        try {
            binding?.etMobileNumber!!.setText(mobNumbers!![0])
            selectedMobileNumber = mobNumbers[0]
            binding!!.etMobileNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    selectedMobileNumber = ""
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    when (s?.length) {
                        0 -> {
                            selectedMobileNumber = ""
                            binding?.tvCancel!!.visibility = GONE
                        }

                        10 -> {
                            binding?.tvCancel!!.visibility = VISIBLE
                            selectedMobileNumber = s.toString().trim()
                        }
                    }
                }

            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun analyseCapability() {
        val genericAPIService = GenericAPIService(activity as BaseActivity)
        val analyseCapabilityReq = AnalyseCapabilityReq()
        analyseCapabilityReq.userId = (activity as BaseActivity).userDetails.userId
        val token = userToken
        analyseCapabilityReq.devicetype = "Android"
        genericAPIService.analyseCapability(analyseCapabilityReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            val genericResponse = Gson().fromJson(
                responseBody,
                RegisterDeviceResponse::class.java
            )
            if (genericResponse.status == Constants.STATUS_SUCCESS) {
                //Success
                val token = userToken
                val userId = (activity as BaseActivity).userDetails.userId
                val req = RegisterUserReq()
                cnModel.saveStep2Registration(userId, req, token)
            } else {

                CNAlertDialog.showStatusWithCallback(
                    this,
                    genericResponse.message,
                    getString(R.string.failure_retry),
                    R.drawable.failure_new, R.color.cb_errorRed
                )
                CNAlertDialog.setRequestCode(1)
            }


        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                CNAlertDialog.showStatusWithCallback(
                    this,
                    throwable?.localizedMessage,
                    getString(R.string.failure_retry),
                    R.drawable.failure_new, R.color.cb_errorRed
                )
                CNAlertDialog.setRequestCode(1)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadFinBit(weblink: String) {
        binding!!.wvFinBit.visibility = VISIBLE
        if (binding!!.wvFinBit.visibility == VISIBLE) {
            binding!!.llProceed.visibility = GONE
            binding!!.llPageName.visibility = GONE
        } else {
            binding!!.llProceed.visibility = VISIBLE
            binding!!.llPageName.visibility = VISIBLE
        }
        binding!!.wvFinBit.settings.javaScriptEnabled = true
        binding!!.wvFinBit.settings.domStorageEnabled = true
        binding!!.wvFinBit.settings.allowContentAccess = true
        binding!!.wvFinBit.settings.allowUniversalAccessFromFileURLs = true
        binding!!.wvFinBit.settings.allowFileAccess = true
        binding!!.wvFinBit.settings.useWideViewPort = true
        binding!!.wvFinBit.settings.loadWithOverviewMode = true
        binding!!.wvFinBit.loadUrl(weblink)
        val mActivityRef = WeakReference<Activity>(this)
        binding!!.wvFinBit.webViewClient = object : WebViewClient() {
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

                    url.contains("cnmobileapp/close?status=SUCCESS") -> {

                        val obj = JSONObject()
                        try {
                            obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                            obj.put("BankName", bankName)
                            obj.put("bankDetailsTaken", "true")
                        } catch (e: JSONException) {
                            throw RuntimeException(e)
                        }
                        TrackingUtil.pushEvent(
                            obj,
                            getString(R.string.bank_details_taken_server_event)
                        )

                        BankProgressDialog.showProgressDialog(
                            activityContext,
                            Constants.LOADING_MESSAGE
                        )
                        startTimer()
                        binding!!.wvFinBit.visibility = GONE
                        binding!!.llProceed.visibility = VISIBLE
                        binding!!.llPageName.visibility = VISIBLE
                    }

                    url.contains("cnmobileapp/close?status=ERROR") -> {
                        //showSuccess(false, userId)
                        //displayToast("Bank statement uploaded failed")
                        BankProgressDialog.hideProgressDialog()

                        binding!!.wvFinBit.visibility = GONE
                        binding!!.llProceed.visibility = VISIBLE
                        binding!!.llPageName.visibility = VISIBLE
                        binding?.cbConfrimBank?.isChecked = false
                        selectedType = null
                        getAnalysisList(true)
                    }

                    url.contains("cnmobileapp/close?status=CANCEL") -> {
                        showSuccess(false, userId)
                        //displayToast("Bank statement uploaded failed")
                        binding!!.wvFinBit.visibility = GONE
                        binding!!.llProceed.visibility = VISIBLE
                        binding!!.llPageName.visibility = VISIBLE
                        binding?.cbConfrimBank?.isChecked = false
                        selectedType = null
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
                // view.visibility = View.VISIBLE
                //val css = ".menu_height{height:35px;}.. etc..." //your css as String
                //wvFinBit.evaluateJavascript("document.getElementsByClassName('header-title')[0].innerHTML = 'CapitalNow';document.getElementsByClassName('bank_link')[0].style.display = 'none';if(document.location.pathname == '/web/linkedAccounts') {document.getElementsByClassName('btn-primary')[0].style.display = 'none';document.getElementsByClassName('btn-cancel')[0].style.color = '#ffffff';document.getElementsByClassName('btn-cancel')[0].style.backgroundColor = '#0c75e6';}", null)
                super.onPageFinished(view, url)
            }

        }
        binding!!.wvFinBit.settings.builtInZoomControls = true
    }

    private fun startTimer() {
        object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                BankProgressDialog.hideProgressDialog()
                //  showSuccess(true)

                if (referrer == null || referrer!!.isEmpty()) {
                    analyseCapability()
                } else if (referrer == Constants.FIN_BIT_REFERRER.Req_Bank_Change) {
                    submitBankChange()
                } else if (referrer == Constants.FIN_BIT_REFERRER.Latest_Docs) {
                    saveLatestSalSlip()
                } else if (referrer == monitoring) {
                    gotoDashBoard()
                } else {
                    analyseCapability()
                }
            }
        }.start()
    }

    private fun showSuccess(success: Boolean, user_id: String?) {

        if (success) {
            CNAlertDialog.showStatusWithCallback(
                this,
                resources.getString(R.string.bank_success_alert),
                getString(R.string.success_continue),
                R.drawable.success_new, R.color.Primary2
            )
            CNAlertDialog.setRequestCode(1)

        } else {
            CNAlertDialog.showStatusWithCallback(
                this,
                "Bank Statement verification failed, kindly retry.",
                getString(R.string.failure_retry),
                R.drawable.failure_new, R.color.cb_errorRed
            )
            CNAlertDialog.setRequestCode(1)
        }

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {

            }

            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {
                if (buttonType == Constants.ButtonType.POSITIVE) {
                    if (success) {
                        gotoDashBoard()
                        CNAlertDialog.dismiss()
                    } else {

                    }
                }
            }
        })
    }

    fun gotoDashBoard() {
        try {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("from", "fromuploadfinbit")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateStep2Response(user_id: String?, obj: JSONObject) {
        if (obj.getInt("is_limit_changed") == 0) {
            showSuccess(true, user_id)
        } else {
            var list: ArrayList<IdTextData> = ArrayList()
            val arr = obj.getJSONArray("loan_options")
            val listType = object : TypeToken<ArrayList<IdTextData?>?>() {}.type
            list = Gson().fromJson(arr.toString(), listType)
            var limitChangedInfo = ""
            if (obj.has("limit_changed_info")) {
                limitChangedInfo = obj.getString("limit_changed_info")
            }

            showOptions(
                list,
                obj.getString("limit_change_message"),
                obj.getString("new_limit"),
                limitChangedInfo
            )
            //showOptions(list,obj.getString("limit_changed_info"),)

        }
    }

    private fun showOptions(
        list: ArrayList<IdTextData>?,
        msg: String,
        amount: String,
        limitChangedInfo: String
    ) {

        try {
            val masterDataList: ArrayList<IdTextData> = ArrayList()
            for (bank in list!!) {
                val data = IdTextData()
                data.id = bank.id
                data.text = bank.text
                masterDataList.add(data)
            }
            val builder = AlertDialog.Builder(activity as BaseActivity)
            val view = layoutInflater.inflate(R.layout.credit_limit_alert, null)
            val rvData: RecyclerView = view.findViewById(R.id.rvData)
            val tvCreditLimitMessage: TextView = view.findViewById(R.id.tvCreditLimitMessage)
            val tvAmountMessage: TextView = view.findViewById(R.id.tvAmountMessage)
            val tvAgree: TextView = view.findViewById(R.id.tvAgree)
            val tvInfoDownPayment: TextView = view.findViewById(R.id.tvInfoDownPayment)
            val ivInfo: ImageView = view.findViewById(R.id.ivInfo)

            if (limitChangedInfo != null && limitChangedInfo != "") {
                tvInfoDownPayment.text = limitChangedInfo
                tvInfoDownPayment.visibility = VISIBLE
                ivInfo.visibility = VISIBLE
            } else {
                tvInfoDownPayment.visibility = GONE
                ivInfo.visibility = GONE
            }


            rvData.layoutManager = LinearLayoutManager(activity as BaseActivity)

            val adapter = LoanOptionsAdapter(list)
            rvData.adapter = adapter

            tvAgree.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put("isAccepted", "true")
                    obj.put(getString(R.string.interaction_type), "Agree & Continue Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(
                    obj,
                    getString(R.string.credit_eligibility_revision_server_event)
                )

                val idTextResponse = adapter.getSelectedItem()
                if (idTextResponse.isChecked) {
                    // displayToast("api call ")
                    dialog?.dismiss()
                    saveLoanConsent(idTextResponse)
                } else {
                    displayToast("Please select option")
                }
            }

            tvCreditLimitMessage.text = msg
            tvAmountMessage.text = amount

            builder.setView(view)
            builder.setCancelable(true)
            dialog = builder.create()
            dialog?.show()

            val displayMetrics = DisplayMetrics()
            (activity as BaseActivity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val displayWidth: Int = displayMetrics.widthPixels
            val displayHeight: Int = displayMetrics.heightPixels
            val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog?.window!!.attributes)
            val dialogWindowWidth = (displayWidth * 0.8f).toInt()
            val dialogWindowHeight = (displayHeight * 0.75f).toInt()
            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window!!.attributes = layoutParams
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveLoanConsent(idTextData: IdTextData) {
        try {
            val genericAPIService = GenericAPIService(this)
            val saveLoanConsentReq = IdTextData()
            saveLoanConsentReq.id = idTextData.id
            val token = userToken
            genericAPIService.saveLoanConsent(saveLoanConsentReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                val saveLoanConsentResponse = Gson().fromJson(
                    responseBody,
                    SaveLoanConsentResponse::class.java
                )
                if (saveLoanConsentResponse != null && saveLoanConsentResponse.status == Constants.STATUS_SUCCESS) {
                    if (sharedPreferences.getBoolean("fromDocs")) {
                        sharedPreferences.putBoolean("fromDocs", false)
                        onBackPressed()
                    } else {
                        //getProfile((activity as BaseActivity).userDetails.userId)
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("from", "limitchange")
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                } else {
                    displayToast(saveLoanConsentResponse.message)
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {

                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBanksList() {
        val genericAPIService = GenericAPIService(activity, 0)
        val genericRequest = GenericRequest()
        genericRequest.userId = userId
        val token = userToken
        genericAPIService.getBankList(genericRequest, token)
        genericAPIService.setOnDataListener { responseBody ->
            val bankListRes = Gson().fromJson(
                responseBody,
                BankListRes::class.java
            )
            if (bankListRes != null && bankListRes.status == Constants.STATUS_SUCCESS && bankListRes.banksList!!.isNotEmpty()) {
                showBankDialog(bankListRes.banksList!!)
            } else {
                //Failure
                displayToast(bankListRes.message)
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
            }
        }
    }

    private fun submitBankChange() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity)
        val submitBankChangeReq = SubmitBankChangeReq()
        val token = userToken
        genericAPIService.submitBankChange(submitBankChangeReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val submitBankChangeResponse = Gson().fromJson(
                responseBody,
                SubmitBankChangeResponse::class.java
            )
            if (submitBankChangeResponse != null && submitBankChangeResponse.status == Constants.STATUS_SUCCESS) {
                /*startActivity(Intent(this@UploadBankDetailsActivity, DashboardActivity::class.java))*/
                /*sharedPreferences.putBoolean("shouldRefreshDashboardScreen", true)
                finish()*/
                //(activity as DashboardActivity).getApplyLoanData(true)
                gotoDashBoard()
            } else {
                CNAlertDialog.showAlertDialog(
                    this,
                    resources.getString(R.string.title_alert),
                    submitBankChangeResponse.message
                )
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNAlertDialog.showAlertDialog(this, resources.getString(R.string.title_alert), "")
                CNProgressDialog.hideProgressDialog()
            }
        }
    }

    private fun saveLatestSalSlip() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity)
        val submitBankChangeReq = SubmitBankChangeReq()
        val token = userToken
        genericAPIService.saveLatestSalSlip(submitBankChangeReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val submitBankChangeResponse = Gson().fromJson(
                responseBody,
                SubmitBankChangeResponse::class.java
            )
            if (submitBankChangeResponse != null && submitBankChangeResponse.status == Constants.STATUS_SUCCESS) {
                gotoDashBoard()
            } else {
                CNAlertDialog.showAlertDialog(
                    this,
                    resources.getString(R.string.title_alert),
                    submitBankChangeResponse.message
                )
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNAlertDialog.showAlertDialog(this, resources.getString(R.string.title_alert), "")
                CNProgressDialog.hideProgressDialog()
            }
        }
    }

    private fun getBankLink() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity, 0)
        val getBankLinkReq = GetBankLinkReq()
        getBankLinkReq.userId = (activity as BaseActivity).userDetails.userId
        getBankLinkReq.bankCode = bankCode
        getBankLinkReq.referrer = referrer
        getBankLinkReq.mobNo = selectedMobileNumber
        val token = userToken
        genericAPIService.getBankWeblink(getBankLinkReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val webLinkRes = Gson().fromJson(
                responseBody,
                WebLinkRes::class.java
            )
            if (webLinkRes.status == Constants.STATUS_SUCCESS) {
                if (webLinkRes.weblink?.isNotEmpty() == true) {
                    loadFinBit(webLinkRes.weblink!!)
                } else if (webLinkRes.weblink?.isEmpty() == true && webLinkRes.type == "manual") {
                    if (referrer == Constants.FIN_BIT_REFERRER.Req_Bank_Change) {
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("redirect", getString(R.string.request_bank_chnage))
                        //startActivity(intent)
                        (activity as BaseActivity).sharedPreferences.putBoolean(
                            "fromuploaddocs",
                            true
                        )
                        this.finish()

                    } else if (referrer == Constants.FIN_BIT_REFERRER.Latest_Docs) {
                        val intent = Intent(this, BankDetailsActivity::class.java)
                        intent.putExtra("bank_statement_upload_text", dynamicText)
                        intent.putExtra("latest_docs", latestDocs)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, BankDetailsActivity::class.java)
                        intent.putExtra("bank_statement_upload_text", dynamicText)
                        (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", true)
                        startActivity(intent)
                    }
                }
            } else {
                CNAlertDialog.showAlertDialog(
                    this,
                    resources.getString(R.string.title_alert),
                    webLinkRes.message
                )
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNAlertDialog.showAlertDialog(
                    this,
                    resources.getString(R.string.title_alert),
                    "Something went wrong."
                )
                CNProgressDialog.hideProgressDialog()
            }
        }
    }

    private fun getBankWebLinkType() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getBankLinkReq = GetBankLinkReq()
            getBankLinkReq.userId = (activity as BaseActivity).userDetails.userId
            getBankLinkReq.bankCode = bankCode
            getBankLinkReq.referrer = referrer
            getBankLinkReq.type = selectedType
            getBankLinkReq.mobNo = selectedMobileNumber
            val token = userToken
            genericAPIService.getBankWebLinkType(getBankLinkReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val webLinkRes = Gson().fromJson(
                    responseBody,
                    WebLinkRes::class.java
                )
                if (webLinkRes.status == Constants.STATUS_SUCCESS) {
                    if (webLinkRes.weblink?.isNotEmpty() == true) {
                        loadFinBit(webLinkRes.weblink!!)
                    } else if (webLinkRes.weblink?.isEmpty() == true && webLinkRes.type == "manual") {
                        if (referrer == Constants.FIN_BIT_REFERRER.Req_Bank_Change) {
                            val intent = Intent(this, DashboardActivity::class.java)
                            intent.putExtra("redirect", getString(R.string.request_bank_chnage))
                            //startActivity(intent)
                            (activity as BaseActivity).sharedPreferences.putBoolean(
                                "fromuploaddocs",
                                true
                            )
                            this.finish()

                        } else if (referrer == Constants.FIN_BIT_REFERRER.Latest_Docs) {
                            val intent = Intent(this, BankDetailsActivity::class.java)
                            intent.putExtra("bank_statement_upload_text", dynamicText)
                            intent.putExtra("latest_docs", latestDocs)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, BankDetailsActivity::class.java)
                            intent.putExtra("bank_statement_upload_text", dynamicText)
                            (activity as BaseActivity).sharedPreferences.putBoolean(
                                "fromDocs",
                                true
                            )
                            startActivity(intent)
                        }
                    }
                } else {
                    CNProgressDialog.hideProgressDialog()
                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        webLinkRes.message
                    )
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        "Something went wrong."
                    )
                    CNProgressDialog.hideProgressDialog()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showBankDialog(banksList: List<BankDetails>) {
        try {
            val masterDataList: ArrayList<MasterData> = ArrayList()
            for (bank in banksList) {
                val data = MasterData()
                data.id = bank.bankCode
                data.name = bank.bankName
                masterDataList.add(data)
            }
            val builder = AlertDialog.Builder(activity as BaseActivity)
            val view = layoutInflater.inflate(R.layout.filter_dialog, null)
            val rvData: RecyclerView = view.findViewById(R.id.rvData)
            rvData.layoutManager = LinearLayoutManager(activity as BaseActivity)
            val etSearchCode = view.findViewById<EditText>(R.id.etSearch)
            etSearchCode.hint = getString(R.string.search_bank)

            adapter = ListFilterAdapter(this, masterDataList, SelectedIdCallback { selectedId ->
                try {
                    for (bank in banksList) {
                        if (bank.bankCode == selectedId) {
                            setSelectedBank(bank)
                            break
                        }
                    }
                    dialog?.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            rvData.adapter = adapter
            etSearchCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s != "") {
                        filterData(s, masterDataList)
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
            builder.setView(view)
            builder.setCancelable(true)
            dialog = builder.create()
            dialog?.show()

            val displayMetrics = DisplayMetrics()
            (activity as BaseActivity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
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

    private fun setSelectedBank(bank: BankDetails) {
        selectedbank = bank
        bankCode = bank.bankCode
        isBankSelected = true
        binding!!.etSelectBank.setText(bank.bankName)
        //getAnalysisType()
        getAnalysisList(false)
    }


    fun filterData(s: CharSequence, countryCodeArrayList: java.util.ArrayList<MasterData>) {
        val filterList: java.util.ArrayList<MasterData> = java.util.ArrayList<MasterData>()
        for (i in countryCodeArrayList.indices) {
            val item: MasterData = countryCodeArrayList[i]
            if (item.name.lowercase(Locale.ROOT).contains(s)) {
                filterList.add(item)
            }
        }
        adapter.updateList(filterList)
    }

    override fun onBackPressed() {

        if (binding!!.wvFinBit.visibility == VISIBLE) {
            binding!!.wvFinBit.visibility = GONE
            binding!!.llProceed.visibility = VISIBLE
            binding!!.llPageName.visibility = VISIBLE
            binding?.cbConfrimBank?.isChecked = false
            selectedType = null
            getAnalysisList(true)
        } else {
            super.onBackPressed()
        }
    }

    override fun onVolleyErrorResponse(error: VolleyError?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(
            activityContext,
            resources.getString(R.string.title_error),
            resources.getString(R.string.error_failure)
        )
    }

    open fun logoutUser(user_id: String?, message: String?) {

        CNProgressDialog.hideProgressDialog()
        CNAlertDialog()
        CNAlertDialog.setRequestCode(Constants.ALERT_DIALOG_REQUEST_CODE_COMMON)
        CNAlertDialog.showMaterialAlertDialog(
            this,
            "",
            message,
            R.drawable.loan_hold_icon,
            false,
            R.color.pop_up_color
        )

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {
                val intent = Intent(activityContext, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
                finish()
            }

            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {}
        })

        sharedPreferences = CNSharedPreferences(this)

        val ud = Gson().fromJson(
            sharedPreferences.getString(Constants.USER_DETAILS_DATA),
            UserDetails::class.java
        )

        if (ud != null) {
            ud.userId = user_id
            ud.userStatusId = "23"
        }

        sharedPreferences.putString(Constants.USER_DETAILS_DATA, Gson().toJson(ud))
        userDetails = ud;
    }
}
package com.capitalnowapp.mobile.kotlin.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.MailTo
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.BulletSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityNewApplyLoanBinding
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.kotlin.adapters.NewRepaymentScheduleAdapter
import com.capitalnowapp.mobile.kotlin.adapters.NewTenureListAdapter
import com.capitalnowapp.mobile.models.ApplyLoanNew
import com.capitalnowapp.mobile.models.EligibleOfferDetailsInstalment
import com.capitalnowapp.mobile.models.EligibleOfferDetailsLoanSummery
import com.capitalnowapp.mobile.models.EligibleOfferDetailsTancText
import com.capitalnowapp.mobile.models.GetEligibleOfferDetailReq
import com.capitalnowapp.mobile.models.GetEligibleOfferDetailsResponse
import com.capitalnowapp.mobile.models.GetEligibleOffersLoanType
import com.capitalnowapp.mobile.models.GetEligibleOffersReq
import com.capitalnowapp.mobile.models.GetEligibleOffersResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.gson.Gson
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.Locale

class NewApplyLoanActivity : BaseActivity() {
    private var scheduleAdapter: NewRepaymentScheduleAdapter? = null
    private var getEligibleOffersResponse: GetEligibleOffersResponse? = null
    private var getEligibleOfferDetailsResponse: GetEligibleOfferDetailsResponse? = null
    private var discountMessage: String? = null
    private var utcId: Int? = null
    private var promocode: String? = null
    private var tancText: EligibleOfferDetailsTancText? = null
    private var instalments: List<EligibleOfferDetailsInstalment>? = null
    private var loanSummery: EligibleOfferDetailsLoanSummery? = null
    private var adapter: NewTenureListAdapter? = null
    private var loanTypes: List<GetEligibleOffersLoanType>? = null
    private var binding: ActivityNewApplyLoanBinding? = null
    private var activity: AppCompatActivity? = null
    var dialog: AlertDialog? = null
    private var scale: Float = 0.0f
    var validationMsg = ""
    private var applyNewLoanBean: ApplyLoanNew? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    var mCurrentLocation: Location? = null
    private var mRequestingLocationUpdates = false
    public var currentLocation: String? = ""
    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewApplyLoanBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            getLocation()
            getEligibleOffers()

            promocode = binding?.etReferralCode?.text.toString().trim { it <= ' ' }

            binding?.etReferralCode?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    promocode = s.toString()
                }
            })
            binding?.tvApplyPromo?.setOnClickListener {
                if (binding?.etReferralCode?.text?.length != 0) {
                    getEligibleOfferDetails()
                } else {
                    Toast.makeText(
                        this,
                        "Enter Promocode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            binding?.tvBack?.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            binding?.tvRemovePromo?.setOnClickListener {
                binding?.etReferralCode?.text?.clear()
                getEligibleOfferDetails()
            }

            binding?.tvAgree?.setOnClickListener {
                validateFields()
            }
            applyNewLoanBean = ApplyLoanNew()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        currentLocation = location.latitude.toString() +","+location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun validateFields() {
        try {
            var count = 0
            if (adapter?.utcId == -1) {
                validationMsg = "Select loan type to continue."
                count++
            } else if (binding?.cbBorrowerTerms?.isChecked == false) {
                validationMsg = "Tick to accept terms."
                count++
            }
            if (count > 0) {
                Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
            } else {
                applyLoan()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyLoan() {
        try {
            val amount = adapter?.amount!!.toInt()
            //applyNewLoanBean!!.receivedOTP = ""
            applyNewLoanBean!!.amount = amount
            applyNewLoanBean!!.tenureDays = 0
            applyNewLoanBean!!.serviceFee = 0
            applyNewLoanBean!!.processingCharges = 0
            applyNewLoanBean!!.newProcessingCharges = 0
            applyNewLoanBean!!.total = 0
            applyNewLoanBean!!.promo_code = promocode
            applyNewLoanBean!!.Qcr_req_promo_code = promocode
            applyNewLoanBean!!.otpPassword = ""

            applyNewLoanBean!!.amazonNumber = ""
            applyNewLoanBean!!.amazonAmount = ""
            applyNewLoanBean!!.bankAmount = ""
            applyNewLoanBean!!.loanType = "1"
            applyNewLoanBean!!.cashback_amt = "0"
            applyNewLoanBean!!.tenureType = adapter!!.selectedloantype
            applyNewLoanBean!!.emiCount =
                getEligibleOfferDetailsResponse!!.data!!.instalments!!.size.toString()
            applyNewLoanBean!!.current_location = currentLocation
            applyNewLoanBean!!.instalmentDataList = scheduleAdapter?.totalInstallments
            applyNewLoanBean!!.qcr_accept_pre_agreement = 1

            val token = (activity as BaseActivity).userToken
            val userId = (activity as BaseActivity).userDetails.userId
            (activity as BaseActivity).cnModel.validateOTPAndApplyLoanEMI2(
                this,
                userId,
                applyNewLoanBean,
                token
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateApplyLoanStatus(response: JSONObject) {
        CNProgressDialog.hideProgressDialog()
        try {
            val message = response.getString("message")
            val statusRedirect = response.getInt("status_redirect")
            CNAlertDialog()
            CNAlertDialog.setRequestCode(Constants.ALERT_DIALOG_REQUEST_CODE_COMMON)
            if (statusRedirect > 0) {
                if (statusRedirect == Constants.STATUS_REDIRECT_CODE_PENDING_DOCUMENTS) { // Pending Documents
                    // Pending Documents
                    //(activity as DashboardActivity).isFromApply = true
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("from", "NewApplyLoanToPendingDocs")
                    //intent.putExtra("errorMessage" , finalErrorMessage)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    //(activity as DashboardActivity).getApplyLoanData(false)
                } else if (statusRedirect == Constants.STATUS_REDIRECT_CODE_FIVE_REFERENCES) { // Five References
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("from", "NewApplyLoanToReferences")
                    //intent.putExtra("errorMessage" , finalErrorMessage)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    /*(activity as DashboardActivity).getApplyLoanData(false)
                    (activity as DashboardActivity).onBackPressed()*/
                }
            } else {
                showSuccess(message)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun showAlertDialog(message: String?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(this, resources.getString(R.string.title_alert), message)
    }

    private fun showSuccess(message: String) {
        CNAlertDialog.showAlertDialogWithCallback(
            this,
            "",
            message, false, "", ""
        )

        CNAlertDialog.setRequestCode(1)

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {

            }

            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {
                if (buttonType == Constants.ButtonType.POSITIVE) {
                    binding?.etReferralCode?.visibility = View.GONE
                    sharedPreferences.putBoolean(
                        Constants.SP_REFER_CODE_IS_REGISTERED,
                        false
                    )
                    sharedPreferences.putString(
                        Constants.SP_REFER_CODE,
                        ""
                    )
                    if (userDetails.hasTakenFirstLoan == 1) {
                        val intent = Intent(activityContext,DashboardActivity::class.java)
                        intent.putExtra("from", "NewApplyLoanActivityFalse")
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        val intent = Intent(activityContext,DashboardActivity::class.java)
                        intent.putExtra("from", "NewApplyLoanActivityTrue")
                        //intent.putExtra("errorMessage" , finalErrorMessage)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    onBackPressed()
                    CNAlertDialog.dismiss()
                }
            }
        })
    }

    fun getEligibleOfferDetails() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val getEligibleOfferDetailReq = GetEligibleOfferDetailReq()
            val token = userToken
            utcId = adapter?.utcId
            getEligibleOfferDetailReq.uctId = utcId.toString()
            getEligibleOfferDetailReq.promoCode = promocode
            genericAPIService.getEligibleOfferDetails(getEligibleOfferDetailReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getEligibleOfferDetailsResponse =
                    Gson().fromJson(responseBody, GetEligibleOfferDetailsResponse::class.java)
                if (getEligibleOfferDetailsResponse != null && getEligibleOfferDetailsResponse!!.status == true) {
                    setInstalmentData()
                } else {
                    Toast.makeText(
                        this,
                        getEligibleOfferDetailsResponse!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun setInstalmentData() {
        try {
            instalments = getEligibleOfferDetailsResponse?.data?.instalments
            if (getEligibleOfferDetailsResponse != null && getEligibleOfferDetailsResponse!!.data?.loanSummery != null) {
                loanSummery = getEligibleOfferDetailsResponse!!.data?.loanSummery
                binding?.llLoanSummery?.visibility = View.VISIBLE
                binding?.tvLoanSummaryText?.visibility = View.VISIBLE
                setLoanSummery(loanSummery)
            } else {
                binding?.llLoanSummery?.visibility = View.GONE
                binding?.tvLoanSummaryText?.visibility = View.GONE
            }

            if (getEligibleOfferDetailsResponse != null && getEligibleOfferDetailsResponse!!.data?.instalments != null) {
                binding?.rvSheduleData?.visibility = View.VISIBLE
                binding?.tvRepaySchedule?.visibility = View.VISIBLE
                binding?.llPromocode?.visibility = View.VISIBLE
                binding?.llBorrowerTerms?.visibility = View.VISIBLE
                setInstallments(getEligibleOfferDetailsResponse)
            } else {
                binding?.rvSheduleData?.visibility = View.GONE
                binding?.tvRepaySchedule?.visibility = View.GONE
                binding?.llPromocode?.visibility = View.GONE
                binding?.llBorrowerTerms?.visibility = View.GONE
            }
            if (getEligibleOffersResponse?.data?.loanTypes != null && getEligibleOffersResponse?.data?.loanTypes!![0].loanType == "4") {
                binding?.rvSheduleData?.visibility = View.GONE
                binding?.tvRepaySchedule?.visibility = View.VISIBLE
                binding?.llPromocode?.visibility = View.GONE
                binding?.llInsurance?.visibility = View.GONE
                binding?.llDayRepayment?.visibility = View.VISIBLE
                binding?.llBorrowerTerms?.visibility = View.VISIBLE
                setDayLoanRepaymentData()
            } else {
                binding?.llDayRepayment?.visibility = View.GONE
            }
            if (getEligibleOfferDetailsResponse != null && getEligibleOfferDetailsResponse!!.data?.tancText != null) {
                tancText = getEligibleOfferDetailsResponse!!.data?.tancText
                setBAandTCText(tancText)
            }
            if (promocode != null && promocode != "") {
                discountMessage = getEligibleOfferDetailsResponse?.data?.discountMessage
                showApplyPromoCodePopUp(discountMessage)
            }
            if (getEligibleOfferDetailsResponse?.data?.discountMessage != null && getEligibleOfferDetailsResponse?.data?.discountMessage != "") {
                binding?.tvApplyPromo?.visibility = View.GONE
                binding?.tvRemovePromo?.visibility = View.VISIBLE
            } else {
                binding?.tvApplyPromo?.visibility = View.VISIBLE
                binding?.tvRemovePromo?.visibility = View.GONE
            }
            binding?.tvSavedText?.text = getEligibleOfferDetailsResponse?.data?.discountMessage
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDayLoanRepaymentData() {
        try {
            binding?.tvDayEmiAmount?.text =
                "Rs." + getEligibleOfferDetailsResponse?.data?.instalments!![0].emiAmount
            binding?.tvDayEmiDueDate?.text =
                getEligibleOfferDetailsResponse?.data?.instalments!![0].dueDate
            binding?.tvDayEmiInterest?.text =
                "Rs." + getEligibleOfferDetailsResponse?.data?.instalments!![0].interestAmount
            binding?.tvDayEmiPrincipalAmount?.text =
                "Rs." + getEligibleOfferDetailsResponse?.data?.instalments!![0].principalAmount
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setBAandTCText(tancText: EligibleOfferDetailsTancText?) {
        try {
            var startIndex: Int
            var endIndex: Int
            var ss: SpannableString? = null
            val words: List<String> = tancText?.findText!!.split("||")
            val links: List<String> = tancText.replaceLinks!!.split("||")
            ss = SpannableString(tancText.message)
            for (w in words.withIndex()) {
                val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        showTermsPolicyDialog(w.value, links[w.index])
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
                ss.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.Primary1)),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                scale = resources.displayMetrics.density
                binding?.tvBorrowerTerms?.text = ss
                binding?.tvBorrowerTerms?.movementMethod = LinkMovementMethod.getInstance()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = androidx.appcompat.app.AlertDialog.Builder(
            (activity as BaseActivity).activityContext,
            R.style.RulesAlertDialogStyle
        )
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_terms_conditions, null)
        alert.setView(dialogView)
        val tvTitle: CNTextView = dialogView.findViewById(R.id.et_title)
        val tvBack: CNTextView = dialogView.findViewById(R.id.tvBack)
        tvTitle.text = title
        val pb = dialogView.findViewById<ProgressBar>(R.id.pb)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.settings.loadWithOverviewMode = true
        val dialog: Dialog = alert.create()
        val mActivityRef = WeakReference<Activity>(activity)
        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
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
        tvBack.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun setInstallments(getEligibleOfferDetailsResponse: GetEligibleOfferDetailsResponse?) {
        try {
            binding?.rvSheduleData?.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            scheduleAdapter = NewRepaymentScheduleAdapter(getEligibleOfferDetailsResponse, this)
            binding?.rvSheduleData?.adapter = scheduleAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setLoanSummery(loanSummery: EligibleOfferDetailsLoanSummery?) {
        try {
            if(loanSummery?.isInsurance== true){
                binding?.llInsurance?.visibility = View.VISIBLE
            }else {
                binding?.llInsurance?.visibility = View.GONE
            }
            binding?.tvLoanAmountText?.text = loanSummery?.loanAmountText
            binding?.tvLoanAmount?.text = "Rs. " + loanSummery?.loanAmount
            binding?.tvProcessingFeeText?.text = loanSummery?.processingFeeText
            binding?.tvProcessingFee?.text = "(-)" + " Rs. " + loanSummery?.processingFeeAmount
            binding?.tvGst?.text = loanSummery?.gstText
            binding?.tvGstAmount?.text = "(-)" + " Rs. " + loanSummery?.gstAmount
            binding?.tvInsuranceText?.text = loanSummery?.insuranceText
            binding?.tvInsuranceAmount?.text = "(-)" + " Rs. " + loanSummery?.insuranceAmount
            binding?.tvDisbursalText?.text = loanSummery?.disbursalText
            binding?.tvDisbursalAmount?.text = "Rs. " + loanSummery?.disbursalAmount

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getEligibleOffers() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val getEligibleOffersReq = GetEligibleOffersReq()
            val token = userToken
            genericAPIService.getEligibleOffers(getEligibleOffersReq, token)
            //Log.d("save Salary req", Gson().toJson(saveSalaryReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getEligibleOffersResponse =
                    Gson().fromJson(responseBody, GetEligibleOffersResponse::class.java)
                if (getEligibleOffersResponse != null && getEligibleOffersResponse!!.status == true) {
                    loanTypes = getEligibleOffersResponse?.data?.loanTypes
                    val rememberText = getEligibleOffersResponse?.data?.remember
                    setTenureList(loanTypes)
                    setRememberText(rememberText)
                } else {
                    Toast.makeText(
                        this,
                        getEligibleOffersResponse!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
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


    private fun setRememberText(rememberText: List<String>?) {
        try {
            val remember = rememberText?.toBulletedList()
            binding?.tvRememberText?.text = remember
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Function for Bullet points
    private fun List<String>.toBulletedList(): CharSequence {
        return SpannableString(this.joinToString("\n")).apply {
            this@toBulletedList.foldIndexed(0) { index, acc, span ->
                val end = acc + span.length + if (index != this@toBulletedList.size - 1) 1 else 0
                this.setSpan(BulletSpan(16), acc, end, 0)
                end
            }
        }
    }

    private fun setTenureList(loanTypes: List<GetEligibleOffersLoanType>?) {
        try {
            binding?.rvData?.layoutManager = LinearLayoutManager(this)
            adapter = NewTenureListAdapter(loanTypes, this)
            binding?.rvData?.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("MissingInflatedId", "SetTextI18n")
    private fun showApplyPromoCodePopUp(discountMessage: String?) {
        try {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.new_applyloan_promo_popup, null)
            val tvConfirm = view.findViewById<TextView>(R.id.tvConfirm)
            val tvDiscountText = view.findViewById<TextView>(R.id.tvDiscountText)
            val tvCoupon = view.findViewById<TextView>(R.id.tvCoupon)

            tvCoupon.text = "'$promocode' applied"
            tvDiscountText.text = discountMessage
            tvConfirm.setOnClickListener {
                dialog?.dismiss()
            }

            builder.setView(view)
            builder.setCancelable(true)
            dialog = builder.create()
            dialog?.show()
            dialog?.setCanceledOnTouchOutside(false)

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
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

    @SuppressLint("MissingInflatedId")
    fun showAmountBreakUp(installments: EligibleOfferDetailsInstalment) {
        try {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.new_amount_breakup, null)
            val tvPrincipalAmt = view.findViewById<TextView>(R.id.tvPrincipalAmt)
            val tvTotalInterest = view.findViewById<TextView>(R.id.tvTotalInterest)
            val tvDiscountInterest = view.findViewById<TextView>(R.id.tvDiscountInterest)


            if (installments.principalAmount != null) {
                tvPrincipalAmt.text = installments.principalAmount
            } else {
                tvPrincipalAmt.text = ""
            }
            if (installments.interestAmount != null) {
                tvTotalInterest.text = installments.interestAmount

            } else {
                tvDiscountInterest.visibility = View.GONE
                tvTotalInterest.text = ""
            }

            if (installments.discountInterestAmount != null) {
                tvDiscountInterest.visibility = View.VISIBLE
                tvDiscountInterest.text = installments.discountInterestAmount
            } else {
                tvDiscountInterest.visibility = View.GONE
                tvDiscountInterest.text = ""
            }
            if (getEligibleOfferDetailsResponse?.data?.discountMessage != null && getEligibleOfferDetailsResponse?.data?.discountMessage != "") {
                tvTotalInterest.paintFlags = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                tvDiscountInterest.visibility = View.VISIBLE
            } else {
                tvDiscountInterest.visibility = View.GONE
            }

            builder.setView(view)
            builder.setCancelable(true)
            dialog = builder.create()
            dialog?.window?.setGravity(Gravity.CENTER)
            dialog?.show()
            dialog?.setCanceledOnTouchOutside(true)
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val displayWidth: Int = displayMetrics.widthPixels
            val displayHeight: Int = displayMetrics.heightPixels
            val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog?.window!!.attributes)
            val dialogWindowWidth = (displayWidth * 0.6f).toInt()
            val dialogWindowHeight = (displayHeight * 0.2f).toInt()
            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window!!.attributes = layoutParams
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)

    }

}

package com.capitalnowapp.mobile.kotlin.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View.*
import android.view.Window
import android.webkit.WebView
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.activities.LoginActivity
import com.capitalnowapp.mobile.beans.*
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.constants.Constants.ButtonType
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNButton
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityDashNewBinding
import com.capitalnowapp.mobile.fragments.HomeFragment
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.kotlin.activities.offer.*
import com.capitalnowapp.mobile.kotlin.adapters.NavMenuAdapter
import com.capitalnowapp.mobile.kotlin.fragments.*
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.*
import com.capitalnowapp.mobile.models.loan.AmtPayable
import com.capitalnowapp.mobile.models.userdetails.UserDetails
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.retrofit.ProgressAPIService
import com.capitalnowapp.mobile.util.*
import com.easebuzz.payment.kit.PWECouponsActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.payu.base.models.*
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.models.PayUCheckoutProConfig
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_dash_new.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


open class DashboardActivity : BaseActivity(), PaymentResultWithDataListener {


    private var redirectCode: Int = -1

    @JvmField
    var redirectToNach: Boolean = false

    @JvmField
    var applyLoanData: ApplyLoanData? = null

    @JvmField
    var twlProcessingFee: TwlProcessingFee? = null

    @JvmField
    var userData: UserData? = null

    @JvmField
    var loansResponse: JSONObject? = null

    companion object {
        const val CAMERA_PERMISSION_CODE = 100
        private const val GALLARY_PERMISSION_CODE = 1000
    }

    private var tempFile: File? = null

    private var selectImageFromDashBoard: Boolean = true


    //var nachCardData: NachCardData? = null
    private var redirectToPan: Boolean = false

    private var fromTwlLoans: Boolean = false


    var redirectToFaceMatch: Boolean = false
    var canRedirect: Boolean = false

    private var aadharotpBundle: Bundle? = null
    private var refRequestCountBundle: Bundle? = null
    val APPLY_LOAN_REQUEST_CODE = 10005

    private var currentLoanId: Int = 0
    private var screenTitles: Array<String>? = emptyArray()
    private var screenIcons: Array<Drawable?> = emptyArray()

    private var contactsSaved: String = "0"
    private var locationSaved: String = "0"
    private var freshChatRestoreId: String? = ""
    private lateinit var currentFrag: Fragment
    private lateinit var items: java.util.ArrayList<String>
    lateinit var navMenuAdapter: NavMenuAdapter
    private var binding: ActivityDashNewBinding? = null
    private lateinit var ivUser: ImageView
    private lateinit var ivUserPic: ImageView
    private lateinit var ivCamera: ImageView
    private lateinit var txt_prfname: TextView
    private var current_operation: Int = 1
    private var activity: AppCompatActivity? = null
    private var mMakePhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 102
    private val RESULT_LOAD_IMAGE = 101
    private var currenntPath = ""
    private var receiptId = ""
    private var cropFileName: String? = null
    public var salary: String? = ""
    public var refresh: Boolean? = false
    private var cropGooglePhotosUri: Boolean? = false
    private var fileUploadAjaxRequest: FileUploadAjaxRequest? = null
    var rewardsRedirection: String? = ""
    var rewardsRedirectionId: String? = ""
    var signatureRedirection: String? = ""
    var referenceRedirection: String? = ""

    private lateinit var llMainContent: LinearLayout
    var selectedTab: String? = ""
    private lateinit var tvMenuTitle: TextView
    lateinit var tvApplyLoan: CNTextView

    private var messages: String? = ""
    public var currentLocation: String? = ""

    /* Location Fetching Related Variables */
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    var mCurrentLocation: Location? = null
    private var mRequestingLocationUpdates = false

    lateinit var city: String
    lateinit var lang: String
    var isFromApply: Boolean? = false
    var isGuidelinesShown = false
    var fromReference = false
    var isFinBit = true
    var fromRedirectionBAC = false
    private var tranid: String? = ""
    private var surl: String? = ""
    private var furl: String? = ""
    private var bankRefNum: String? = ""

    var addReferencesFragment = ReferencesNewFragment()
    var pweActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashNewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        sharedPreferences = CNSharedPreferences(this)
        userDetails = Gson().fromJson(
            sharedPreferences.getString(Constants.USER_DETAILS_DATA),
            UserDetails::class.java
        )
        if (userDetails != null && userDetails.userId != null) {
            userId = userDetails.userId
        }
        cnModel = CNModel(this, this, Constants.RequestFrom.APPLY_LOAN)
        initView()
        loadMenuItems()
        if (NetworkConnectionDetector(this).isNetworkConnected) {
            replaceFrag(HomeFragment(), getString(R.string.home), null)
        } else {
            replaceFrag(NoInternetFragment(), getString(R.string.no_internet), null)
        }
        updateToggle(this, R.drawable.ic_menu_black_24dp)
        isGuidelinesShown = sharedPreferences.getBoolean(Constants.DOCS_HELP_SHOWN)
        //getNotificationCount()
        //loop()

        if (intent.extras != null) {
            if (intent.hasExtra("salary")) {
                salary = intent.extras!!.getString("salary")
            }
            if (intent.hasExtra("refresh")) {
                refresh = intent.extras!!.getBoolean("refresh")
            }
            if (intent.hasExtra("redirect")) {
                if (intent.extras!!.getString("redirect")
                        .equals(getString(R.string.request_bank_chnage))
                ) {
                    selectedTab = getString(R.string.request_bank_chnage)
                    setSelected(selectedTab!!)
                } else if (intent.extras!!.getString("redirect")
                        .equals(getString(R.string.contact_us))
                ) {
                    selectedTab = getString(R.string.contact_us)
                    setSelected(selectedTab!!)
                } else if (intent.extras!!.getString("redirect")
                        .equals(getString(R.string.upload_documents))
                ) {
                    selectedTab = getString(R.string.upload_documents)
                    setSelected(selectedTab!!)
                } else if (intent.extras!!.getString("redirect")
                        .equals(getString(R.string.active_loans))
                ) {
                    selectedTab = getString(R.string.active_loans)
                    setSelected(selectedTab!!)
                } else if (intent.extras!!.getString("redirect")
                        .equals(getString(R.string.latest_documents))
                ) {
                    loansResponse = JSONObject()
                    loansResponse!!.put("manual_upload", intent.extras!!.getInt("fromUploadBank"))
                    selectedTab = getString(R.string.latest_documents)
                    setSelected(selectedTab!!)
                }
            }

            if (intent.extras?.get("from") != null) {
                val from = intent.extras!!.getString("from").toString()
                when (from) {
                    "csapply" -> {
                        getApplyLoanDataBase(false)
                    }

                    "manualbankdetails" -> {
                        getApplyLoanData(true)
                    }

                    "missingbankdetails" -> {
                        getApplyLoanData(true)
                    }

                    "limitchange" -> {
                        getApplyLoanData(true)

                    }

                    "fromuploadfinbit" -> {
                        getApplyLoanData(true)
                    }

                    "reg3" -> {
                        getApplyLoanData(true)
                    }

                    "fromEnach" -> {
                        getApplyLoanData(false)
                    }

                    "frommandatorypermission" -> {
                        getApplyLoanData(true)
                    }

                    "deleteAccount" -> {
                        getApplyLoanData(false)
                    }

                    "NewLoanActivity" -> {
                        getApplyLoanData(true)
                    }

                    "Exception" -> {
                        showExceptionDialog()
                    }

                    "NewApplyLoanActivityFalse" -> {
                        getApplyLoanData(false)
                    }

                    "NewApplyLoanActivityTrue" -> {
                        getApplyLoanData(true)
                    }

                    "NewApplyLoanToPendingDocs" -> {
                        getApplyLoanData(true)
                    }

                    "NewApplyLoanToReferences" -> {
                        getApplyLoanData(true)
                    }

                    "fromBaseActivity" -> {
                        getApplyLoanData(true)
                    }

                    "digiLockersuccess" -> {
                        getApplyLoanData(true)
                    }

                    "digiLockerfailure" -> {
                        getApplyLoanData(false)
                    }

                }
            }

        }
        if (refresh == true) {
            getApplyLoanData(false)
        }

        pweActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            if (data != null) {
                val result = data.getStringExtra("result")
                val paymentresponse = data.getStringExtra("payment_response")?.trimIndent()
                val jsonObject = JSONObject(paymentresponse!!)
                if (result == "bank_back_pressed" && paymentresponse.contains("error")) {
                    CNAlertDialog.showAlertDialogWithCallback(
                        activity,
                        "Alert",
                        "Payment Process cancelled by user",
                        false,
                        "OK",
                        ""
                    )
                } else {
                    tranid = jsonObject.getString("txnid") as String
                    bankRefNum = jsonObject.getString("bank_ref_num") as String
                    surl = jsonObject.getString("surl") as String + bankRefNum
                    furl = jsonObject.getString("furl") as String + tranid
                }


                try {
                    // Handle response here
                    when (result) {
                        "payment_successfull" -> {
                            easeBuzzResponseHandler()

                        }

                        "user_cancelled" -> {
                            CNAlertDialog.showAlertDialogWithCallback(
                                activity,
                                "Alert",
                                "Payment Process cancelled by user",
                                false,
                                "OK",
                                ""
                            )
                        }

                        "payment_failed" -> {
                            val intent =
                                Intent(activityContext, EaseBuzzWebViewActivity::class.java)
                            intent.putExtra("furl", furl)
                            startActivity(intent)
                            finish()
                        }
                    }

                } catch (e: Exception) {
                    // Handle exception here
                }
            }
        }
    }


    private fun easeBuzzResponseHandler() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val easebuzzResponseRequest = EasebuzzResponseRequest()
            easebuzzResponseRequest.txnid = tranid
            val token = userToken
            genericAPIService.easebuzzResponseHandler(easebuzzResponseRequest, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val easebuzzResponseResponse = Gson().fromJson(
                    responseBody, EasebuzzResponseResponse::class.java
                )
                if (easebuzzResponseResponse != null && easebuzzResponseResponse.status == true) {
                    val intent = Intent(activityContext, EaseBuzzWebViewActivity::class.java)
                    intent.putExtra("surl", surl)
                    startActivity(intent)
                } else {
                    //status false the do something
                    Toast.makeText(
                        this,
                        "Something went wrong. Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showExceptionDialog() {
        try {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.exception_popup, null)
            builder.setView(view)
            val tvOk = view.findViewById<TextView>(R.id.tvOk)
            val dialog = builder.create()
            /*if (intent.extras != null) {
                val finalErrorMessage = intent.getStringExtra("errorMessage")!!
                FirebaseCrashlytics.getInstance().recordException(Exception(finalErrorMessage))

            }*/
            tvOk.setOnClickListener {
                dialog.dismiss()
            }
            builder.setCancelable(true)
            dialog?.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setGuideline(flag: Boolean) {
        sharedPreferences.putBoolean(Constants.DOCS_HELP_SHOWN, flag)
        isGuidelinesShown = sharedPreferences.getBoolean(Constants.DOCS_HELP_SHOWN)
    }

    private fun showRateDialog() {
        CNAlertDialog.showRateDialog(this)
        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {

            }

            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {

                if (buttonType == Constants.ButtonType.POSITIVE) {
                    if (requestCode == 1) {
                        rateUs()
                        submitUserRateStatus(requestCode)
                    } else if (requestCode == 2) {
                        submitUserRateStatus(currentLoanId)
                    }
                } else if (buttonType == Constants.ButtonType.NEGATIVE) {
                    submitUserRateStatus(requestCode)
                }
            }
        })
    }

    fun rateUs() {
        try {
            val uri: Uri = Uri.parse("market://details?id=" + activityContext.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activityContext.packageName)
                )
            )
        }
    }

    private fun submitUserRateStatus(requestCode: Int) {
        val token = (activity as BaseActivity).userToken
        cnModel.saveUserRatedAppStatus(userId, requestCode, token)
    }

    private fun initView() {
        //refreshScreen()
        tvMenuTitle = findViewById(R.id.tvMenuTitle)
        llMainContent = findViewById(R.id.llMainContent)
        tvApplyLoan = findViewById(R.id.tvApplyLoan)
        ivUser = findViewById(R.id.ivUser)
        txt_prfname = findViewById(R.id.txt_prfname)
        ivCamera = findViewById(R.id.ivCamera)
        ivCamera.setOnClickListener {
            checkPermission(
                Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE
            )
            selectImage()
        }
        ivUser.setOnClickListener {
            checkPermission(
                Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
            )
            selectImage()
        }

        if (userDetails != null && userDetails.fullName != null) {
            txt_prfname.text = userDetails.fullName
        } else {
            txt_prfname.text = userDetails.firstName
        }

        if (userDetails.userStatusId!! == "12") {
            tvApplyLoan.visibility = INVISIBLE
        } else {
            tvApplyLoan.visibility = INVISIBLE
        }
        tvApplyLoan.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            selectedTab = getString(R.string.apply_now)
            setSelected(selectedTab!!)
        }
        contactsSaved = userDetails.callLogSaved.toString()
        locationSaved = userDetails.locationSaved.toString()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenIcons = loadScreenIcons()
        screenTitles = loadScreenTitles()

        binding?.swipe?.setOnRefreshListener {
            refreshScreen()
        }
        rlContact.setOnClickListener {
            onItemClick(getString(R.string.contact_us))
        }
        rlRate.setOnClickListener {
            closeDrawer()
            rateUs()
        }
        tvLogout.setOnClickListener {
            onItemClick(getString(R.string.logout))
        }

        tvVersion.text = "App v" + BuildConfig.VERSION_NAME

        initLocationServices()
        checkAndRequestLocationPermissions()
        //checkCalanderPermissions()
        //checkAndRequestSMSPermissions()
        if (userDetails.userBasicData != null && userDetails.userBasicData!!.profile_picture != null && userDetails.userBasicData!!.profile_picture != "") {
            setUserImage(ivUser)
            ivCamera.visibility = VISIBLE
        } else {
            ivCamera.visibility = GONE
        }

        if (sharedPreferences.getBoolean("fromProcessingFee")
            && sharedPreferences.getBoolean("processingFeeSuccess")
        ) {
            showProcessPopup()
        }
        getApplyLoanData(false)

    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@DashboardActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@DashboardActivity,
                arrayOf(permission),
                requestCode
            )
        } else {

        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

    private fun showProcessPopup() {
        sharedPreferences.putBoolean("processingFeeSuccess", false)
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.payment_progress_dialog, null)
        builder.setView(view)
        val tvOk = view.findViewById<TextView>(R.id.tvOk)
        val dialog = builder.create()
        tvOk.setOnClickListener {
            dialog.dismiss()
        }
        builder.setCancelable(true)
        dialog?.show()
    }

    fun launchPendingDocs() {
        try {
            replaceFrag(PendingDocsNewFrag(), "text", null)
            toolbar.visibility = GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun launchAdditionalDocs() {
        try {
            replaceFrag(AdditionalDocsNewFrag(), "text", null)
            toolbar.visibility = GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun launchPersonalLon() {
        if (redirectToPan) {
            launchPanActivity()
        } else {
            if (loansResponse != null) {
                if (canApplyLoan) {
                    selectedTab = getString(R.string.apply_now)
                    openApplyNowEMI(selectedTab!!)
                } else {
                    if ((loansResponse!!.has("status_redirect") && loansResponse!!.getInt("status_redirect") == Constants.STATUS_REDIRECT_CODE_PENDING_DOCUMENTS) || (loansResponse!!.has(
                            "status_redirect"
                        ) && loansResponse!!.getInt("status_redirect") == Constants.STATUS_REDIRECT_CODE_FIVE_REFERENCES)
                    ) {
                        showDismissAlert(docsApplyLoanMsg)

                    } else {
                        showAlertDialog(canApplyLoanMsg)
                    }
                }
            }
        }
    }

    fun launchPanActivity() {
        val intent =
            Intent(activityContext, PanVerificationActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchBannerWebView(bannerType: String, url: String, shareMsg: String) {
        if (bannerType == "cricket") {
            val i = Intent(activityContext, BannerWebViewActivity::class.java)
            i.putExtra("url", url)
            i.putExtra("shareMsg", shareMsg)
            startActivity(i)
        } else if (bannerType == "rewards") {
            replaceFrag(RewardPointsNewFragment(), "Reward Points", null)
        } else if (bannerType == "refer") {
            replaceFrag(ReferToEarnFragment(), "Refer & Earn", null)
        }

    }

    fun launchNachActivity() {
        val intent =
            Intent(activityContext, ENachActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchVideoKyc() {
        val intent =
            Intent(activityContext, FaceMatchActivity::class.java)
        toolbar.visibility = GONE
        startActivity(intent)
    }

    fun launchSelectBank() {
        val intent =
            Intent(activityContext, SelectBankActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchVehicleLoan() {
        if (loansResponse != null) {
            if (canApplyLoan) {
                selectedTab = getString(R.string.apply_now)
                replaceFrag(TwoWheelerDashboardFragment(), "Two Wheeler Loan", null)
            } else {
                if ((loansResponse!!.has("status_redirect") && loansResponse!!.getInt("status_redirect") == Constants.STATUS_REDIRECT_CODE_PENDING_DOCUMENTS) || (loansResponse!!.has(
                        "status_redirect"
                    ) && loansResponse!!.getInt("status_redirect") == Constants.STATUS_REDIRECT_CODE_FIVE_REFERENCES)
                ) {
                    getApplyLoanData(true)
                } else {
                    getApplyLoanData(true)
                }
            }
        }
    }

    fun launchMemberUpgrade(memberUpgradeConsentResponse: MemberUpgradeConsentResponse) {
        try {
            val intent =
                Intent(activityContext, MemberUpgradeActivity::class.java)
            intent.putExtra("link", memberUpgradeConsentResponse)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun refreshScreen() {
        try {
            Constants.CURRENT_SCREEN = "4"
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                binding!!.swipe.isRefreshing = false
                return
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (currentFrag is HomeFragment || currentFrag is AadharVerificationFragment) {
                CNAlertDialog.dismiss()
                aadharotpBundle = null
                isFromApply = false
                getApplyLoanData(false)
                val token = userToken
                (currentFrag as HomeFragment).getLoanStatus(token)
                (currentFrag as HomeFragment).getTwlLoanStatus(token)
                (currentFrag as HomeFragment).refreshData()
                (currentFrag as HomeFragment).setNachVisibility()
                (currentFrag as HomeFragment).setMonitoringVisibility()
                (currentFrag as HomeFragment).setCreditCardData()
            }
            binding!!.swipe.isRefreshing = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUserImage(imageView: ImageView) {
        if (userDetails.userBasicData != null && userDetails.userBasicData!!.profile_picture != null && userDetails.userBasicData!!.profile_picture != "") {
            Glide.with(getApplicationContext())
                .load(userDetails.userBasicData!!.profile_picture)
                .apply(RequestOptions.circleCropTransform())
                .error(ContextCompat.getDrawable(activityContext, R.drawable.ic_camera))
                .into(imageView)
            ivCamera.visibility = VISIBLE
        } else {
            ivCamera.visibility = GONE
        }
    }

    private fun setUserImageDialog(imageView: ImageView) {
        if (userDetails.userBasicData != null && userDetails.userBasicData!!.profile_picture != null && userDetails.userBasicData!!.profile_picture != "") {
            Glide.with(this)
                .load(userDetails.userBasicData!!.profile_picture)
                .error(ContextCompat.getDrawable(activityContext, R.drawable.ic_camera))
                .into(imageView)
            ivCamera.visibility = VISIBLE
        } else {
            ivCamera.visibility = GONE
        }
    }


    fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    fun selectImage() {
        try {

            val alertDialog = Dialog(activityContext)
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setContentView(R.layout.profileimage_dialog)
            alertDialog.window!!.setLayout(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.setCanceledOnTouchOutside(true)
            val userPic = alertDialog.findViewById<ImageView>(R.id.ivUserPic)
            val takePhoto = alertDialog.findViewById<CNButton>(R.id.btnTakePhoto)
            val gallery = alertDialog.findViewById<CNButton>(R.id.btnGallery)
            val cancel = alertDialog.findViewById<ImageView>(R.id.ivCancel)
            setUserImage(userPic)
            takePhoto.setOnClickListener {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity((activity as DashboardActivity).packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        mMakePhotoUri = Uri.fromFile(photoFile)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    try {
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                (activity as DashboardActivity),
                                BuildConfig.APPLICATION_ID + ".fileprovider",
                                photoFile
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                            alertDialog.dismiss()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            gallery.setOnClickListener {
                chooseFile()
                alertDialog.dismiss()
            }
            cancel.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun selectImage1(fromDashboard: Boolean) {
        checkPermission(
            Manifest.permission.CAMERA,
            CAMERA_PERMISSION_CODE
        )
        selectImageFromDashBoard = fromDashboard
        val options = arrayOf<CharSequence>(
            resources.getString(R.string.take_photo),
            resources.getString(R.string.chooseFromGallery),
            resources.getString(R.string.cancel)
        )
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setItems(options) { dialog, item ->
            if (options[item] == resources.getString(R.string.take_photo)) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity((activity as DashboardActivity).packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        mMakePhotoUri = Uri.fromFile(photoFile)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    try {
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                (activity as DashboardActivity),
                                BuildConfig.APPLICATION_ID + ".fileprovider",
                                photoFile
                            )
                            /*val bitmap = BitmapFactory.decodeFile(photoFile.path)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 2, FileOutputStream(photoFile))
                            val uri = getImageUri((activity as DashboardActivity), bitmap)*/
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (options[item] == resources.getString(R.string.chooseFromGallery)) {
                chooseFile()
            } else if (options[item] == resources.getString(R.string.cancel)) {
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }


    @Throws(IOException::class)
    open fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_temp$timeStamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        if (image.absolutePath != "") {
            currenntPath = image.absolutePath
        }
        // Save a file: path for use with ACTION_VIEW intents
        return image
    }

    fun saveBitmapToFile(file: File): File? {
        return try {

            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            file
        } catch (e: java.lang.Exception) {
            null
        }
    }

    private fun chooseFile() {

        openDocumentPicker()

    }

    private fun openDocumentPicker() {
        val mimeTypes = arrayOf("image/*", "application/pdf")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        //intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
        intent.type = "image/*|application/pdf"
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        startActivityForResult(intent, Constants.OPEN_DOCUMENT_REQUEST_CODE)
    }

    private fun getImageChooserIntent(): Intent {
        val mimeTypes = arrayOf("image/*")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        //intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
        intent.type = "image/*|application/pdf"
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        return intent
    }

    private fun openGallery() {
        val intent =
            getImageChooserIntent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startActivityForResult(intent, Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD)
        } else {
            if (intent.resolveActivity(activity?.packageManager!!) != null) {
                startActivityForResult(intent, Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD)
            } else {
                displayToast(resources.getString(R.string.no_support_for_storage))
            }
        }
    }

    fun onItemClick(text: String) {
        closeDrawer()
        if (NetworkConnectionDetector(this).isNetworkConnected) {

            when (text) {
                getString(R.string.home) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Home Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    replaceFrag(HomeFragment(), text, null)
                    intent.putExtra("loansResponse", loansResponse.toString())
                }

                getString(R.string.profile) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Profile Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    replaceFrag(ProfileFragment(), text, null)
                    //replaceFrag(DigiLockerFragment(), text, null)
                    /*val intent =
                        Intent(activityContext, DigiLockerActivity() ::class.java)
                    startActivity(intent)*/
                }

                getString(R.string.borrower_agreement_consent) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(
                            getString(R.string.interaction_type),
                            "Borrower Agreement Access Code Menu Clicked"
                        )
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    //showConsent(loanAgreementConsent)
                    if (fromRedirectionBAC) {
                        val bundle = Bundle()
                        loanAgreementConsent = if (loansResponse!!.has("loan_agreement_consent")) {
                            val loanAgreementConsent: LoanAgreementConsent = Gson().fromJson(
                                loansResponse!!.getString("loan_agreement_consent"),
                                LoanAgreementConsent::class.java
                            )
                            if (!loanAgreementConsent.agreementLink.isNullOrEmpty()) {
                                loanAgreementConsent
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                        bundle.putSerializable("loanData", loanAgreementConsent)
                        replaceFrag(
                            BorrowerAgreementFragment(),
                            getString(R.string.borrower_agreement_consent),
                            bundle
                        )
                        fromRedirectionBAC = false
                    } else {
                        replaceFrag(HomeFragment(), getString(R.string.home), null)
                    }
                }

                getString(R.string.menu_item_kyc) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Aadhar Verification Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    //showConsent(loanAgreementConsent)
                    if (aadharotpBundle != null) {
                        replaceFrag(
                            AadharOtpFragment(),
                            getString(R.string.menu_item_kyc),
                            aadharotpBundle
                        )
                    } else {
                        replaceFrag(
                            AadharVerificationFragment(),
                            getString(R.string.menu_item_kyc),
                            null
                        )
                        //toolbar.visibility = GONE
                    }
                }

                getString(R.string.apply_now) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Apply Now Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    //replaceFrag(HomeFragment(), getString(R.string.home), null)
                    if (!allPermissionsGranted()) {
                        permissionsRedirectPage = 4
                        val i = Intent(this, MandatoryPermissionsActivity::class.java)
                        startActivity(i)
                    } else if (userDetails.userStatusId == "1") {
                        if (userDetails.email == null || userDetails.email == "") {
                            val i = Intent(this, FederalRegistrationActivity::class.java)
                            startActivity(i)
                        } else {
                            getApplyLoanData(true)
                        }
                    } else {
                        getApplyLoanData(true)
                    }

                }

                getString(R.string.my_loans) -> {

                    replaceFrag(MyLoansFrag(), text, null)
                }

                getString(R.string.loan_partners) -> {
                    val bundle = Bundle()
                    val Link = loansResponse!!.getString("loan_partner_link")
                    bundle.putString("loan_partner_link", Link)
                    replaceFrag(LoanPartnersFragment(), text, bundle)
                }
                /*getString(R.string.our_partners) -> {
                    val bundle = Bundle()
                    val link = loansResponse!!.getString("our_partner_link")
                    bundle.putString("our_partner_link", link)
                    replaceFrag(OurPartnersFragment(), text, bundle)
                }*/

                getString(R.string.add_signature) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Add Signature Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    val bundle = Bundle()
                    val jsonObj = loansResponse!!.getJSONObject("consent_text")
                    val userTermsData = UserTermsData()
                    userTermsData.message = jsonObj.getString("message")
                    userTermsData.findText = jsonObj.getString("find_text")
                    userTermsData.replaceLinks = jsonObj.getString("replace_links")
                    bundle.putSerializable("userTermsData", userTermsData)
                    replaceFrag(SignatureConsentFragment(), text, bundle)


                }

                getString(R.string.active_loans) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(
                            getString(R.string.interaction_type),
                            "Active Personal Loans Clicked"
                        )
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    if (userData != null && userData!!.showPlOfferCard && !userData!!.showPlCard) {
                        replaceFrag(CSPLAcriveLoansFragment(), text, null)
                    } else {
                        replaceFrag(ActiveLoansHomeFragment(), text, null)
                    }
                }

                getString(R.string.twl_active_loans) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(
                            getString(R.string.interaction_type),
                            "Active Two Wheeler Loan Clicked"
                        )
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    replaceFrag(TwlActiveLoansFragment(), text, null)
                }

                getString(R.string.cleared_loans) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Cleared Loans Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    replaceFrag(CloseLoansHomeFragment(), text, null)
                    /*val intent =
                        Intent(activityContext, Reg2Activity()::class.java)
                    startActivity(intent)*/
                }

                getString(R.string.reward_points) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Reward Points Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    //replaceFrag(RewardPointsFragment(), text, null)
                    replaceFrag(RewardPointsNewFragment(), text, null)
                    toolbar.visibility = GONE

                }

                getString(R.string.manager_details) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Manager Details Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    replaceFrag(ManagerDetailsFragment(), text, null)
                }

                getString(R.string.privacy_policy) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Privacy Policy Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    replaceFrag(PrivacyPolicyFragment(), text, null)
                }

                getString(R.string.transactions) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Transactions Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    replaceFrag(TransactionsFragment(), text, null)
                }

                getString(R.string.credit_line) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Credit Line Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    replaceFrag(CreditLineFragment(), text, null)
                }

                getString(R.string.cnpl_history) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "CNPL History Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    val bundle = Bundle()
                    val token = userToken
                    val url = "https://www.capitalnow.in/cn-ipl-2024-matches-history/$token"
                    bundle.putString("url", url)
                    replaceFrag(CNPLHistoryFragment(), text, bundle)
                }

                getString(R.string.data_deletion) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Data Deletion Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    replaceFrag(DataDeletionFragment(), text, null)
                }

                getString(R.string.upload_documents) -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Upload Documents Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    if (loansResponse!!.has("status_redirect") && (loansResponse!!.getInt("status_redirect") == Constants.STATUS_REDIRECT_CODE_BANK_STATEMENT)) {
                        if (isFinBit && loansResponse!!.getBoolean("show_netbanking")) {
                            val intent =
                                Intent(activityContext, UploadBankDetailsActivity::class.java)
                            if (loansResponse!!.has("bank_statement_upload_text")) {
                                intent.putExtra(
                                    "bank_statement_upload_text",
                                    loansResponse!!.getString("bank_statement_upload_text")
                                )
                                intent.putExtra("loansResponse", loansResponse.toString())
                            }
                            if (loansResponse!!.has("monitoring_card_data")) {
                                val bankCode = loansResponse!!.getString("bank_code")
                                intent.putExtra("bank_code", bankCode)
                                val bankName = loansResponse!!.getString("bank_name")
                                intent.putExtra("bank_name", bankName)
                                val monitoring = loansResponse!!.getString("type")
                                intent.putExtra("type", monitoring)
                                val readOnly = loansResponse!!.getBoolean("readonly")
                                intent.putExtra("readonly", readOnly)
                                intent.putExtra("loansResponse", loansResponse.toString())
                            }
                            startActivity(intent)
                        } else if (!loansResponse!!.getBoolean("show_netbanking")) {
                            val intent = Intent(activityContext, BankDetailsActivity::class.java)
                            if (loansResponse!!.has("bank_statement_upload_text")) {
                                intent.putExtra(
                                    "bank_statement_upload_text",
                                    loansResponse!!.getString("bank_statement_upload_text")
                                )
                            }
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val bundle = Bundle()
                        val requiredDocuments =
                            Gson().fromJson(loansResponse.toString(), RequiredDocuments::class.java)
                        bundle.putSerializable(Constants.SP_REQUIRED_DOCUMENTS, requiredDocuments)
                        replaceFrag(UploadDocsNewFrag(), text, bundle)
                    }
                }

                "Latest Documents" -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(
                            getString(R.string.interaction_type),
                            "Latest Documents Upload Clicked"
                        )
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    var bundle = Bundle()
                    bundle.putString("loansResponse", loansResponse.toString())
                    replaceFrag(LatestDocumentsFrag(), text, bundle)

                }

                getString(R.string.add_references), getString(R.string.add_latest_references) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(
                            getString(R.string.interaction_type),
                            "Add Latest References Clicked"
                        )
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    val bundle = Bundle()
                    if (loansResponse!!.has("ref_request_count")) {
                        val refRequestCount = loansResponse!!.getString("ref_request_count")
                        bundle.putString("ref_request_count", refRequestCount)
                    } else {
                        val refRequestCount = "5"
                        bundle.putString("ref_request_count", refRequestCount)
                    }
                    replaceFrag(addReferencesFragment, "", bundle)


                }

                getString(R.string.refer_and_earn) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Refer & Earn Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    replaceFrag(ReferToEarnFragment(), text, null)
                    toolbar.visibility = GONE
                }

                getString(R.string.contact_us) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Contact Us Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))

                    replaceFrag(ContactUsNewFragment(), text, null)
                }


                getString(R.string.request_bank_chnage) -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", userDetails.qcId)
                        obj.put(getString(R.string.interaction_type), "Request Bank Change Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.side_nav_bar_interacted))
                    if (loansResponse != null) {
                        if (loansResponse!!.has("bank_change_type")) {
                            val value = loansResponse!!.getString("bank_change_type")
                            if (value.isEmpty() || value.equals("fin")) {
                                replaceFrag(RequestBankChangeNewFragment(), text, null)
                            } else {
                                replaceFrag(RequestBankChangeFragment(), text, null)
                            }
                        } else {
                            replaceFrag(RequestBankChangeNewFragment(), text, null)
                        }

                    } else {
                        replaceFrag(RequestBankChangeFragment(), text, null)
                    }
                }


                getString(R.string.help) -> {
                    replaceFrag(FAQFragment(), text, null)
                }

                getString(R.string.talk_us) -> {
                    //startFreshChatConversation()
                }

                getString(R.string.pan_hard_pull) -> {
                    replaceFrag(PanHardPullFragment(), text, null)
                    toolbar.visibility = GONE
                }

                getString(R.string.logout) -> {
                    showLogoutDialog()
                }
            }

        } else {
            val bundle = Bundle()
            val noInternetFragment = NoInternetFragment()
            bundle.putString("fragmentInstance", text)
            noInternetFragment.arguments = bundle
            replaceFrag(noInternetFragment, text, null)
        }

        if (text == getString(R.string.apply_now)) {
            if (canApplyLoan) {
                selectedTab = text
                navMenuAdapter.notifyDataSetChanged()
            }
        } else {
            if (text != getString(R.string.logout)) {
                selectedTab = text
                navMenuAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        val locationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val audioPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        return locationPermission == PackageManager.PERMISSION_GRANTED && cameraPermission == PackageManager.PERMISSION_GRANTED && audioPermission == PackageManager.PERMISSION_GRANTED
    }


    private fun openApplyNowEMI(text: String) {
        try {
            if (loansResponse!!.has("tenure_data")) {
                val bundle = Bundle()
                val applyLoanData =
                    Gson().fromJson(loansResponse.toString(), ApplyLoanData::class.java)
                bundle.putSerializable(Constants.SP_APPLY_LOAN_DATA, applyLoanData)
                bundle.putBoolean(Constants.SP_IS_LIMIT_EXHAUSTED, false)
                bundle.putBoolean("fromAmazon", false)
                replaceFrag(ApplyLoanEMIFragment(), text, bundle)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showLogoutDialog() {

        val alertDialog = Dialog(activityContext)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.logout_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(true)
        val button = alertDialog.findViewById<CNButton>(R.id.btLogout)
        val cancel = alertDialog.findViewById<CNButton>(R.id.btCancel)
        alertDialog.findViewById<CNTextView>(R.id.tvUserName).text = userDetails.fullName
        button.setOnClickListener {


            val obj = JSONObject()
            try {
                obj.put("cnid", userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Logout button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.logout_popup_interacted))

            alertDialog.dismiss()
            userDetails = null
            sharedPreferences.putString(Constants.USER_DETAILS_DATA, Gson().toJson(userDetails))
            sharedPreferences.putString(Constants.USER_REGISTRATION_DATA, null)
            val logInIntent = Intent(activityContext, LoginActivity::class.java)
            logInIntent.flags =
                Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logInIntent)
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
            finish()
        }
        cancel.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Cancel button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.logout_popup_interacted))

            alertDialog.dismiss()
        }

        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    fun replaceFrag(frag: Fragment, text: String, bundle: Bundle?) {
        try {
            currentFrag = frag
            if (bundle != null) {
                currentFrag.arguments = bundle
            }
            if (text == getString(R.string.add_latest_references)) {
                tvMenuTitle.text = getString(R.string.add_references)
            } else {
                tvMenuTitle.text = text
            }
            val transaction: FragmentTransaction = this.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contentFrame, frag)
            transaction.commit()

            if (currentFrag is ReferToEarnFragment || currentFrag is RewardPointsFragment) {
                toolbar.visibility = GONE
            } else {
                toolbar.visibility = VISIBLE
            }

            if (currentFrag is SignatureConsentFragment) {
                toolbar.visibility = GONE
            } else {
                toolbar.visibility = VISIBLE
            }

            if (currentFrag is ApplyLoanFragment || currentFrag is UploadDocsNewFrag || currentFrag is ReferencesNewFragment || currentFrag is ApplyLoanEMIFragment || currentFrag is LatestDocumentsFrag) {
                tvApplyLoan.visibility = INVISIBLE
            } else {
                tvApplyLoan.visibility = INVISIBLE
            }

            if (currentFrag is ReferencesNewFragment) {
                tvAction?.visibility = GONE
                toolbar.visibility = GONE
            } else {
                tvAction?.visibility = GONE
            }

            tvAction?.setOnClickListener {
                if (currentFrag is ReferencesNewFragment) {
                }
            }
            swipe.isEnabled = currentFrag is HomeFragment

            if (currentFrag is HomeFragment) {
                toolbar.visibility = VISIBLE
                setConsentData(loanAgreementConsent, currentFrag as HomeFragment)
                if (twlProcessingFee != null) {
                    (currentFrag as HomeFragment).setProcessingFee()
                }
                (currentFrag as HomeFragment).setNachVisibility()

                (currentFrag as HomeFragment).setMonitoringVisibility()
                //(currentFrag as HomeFragment).SetWelcomeBackVisibility()
                (currentFrag as HomeFragment).setCreditCardData()
                (currentFrag as HomeFragment).bannerImages()
            }
            if (currentFrag is DataDeletionFragment) {
                toolbar.visibility = GONE
            }

            if (currentFrag is TransactionsFragment) {
                toolbar.visibility = GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadMenuItems() {
        items = ArrayList()
        items.add(getString(R.string.home))
        //items.add(getString(R.string.profile))
        items.add(getString(R.string.apply_now))
        /*if(loansResponse != null && loansResponse!!.has("apply_loan_redirection") && loansResponse!!.getBoolean("apply_loan_redirection")){
            launchPersonalLon()
        }*/
        if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                "status_redirect"
            ) == Constants.STATUS_REDIRECT_CODE_PENDING_DOCUMENTS
        ) {
            items.add(getString(R.string.upload_documents))
        }
        if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                "status_redirect"
            ) == Constants.STATUS_REDIRECT_CODE_LATEST_DOCUMENTS
        ) {
            items.add(getString(R.string.latest_documents))
        }
        if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                "status_redirect"
            ) == Constants.STATUS_REDIRECT_CODE_AADHAR
        ) {
            items.add(getString(R.string.menu_item_kyc))
        }
        if (loansResponse != null && loansResponse!!.has("show_loan_partner_menu") && loansResponse!!.getBoolean(
                "show_loan_partner_menu"
            )
        ) {
            items.add(getString(R.string.loan_partners))
        } /*else {
            items.add(getString(R.string.our_partners))
        }*/

        if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                "status_redirect"
            ) == Constants.STATUS_REDIRECT_CODE_SIGNATURE
        ) {
            items.add(getString(R.string.add_signature))
        }


        if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                "status_redirect"
            ) == Constants.STATUS_REDIRECT_CODE_BANK_STATEMENT
        ) {
            items.add(getString(R.string.menu_bank_statement))
        }
        if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                "status_redirect"
            ) == Constants.STATUS_REDIRECT_CODE_FIVE_REFERENCES
        ) {
            if (addReferencesFragment.arguments != null) {
                items.add(getString(R.string.add_latest_references))
            } else {
                items.add(getString(R.string.add_references))
            }
        }
        if (loanAgreementConsent != null && !loanAgreementConsent.ubacAcceptStatus && loanAgreementConsent.ubac_show_menu) {
            items.add(getString(R.string.borrower_agreement_consent))
        }
        //items.add(getString(R.string.my_loans))

        items.add(getString(R.string.active_loans))
        items.add(getString(R.string.twl_active_loans))
        items.add(getString(R.string.cleared_loans))
        items.add(getString(R.string.transactions))
        items.add(getString(R.string.credit_line))
        items.add(getString(R.string.reward_points))

        if (userDetails.hasTakenFirstLoan == 1) {
            items.add(getString(R.string.request_bank_chnage))
            llContact.visibility = VISIBLE
        } else {
            //llContact.visibility = GONE
            rlContact.visibility = VISIBLE
            rlRate.visibility = GONE
        }
        items.add(getString(R.string.refer_and_earn))
        if (userDetails.showIplHistory == "1") {
            items.add(getString(R.string.cnpl_history))
        }
        //   items.add(getString(R.string.help))
        items.add(getString(R.string.manager_details))
        items.add(getString(R.string.privacy_policy))
        items.add(getString(R.string.data_deletion))


        //items.add("")
        if (selectedTab == "") {
            selectedTab = getString(R.string.home)
        }
        //  binding?.rvMenuItems?.layoutManager = LinearLayoutManager(activityContext)
        navMenuAdapter = NavMenuAdapter(items, this, selectedTab!!)
        //  binding?.rvMenuItems?.adapter = navMenuAdapter

        val list: RecyclerView = findViewById(R.id.navigation_rv)
        list.isNestedScrollingEnabled = false
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = navMenuAdapter

        Handler(Looper.getMainLooper()).postDelayed({
            binding?.llMainContent?.root?.visibility = VISIBLE
        }, 500)
    }

    fun openParticularFragment(targetFragment: String) {
        onItemClick(targetFragment)
    }

    fun getApplyLoanData(canRedirect: Boolean) {
        //binding?.llMainContent?.root?.visibility = GONE
        //  binding?.navigationLayout?.visibility = GONE

        updateToggle(this, R.drawable.ic_custom_hamburger)
        val currentScreen = Constants.CURRENT_SCREEN
        val token = userToken
        cnModel.getApplyLoanData(userId, token, currentScreen, canRedirect)
        Constants.CURRENT_SCREEN = ""

        this.canRedirect = canRedirect
        CNProgressDialog.hideProgressDialog()
    }

    override fun updateApplyLoansData(response: JSONObject, canredirect: Boolean) {
        CNProgressDialog.hideProgressDialog()
        try {
            loansResponse = response
            redirectToNach = false
            applyLoanData = Gson().fromJson(loansResponse.toString(), ApplyLoanData::class.java)
            canApplyLoan = true
            if (response.has("message")) {
                canApplyLoanMsg = response.getString("message")
            }
            when (response.getString("status")) {
                Constants.STATUS_SUCCESS -> {
                    when (response.getInt("status_redirect")) {

                        Constants.STATUS_REDIRECT_CODE_FACEMATCH -> {
                            // Apply Loan Slider
                            if (canredirect) {
                                launchVideoKyc()
                            }
                        }

                        Constants.STATUS_REDIRECT_CODE_SELECT_BANK -> {
                            // Apply Loan Slider
                            if (canredirect) {
                                launchSelectBank()
                            }
                        }
                        Constants.STATUS_REDIRECT_CODE_PENDING_DOCUMENTS -> { // Pending Documents
                            if (response.has("apply_loan_message")) {
                                docsApplyLoanMsg = response.getString("apply_loan_message")
                            }
                            canApplyLoan = false
                            if (isFromApply!!) {
                                selectedTab = getString(R.string.upload_documents)
                                setSelected(selectedTab!!)
                            } else {
                                Handler().postDelayed({
                                    showDismissAlert("")
                                }, 500)
                            }
                            updateToggle(this, R.drawable.ic_custom_hamburger)
                        }

                        Constants.`STATUS_REDIRECT_CODE_FIVE_REFERENCES`, Constants.STATUS_REDIRECT_CODE_LATEST_DOCUMENTS -> { // Five References
                            if (canredirect) {
                                if (response.has("apply_loan_message")) {
                                    docsApplyLoanMsg = response.getString("apply_loan_message")
                                }

                                canApplyLoan = false
                                updateToggle(this, R.drawable.ic_custom_hamburger)

                                if (loansResponse!!.has("reference_notice")) {
                                    val msg = loansResponse!!.getString("reference_notice")
                                    val bundle = Bundle()
                                    bundle.putString("msg", msg)
                                    addReferencesFragment.arguments = bundle
                                }
                                if (loansResponse!!.has("ref_request_count")) {
                                    val refrequestCount =
                                        loansResponse!!.getString("ref_request_count")
                                    val bundle = Bundle()
                                    bundle.putString("ref_request_count", refrequestCount)
                                    addReferencesFragment.arguments = bundle
                                }
                                /*Handler().postDelayed({
                                    showDismissAlert("")
                            }, 500)*/
                                selectedTab =
                                    if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                                            "status_redirect"
                                        ) == Constants.STATUS_REDIRECT_CODE_PENDING_DOCUMENTS
                                    ) {
                                        getString(R.string.upload_documents)
                                    } else if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                                            "status_redirect"
                                        ) == Constants.STATUS_REDIRECT_CODE_LATEST_DOCUMENTS
                                    ) {
                                        getString(R.string.latest_documents)
                                    } else {
                                        if (addReferencesFragment.arguments != null) {
                                            getString(R.string.add_latest_references)
                                        } else {
                                            getString(R.string.add_references)
                                        }
                                    }
                                setSelected(selectedTab!!)
                            }


                        }

                        Constants.STATUS_REDIRECT_CODE_APPLY_LOAN -> {
                            // Apply Loan Slider

                            canApplyLoan = true
                            if (loansResponse!!.has("apply_loan_redirection") && loansResponse!!.getBoolean(
                                    "apply_loan_redirection"
                                )
                            ) {
                                if (canredirect) { //added to fix the auto redirection during refresh
                                    launchPersonalLon()
                                }
                            } /*else {
                                    launchDashBoard()
                                }*/
                            selectedTab = getString(R.string.apply_now)
                            //setSelected(selectedTab!!)


                        }

                        Constants.STATUS_REDIRECT_CODE_PAN -> {
                            // Apply Loan Slider
                            if (canredirect) { //added to fix the auto redirection during refresh
                                redirectToPan = true
                            }
                        }

                        Constants.STATUS_REDIRECT_CODE_NACH -> {
                            // Apply Loan Slider
                            redirectToNach = true
                            if (canredirect) {
                                launchNachActivity()
                            }

                        }
                        /*Constants.STATUS_REDIRECT_SALARY_NEW -> {
                            if (canredirect) {
                                launchNewSalary()
                            }

                        }*/
                        Constants.STATUS_REDIRECT_REGISTRATION -> {
                            if (canredirect) { //added to fix the auto redirection during refresh
                                launchRegistrationHome()
                            }
                        }

                        Constants.STATUS_REDIRECT_REG_1 -> {
                            if (canredirect) { //added to fix the auto redirection during refresh
                                launchReg1Activity()
                            }
                        }

                        Constants.STATUS_REDIRECT_REG_2 -> {
                            if (canredirect) { //added to fix the auto redirection during refresh
                                launchReg2Activity()
                            }
                        }

                        Constants.STATUS_REDIRECT_REG_3 -> {
                            if (canredirect) { //added to fix the auto redirection during refresh
                                launchReg3Activity()
                            }
                        }

                        Constants.STATUS_REDIRECT_HOLD -> {
                            if (canredirect) { //added to fix the auto redirection during refresh
                                launchHoldActivity()
                            }
                        }

                        Constants.STATUS_REDIRECT_CODE_NEW_LOAN -> {
                            if (canredirect) { //added to fix the auto redirection during refresh
                                launchNewLoanActivity()
                            }
                        }

                        Constants.STATUS_REDIRECT_CODE_NEW_APPLY_LOAN -> {
                            if (canredirect) { //added to fix the auto redirection during refresh
                                launchNewApplyLoanActivity()
                            }
                        }

                        Constants.STATUS_REDIRECT_CODE_UNDER_REVIEW -> {
                            if (canredirect) { //added to fix the auto redirection during refresh
                                launchCNUnderReviewActivity()
                            }
                        }

                        Constants.STATUS_REDIRECT_DIGILOCKER -> {
                            if (canredirect) { //added to fix the auto redirection during refresh
                                launchDigiLockerActivity()
                            }
                        }


                        Constants.STATUS_REDIRECT_CODE_BANK_MISSING_DETAILS -> {
                            // Apply Loan Slider
                            if (canredirect) { //added to fix the auto redirection during refresh
                                val intent =
                                    Intent(activityContext, MissingBankDetailsActivity::class.java)
                                if (loansResponse!!.has("bank_account_num")) {
                                    intent.putExtra(
                                        "bank_account_number",
                                        loansResponse!!.getInt("bank_account_num")
                                    )
                                }
                                if (loansResponse!!.has("ifsc_code")) {
                                    intent.putExtra(
                                        "ifsc_code",
                                        loansResponse!!.getInt("ifsc_code")
                                    )
                                }
                                if (loansResponse!!.has("bank_note")) {
                                    intent.putExtra(
                                        "bank_note",
                                        loansResponse!!.getString("bank_note")
                                    )
                                }
                                startActivity(intent)
                                finish()
                            }
                        }

                        Constants.STATUS_REDIRECT_CODE_AADHAR -> {
                            // Apply Loan Slider
                            if (canredirect) { //added to fix the auto redirection during rrefresh
                                selectedTab = getString(R.string.menu_item_kyc)
                                setSelected(selectedTab!!)
                            }
                        }

                        Constants.STATUS_REDIRECT_CODE_BANK_STATEMENT -> {
                            // Apply Loan Slider
                            if (canredirect) { //added to fix the auto redirection during rrefresh
                                selectedTab = getString(R.string.menu_bank_statement_upload)
                                setSelected(selectedTab!!)
                            }
                        }

                        Constants.STATUS_REDIRECT_CODE_SIGNATURE -> {
                            // Apply Loan Slider
                            if (canredirect) { //added to fix the auto redirection during rrefresh
                                selectedTab = getString(R.string.add_signature)
                                setSelected(selectedTab!!)
                                toolbar.visibility = GONE
                            }
                        }

                        Constants.STATUS_REDIRECT_CODE_BORROWER_AGGREMENT -> {
                            // Apply Loan Slider
                            if (canredirect) { //added to fix the auto redirection during rrefresh
                                fromRedirectionBAC = true
                                selectedTab = getString(R.string.borrower_agreement_consent)
                                setSelected(selectedTab!!)
                            }
                        }
                    }
                }

                Constants.LIMIT_EXHAUSTED -> {
                    canApplyLoan = false
                    showAlertDialog(canApplyLoanMsg)
                }

                else -> {
                    canApplyLoan = false
                    val message = response.getString("message")
                    canApplyLoanMsg = message
                    showAlertDialog(message)
                }
            }


            if (response.has("app_rated")) {
                if (response.has("loan_id")) {
                    currentLoanId = response.getInt("loan_id")
                }
                val appRated = response.getInt("app_rated")
                /*if (appRated == 0) {
                    showRateDialog()
                }*/
            }
            if (response.has("twl_processing_fees")) {
                twlProcessingFee = Gson().fromJson(
                    response.getString("twl_processing_fees"),
                    TwlProcessingFee::class.java
                )
            } else {
                twlProcessingFee = null
            }
            /*if (response.has("nach_card_data")) {
                nachCardData = Gson().fromJson(
                    response.getString("nach_card_data"),
                    NachCardData::class.java
                )
            } else {
                twlProcessingFee = null
            }*/
            loanAgreementConsent = if (response.has("loan_agreement_consent")) {
                val loanAgreementConsent: LoanAgreementConsent = Gson().fromJson(
                    response.getString("loan_agreement_consent"),
                    LoanAgreementConsent::class.java
                )
                if (!loanAgreementConsent.agreementLink.isNullOrEmpty()) {
                    loanAgreementConsent
                } else {
                    null
                }
            } else {
                null
            }
            if (currentFrag is HomeFragment) {
                setConsentData(loanAgreementConsent, currentFrag as HomeFragment)
                if (twlProcessingFee != null) {
                    (currentFrag as HomeFragment).setProcessingFee()
                }
                (currentFrag as HomeFragment).setNachVisibility()
                (currentFrag as HomeFragment).setMonitoringVisibility()
                (currentFrag as HomeFragment).setCreditCardData()
                (currentFrag as HomeFragment).showSecurityPopUp()
                (currentFrag as HomeFragment).showCustomPopUp()
                (currentFrag as HomeFragment).SetWelcomeBackVisibility()
            }
            loadMenuItems()
            if (redirectToNach && currentFrag is HomeFragment) {
                (currentFrag as HomeFragment).setNachVisibility()
            }

            //DeepLink Redirection

            if (intent != null) {
                if (intent.hasExtra("destination")) {
                    val uri = intent.getStringExtra("destination")
                    if (uri.toString().contains("capitalnow/rewardpoints")) {
                        intent.putExtra("destination", "")
                        replaceFrag(RewardPointsNewFragment(), "Reward Points", null)
                    } else if (uri.toString().contains("capitalnow/activepersonalloan")) {
                        intent.putExtra("destination", "")
                        replaceFrag(ActiveLoansHomeFragment(), "Active Personal Loans", null)
                    } else if (uri.toString().contains("capitalnow/activetwlloan")) {
                        intent.putExtra("destination", "")
                        replaceFrag(TwlActiveLoansFragment(), "Active Two Wheeler Loan", null)
                    } else if (uri.toString().contains("capitalnow/applynow")) {
                        intent.putExtra("destination", "")
                        getApplyLoanData(true)
                    } else if (uri.toString().contains("capitalnow/referandearn")) {
                        intent.putExtra("destination", "")
                        replaceFrag(ReferToEarnFragment(), "Refer & Earn", null)
                    }
                    //PushNotification Redirection
                } else if (intent.hasExtra("redirectCode")) {
                    val redirectCode = intent.getIntExtra("redirectCode", -1)
                    when (redirectCode) {
                        2 -> {
                            intent.putExtra("redirectCode", -1)
                            replaceFrag(RewardPointsNewFragment(), "Reward Points", null)
                        }

                        3 -> {
                            intent.putExtra("redirectCode", -1)
                            replaceFrag(ActiveLoansHomeFragment(), "Active Personal Loans", null)
                        }

                        4 -> {
                            intent.putExtra("redirectCode", -1)
                            replaceFrag(TwlActiveLoansFragment(), "Active Two Wheeler Loans", null)
                        }

                        5 -> {
                            intent.putExtra("redirectCode", -1)
                            getApplyLoanData(true)
                        }

                        6 -> {
                            intent.putExtra("redirectCode", -1)
                            replaceFrag(ReferToEarnFragment(), "Refer & Earn", null)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //   binding?.llMainContent?.root?.visibility = VISIBLE
            binding?.navigationLayout?.visibility = VISIBLE
        }
    }


    fun launchNewSalary() {
        val intent =
            Intent(activityContext, NewLoanActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchRegistrationHome() {
        val intent =
            Intent(activityContext, RegistrationHomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchReg1Activity() {
        val intent =
            Intent(activityContext, Reg1Activity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchReg2Activity() {
        val intent =
            Intent(activityContext, Reg2Activity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchReg3Activity() {
        val intent =
            Intent(activityContext, Reg3Activity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchHoldActivity() {
        val intent =
            Intent(activityContext, HoldActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchNewLoanActivity() {
        val intent =
            Intent(activityContext, NewLoanActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchNewApplyLoanActivity() {
        val intent =
            Intent(activityContext, NewApplyLoanActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchCNUnderReviewActivity() {
        val intent =
            Intent(activityContext, UnderReviewCNActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun launchDigiLockerActivity() {
        val intent =
            Intent(activityContext, DigiLockerActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchDashBoard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }
    private fun setConsentData(
        loanAgreementConsentData: LoanAgreementConsent?,
        currentFrag: HomeFragment
    ) {
        try {
            currentFrag.setConsentData(loanAgreementConsentData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDismissAlert(docsApplyLoanMsg: String) {
        if (CNAlertDialog.alertDialog != null && CNAlertDialog.alertDialog.isShowing) {
            CNAlertDialog.dismiss()
        }
        var msg = ""
        msg = if (docsApplyLoanMsg != "") {
            docsApplyLoanMsg
        } else {
            canApplyLoanMsg
        }
        CNAlertDialog.setRequestCode(1)

        val imgId: Int =
            if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && (loansResponse!!.getInt(
                    "status_redirect"
                ) == Constants.STATUS_REDIRECT_CODE_PENDING_DOCUMENTS
                        || loansResponse!!.getInt("status_redirect") == Constants.STATUS_REDIRECT_CODE_LATEST_DOCUMENTS)
            ) {
                R.drawable.upload_docs_dialog_icon
            } else {
                R.drawable.references_add_icon
            }

        CNAlertDialog.showMaterialAlertDialog(
            this,
            getString(R.string.upload_now),
            msg,
            imgId,
            true,
            R.color.pop_up_color
        )
        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {}
            override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {
                if (buttonType == ButtonType.POSITIVE) {
                    selectedTab =
                        if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                                "status_redirect"
                            ) == Constants.STATUS_REDIRECT_CODE_PENDING_DOCUMENTS
                        ) {
                            getString(R.string.upload_documents)
                        } else if (loansResponse != null && loansResponse!!.getString("status") == Constants.STATUS_SUCCESS && loansResponse!!.getInt(
                                "status_redirect"
                            ) == Constants.STATUS_REDIRECT_CODE_LATEST_DOCUMENTS
                        ) {
                            getString(R.string.latest_documents)
                        } else {
                            if (addReferencesFragment.arguments != null) {
                                getString(R.string.add_latest_references)
                            } else {
                                getString(R.string.add_references)
                            }
                        }
                    setSelected(selectedTab!!)
                }
            }
        })
    }

    fun initLocationServices() {
        try {
            if (checkGooglePlayServices()) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                mSettingsClient = LocationServices.getSettingsClient(this)
                mRequestingLocationUpdates = false
                mLocationRequest = LocationRequest()
                mLocationRequest!!.interval = Constants.UPDATE_INTERVAL.toLong()
                mLocationRequest!!.fastestInterval = Constants.FASTEST_UPDATE_INTERVAL.toLong()
                mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        // location is received
                        Log.d("location", "location updated")
                        mCurrentLocation = locationResult.lastLocation
                        //updateLocation()
                    }
                }
                val builder = LocationSettingsRequest.Builder()
                builder.addLocationRequest(mLocationRequest!!)
                mLocationSettingsRequest = builder.build()
            } else {
                Utility.displayToast(
                    activityContext,
                    resources.getString(R.string.google_play_services_not_available),
                    Toast.LENGTH_LONG
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /*open fun updateLocation() {
        if (mCurrentLocation != null) {
            currentLocation =
                mCurrentLocation!!.latitude.toString() + "," + mCurrentLocation!!.longitude

            if (mRequestingLocationUpdates) {
                // pausing location updates
                stopLocationUpdates()
            }
        }
    }*/


    /**
     * Method to verify google play services on the device
     */
    private fun checkGooglePlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(activityContext)
        return status == ConnectionResult.SUCCESS
    }

    /*private fun stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
            .addOnCompleteListener(this) {

            }
    }*/

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        sharedPreferences.putBoolean(Constants.From_Vehicle_Details, false)
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer()
        } else {
            when (currentFrag) {
                is HomeFragment -> {
                    toolbar.visibility = VISIBLE
                    CNAlertDialog.setRequestCode(1)
                    CNAlertDialog.showAlertDialogWithCallback(
                        activityContext,
                        "",
                        getString(R.string.back_button_exit),
                        true,
                        "",
                        ""
                    )
                    CNAlertDialog.setListener(object : AlertDialogSelectionListener {
                        override fun alertDialogCallback() {}
                        override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {
                            if (buttonType == ButtonType.POSITIVE) {
                                finishAffinity()
                            }
                        }
                    })
                }

                is AadharOtpFragment -> {
                    selectedTab = getString(R.string.menu_item_kyc)
                    aadharotpBundle = null
                    setSelected(selectedTab!!)
                }

                is VehicleApplyLoanFragment -> {
                    CNAlertDialog.setRequestCode(1)
                    CNAlertDialog.showAlertDialogWithCallback(
                        activityContext,
                        "Alert",
                        "Are you sure you want to discard this loan offer?",
                        true,
                        "",
                        ""
                    )
                    CNAlertDialog.setListener(object : AlertDialogSelectionListener {
                        override fun alertDialogCallback() {}
                        override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {
                            if (buttonType == ButtonType.POSITIVE) {
                                selectedTab = getString(R.string.home)
                                setSelected(selectedTab!!)
                            }
                        }
                    })
                }

                is SignatureConsentFragment, is ReferencesNewFragment -> {
                    if (sharedPreferences.getBoolean("fromDocs")) {
                        sharedPreferences.putBoolean("fromDocs", false)
                        replaceFrag(PendingDocsNewFrag(), "Pending Documents", null)
                        toolbar.visibility = GONE
                    } else {
                        replaceFrag(HomeFragment(), "Home", null)
                        toolbar.visibility = VISIBLE
                    }
                }

                is RewardRedeemedCouponNewFragment -> {
                    val bundle = Bundle()
                    if (rewardsRedirection == "fromRewardRedeem") {
                        bundle.putString("cupId", rewardsRedirectionId)
                        replaceFrag(RewardPointsNewFragment(), "", bundle)
                    } else if (rewardsRedirection == "fromRewardHistory") {
                        bundle.putString("redeemedLogId", rewardsRedirectionId)
                        replaceFrag(RewardRedeemHistoryFragment(), "", bundle)
                    }
                }

                is RewardRedeemHistoryFragment -> {
                    replaceFrag(RewardPointsNewFragment(), "", null)
                }

                else -> {
                    selectedTab = getString(R.string.home)
                    setSelected(selectedTab!!)
                }

            }
        }
    }

    private fun getNotificationCount() {
        try {
            val genericAPIService = GenericAPIService(activityContext)
            val genericRequest = GenericRequest()
            genericRequest.userId = userDetails.userId
            genericRequest.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(this)
            val token = userToken
            genericAPIService.getNotificationCount(genericRequest, token)

            genericAPIService.setOnDataListener { responseBody ->
                val genericResponse = Gson().fromJson(responseBody, GenericResponse::class.java)
                if (genericResponse.status)
                    updateNotificationCount(genericResponse.dataStr)
            }
            genericAPIService.setOnErrorListener {
                Log.e("getNotification", it.message.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateNotificationCount(dataStr: String?) {
        navMenuAdapter.setNotificationCount(dataStr)
    }

    private fun loadScreenTitles(): Array<String> {
        return resources.getStringArray(R.array.ld_activityScreenTitles)
    }

    private fun loadScreenIcons(): Array<Drawable?> {
        val ta = resources.obtainTypedArray(R.array.ld_activityScreenIcons)
        val icons = arrayOfNulls<Drawable>(ta.length())
        for (i in 0 until ta.length()) {
            val id = ta.getResourceId(i, 0)
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id)
            }
        }
        ta.recycle()
        return icons
    }

    override fun onPaymentError(p0: Int, p1: String, p2: PaymentData?) {

        val obj = JSONObject()
        try {
            obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
            obj.put("isSuccess", "false")

        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.personal_Loan_payment_server_Event))

        CNProgressDialog.hideProgressDialog()
        if (p1.contains("error") && p1.contains("description")) {
            val json = JSONObject(p1)
            showAlertDialog(json.getJSONObject("error").getString("description"))
        } else {
            showAlertDialog(p1)
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {

        val obj = JSONObject()
        try {
            obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
            obj.put("isSuccess", "true")

        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.personal_Loan_payment_server_Event))

        CNProgressDialog.showProgressDialog(currentActivity, Constants.LOADING_MESSAGE)

        if (!fromTwlLoans) {
            saveLoansData(p1)
        } else {
            saveTwlLoansData(p1)
        }

    }

    private fun saveLoansData(p1: PaymentData?) {
        val token = (activity as BaseActivity).userToken
        cnModel!!.savePaymentData(
            this@DashboardActivity,
            userId,
            p1?.paymentId,
            p1?.orderId,
            p1?.signature,
            token
        )
    }

    private fun saveTwlLoansData(p1: PaymentData?) {
        val token = (activity as BaseActivity).userToken
        cnModel!!.saveTwlPaymentData(
            this@DashboardActivity,
            userId,
            p1?.paymentId,
            p1?.orderId,
            p1?.signature,
            token
        )
    }

    fun updatePaymentData(paymentClearData: PaymentClearData) {
        CNProgressDialog.hideProgressDialog()
        if (paymentClearData.transaction_status == 0) {
            showAlertDialog("Payment Failed at CN Api")
        } else {
            navigateToPaymentStatusPage(paymentClearData)
        }
    }

    fun onError(msg: String) {
        showAlertDialog(msg)
    }

    fun navigateToPaymentStatusPage(paymentClearData: PaymentClearData) {
        val intent = Intent(activity, PaymentStatusActivity::class.java)
        intent.putExtra(Constants.BUNDLE_PAYMENT_STATUS, paymentClearData)
        startActivity(intent)
    }

    fun redirect(notification: NotificationObj) {
        if (notification.cta != null && notification.cta.action != null
            && notification.cta.action != ""
        ) {
            if (notification.cta.type == "fn") {
                when (notification.cta.action) {
                    "ProfileFragment()" -> {
                        selectedTab = getString(R.string.home)
                        setSelected(selectedTab!!)
                    }

                    "MyLoansFrag()" -> {
                        selectedTab = getString(R.string.my_loans)
                        setSelected(selectedTab!!)
                    }

                    "ReferToEarnFragment()" -> {
                        selectedTab = getString(R.string.refer_and_earn)
                        setSelected(selectedTab!!)
                    }

                    "ContactUsFragment()" -> {
                        selectedTab = getString(R.string.contacts)
                        setSelected(selectedTab!!)
                    }

                    "RequestBankChangeFragment()" -> {
                        selectedTab = getString(R.string.request_bank_chnage)
                        setSelected(selectedTab!!)
                    }

                    "FAQFragment()" -> {
                        selectedTab = getString(R.string.help)
                        setSelected(selectedTab!!)
                    }

                    "startFreshChatConversation()" -> {
                        selectedTab = getString(R.string.talk_us)
                        setSelected(selectedTab!!)
                    }

                    "ApplyLoanActivity" -> {
                        tvApplyLoan.callOnClick()
                    }

                    "AccountManagerDetailsActivity" -> {
                        // gotoAcntManagerInfo()
                    }
                }
            } else {
                val uri = Uri.parse(notification.cta.action) // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }

    open fun updateToggle(context: Context, drawable: Int) {
        toolbar.post {
            val d = ResourcesCompat.getDrawable(context.resources, drawable, null)
            DrawableCompat.setTint(d!!, ContextCompat.getColor(this, R.color.Accent1))
            toolbar.navigationIcon = d

            val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, 0, 0
            )
            drawer_layout.setDrawerListener(toggle)
            toggle.syncState()
        }
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createFileFromContentUri(fileUri: Uri): File {

        var fileName: String = ""

        fileUri.let { returnUri ->
            this.contentResolver.query(returnUri, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        //  For extract file mimeType
        val fileType: String? = fileUri.let { returnUri ->
            this.contentResolver.getType(returnUri)
        }

        val iStream: InputStream = this.contentResolver.openInputStream(fileUri)!!
        val outputDir: File = this.cacheDir!!
        val outputFile: File = File(outputDir, fileName)
        copyStreamToFile(iStream, outputFile)
        iStream.close()
        return outputFile
    }

    fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        //retrieve scan result
        super.onActivityResult(requestCode, resultCode, intent)
        try {
            if (requestCode == APPLY_LOAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                tvApplyLoan.callOnClick()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            if (resultCode == RESULT_OK && requestCode == Constants.OPEN_DOCUMENT_REQUEST_CODE) {
                try {
                    intent?.data?.also { documentUri ->
                        (activity as BaseActivity).contentResolver.takePersistableUriPermission(
                            documentUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                        //openDocument(documentUri)
                        tempFile = createFileFromContentUri(documentUri)
                        val filePath = tempFile!!.path
                        val fileUri = tempFile!!.toUri()
                        val selectedDocumentUri: Uri = fileUri
                        selectedDocumentUri.let { activity?.contentResolver?.getType(it) }
                        val fileType = Utility.getMimeType(this, selectedDocumentUri)

                        if (fileType.equals("JPG", ignoreCase = true) || fileType.equals(
                                "JPEG",
                                ignoreCase = true
                            ) || fileType.equals("PNG", ignoreCase = true) || fileType.equals(
                                "PDF",
                                ignoreCase = true
                            )
                        ) {
                            var isGooglePhotosUri = false
                            var selectedFilePath: String? = null
                            if (isGoogleDriveUri(selectedDocumentUri)) {
                                try {
                                    val inputStream: InputStream? =
                                        activity?.contentResolver?.openInputStream(
                                            selectedDocumentUri
                                        )
                                    if (inputStream != null) {
                                        selectedFilePath =
                                            RealPathUtil.getPathFromUri(this, selectedDocumentUri)
                                        isGooglePhotosUri = true
                                    }
                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                }
                            } else {
                                selectedFilePath =
                                    RealPathUtil.getPathFromUri(this, selectedDocumentUri)
                                if (selectedFilePath == null || selectedFilePath == "" || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)) {
                                    selectedFilePath = FileUtils.makeFileCopyInCacheDir(
                                        selectedDocumentUri,
                                        (activity as DashboardActivity)
                                    )
                                }
                                /*selectedFilePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                makeFileCopyInCacheDir(selectedDocumentUri)
                            } else {
                                RealPathUtil.getPathFromUri(context, selectedDocumentUri)
                            }*/
                            }
                            val selectedFile = File(selectedFilePath)

                            val selectedFileSizeInMB = selectedFile.length() / (1024 * 1024)
                            if (selectedFileSizeInMB > Constants.FILE_UPLOAD_LIMIT) {
                                displayToast(
                                    String.format(
                                        "Uploading file size must be less than %d MB.",
                                        Constants.FILE_UPLOAD_LIMIT
                                    )
                                )
                                return
                            } else {
                                if (selectImageFromDashBoard) {
                                    selectedFilePath?.let {
                                        showCrop(
                                            it,
                                            isGooglePhotosUri,
                                            selectedFilePath,
                                            selectedDocumentUri
                                        )
                                    }
                                } else {
                                    if (currentFrag is HomeFragment) {
                                        (currentFrag as HomeFragment).setSelectedImage(
                                            selectedFilePath
                                        )
                                    }
                                }
                            }

                        } else {
                            displayToast(getString(R.string.upload_docs_format_validation_msg))
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                try {
                    val result = intent?.getStringExtra("crop")
                    if (resultCode == RESULT_OK) {
                        //uploadFileWithProgress(result!!, cropGooglePhotosUri!!, cropFileName!!)
                        // ivUser.setImageURI(Uri.parse(File(result).absolutePath))
                        uploadProfileImage(File(result).absolutePath, userId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                if (mMakePhotoUri != null) {
                    val selectedFile = File(mMakePhotoUri!!.path.toString())
                    val reducedFile = saveBitmapToFile(selectedFile)
                    val selectedFilePath = reducedFile!!.path
                    val selectedFileSizeInMB = reducedFile.length() / (1024 * 1024)
                    if (selectedFileSizeInMB > Constants.FILE_UPLOAD_LIMIT) {
                        displayToast(
                            String.format(
                                "Uploading file size must be less than %d MB.",
                                Constants.FILE_UPLOAD_LIMIT
                            )
                        )
                        return
                    } else {
                        if (selectImageFromDashBoard) {
                            selectedFilePath.let {
                                showCrop(it, false, selectedFilePath, mMakePhotoUri!!)
                            }
                        } else {
                            if (currentFrag is HomeFragment) {
                                (currentFrag as HomeFragment).setSelectedImage(selectedFilePath)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showCrop(
        selectedFile: String,
        googlePhotosUri: Boolean,
        fileName: String,
        selectedDocumentUri: Uri
    ) {
        try {
            cropGooglePhotosUri = googlePhotosUri
            cropFileName = fileName

            val intent = Intent(activity, CropActivity::class.java)
            intent.putExtra("selectedImage", selectedDocumentUri)
            intent.putExtra("isProfilePic", true)
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadProfileImage(selectedFile: String?, userId: String) {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this)
            val token = userToken
            genericAPIService.uploadProfileImage(
                selectedFile,
                userId,
                Utility.getInstance().getDeviceUniqueId(this),
                token
            )
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val fileUploadResponse =
                    Gson().fromJson(responseBody, FileUploadResponse::class.java)
                if (fileUploadResponse != null && fileUploadResponse.status) {
                    if (fileUploadResponse.fileUrl != null && fileUploadResponse.fileUrl != "") {
                        val userdat = userDetails
                        if (userDetails != null && userDetails.userBasicData != null) {
                            userDetails.userBasicData!!.setProfilePicture(fileUploadResponse.fileUrl)
                            setUserImage(ivUser)
                        } else {
                            Toast.makeText(
                                this,
                                "Something went wrong. Please try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    if (tempFile != null) {
                        tempFile?.delete()
                    }
                }
            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                displayToast(it.message.toString())
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun uploadFileWithProgress(
        selectedFilePath: String,
        isExternalDoc: Boolean,
        fileName: String
    ) {
        val file = File(selectedFilePath)
        CNProgressDialog.showUploadProgressDialog(activity, Constants.LOADING_MESSAGE)
        val fileBody =
            ProgressAPIService(selectedFilePath, object : ProgressAPIService.UploadCallbacks {
                override fun onProgressUpdate(percentage: Int) {
                    Log.d("percentage", percentage.toString())
                }

                override fun onError() {
                    Log.e("imageupload", "error")
                    CNProgressDialog.hideProgressDialog()
                }

                override fun onFinish() {
                    Log.e("imageupload", "finish")
                    CNProgressDialog.hideProgressDialog()
                }

            })

        val fileToUpload: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, fileBody)
        val apiKey = Constants.CN_API_KEY.toRequestBody("text/plain".toMediaTypeOrNull())
        val userId: RequestBody = (activity as DashboardActivity).userDetails.userId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val call: retrofit2.Call<FileUploadResponse?>? =
            fileBody.apiService.uploadFile(fileToUpload, apiKey, userId)
        call?.enqueue(object : Callback<FileUploadResponse?> {
            override fun onResponse(
                call: retrofit2.Call<FileUploadResponse?>,
                response: Response<FileUploadResponse?>
            ) {
                if (response.body() != null && response.body()!!.status) {
                    val fileUploadResponse = response.body()
                    if (fileUploadResponse != null && fileUploadResponse.status) {
                        //updateSelectedFilePath(fileUploadResponse.fileUrl, isExternalDoc, fileName)
                    }
                }
                CNProgressDialog.hideProgressDialog()
            }

            override fun onFailure(call: retrofit2.Call<FileUploadResponse?>, t: Throwable) {
                Log.e("imageupload", t.toString())
            }
        })
    }

    private fun updateSelectedFilePath(fileUrl: String, isExternalDoc: Boolean, fileName: String?) {
        try {
            val textView: CNTextView? = null
            fileUploadAjaxRequest = FileUploadAjaxRequest()
            fileUploadAjaxRequest!!.userId = (activity as BaseActivity).userId
            var fileUrls: String? = ""
            fileUrls = fileUrl

            fileUploadAjaxRequest!!.fileUrls = fileUrls
            updateFileAjaxCall()

            var strName = ""
            strName = if (fileName?.length!! > 20) {
                fileName.substring(0, 10) + "..."
            } else {
                fileName
            }
            if (textView != null) {
                textView.text = strName
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_remove_image,
                    0
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun updateFileAjaxCall() {
        val genericAPIService = GenericAPIService(this)
        fileUploadAjaxRequest?.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
        Log.d("file ajax", Gson().toJson(fileUploadAjaxRequest))
        val token = (currentActivity as BaseActivity).userToken
        genericAPIService.uploadFileToServer(fileUploadAjaxRequest, token)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        CNProgressDialog.hideProgressDialog()

        if (sharedPreferences == null) {
            sharedPreferences = CNSharedPreferences(this)
        }

        if (sharedPreferences.getBoolean("shouldRefreshDashboardScreen")) {
            sharedPreferences.putBoolean("shouldRefreshDashboardScreen", false)
            refreshScreen()
            selectedTab = getString(R.string.home)
            setSelected(selectedTab!!)
        }

        if (selectedTab != "" && selectedTab == getString(R.string.talk_us) && !isFromApply!!) {
            selectedTab = getString(R.string.home)
            setSelected(selectedTab!!)
        }

        try {
            if (mRequestingLocationUpdates) {
                startLocationUpdates()
            }
            if (sharedPreferences.getBoolean(Constants.From_Vehicle_Details)) {
                val bundle = Bundle()
                bundle.putInt("limit", sharedPreferences.getInt(Constants.Loan_Limit))
                bundle.putString(
                    "selectedDealerId",
                    sharedPreferences.getString(Constants.SelectedDealerId)
                )
                bundle.putInt(
                    "selectedVehicleId",
                    sharedPreferences.getString(Constants.SelectedVehicleId).toInt()
                )
                bundle.putInt(
                    "selectedVehiclePrice",
                    sharedPreferences.getString(Constants.SelectedVehiclePrice).toInt()
                )
                bundle.putString(
                    "selectedVehicleArea",
                    sharedPreferences.getString(Constants.SelectedVehicleArea)
                )
                bundle.putString(
                    "selectedVehicleCity",
                    sharedPreferences.getString(Constants.SelectedVehicleCity)
                )
                bundle.putString(
                    "selectedVehicleDealer",
                    sharedPreferences.getString(Constants.SelectedVehicleDealer)
                )
                bundle.putString(
                    "selectedVehicleBrand",
                    sharedPreferences.getString(Constants.SelectedVehicleBrand)
                )
                replaceFrag(VehicleApplyLoanFragment(), getString(R.string.apply_now), bundle)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (canRedirect) {
            if (permissionsRedirectPage >= 0 && currentFrag !is ReferencesNewFragment) {
                when (permissionsRedirectPage) {
                    4 -> {
                        launchPersonalLon()
                    }

                    /*104-> {

                        launchVehicleLoan()
                    }*/

                    /*5 -> {
                        launchPanActivity()
                    }*/
                }
            }
        }

        if (sharedPreferences.getBoolean("fromuploaddocs")) {
            replaceFrag(RequestBankChangeFragment(), getString(R.string.request_bank_chnage), null)
            sharedPreferences.putBoolean("fromuploaddocs", false)
        }

    }

    fun setSelected(str: String) {
        if (str == getString(R.string.apply_now)) {
            if (canApplyLoan) {
                navMenuAdapter.setSelectedTab(str)
            }
        } else if (str != getString(R.string.logout)) {
            navMenuAdapter.setSelectedTab(str)
        }
    }

    private fun startLocationUpdates() {
        try {
            mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest!!)
                .addOnSuccessListener(this) {
                    Log.i(LoginActivity.TAG, "All location settings are satisfied.")
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@addOnSuccessListener
                    }
                    mFusedLocationClient!!.requestLocationUpdates(
                        mLocationRequest!!,
                        mLocationCallback!!,
                        Looper.myLooper()!!
                    )
                    //updateLocation()
                }.addOnFailureListener(this) { e ->
                }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun checkAndRequestLocationPermissions() {
        if (Utility.verifyAndRequestUserForPermissions(
                this,
                Constants.PERMISSION_GET_CURRENT_LOCATION,
                Constants.PERMISSIONS_GET_CURRENT_LOCATION,
                Constants.REQUEST_CODE_GET_CURRENT_LOCATION
            )
        ) {
            mRequestingLocationUpdates = true
            startLocationUpdates()
        }
    }


    fun showConsent(consentData: LoanAgreementConsent) {
        val intent = Intent(this, ConsentDocActivity::class.java).putExtra("data", consentData)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                getApplyLoanData(false)
                selectedTab = getString(R.string.home)
                setSelected(selectedTab!!)
            }
        }

    override fun onDestroy() {
        try {
            trimCache(this)
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        super.onDestroy()
    }

    open fun trimCache(context: Context) {
        try {
            val dir = context.cacheDir
            if (dir != null && dir.isDirectory) {
                deleteDir(dir)
            }
        } catch (e: java.lang.Exception) {
            // TODO: handle exception
        }
    }


    open fun deleteDir(dir: File?): Boolean {
        try {
            if (dir != null && dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dir!!.delete()
    }

    fun replaceAadharOtp(bundle: Bundle) {
        selectedTab = getString(R.string.menu_item_kyc)
        aadharotpBundle = bundle
        setSelected(selectedTab!!)
    }

    fun invokeVehiclePayment() {
        if (twlProcessingFee != null && twlProcessingFee!!.reqLoanId!! > 0) {
            sharedPreferences.putBoolean("fromProcessingFee", true)
            twlProcessingOrder(twlProcessingFee!!.reqLoanId, twlProcessingFee!!.total)
        }
    }

    private fun twlProcessingOrder(reqLoanId: Int?, total: Int?) {
        CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this)
        val getTwlProcessingOrderReq = TwlProcessingOrderReq()
        getTwlProcessingOrderReq.amount = twlProcessingFee!!.total
        getTwlProcessingOrderReq.selectedIds = twlProcessingFee!!.reqLoanId
        getTwlProcessingOrderReq.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
        getTwlProcessingOrderReq.wclient = 0
        getTwlProcessingOrderReq.userId = userDetails.userId
        val token = userToken
        genericAPIService.twlProcessingOrder(getTwlProcessingOrderReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val orderData = Gson().fromJson(
                responseBody, OrderData::class.java
            )
            if (orderData != null && orderData.status == Constants.STATUS_SUCCESS) {
                updateOrderData(orderData)
            } else {

            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }
    }

    fun startPayment(totalPaidAmount: Int, amtPayableList: ArrayList<AmtPayable>?) {
        sharedPreferences.putBoolean("fromProcessingFee", false)
        fromTwlLoans = false
        val LOCK = "LOCK"
        synchronized(LOCK) {
            val startTime =
                Utility.formatTime(System.currentTimeMillis(), Constants.YYYY_MM_DD_HH_MM_SS)
            val endTime = ""
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val token = (activity as BaseActivity).userToken
            cnModel!!.getOrderData(this, userId, totalPaidAmount, amtPayableList, token)
        }
    }

    fun startTwlPayment(twlAmtPayable: TwlAmtPayable) {
        sharedPreferences.putBoolean("fromProcessingFee", false)
        fromTwlLoans = true
        val LOCK = "LOCK"
        synchronized(LOCK) {
            val startTime =
                Utility.formatTime(System.currentTimeMillis(), Constants.YYYY_MM_DD_HH_MM_SS)
            val endTime = ""
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val token = (activity as BaseActivity).userToken
            cnModel!!.getTwlOrderData(this, userId, twlAmtPayable, token)
        }
    }

    fun updateOrderData(orderData: OrderData) {
        try {
            CNProgressDialog.hideProgressDialog()
            when (orderData.gatewayType) {
                3 -> {
                    launchPayU(orderData)
                }

                4, 5 -> {
                    launchCCA(orderData)
                }

                8 -> {
                    launchEaseBuzz(orderData)
                }

                else -> {
                    invokeRazorPay(orderData)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun launchEaseBuzz(orderData: OrderData) {
        try {
            val intentProceed = Intent(this, PWECouponsActivity::class.java)
            intentProceed.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
            intentProceed.putExtra("access_key", orderData.accessKey)
            intentProceed.putExtra("pay_mode", "production")
            pweActivityResultLauncher!!.launch(intentProceed)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun invokeRazorPay(orderData: OrderData) {
        val checkout = Checkout()
        checkout.setImage(R.mipmap.ic_launcher)
        try {
            receiptId = orderData.receiptId
            val options = JSONObject()
            options.put("name", orderData.checkoutName)
            options.put("description", orderData.checkoutDescription)
            options.put("order_id", orderData.razorPayOrderId)
            options.put("currency", "INR")
            options.put("amount", orderData.amount)
            options.put("prefill.email", orderData.userEmail)
            options.put("prefill.contact", orderData.userMobile)
            checkout.setKeyID(orderData.razorPayApiKey)
            //checkout.setKeyID("rzp_test_NcADfERIZYbXHp")
            checkout.open(this, options)
        } catch (e: Exception) {
            Log.e("Tag", "Error in starting Razorpay Checkout", e)
        }
    }

    private fun launchCCA(orderData: OrderData) {
        val intent: Intent =
            Intent(activity, CCAWebActivity::class.java).putExtra("orderData", orderData)
                .putExtra("fromTwlLoan", fromTwlLoans)
        startActivity(intent)
    }

    private fun launchPayU(orderData: OrderData) {

        val paymentParams = preparePayUBizParams(orderData)
        initUiSdk(paymentParams, orderData)
    }

    fun preparePayUBizParams(orderData: OrderData): PayUPaymentParams {
        val cred = "${orderData.mkey}:${orderData.getmEmail()}"

        val vasForMobileSdkHash = HashGenerationUtils.generateHashFromSDK(
            "${orderData.mkey}|${PayUCheckoutProConstants.CP_VAS_FOR_MOBILE_SDK}|${PayUCheckoutProConstants.CP_DEFAULT}|",
            orderData.getmSign()
        )
        val paymenRelatedDetailsHash = HashGenerationUtils.generateHashFromSDK(
            "${orderData.mkey}|${PayUCheckoutProConstants.CP_PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK}|${cred}|",
            orderData.getmSign()
        )

        val additionalParamsMap: HashMap<String, Any?> = HashMap()
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF1] = orderData.getcId()
        /*additionalParamsMap[PayUCheckoutProConstants.CP_UDF2] = "udf2"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF3] = "udf3"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF4] = "udf4"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF5] = "udf5"*/
        additionalParamsMap[PayUCheckoutProConstants.CP_VAS_FOR_MOBILE_SDK] = vasForMobileSdkHash
        additionalParamsMap[PayUCheckoutProConstants.CP_PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK] =
            paymenRelatedDetailsHash

        return PayUPaymentParams.Builder().setAmount(orderData.amount)
            .setIsProduction(orderData.isPayULive)
            .setKey(orderData.mkey)
            .setProductInfo(orderData.productinfo)
            .setPhone(orderData.userMobile)
            .setTransactionId(orderData.txnid)
            .setFirstName(orderData.userName)
            .setEmail(orderData.userEmail)
            .setSurl(Constants.MAIN_URL + "/" + orderData.surl)
            .setFurl(Constants.MAIN_URL + "/" + orderData.furl)
            .setUserCredential(cred)
            .setAdditionalParams(additionalParamsMap)
            .build()
    }

    private fun initUiSdk(payUPaymentParams: PayUPaymentParams, orderData: OrderData) {
        PayUCheckoutPro.open(
            activity as BaseActivity,
            payUPaymentParams,
            getCheckoutProConfig(orderData),
            object : PayUCheckoutProListener {

                override fun onPaymentSuccess(response: Any) {
                    sharedPreferences.putBoolean("processingFeeSuccess", true)
                    processResponse(response)
                    showPayUStatusAlert(true)
                    val obj = JSONObject()
                    try {
                        obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                        obj.put("isSuccess", "true")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(
                        obj,
                        getString(R.string.personal_Loan_payment_server_Event)
                    )
                }

                fun onTwlPaymentSuccess(response: Any) {
                    sharedPreferences.putBoolean("processingFeeSuccess", true)
                    processTwlResponse(response)
                    showPayUStatusAlert(true)
                }

                override fun onPaymentFailure(response: Any) {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                        obj.put("isSuccess", "false")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(
                        obj,
                        getString(R.string.personal_Loan_payment_server_Event)
                    )

                    response as HashMap<*, *>
                    Log.d(
                        BaseApiLayerConstants.SDK_TAG,
                        "payuResponse ; > " + response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                                + ", merchantResponse : > " + response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                    )
                    showPayUStatusAlert(false)
                    /*val intent = Intent(activity, DashboardActivity::class.java)
                    startActivity(intent)*/
                }

                override fun onPaymentCancel(isTxnInitiated: Boolean) {
                    (activity as BaseActivity).displayToast(getString(R.string.transaction_cancelled_by_user))
                }

                override fun onError(errorResponse: ErrorResponse) {

                    val errorMessage: String
                    errorMessage =
                        if (errorResponse.errorMessage != null && errorResponse.errorMessage!!.isNotEmpty())
                            errorResponse.errorMessage!!
                        else
                            resources.getString(R.string.some_error_occurred)
                    (activity as BaseActivity).displayToast(errorMessage)
                }

                override fun generateHash(
                    map: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener
                ) {
                    if (map.containsKey(PayUCheckoutProConstants.CP_HASH_STRING)
                        && map.containsKey(PayUCheckoutProConstants.CP_HASH_STRING) != null
                        && map.containsKey(PayUCheckoutProConstants.CP_HASH_NAME)
                        && map.containsKey(PayUCheckoutProConstants.CP_HASH_NAME) != null
                    ) {

                        val hashData = map[PayUCheckoutProConstants.CP_HASH_STRING]
                        val hashName = map[PayUCheckoutProConstants.CP_HASH_NAME]

                        val hash: String? =
                            HashGenerationUtils.generateHashFromSDK(
                                hashData!!, orderData.getmSign()
                            )
                        if (!TextUtils.isEmpty(hash)) {
                            val hashMap: HashMap<String, String?> = HashMap()
                            hashMap[hashName!!] = hash!!
                            hashGenerationListener.onHashGenerated(hashMap)
                        }
                    }
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                }
            })
    }

    private fun getCheckoutProConfig(orderData: OrderData): PayUCheckoutProConfig {
        val checkoutProConfig = PayUCheckoutProConfig()
        checkoutProConfig.paymentModesOrder = getCheckoutOrderList(orderData)
        checkoutProConfig.merchantName = orderData.checkoutName
        checkoutProConfig.merchantLogo = R.mipmap.ic_launcher
        checkoutProConfig.showCbToolbar = false
        checkoutProConfig.autoSelectOtp = true
        checkoutProConfig.merchantResponseTimeout = orderData.redirect_time
        return checkoutProConfig
    }

    private fun getCheckoutOrderList(orderData: OrderData): ArrayList<PaymentMode> {
        val checkoutOrderList = ArrayList<PaymentMode>()
        if (orderData.gpay) {
            checkoutOrderList.add(
                PaymentMode(
                    PaymentType.UPI,
                    PayUCheckoutProConstants.CP_GOOGLE_PAY
                )
            )
        }
        if (orderData.phonepe) {
            checkoutOrderList.add(
                PaymentMode(
                    PaymentType.WALLET,
                    PayUCheckoutProConstants.CP_PHONEPE
                )
            )
        }
        if (orderData.paytm) {
            checkoutOrderList.add(
                PaymentMode(
                    PaymentType.WALLET,
                    PayUCheckoutProConstants.CP_PAYTM
                )
            )
        }
        return checkoutOrderList
    }

    private fun processResponse(response: Any) {
        response as HashMap<*, *>
        Log.d(
            BaseApiLayerConstants.SDK_TAG,
            "payuResponse ; > " + response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    + ", merchantResponse : > " + response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
        )
        val obj: JSONObject = JSONObject(response)
        val param: String = obj.get("payuResponse") as String
        val valuesObj: JSONObject = JSONObject(param)

        val utility = Utility.getInstance()
        valuesObj.put("device_unique_id", utility.getDeviceUniqueId(activity))
        val token = userToken
        valuesObj.put("api_key", token)
        cnModel?.savePayUData(valuesObj)
    }

    private fun processTwlResponse(response: Any) {
        response as HashMap<*, *>
        Log.d(
            BaseApiLayerConstants.SDK_TAG,
            "payuResponse ; > " + response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    + ", merchantResponse : > " + response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
        )
        val obj: JSONObject = JSONObject(response)
        val param: String = obj.get("payuResponse") as String
        val valuesObj: JSONObject = JSONObject(param)

        val utility = Utility.getInstance()
        valuesObj.put("device_unique_id", utility.getDeviceUniqueId(activity))
        val token = userToken
        valuesObj.put("api_key", token)
        cnModel?.saveTwlPayUData(valuesObj)
    }

    private fun showPayUStatusAlert(b: Boolean) {
        CNAlertDialog.setRequestCode(1)
        var title = ""
        title = if (b) {
            "Payment Success"
        } else {
            "Payment Failed"
        }
        CNAlertDialog.showAlertDialogWithCallback(activity, title, "", false, "", "")

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {}
            override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {
                if (buttonType == ButtonType.POSITIVE) {
                    if (b) {
                        val intent = Intent(activity, DashboardActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        })
    }

    fun capAlertDialog(message: String) {
        CNAlertDialog.showAlertDialogWithCallback(activity, "Alert", message, false, "OK", "")
    }


    fun twlRedirect() {
        try {
            if (userData != null && userData?.twlEligStatusRedirect == 4) {
                launchVehicleLoan()
            } else {
                getApplyLoanData(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}


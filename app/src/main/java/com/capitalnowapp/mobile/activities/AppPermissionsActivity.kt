package com.capitalnowapp.mobile.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.beans.UserLoginData
import com.capitalnowapp.mobile.beans.UserTermsData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.models.login.RegisterDeviceRequest
import com.capitalnowapp.mobile.models.login.RegisterDeviceResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.Utility
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_app_permissions.btnSettings
import kotlinx.android.synthetic.main.activity_app_permissions.cb_agree_terms
import kotlinx.android.synthetic.main.activity_app_permissions.continue_button
import kotlinx.android.synthetic.main.activity_app_permissions.deny_button
import kotlinx.android.synthetic.main.activity_app_permissions.llCamera
import kotlinx.android.synthetic.main.activity_app_permissions.llLocation
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date

class AppPermissionsActivity : BaseActivity() {
    private var userLoginData: UserLoginData? = null
    private var tv_privacy_link: TextView? = null
    private var tvLocation: TextView? = null
    private var tvStorage: TextView? = null
    private var ivLocation: ImageView? = null
    private var ivStorage: ImageView? = null
    private var ivBack: ImageView? = null
    private var tvPermissionsTitle: CNTextView? = null
    private var userTermsData: UserTermsData? = null
    private var deviceToken = ""
    private var setError = false
    private var showSettings = 0
    private var llMandatoryLabel: LinearLayout? = null
    private var from = ""
    private var activity: AppCompatActivity? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_permissions)
        try {
            val bundle = intent.extras
            if (bundle != null) {
                userLoginData = bundle.getSerializable(Constants.BUNDLE_USER_DATA) as UserLoginData?
            }
            val termsData = sharedPreferences.getString("terms_data")
            if (termsData != null && !termsData.isEmpty()) {
                userTermsData = Gson().fromJson(termsData, UserTermsData::class.java)
            }
            if (bundle?.get("from") != null) {
                from = bundle.getString("from").toString()
            }

            currentActivity = this
            tv_privacy_link = findViewById(R.id.tv_privacy_link)
            tvLocation = findViewById(R.id.tvLocation)
            tvStorage = findViewById(R.id.tvStorage)
            ivLocation = findViewById(R.id.ivLocation)
            ivStorage = findViewById(R.id.ivStorage)
            ivBack = findViewById(R.id.ivBack)

            llMandatoryLabel = findViewById(R.id.llMandatoryLabel)
            tvPermissionsTitle = findViewById(R.id.tvPermissionsTitle)
            sharedPreferences.putBoolean(Constants.PERMISSIONS_REQUESTED, true)
            cb_agree_terms.isChecked = false
            cb_agree_terms.setOnClickListener(View.OnClickListener {
                if (cb_agree_terms.isChecked) {
                    enableAgreeButton()
                }
            })
            deny_button.setOnClickListener(View.OnClickListener {
                currentActivity = null
                sharedPreferences.putBoolean(Constants.IS_PERMISSION_AGGREED, false)
                loadDenyActivity()
            })
            if (userTermsData != null) {
                setLink()
            }
            deviceToken = sharedPreferences.getString(Constants.SP_DEVICE_TOKEN)
            if (deviceToken.isEmpty()) {
                generateFCMDeviceToken()

            } else {
                Log.d("- fcmToken: ", deviceToken)
                if (!sharedPreferences.getBoolean(Constants.SP_IS_REGISTER_DEVICE)) {
                    registerDevice()
                }
            }

            llMandatoryLabel?.setOnClickListener {
                setError = true
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                )
            }
            btnSettings?.setOnClickListener {
                setError = true
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


        // We are generating FCM device token if it is not already saved.

    }

    private fun generateFCMDeviceToken() {
        // Get FCM Device Token
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Utility.logResult(
                        SplashScreen.TAG,
                        "********* Get InstanceId failed : " + task.exception
                    )
                    return@OnCompleteListener
                }
                val fcmToken = task.result
                Utility.logResult(
                    SplashScreen.TAG,
                    "********** generateFCMDeviceToken - fcmToken: $fcmToken"
                )
                Log.d("- fcmToken: ", fcmToken)
                sharedPreferences.putString(Constants.SP_DEVICE_TOKEN, fcmToken)
                if (!sharedPreferences.getBoolean(Constants.SP_IS_REGISTER_DEVICE)) {
                    registerDevice()
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun registerDevice() {
        val utility = Utility.getInstance()
        val genericAPIService = GenericAPIService(activityContext)
        val registerDeviceRequest = RegisterDeviceRequest()
        registerDeviceRequest.deviceToken = deviceToken
        registerDeviceRequest.deviceUniqueId = utility.getDeviceUniqueId(this);
        registerDeviceRequest.devicetype = "Android"
        registerDeviceRequest.lat = ""
        registerDeviceRequest.long1 = ""
        val token = userToken
        genericAPIService.registerDevice(registerDeviceRequest, token)
        genericAPIService.setOnDataListener { responseBody ->
            val genericResponse = Gson().fromJson(
                responseBody,
                RegisterDeviceResponse::class.java
            )
            if (genericResponse.status == "success") {
                sharedPreferences.putBoolean(Constants.SP_IS_REGISTER_DEVICE, true)
                sharedPreferences.putString(
                    Constants.LOGGED_TIME,
                    SimpleDateFormat("dd MMM yyyy hh:mm:ss").format(Date())
                )
            }
        }
        genericAPIService.setOnErrorListener {
        }
    }

    private fun setLink() {
        var startIndex: Int
        var endIndex: Int
        var ss: SpannableString? = null
        if (userTermsData != null && userTermsData!!.message != null) {
            val words: List<String> = userTermsData!!.findText!!.split("||")
            val links: List<String> = userTermsData!!.replaceLinks!!.split("||")
            ss = SpannableString(userTermsData!!.message)
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
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.Primary2)),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                tv_privacy_link!!.text = ss
                tv_privacy_link!!.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder(this, R.style.RulesAlertDialogStyle)
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_terms_conditions, null)
        alert.setView(dialogView)
        val tvTitle: CNTextView = dialogView.findViewById(R.id.et_title)
        val tvBack: CNTextView = dialogView.findViewById(R.id.tvBack)
        tvTitle.text = title
        val pb = dialogView.findViewById<ProgressBar>(R.id.pb)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        val dialog: Dialog = alert.create()
        val mActivityRef = WeakReference<Activity>(this)
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

    fun enableAgreeButton() {
        continue_button!!.isEnabled = true
        continue_button!!.setOnClickListener {

            //if (llMandatoryLabel?.visibility == View.VISIBLE) {
            if (showSettings >= 2) {
                llMandatoryLabel?.callOnClick()
            } else {
                showSettings++
                if (cb_agree_terms!!.isChecked) {
                    sharedPreferences.putBoolean(Constants.IS_PERMISSION_AGGREED, true)
                    if (allMandatoryPermissionsGranted(true)) {
                        currentActivity = null
                        sharedPreferences.putBoolean(Constants.PERMISSIONS_REQUESTED, false)
                        //launchDesiredActivity()

                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this@AppPermissionsActivity,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        ) {
                            //startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)))
                            // showCustomDialog()
                            //llMandatoryLabel?.visibility = View.VISIBLE
                        }
                    }

                } else {
                    currentActivity = null
                    sharedPreferences.putBoolean(Constants.IS_PERMISSION_AGGREED, false)
                    Toast.makeText(
                        this,
                        resources.getString(R.string.terms_checkbox_alert_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onBackPressed() {

    }

    fun loadDenyActivity() {
        val intent = Intent(this@AppPermissionsActivity, PermissionDenyAlertActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        //finish()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val locationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (grantResults.size > 0) {
                moveWithOutPermission()
            }
        }
    }

    fun moveWithOutPermission() {
            if (userDetails.userStatusId == "1") {
                if (userDetails.email != null && userDetails.email != "") {
                    val intent = Intent(activityContext, DashboardActivity::class.java)
                    startActivity(intent)
                }

            } else {

                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("refresh", true)
                    startActivity(intent)

            }

    }

    fun setPermissionContent() {
        val locationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val cameraPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val audioPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)


        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            llLocation.visibility = View.VISIBLE
        } else {
            llLocation.visibility = View.GONE
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED && audioPermission != PackageManager.PERMISSION_GRANTED) {
            llCamera.visibility = View.VISIBLE
        } else {
            llCamera.visibility = View.GONE
        }

    }

    /*override fun checkPermissions() {
        try {
            if (allMandatoryPermissionsGranted(false)) {
                if (userDetails.userStatusId == "1") {
                    if (userDetails.email != null && userDetails.email != "") {
                        val intent = Intent(activityContext, DashboardActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent =
                            Intent(activityContext, FederalRegistrationActivity::class.java)
                        startActivity(intent)
                    }
                    *//*val intent = Intent(this, RegistrationHomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("refresh", true)
                    startActivity(intent)*//*
                } else {
                    if (userDetails.hasMembership == 1) {
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("refresh", true)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, PanVerificationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("refresh", true)
                        startActivity(intent)
                    }
                }

            } else {
                val locationPermission =
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                val cameraPermission =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                val audioPermission =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

                if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                    llLocation.visibility = View.VISIBLE
                } else {
                    llLocation.visibility = View.GONE
                }

                if (cameraPermission != PackageManager.PERMISSION_GRANTED && audioPermission != PackageManager.PERMISSION_GRANTED) {
                    llCamera.visibility = View.VISIBLE
                } else {
                    llCamera.visibility = View.GONE
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/


    private fun setError() {
        if (allMandatoryPermissionsGranted(false)) {
            currentActivity = null
            sharedPreferences.putBoolean(Constants.PERMISSIONS_REQUESTED, false)
            if (from != null && !from.equals("")) {
                //  finish()

                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                launchDesiredActivity()
            }
        } else {

            val locationPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            /*val smsPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            val calendarPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)*/

            if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                setErrorLabels(true)
                tvLocation?.setTextColor(ContextCompat.getColor(this, R.color.cb_errorRed))
                ivLocation?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_red_tick
                    )
                )
            } else {
                tvLocation?.setTextColor(ContextCompat.getColor(this, R.color.black))
                ivLocation?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.done_new
                    )
                )
            }
            /*if (smsPermission != PackageManager.PERMISSION_GRANTED) {
                setErrorLabels(true)
                tvSms?.setTextColor(ContextCompat.getColor(this, R.color.cb_errorRed))
                ivSms?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_red_tick
                    )
                )
            } else {
                tvSms?.setTextColor(ContextCompat.getColor(this, R.color.black))
                ivSms?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.done_new))
            }
            if (calendarPermission != PackageManager.PERMISSION_GRANTED) {
                setErrorLabels(true)
                tvCalender?.setTextColor(ContextCompat.getColor(this, R.color.cb_errorRed))
                ivCalender?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_red_tick
                    )
                )
            } else {
                tvCalender?.setTextColor(ContextCompat.getColor(this, R.color.black))
                ivCalender?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.done_new
                    )
                )
            }
            if (contactsPermission != PackageManager.PERMISSION_GRANTED) {
                setErrorLabels(true)
                tvContacts?.setTextColor(ContextCompat.getColor(this, R.color.cb_errorRed))
                ivContacts?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_red_tick
                    )
                )
            } else {
                tvContacts?.setTextColor(ContextCompat.getColor(this, R.color.black))
                ivContacts?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.done_new
                    )
                )
            }
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                setErrorLabels(true)
                tvStorage?.setTextColor(ContextCompat.getColor(this, R.color.cb_errorRed))
                ivStorage?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_red_tick
                    )
                )
            } else {
                tvStorage?.setTextColor(ContextCompat.getColor(this, R.color.black))
                ivStorage?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.done_new
                    )
                )
            }*/
        }
    }

    private fun showCustomDialog() {
        CNAlertDialog.setRequestCode(1)
        CNAlertDialog.showSettingsAlertDialog(
            this,
            "GO TO SETTINGS",
            "This allows us to verify the location you’ve entered while using the App.",
            true,
            "Settings",
            "Cancel"
        )
        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {

            }

            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {
                if (buttonType == Constants.ButtonType.POSITIVE) {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                    )
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.putBoolean(Constants.PERMISSIONS_REQUESTED, false)
        currentActivity = null
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        setPermissionContent()
        /*if(setError) {
            moveWithOutPermission()
        }*/
    }

    private fun setErrorLabels(b: Boolean) {
        try {
            if (b) {
                llMandatoryLabel?.visibility = View.VISIBLE
            } else {
                llMandatoryLabel?.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
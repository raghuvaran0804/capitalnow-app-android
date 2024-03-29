package com.capitalnowapp.mobile.kotlin.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
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
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.UserTermsData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityMandatoryPermissionsBinding
import com.capitalnowapp.mobile.models.UpdateNewLocationReq
import com.capitalnowapp.mobile.models.UpdateNewLocationResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_app_permissions.llMandatoryLabel
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class MandatoryPermissionsActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    val TAG = MandatoryPermissionsActivity::class.java.simpleName
    var binding: ActivityMandatoryPermissionsBinding? = null
    private var firstTimeLoad = true
    private var userTermsData: UserTermsData? = null
    private var setError = false
    private var firsttime = true
    private var salary = ""
    private var activity: AppCompatActivity? = null
    private var turnOnDisabledByUser = false

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrentLocation: Location? = null
    private var mRequestingLocationUpdates = false
    private var currentLocation: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMandatoryPermissionsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_mandatory_permissions)
        setContentView(binding?.root)
        initView(savedInstanceState)
    }

    private fun initView(savedInstanceState: Bundle?) {

        val obj = JSONObject()
        try {
            obj.put("cnid", userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.permissions_page_landed))

        if (intent.extras != null) {
            if (intent.extras!!.getString("salary") != null) {
                salary = intent.extras!!.getString("salary").toString()
            }
        }
        checkPermissions()
        binding!!.ivBack.setOnClickListener {
            permissionsRedirectPage = 10
            if (salary.equals("high")) {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("refresh", true)
                startActivity(intent)
            }

        }
        val termsData = sharedPreferences.getString("terms_data")
        if (termsData != null && !termsData.isEmpty()) {
            userTermsData = Gson().fromJson(termsData, UserTermsData::class.java)
        }
        if (userTermsData != null) {
            setLink()
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
        binding?.btnSettings?.setOnClickListener {
            setError = true
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
            )
        }
        binding!!.continueButton.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", userDetails.qcId)
                obj.put("permissionsGranted", "true")
                obj.put(getString(R.string.interaction_type), "NEXT Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.permissions_page_interacted))
            sharedPreferences.putBoolean(Constants.IS_PERMISSION_AGGREED, true)
            if (binding?.cbAgreeTerms?.isChecked == true) {
                if (allMandatoryPermissionsGranted1(true)) {
                    //finish()
                    if (salary.equals("high")) {
                        val intent = Intent(this, RegistrationHomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("from", "frommandatorypermission")
                        startActivity(intent)
                        //(activity as DashboardActivity).getApplyLoanData(true)
                    }
                } else {
                    if (!firsttime) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                            || !ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.CAMERA
                            )
                            || !ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.RECORD_AUDIO
                            )
                        ) {
                            binding?.llMandatoryLabel?.visibility = View.VISIBLE

                        }
                    } else {
                        firsttime = false
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.terms_checkbox_alert_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        firstTimeLoad = false


        /* login_button = findViewById(R.id.login_button);
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // attemptLogin();
                }
            });*/
        initLocationServices()
        restoreValuesFromBundle(savedInstanceState)
        checkAndRequestLocationPermissions()
    }

    private fun checkAndRequestLocationPermissions() {
        if (Utility.verifyAndRequestUserForPermissions(
                this@MandatoryPermissionsActivity,
                Constants.PERMISSION_GET_CURRENT_LOCATION,
                Constants.PERMISSIONS_GET_CURRENT_LOCATION,
                Constants.REQUEST_CODE_GET_CURRENT_LOCATION
            )
        ) {
            mRequestingLocationUpdates = true
            startLocationUpdates()
        }
    }

    private fun restoreValuesFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constants.IS_REQUESTING_UPDATES)) {
                mRequestingLocationUpdates =
                    savedInstanceState.getBoolean(Constants.IS_REQUESTING_UPDATES)
            }
            if (savedInstanceState.containsKey(Constants.LAST_KNOWN_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(Constants.LAST_KNOWN_LOCATION)
            }
        }
        updateLocation()
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

                binding?.tvPrivacyLink?.text = ss
                binding?.tvPrivacyLink?.movementMethod = LinkMovementMethod.getInstance()
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
                //if (mRequestingLocationUpdates) {
                startLocationUpdates()
                checkPermissions()
            }
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(activityContext)
        return status == ConnectionResult.SUCCESS
    }

    fun initLocationServices() {
        try {
            if (checkGooglePlayServices()) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                mSettingsClient = LocationServices.getSettingsClient(this)
                mRequestingLocationUpdates = false
                mLocationRequest = LocationRequest.create().apply {
                    interval = Constants.UPDATE_INTERVAL.toLong()
                    fastestInterval = Constants.FASTEST_UPDATE_INTERVAL.toLong()
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        // location is received
                        mCurrentLocation = locationResult.lastLocation
                        updateLocation()
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun updateLocation() {
        if (mCurrentLocation != null) {
            currentLocation =
                mCurrentLocation!!.latitude.toString() + "," + mCurrentLocation!!.longitude
                // pausing location updates
                updateNewLocation()
                stopLocationUpdates()
        }
    }

    fun stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
            .addOnCompleteListener(this) {
                //Utils.displayToast(context, "Location updates stopped!");
            }
    }

    private fun updateNewLocation() {
        val genericAPIService = GenericAPIService(activityContext, 0)
        val updateNewLocationReq = UpdateNewLocationReq()
        val token = userToken
        val platform = "Android"
        updateNewLocationReq.platform = platform
        updateNewLocationReq.currentNewLocation = currentLocation
        genericAPIService.UpdateNewLocation(updateNewLocationReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            val updateNewLocationResponse = Gson().fromJson(
                responseBody,
                UpdateNewLocationResponse::class.java
            )
            if (updateNewLocationResponse.status == true) {
            } else {

            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
            CNAlertDialog.showAlertDialog(
                activityContext,
                resources.getString(R.string.title_alert),
                getString(R.string.error_failure)
            )
        }
    }

    private fun checkPermissions() {
        if (allMandatoryPermissionsGranted1(false)) {
            //finish()
            //binding?.llMandatoryLabel?.visibility = View.GONE

                if (userDetails.userStatusId == "1") {
                    if (userDetails.email == null || userDetails.email == "") {
                        val intent = Intent(activityContext, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("refresh", true)
                        startActivity(intent)
                    }

                }else{
                    val intent = Intent(activityContext, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("refresh", true)
                    startActivity(intent)
                }


        } else {
            val locationPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            val cameraPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            val audioPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

            if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                binding!!.llLocation.visibility = View.VISIBLE
            } else {
                binding!!.llLocation.visibility = View.GONE
            }
            if (cameraPermission != PackageManager.PERMISSION_GRANTED || audioPermission != PackageManager.PERMISSION_GRANTED) {
                binding!!.llCamera.visibility = View.VISIBLE
            } else {
                binding!!.llCamera.visibility = View.GONE
            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()

        try {
            if (checkPermissions1() && mRequestingLocationUpdates) {
                startLocationUpdates()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun checkPermissions1(): Boolean {
        val permissionState =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun startLocationUpdates() {
        try {
            mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest!!)
                .addOnSuccessListener(this) {
                    Log.i(TAG, "All location settings are satisfied.")
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
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
                        mLocationCallback!!, Looper.myLooper()!!
                    )
                    updateLocation()
                }.addOnFailureListener(this) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.i(
                                TAG,
                                "Location settings are not satisfied. Attempting to upgrade location settings "
                            )
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                if (!turnOnDisabledByUser) {
                                    turnOnDisabledByUser = true
                                    val rae = e as ResolvableApiException
                                    rae.startResolutionForResult(
                                        this@MandatoryPermissionsActivity,
                                        Constants.REQUEST_CODE_GET_CURRENT_LOCATION
                                    )
                                }
                            } catch (sie: SendIntentException) {
                                Log.i(
                                    TAG,
                                    "PendingIntent unable to execute request."
                                )
                            }
                        }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                            Log.e(TAG, errorMessage)
                            Toast.makeText(activityContext, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                    updateLocation()
                }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onDestroy() {
        super.onDestroy()

    }


}
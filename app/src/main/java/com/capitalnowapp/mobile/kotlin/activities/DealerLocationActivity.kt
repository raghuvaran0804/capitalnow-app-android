package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.LoginActivity
import com.capitalnowapp.mobile.constants.Constants
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_dealer_location.tvConfirm


class DealerLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private lateinit var area: String
    private lateinit var city: String
    private var turnOnDisabledByUser = false

    /* Location Fetching Related Variables */
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrentLocation: Location? = null
    private var mRequestingLocationUpdates = false
    private var currentLocation: String? = null
    var supportMapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var tvCity: TextView? = null
    private var tvArea: TextView? = null
    private var tvInfo: TextView? = null
    private var tvCancel: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dealer_location)


        supportMapFragment = supportFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment?

        tvCity = findViewById(R.id.tvCity)
        tvInfo = findViewById(R.id.tvInfo)
        tvArea = findViewById(R.id.tvArea)
        tvCancel = findViewById(R.id.tvCancel)


        tvCancel?.setOnClickListener {
            finish()
        }
        tvConfirm.setOnClickListener {
            if (validateFields()) {
                submitData()
            }
        }

        tvArea!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    tvInfo?.visibility = GONE
                } else {
                    tvInfo?.visibility = VISIBLE
                }
            }
        })

        supportMapFragment?.getMapAsync(this)
        initLocationServices()
    }

    private fun submitData() {
        val intent = Intent(this@DealerLocationActivity, ChooseDealerActivity::class.java)
        intent.putExtra("selectedCity", city)
        intent.putExtra("selectedArea", area)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    private fun validateFields(): Boolean {
        city = tvCity?.text.toString()
        area = tvArea?.text.toString()
        return city.isNotEmpty() && area.isNotEmpty()
    }
    fun updateLocation() {
        if (mCurrentLocation != null) {
            currentLocation =
                mCurrentLocation!!.latitude.toString() + "," + mCurrentLocation!!.longitude
            if (mRequestingLocationUpdates) {
                // pausing location updates
                stopLocationUpdates()
            }
            if (mMap != null) {
                mMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            mCurrentLocation!!.latitude,
                            mCurrentLocation!!.longitude
                        ), 18F
                    )
                )
            }
        }
    }
    fun initLocationServices() {
        try {
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
                    mCurrentLocation = locationResult.lastLocation
                    updateLocation()
                }
            }
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest!!)
            mLocationSettingsRequest = builder.build()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        try {
            mSettingsClient?.checkLocationSettings(mLocationSettingsRequest!!)
                ?.addOnSuccessListener(this, OnSuccessListener<LocationSettingsResponse?> {
                    Log.i(LoginActivity.TAG, "All location settings are satisfied.")
                    mFusedLocationClient?.requestLocationUpdates(
                        mLocationRequest!!,
                        mLocationCallback!!,
                        Looper.myLooper()!!
                    )

                })?.addOnFailureListener(this, OnFailureListener { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.i(
                                LoginActivity.TAG,
                                "Location settings are not satisfied. Attempting to upgrade location settings "
                            )
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                if (!turnOnDisabledByUser) {
                                    turnOnDisabledByUser = true
                                    val rae = e as ResolvableApiException
                                    rae.startResolutionForResult(
                                        this@DealerLocationActivity,
                                        Constants.REQUEST_CODE_GET_CURRENT_LOCATION
                                    )
                                } else {
                                    /* Toast.makeText(
                                         this,
                                         "Location is mandatory",
                                         Toast.LENGTH_SHORT
                                     ).show()
                                     this@DealerLocationActivity.finish()*/
                                }
                            } catch (sie: SendIntentException) {
                                Log.i(LoginActivity.TAG, "PendingIntent unable to execute request.")
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                            Log.e(LoginActivity.TAG, errorMessage)
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                    updateLocation()
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
            .addOnCompleteListener(this) {
                //Utils.displayToast(context, "Location updates stopped!");
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap!!.setOnCameraIdleListener {
            Log.d("latlong is...", mMap!!.cameraPosition.target.toString())
            setData(mMap!!.cameraPosition.target.latitude, mMap!!.cameraPosition.target.longitude)
        }
    }

    private fun setData(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this)
        try {
            val addressList =
                geocoder.getFromLocation(latitude, longitude, 1)
            if (addressList != null && addressList.size > 0) {
                tvCity?.text = addressList[0].locality
                tvArea?.text = addressList[0].subLocality
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
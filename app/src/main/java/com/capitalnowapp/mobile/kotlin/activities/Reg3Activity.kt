package com.capitalnowapp.mobile.kotlin.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityReg3Binding
import com.capitalnowapp.mobile.models.ApplyLoanServiceDataResponse
import com.capitalnowapp.mobile.models.GenericRequest
import com.capitalnowapp.mobile.models.PinCodeResponse
import com.capitalnowapp.mobile.models.Registrations.PinCodeReq
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationThreeReq
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationThreeResponse
import com.capitalnowapp.mobile.models.userdetails.UserDetails
import com.capitalnowapp.mobile.models.userdetails.UserDetailsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson

@Suppress("UNUSED_CHANGED_VALUE")
class Reg3Activity : BaseActivity() {
    private var stateCity2: String? = null
    private var stateCity1: String? = null
    private var pinCodeResponse: PinCodeResponse? = null
    private var saveRegistrationThreeResponse: SaveRegistrationThreeResponse? = null
    private var applyLoanServiceDataResponse: ApplyLoanServiceDataResponse? = null
    private var binding: ActivityReg3Binding? = null
    private var userDetailsResponse: UserDetailsResponse? = null
    private var preAdr1: String? = ""
    private var offAdr1: String? = ""
    private var offAdr2: String? = ""
    private var preAdr2: String? = ""
    private var landMark: String? = ""
    private var pincode: String? = ""
    private var offPincode: String? = ""
    private var isPerPinCode = true
    var validationMsg = ""
    var dialog: AlertDialog? = null
    private var activity: AppCompatActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReg3Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        try {
            getProfile(userId)
            binding?.etAdr1?.addTextChangedListener (object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    preAdr1 = s.toString()
                }
            })
            binding?.etAdr2?.addTextChangedListener (object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    preAdr2 = s.toString()
                }
            })
            binding?.etLandmark?.addTextChangedListener (object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    landMark = s.toString()
                }
            })
            binding?.etPincode?.addTextChangedListener (object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if(s?.length == 6) {
                        isPerPinCode = true
                        pincode = s.toString()
                        stateAndCityFromPinCode(pincode!!)
                        binding?.tvStateAndCity?.visibility = View.VISIBLE
                    }else {
                        binding?.tvStateAndCity?.visibility = View.GONE
                    }
                }
            })

            binding?.etOffAdr1?.addTextChangedListener (object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    offAdr1 = s.toString()
                }
            })
            binding?.etOffAdr2?.addTextChangedListener (object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    offAdr2 = s.toString()
                }
            })
            binding?.etOffPincode?.addTextChangedListener (object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if(s?.length == 6) {
                        isPerPinCode = false
                        offPincode = s.toString()
                        stateAndCityFromPinCode(offPincode!!)
                        binding?.tvOffStateAndCity?.visibility = View.VISIBLE
                    }else {
                        binding?.tvOffStateAndCity?.visibility = View.GONE
                    }
                }
            })

            binding?.tvProceed?.setOnClickListener {
                validateFields()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun validateFields() {
        try {
            preAdr1 = binding?.etAdr1?.text.toString().trim { it <= ' ' }
            preAdr2 = binding?.etAdr2?.text.toString().trim { it <= ' ' }
            landMark = binding?.etLandmark?.text.toString().trim { it <= ' ' }
            pincode = binding?.etPincode?.text.toString().trim { it <= ' ' }
            offAdr1 = binding?.etOffAdr1?.text.toString().trim { it <= ' ' }
            offAdr2 = binding?.etOffAdr2?.text.toString().trim { it <= ' ' }
            offPincode = binding?.etOffPincode?.text.toString().trim { it <= ' ' }

            var count = 0
            if (preAdr1!!.isEmpty()) {
                validationMsg = "Present Address (Address Line 1) is required."
                count++
            }else  if (preAdr2!!.isEmpty()) {
                validationMsg = "Present Address (Address Line 2) is required."
                count++
            }else if(!preAdr2!!.matches(".*[0-9].*".toRegex())){
                validationMsg = "Present Address (Address Line 2) must contain 1 number"
                count++
            }else  if (landMark!!.isEmpty()) {
                validationMsg = "Present Address (Landmark) is required."
                count++
            }else  if (pincode!!.isEmpty() || pincode!!.length < 6) {
                validationMsg = "Present Address (Pin code) is required."
                count++
            }else  if (offAdr1!!.isEmpty()) {
                validationMsg = "Office Address (Address Line 1) is required."
                count++
            }else  if (offAdr2!!.isEmpty()) {
                validationMsg = "Office Address (Address Line 2) is required."
                count++
            }else  if (offPincode!!.isEmpty() || offPincode!!.length < 6) {
                validationMsg = "Office Address (Pin code) is required."
                count++
            }
            if (count > 0) {
                Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
            } else {
                saveRegistrationThree()
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun saveRegistrationThree() {
        try{
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val saveRegistrationThreeReq = SaveRegistrationThreeReq()
            saveRegistrationThreeReq.preAddressLine1 = preAdr1
            saveRegistrationThreeReq.preAddressLine2 = preAdr2
            saveRegistrationThreeReq.preLandmark = landMark
            saveRegistrationThreeReq.prePincode = pincode
            saveRegistrationThreeReq.offAddressLine1 = offAdr1
            saveRegistrationThreeReq.offAddressLine2 = offAdr2
            saveRegistrationThreeReq.offPincode = offPincode
            val token = userToken
            genericAPIService.saveRegistrationThree(saveRegistrationThreeReq, token)
            Log.d("req", Gson().toJson(saveRegistrationThreeReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                saveRegistrationThreeResponse = Gson().fromJson(
                    responseBody,
                    SaveRegistrationThreeResponse::class.java
                )
                if (saveRegistrationThreeResponse != null && saveRegistrationThreeResponse!!.status == true) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("from", "reg3")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    if (saveRegistrationThreeResponse!!.statusCode == Constants.STATUS_CODE_UNAUTHORISED) {
                        (activity as BaseActivity).logout()

                    }else {
                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        saveRegistrationThreeResponse!!.message
                    )
                    }
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun stateAndCityFromPinCode(pincode: String) {
        try{

            val genericAPIService = GenericAPIService(activity,0)
            val pinCodeReq = PinCodeReq()
            pinCodeReq.pincode = pincode
            val token = userToken
            genericAPIService.stateAndCityFromPinCode(pinCodeReq, token)
            genericAPIService.setOnDataListener { responseBody ->

                pinCodeResponse = Gson().fromJson(
                    responseBody,
                    PinCodeResponse::class.java
                )
                if (pinCodeResponse != null && pinCodeResponse!!.status == true) {
                    if(isPerPinCode){
                        binding?.tvStateAndCity?.text = pinCodeResponse?.stateCity
                    }else {
                        binding?.tvOffStateAndCity?.text = pinCodeResponse?.stateCity
                    }
                } else {
                    if(isPerPinCode){
                        binding?.etPincode?.text?.clear()
                    }else {
                        binding?.etOffPincode?.text?.clear()
                    }
                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        pinCodeResponse?.message
                    )
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {

                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun getProfile(userId: String?) {
        try{
            this.userId = userId
            val genericAPIService = GenericAPIService(activityContext, 0)
            val genericRequest = GenericRequest()
            genericRequest.userId = userId
            genericRequest.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(this)
            genericAPIService.getUserData(genericRequest)
            genericAPIService.setOnDataListener { responseBody ->
                userDetailsResponse = Gson().fromJson(
                    responseBody,
                    UserDetailsResponse::class.java
                )
                if (userDetailsResponse != null && userDetailsResponse!!.statusCode == Constants.STATUS_CODE_UNAUTHORISED) {
                    logout()
                } else {
                    if (userDetailsResponse?.userDetails != null && userDetailsResponse!!.userDetails!!.qcId != null && userDetailsResponse!!.userDetails!!.qcId != ""
                    ) {
                        sharedPreferences.putString(
                            Constants.RAZOR_PAY_API_KEY,
                            userDetailsResponse!!.razorPayApiKey
                        )
                        sharedPreferences.putString(
                            Constants.USER_DETAILS_DATA,
                            Gson().toJson(userDetailsResponse!!.userDetails)
                        )
                        userDetails = userDetailsResponse!!.userDetails
                        setData()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.error_failure),
                            Toast.LENGTH_SHORT
                        ).show()
                        userDetails = Gson().fromJson(
                            sharedPreferences.getString(Constants.USER_DETAILS_DATA),
                            UserDetails::class.java
                        )
                    }
                }
            }
            genericAPIService.setOnErrorListener {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_failure),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun setData() {
        try{
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.preAddressLine1 !=null){
                binding?.etAdr1?.setText(userDetailsResponse?.userDetails?.preAddressLine1)
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.preAddressLine2 !=null){
                binding?.etAdr2?.setText(userDetailsResponse?.userDetails?.preAddressLine2)
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.preLandmark !=null){
                binding?.etLandmark?.setText(userDetailsResponse?.userDetails?.preLandmark)
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.prePincode !=null){
                binding?.etPincode?.setText(userDetailsResponse?.userDetails?.prePincode)
                binding?.etPincode?.isEnabled = false
                binding?.etPincode?.isFocusable = false
            }else {
                binding?.etPincode?.isEnabled = true
                binding?.etPincode?.isFocusable = true
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.preStateCity !=null){
                binding?.tvStateAndCity?.text = userDetailsResponse?.userDetails?.preStateCity
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.offAddressLine1 !=null){
                binding?.etOffAdr1?.setText(userDetailsResponse?.userDetails?.offAddressLine1)
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.offAddressLine2 !=null){
                binding?.etOffAdr2?.setText(userDetailsResponse?.userDetails?.offAddressLine2)
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.offPincode !=null){
                binding?.etOffPincode?.setText(userDetailsResponse?.userDetails?.offPincode)
                binding?.etOffPincode?.isEnabled = false
                binding?.etOffPincode?.isFocusable = false
            }else {
                binding?.etOffPincode?.isEnabled = true
                binding?.etOffPincode?.isFocusable = true
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.offStateCity !=null){
                binding?.tvOffStateAndCity?.text = userDetailsResponse?.userDetails?.offStateCity
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

}
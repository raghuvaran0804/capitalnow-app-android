package com.capitalnowapp.mobile.kotlin.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityReg2Binding
import com.capitalnowapp.mobile.models.ApplyLoanServiceDataReq
import com.capitalnowapp.mobile.models.ApplyLoanServiceDataResponse
import com.capitalnowapp.mobile.models.GenericRequest
import com.capitalnowapp.mobile.models.GetVerifyAlternateEmailResponse
import com.capitalnowapp.mobile.models.MasterJsonResponse
import com.capitalnowapp.mobile.models.Registrations.GetVerifyAlternateMobileResponse
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationTwoReq
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationTwoResponse
import com.capitalnowapp.mobile.models.Registrations.VerifyAltMobileByOTPReq
import com.capitalnowapp.mobile.models.Registrations.VerifyAltMobileByOTPResponse
import com.capitalnowapp.mobile.models.Registrations.VerifyAlternateMobileReq
import com.capitalnowapp.mobile.models.VerifyAlternateEmailReq
import com.capitalnowapp.mobile.models.VerifyEmailByOTPReq
import com.capitalnowapp.mobile.models.VerifyEmailByOTPReqResponse
import com.capitalnowapp.mobile.models.userdetails.UserDetails
import com.capitalnowapp.mobile.models.userdetails.UserDetailsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.chaos.view.PinView
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class Reg2Activity : BaseActivity() {
    private var isOfficial: Int? = 0
    private var getVerifyAlternateEmail: VerifyAlternateEmailReq? = null
    private var getVerifyAlternateEmailResponse: GetVerifyAlternateEmailResponse? = null
    private var verifyAltMobileByOTPResponse: VerifyAltMobileByOTPResponse? = null
    private var getVerifyAlternateMobileResponse: GetVerifyAlternateMobileResponse? = null
    private var saveRegistrationTwoResponse: SaveRegistrationTwoResponse? = null
    private var applyLoanServiceDataResponse: ApplyLoanServiceDataResponse? = null
    private var binding: ActivityReg2Binding? = null
    private var userDetailsResponse: UserDetailsResponse? = null
    private var alternateMobileNo: String? = ""
    private var alternateEmail: String? = ""
    private var experience: String? = ""
    private var salaryDate: String? = ""
    var masterJsonResponse: MasterJsonResponse? = null
    var experienceListMap: LinkedHashMap<String, String>? = null
    var salDateListMap: LinkedHashMap<String, String>? = null
    var experienceListKeys: Array<String>? = null
    var salDateListKeys: Array<String>? = null

    var validationMsg = ""
    private var activity: AppCompatActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReg2Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        try {
            getProfile(userId)
            binding?.cbAgreeTerms?.setOnClickListener {
                if (binding?.cbAgreeTerms!!.isChecked) {
                    binding?.tvEmail?.text = "Alternate Email Id"
                    binding?.tvdtText?.visibility = View.VISIBLE
                    isOfficial = 1
                } else {
                    binding?.tvEmail?.text = "Official Email Id"
                    binding?.tvdtText?.visibility = View.GONE
                    isOfficial = 0
                }
            }


            binding?.etAlterMobileNo?.addTextChangedListener(object : TextWatcher {
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
                    if (s!!.length == 10) {
                        alternateMobileNo = s.toString()
                    }
                }
            })

            binding?.etAlterEmail?.addTextChangedListener(object : TextWatcher {
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

                    alternateEmail = binding?.etAlterEmail?.text.toString().trim { it <= ' ' }

                }
            })

            experienceListMap = LinkedHashMap<String, String>()

            experienceListMap!!["Below 1 year"] = "1"
            experienceListMap!!["1-2 Years"] = "2"
            experienceListMap!!["2-3 Years"] = "3"
            experienceListMap!!["3-4 Years"] = "4"
            experienceListMap!!["4-5 Years"] = "5"
            experienceListMap!!["5+ Years"] = "6"

            experienceListKeys = experienceListMap!!.keys.toTypedArray()

            salDateListMap = LinkedHashMap<String, String>()

            salDateListMap!!["First Working Day of Month"] = "1"
            salDateListMap!!["Last Working Day of Month"] = "2"
            salDateListMap!!["1st of Month"] = "3"
            salDateListMap!!["2nd of Month"] = "4"
            salDateListMap!!["3rd of Month"] = "5"
            salDateListMap!!["4th of Month"] = "6"
            salDateListMap!!["5th of Month"] = "7"
            salDateListMap!!["6th of Month"] = "8"
            salDateListMap!!["7th of Month"] = "9"
            salDateListMap!!["8th of Month"] = "10"
            salDateListMap!!["9th of Month"] = "11"
            salDateListMap!!["10th of Month"] = "12"
            salDateListMap!!["11th of Month"] = "13"
            salDateListMap!!["12th of Month"] = "14"
            salDateListMap!!["13th of Month"] = "15"
            salDateListMap!!["14th of Month"] = "16"
            salDateListMap!!["15th of Month"] = "17"
            salDateListMap!!["16th of Month"] = "18"
            salDateListMap!!["17th of Month"] = "19"
            salDateListMap!!["18th of Month"] = "20"
            salDateListMap!!["19th of Month"] = "21"
            salDateListMap!!["20th of Month"] = "22"
            salDateListMap!!["21st of Month"] = "23"
            salDateListMap!!["22nd of Month"] = "24"
            salDateListMap!!["23rd of Month"] = "25"
            salDateListMap!!["24th of Month"] = "26"
            salDateListMap!!["25th of Month"] = "27"
            salDateListMap!!["26th of Month"] = "28"
            salDateListMap!!["27th of Month"] = "29"
            salDateListMap!!["28th of Month"] = "30"
            salDateListMap!!["29th of Month"] = "31"
            salDateListMap!!["30th of Month"] = "32"

            salDateListKeys = salDateListMap!!.keys.toTypedArray()

            binding?.etSalDate?.setOnClickListener {
                if (salDateListKeys != null && salDateListKeys!!.isNotEmpty()) {
                    showSalDateDialog()
                }
            }

            binding?.etExpInYears?.setOnClickListener {
                if (experienceListKeys != null && experienceListKeys!!.isNotEmpty()) {
                    showExperienceDialog()
                }
            }

            binding?.tvVerifyAltMobile?.setOnClickListener {
                if (alternateMobileNo != null && alternateMobileNo!!.length == 10) {
                    verifyAlternateMobile(alternateMobileNo)
                } else {
                    Toast.makeText(
                        this,
                        "Enter Alternate Mobile Number",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
            binding?.tvVerifyOffEmail?.setOnClickListener {
                if (alternateEmail != null && alternateEmail != "" && Patterns.EMAIL_ADDRESS.matcher(
                        alternateEmail!!
                    ).matches()
                ) {
                    if(userDetails.email == alternateEmail){
                        displayToast("Secondary Mail cannot be same as Primary mail")
                        binding?.etAlterEmail?.text?.clear()
                    }else {
                        verifyAlternateEmail(alternateEmail!!)
                    }
                } else {
                    if (binding?.cbAgreeTerms?.isChecked!!) {
                        Toast.makeText(
                            this,
                            "Enter Alternate Email",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Enter Official Email",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            binding?.tvProceed?.setOnClickListener {
                validateFields()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun verifyAlternateMobile(alternateMobileNo: String?) {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val verifyAlternateMobileReq = VerifyAlternateMobileReq()
            verifyAlternateMobileReq.mobileNo = alternateMobileNo
            val token = userToken
            genericAPIService.verifyAlternateMobile(verifyAlternateMobileReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getVerifyAlternateMobileResponse =
                    Gson().fromJson(responseBody, GetVerifyAlternateMobileResponse::class.java)
                if (getVerifyAlternateMobileResponse != null && getVerifyAlternateMobileResponse?.status == true) {
                    showPopup()
                } else {
                    Toast.makeText(
                        this,
                        getVerifyAlternateMobileResponse?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showPopup() {
        var otp = ""
        val alertDialog = Dialog(this)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.verify_email_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        val tvOk = alertDialog.findViewById<TextView>(R.id.tvOk)
        val tvCancel = alertDialog.findViewById<ImageView>(R.id.tvCancel)
        val tvOtp = alertDialog.findViewById<PinView>(R.id.tvOtp)
        //tvOtp.text = otpToSend
        tvOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                otp = s.toString()
                if (otp.length == 6) {
                    hideKeyboard(this@Reg2Activity)
                    tvOk.isEnabled = true
                } else {
                    tvOk.isEnabled = false
                }
            }
        })
        tvOk.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("cnid", userDetails.qcId)
                obj.put("alternateEmail", "")
                obj.put("otpEntered", "True")
                obj.put(getString(R.string.interaction_type), "VERIFY OTP Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(
                obj,
                getString(R.string.alternate_email_verification_OTP_submitted)
            )
            verifyAltMobileByOTP(otp, alertDialog)
        }
        tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun verifyAltMobileByOTP(otp: String, alertDialog: Dialog) {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val verifyAltMobileByOTPReq = VerifyAltMobileByOTPReq()
            verifyAltMobileByOTPReq.mobileNo = alternateMobileNo
            verifyAltMobileByOTPReq.otp = otp
            val token = userToken
            genericAPIService.verifyAltMobileByOTP(verifyAltMobileByOTPReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                verifyAltMobileByOTPResponse =
                    Gson().fromJson(responseBody, VerifyAltMobileByOTPResponse::class.java)
                if (verifyAltMobileByOTPResponse != null && verifyAltMobileByOTPResponse?.status == true) {
                    binding!!.tvVerifyAltMobile.text = "Verified"
                    binding!!.tvVerifyAltMobile.setTextColor(
                        ContextCompat.getColor(
                            this,
                            com.capitalnowapp.mobile.R.color.colorProfileProgress_1
                        )
                    )
                    enableAltMobile(false)
                    userDetails.secMobileVerified = "1"
                    alertDialog.dismiss()
                    sharedPreferences.putString(
                        Constants.USER_DETAILS_DATA,
                        Gson().toJson(userDetails)
                    )
                } else {
                    Toast.makeText(
                        this,
                        verifyAltMobileByOTPResponse?.message,
                        Toast.LENGTH_LONG
                    ).show()
                    CNProgressDialog.hideProgressDialog()
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun verifyAlternateEmail(alternateEmail: String) {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            getVerifyAlternateEmail = VerifyAlternateEmailReq()
            getVerifyAlternateEmail!!.secEmail = alternateEmail
            getVerifyAlternateEmail!!.isOfficial = isOfficial
            val token = userToken
            genericAPIService.verifyAlternateEmail(getVerifyAlternateEmail, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getVerifyAlternateEmailResponse =
                    Gson().fromJson(responseBody, GetVerifyAlternateEmailResponse::class.java)
                if (getVerifyAlternateEmailResponse != null && getVerifyAlternateEmailResponse!!.status == Constants.STATUS_SUCCESS) {
                    showPopup2()
                } else {
                    Toast.makeText(
                        this,
                        getVerifyAlternateEmailResponse!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showPopup2() {
        var otp = ""
        val alertDialog = Dialog(this)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.verify_email_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        val tvOk = alertDialog.findViewById<TextView>(R.id.tvOk)
        val tvCancel = alertDialog.findViewById<ImageView>(R.id.tvCancel)
        val tvOtp = alertDialog.findViewById<PinView>(R.id.tvOtp)
        //tvOtp.text = otpToSend
        tvOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                otp = s.toString()
                if (otp.length == 6) {
                    hideKeyboard(this@Reg2Activity)
                    tvOk.isEnabled = true
                } else {
                    tvOk.isEnabled = false
                }
            }
        })
        tvOk.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("cnid", userDetails.qcId)
                obj.put("alternateEmail", "")
                obj.put("otpEntered", "True")
                obj.put(getString(R.string.interaction_type), "VERIFY OTP Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(
                obj,
                getString(R.string.alternate_email_verification_OTP_submitted)
            )
            verifyEmailByOTP(otp, alertDialog)
        }
        tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun verifyEmailByOTP(otp: String, alertDialog: Dialog) {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val verifyEmailByOTPReq = VerifyEmailByOTPReq()
            verifyEmailByOTPReq.secEmail = alternateEmail
            verifyEmailByOTPReq.isOfficial = isOfficial
            verifyEmailByOTPReq.otp = otp
            val token = userToken
            genericAPIService.verifyEmailByOTP(verifyEmailByOTPReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val verifyEmailByOTPReqResponse =
                    Gson().fromJson(responseBody, VerifyEmailByOTPReqResponse::class.java)
                if (verifyEmailByOTPReqResponse != null && verifyEmailByOTPReqResponse.status == Constants.STATUS_SUCCESS) {
                    binding!!.tvVerifyOffEmail.text = "Verified"
                    binding!!.tvVerifyOffEmail.setTextColor(
                        ContextCompat.getColor(
                            this,
                            com.capitalnowapp.mobile.R.color.colorProfileProgress_1
                        )
                    )
                    enableSecEmail(false)
                    userDetails.secEmailVerified = "1"
                    binding?.cbAgreeTerms?.isEnabled = false
                    binding?.cbAgreeTerms?.isClickable = false
                    alertDialog.dismiss()
                    sharedPreferences.putString(
                        Constants.USER_DETAILS_DATA,
                        Gson().toJson(userDetails)
                    )
                } else {
                    Toast.makeText(
                        this,
                        verifyEmailByOTPReqResponse.message,
                        Toast.LENGTH_LONG
                    ).show()
                    CNProgressDialog.hideProgressDialog()
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun enableSecEmail(b: Boolean) {
        binding!!.etAlterEmail.isEnabled = b
        binding!!.tvVerifyOffEmail.isEnabled = b

        if (b) {


        } else {
            binding!!.tvVerifyOffEmail.text = "Verified"
            binding?.cbAgreeTerms?.isClickable = false
            binding?.cbAgreeTerms?.isEnabled = false
            binding!!.tvVerifyOffEmail.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.capitalnowapp.mobile.R.color.colorProfileProgress_1
                )
            )
            userDetails.secEmailVerified = "1"
            sharedPreferences.putString(Constants.USER_DETAILS_DATA, Gson().toJson(userDetails))
        }
    }

    private fun enableAltMobile(b: Boolean) {
        binding!!.etAlterMobileNo.isEnabled = b
        binding!!.tvVerifyAltMobile.isEnabled = b

        if (b) {

        } else {
            binding!!.tvVerifyAltMobile?.text = "Verified"
            binding!!.tvVerifyAltMobile.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.capitalnowapp.mobile.R.color.colorProfileProgress_1
                )
            )
            userDetails.secMobileVerified = "1"
            sharedPreferences.putString(Constants.USER_DETAILS_DATA, Gson().toJson(userDetails))
        }
    }

    private fun showSalDateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(salDateListKeys) { _, which ->
            binding?.etSalDate?.setText(salDateListKeys?.get(which))
            salaryDate = salDateListMap?.get(salDateListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun showExperienceDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(experienceListKeys) { _, which ->
            binding?.etExpInYears?.setText(experienceListKeys?.get(which))
            experience = experienceListMap?.get(experienceListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun validateFields() {
        try {
            alternateMobileNo = binding?.etAlterMobileNo?.text.toString().trim { it <= ' ' }
            alternateEmail = binding?.etAlterEmail?.text.toString().trim { it <= ' ' }
            experience = binding?.etExpInYears?.text.toString().trim { it <= ' ' }
            salaryDate = binding?.etSalDate?.text.toString().trim { it <= ' ' }
            var count = 0
            if (alternateMobileNo!!.isEmpty() && alternateMobileNo!!.length < 10) {
                validationMsg = "Alternate Mobile Number is required."
                count++
            } else if (alternateEmail!!.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(alternateEmail!!)
                    .matches()
            ) {
                if (binding?.cbAgreeTerms?.isChecked!!) {
                    validationMsg = "Alternate Email is required."
                    count++
                } else {
                    validationMsg = "Alternate Email is required."
                    count++
                }
                /* validationMsg = "Alternate Email is required."
                 count++*/
            } else if (experience!!.isEmpty()) {
                validationMsg = "Experience is required."
                count++
            } else if (salaryDate!!.isEmpty()) {
                validationMsg = "Salary Date is required."
                count++
            } else if (userDetails != null && userDetails.secMobileVerified != null && userDetails.secMobileVerified!!.isEmpty() || userDetails.secMobileVerified.equals(
                    "0"
                )
            ) {
                count++
                validationMsg = "Verify alternate Mobile Number."
            }
            if (count > 0) {
                Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
            } else {
                saveRegistrationTwo()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getProfile(userId: String?) {
        this.userId = userId
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activityContext, 0)
        val genericRequest = GenericRequest()
        genericRequest.userId = userId
        genericRequest.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(this)
        genericAPIService.getUserData(genericRequest)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
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
            CNProgressDialog.hideProgressDialog()
            Toast.makeText(
                applicationContext,
                getString(R.string.error_failure),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setData() {
        try {
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.altMobile != null) {
                binding?.etAlterMobileNo?.setText(userDetailsResponse?.userDetails?.altMobile)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.altEmail != null) {
                binding?.etAlterEmail?.setText(userDetailsResponse?.userDetails?.altEmail)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.expInYears != null) {
                binding?.etExpInYears?.setText(userDetailsResponse?.userDetails?.expInYears)
            }

            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.salDate != null) {
                binding?.etSalDate?.setText(userDetailsResponse?.userDetails?.salDate)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails!!.secMobileVerified == "1") {
                enableAltMobile(false)
            } else {
                enableAltMobile(true)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails!!.secEmailVerified == "1") {
                enableSecEmail(false)
            } else {
                enableSecEmail(true)
            }

            if(userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails!!.isOfficial == 1){
                binding?.cbAgreeTerms?.isChecked = true
                binding?.tvEmail?.text = "Alternate Email Id"
                binding?.tvdtText?.visibility = View.VISIBLE
            }else if(userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails!!.isOfficial == 0){
                binding?.cbAgreeTerms?.isChecked = false
                binding?.tvEmail?.text = "Official Email Id"
                binding?.tvdtText?.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveRegistrationTwo() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val saveRegistrationTwoReq = SaveRegistrationTwoReq()
            saveRegistrationTwoReq.altMobileNo = alternateMobileNo
            saveRegistrationTwoReq.altEmail = alternateEmail
            saveRegistrationTwoReq.expYears = experience
            saveRegistrationTwoReq.salDate = salaryDate
            saveRegistrationTwoReq.pageNo = "35"
            val token = userToken
            genericAPIService.saveRegistrationTwo(saveRegistrationTwoReq, token)
            Log.d("req", Gson().toJson(saveRegistrationTwoReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                saveRegistrationTwoResponse = Gson().fromJson(
                    responseBody,
                    SaveRegistrationTwoResponse::class.java
                )
                if (saveRegistrationTwoResponse != null && saveRegistrationTwoResponse!!.status == true) {
                    getApplyLoanData(true)
                } else {
                    if (saveRegistrationTwoResponse!!.statusCode == Constants.STATUS_CODE_UNAUTHORISED) {
                        (activity as BaseActivity).logout()

                    } else {
                        CNAlertDialog.showAlertDialog(
                            this,
                            resources.getString(R.string.title_alert),
                            saveRegistrationTwoResponse!!.message
                        )
                    }
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getApplyLoanData(canRedirect: Boolean) {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity)
            val applyLoanServiceDataReq = ApplyLoanServiceDataReq()
            applyLoanServiceDataReq.currentScreen = "35"
            applyLoanServiceDataReq.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(this)
            val token1 = userToken
            genericAPIService.applyLoanServiceData(applyLoanServiceDataReq, token1)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                applyLoanServiceDataResponse = Gson().fromJson(
                    responseBody,
                    ApplyLoanServiceDataResponse::class.java
                )
                if (applyLoanServiceDataResponse != null && applyLoanServiceDataResponse!!.status == "success") {
                    when (applyLoanServiceDataResponse!!.statusRedirect) {
                        34 -> {
                            val intent = Intent(this, Reg1Activity::class.java)
                            startActivity(intent)
                        }

                        36 -> {
                            val intent = Intent(this, Reg3Activity::class.java)
                            startActivity(intent)
                        }

                        4 -> {
                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                        }

                        37 -> {
                            val intent = Intent(this, HoldActivity::class.java)
                            startActivity(intent)
                        }
                    }

                } else {
                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        "Something went wrong please try again later"
                    )
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }
}
package com.capitalnowapp.mobile.kotlin.activities


import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityBasicDetailsBinding
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.kotlin.utils.Validator
import com.capitalnowapp.mobile.models.GetVerifyAlternateEmailResponse
import com.capitalnowapp.mobile.models.VerifyAlternateEmailReq
import com.capitalnowapp.mobile.models.VerifyEmailByOTPReq
import com.capitalnowapp.mobile.models.VerifyEmailByOTPReqResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.chaos.view.PinView
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar
import java.util.Date


class BasicDetailsActivity : RegistrationHomeActivity(), TextWatcher, View.OnFocusChangeListener {

    companion object {
        private const val LOCATION_PERMISSION_CODE = 100

    }
    private var citiesArray: Array<CharSequence?>? = null
    private var currentDate: String = ""
    private var binding: ActivityBasicDetailsBinding? = null
    private var otpToSend: String = ""

    private var isFormatting = false
    private var deletingHyphen = false
    private var hyphenStart = 0
    private var deletingBackward = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        refreshJson()
        initView(binding)
        setBasicBinding(binding!!, this)
    }

    private fun initView(binding: ActivityBasicDetailsBinding?) {

        val obj = JSONObject()
        try {
            obj.put("cnid",userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.basic_details_landed))


        binding?.etHowYouKnow?.setHintTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.intro_page_body_color
                )
        )

        currentDate = Utility.formatDate(Date(), Constants.DOB_DATE_FORMAT)
        binding!!.ivBasicBack.setOnClickListener {
            //onBackPressed()
            val intent =
                Intent(this@BasicDetailsActivity, RegistrationHomeActivity::class.java)
            startActivity(intent)
        }
        setTextViewDrawableColor(binding.tvMale, R.color.black)
        setTextViewDrawableColor(binding.tvFemale, R.color.black)
        setTextViewDrawableColor(binding.tvMarried, R.color.black)
        setTextViewDrawableColor(binding.tvSingle, R.color.black)

        binding.txEtDate.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (isFormatting) return

                // Make sure user is deleting one char, without a selection
                val selStart = Selection.getSelectionStart(s)
                val selEnd = Selection.getSelectionEnd(s)
                if (s.length > 1 // Can delete another character
                        && count == 1 // Deleting only one character
                        && after == 0 // Deleting
                        && s[start] == '-' // a hyphen
                        && selStart == selEnd
                ) { // no selection
                    deletingHyphen = true
                    hyphenStart = start
                    // Check if the user is deleting forward or backward
                    deletingBackward = selStart == start + 1
                } else {
                    deletingHyphen = false
                }
            }

            override fun afterTextChanged(text: Editable) {
                if (isFormatting) return
                isFormatting = true

                // If deleting hyphen, also delete character before or after it
                if (deletingHyphen && hyphenStart > 0) {
                    if (deletingBackward) {
                        if (hyphenStart - 1 < text.length) {
                            text.delete(hyphenStart - 1, hyphenStart)
                        }
                    } else if (hyphenStart < text.length) {
                        text.delete(hyphenStart, hyphenStart + 1)
                    }
                }
                if (text.length == 2 || text.length == 5) {
                    text.append('-')
                }
                isFormatting = false

                if (text.toString().length == 10) {
                    registerUserReq.dob = text.toString()
                    saveData()
                } else {
                    registerUserReq.dob = text.toString()
                    saveData()
                }
            }
        })
        binding.rgGender.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            if (checkedId == binding!!.tvMale.id) {
                setMale()
            } else if (checkedId == binding!!.tvFemale.id) {
                setFemale()
            }

        })

        binding.rgMaritalStatus.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            if (checkedId == binding!!.tvMarried.id) {
                setMarried()
            } else if (checkedId == binding!!.tvSingle.id) {
                setSingle()
            }

        })

        binding.txEtSelectCity.setOnClickListener {
            /*if (allCitiesList != null && allCitiesList!!.size > 0) {
                showCitiesDialog()
            }*/
            checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION_PERMISSION_CODE
            )

            val intent = Intent(this, MapActivity::class.java)
            startActivityForResult(intent, 10001)

        }

        binding.etHowYouKnow.setOnClickListener {
            if (promotionTypesMapKeys != null && promotionTypesMapKeys!!.isNotEmpty()) {
                showPromotionDialog()
            }
        }
        binding?.etRecidenceType.setOnClickListener {
            if(residenceTypesMapKeys != null && residenceTypesMapKeys!!.isNotEmpty()){
                showResidenceDialog()
            }
        }

        binding.tvProfDetails.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid",userDetails.qcId)
                obj.put(getString(R.string.interaction_type),"PROFESSIONAL DETAILS Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.basic_details_page_submitted))

            when (isBasicFilled()) {
                0 -> {
                    startActivity(Intent(this, ProfessionalDetailsActivity::class.java))
                }
                1 -> {
                    displayToast(validationMsg)
                }
                else -> {
                    displayToast(getString(R.string.unique_validation_msg))
                }
            }
        }

        binding!!.tvVerifyEmail.setOnClickListener {
            if (Utility.isValidEmail(binding!!.txEtAlterEmail.text.toString().trim())) {
                if (userDetails.userBasicData?.email.equals(binding!!.txEtAlterEmail.text.toString().trim())) {
                    // both are same
                } else {
                    verifyAlternateEmail()
                }
            } else {
                // enter email validation
            }
        }

        binding.txEtFirstName.addTextChangedListener(this)
        // binding.txEtMiddleName.addTextChangedListener(this)
        binding.txEtLastName.addTextChangedListener(this)
        binding.txEtAlterEmail.addTextChangedListener(this)
        binding.txEtAlterMobile.addTextChangedListener(this)

        binding.txEtAlterEmail.onFocusChangeListener = this
        binding.txEtAlterMobile.onFocusChangeListener = this
        binding.txEtDate.setOnClickListener {
            showDatePicker(binding.txEtDate)
        }
        setData()
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@BasicDetailsActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@BasicDetailsActivity,
                arrayOf(permission),
                requestCode
            )
        } else {

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
                    hideKeyboard(this@BasicDetailsActivity)
                    tvOk.isEnabled = true
                } else {
                    tvOk.isEnabled = false
                }
            }
        })
        tvOk.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("cnid",userDetails.qcId)
                obj.put("alternateEmail","")
                obj.put("otpEntered","True")
                obj.put(getString(R.string.interaction_type),"VERIFY OTP Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.alternate_email_verification_OTP_submitted))
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
            val genericAPIService = GenericAPIService(this,0)
            val verifyEmailByOTPReq = VerifyEmailByOTPReq()
            verifyEmailByOTPReq.secEmail = registerUserReq.secEmail
            verifyEmailByOTPReq.otp = otp
            val token = userToken
            genericAPIService.verifyEmailByOTP(verifyEmailByOTPReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val verifyEmailByOTPReqResponse = Gson().fromJson(responseBody, VerifyEmailByOTPReqResponse::class.java)
                if (verifyEmailByOTPReqResponse != null && verifyEmailByOTPReqResponse.status == Constants.STATUS_SUCCESS) {
                    binding!!.tvVerifyEmail.text = "Verified"
                    binding!!.tvVerifyEmail.setTextColor(ContextCompat.getColor(
                            this,
                            com.capitalnowapp.mobile.R.color.colorProfileProgress_1))
                    enableSecEmail(false)
                    userDetails.secEmailVerified = "1"
                    alertDialog.dismiss()
                    sharedPreferences.putString(Constants.USER_DETAILS_DATA, Gson().toJson(userDetails))
                } else {
                    Toast.makeText(this,
                            verifyEmailByOTPReqResponse.message,
                            Toast.LENGTH_LONG).show()
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

    private fun verifyAlternateEmail() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this,0)
            val getVerifyAlternateEmail = VerifyAlternateEmailReq()
            getVerifyAlternateEmail.secEmail = registerUserReq.secEmail
            val token = userToken
            genericAPIService.verifyAlternateEmail(getVerifyAlternateEmail, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val getVerifyAlternateEmailResponse = Gson().fromJson(responseBody, GetVerifyAlternateEmailResponse::class.java)
                if (getVerifyAlternateEmailResponse != null && getVerifyAlternateEmailResponse.status == Constants.STATUS_SUCCESS) {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid",userDetails.qcId)
                        obj.put("alternateEmail","")

                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.alternate_email_verification_OTP_server_event))

                    showPopup()
                } else {
                    Toast.makeText(this,
                            getVerifyAlternateEmailResponse.message,
                            Toast.LENGTH_LONG).show()
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

    private fun showDatePicker(view: View?) {
        try {
            var preSelectedDate: String? = binding?.txEtDate?.text.toString()
            if (preSelectedDate!!.isEmpty()) {
                preSelectedDate = currentDate
            }
            val dateValues: Array<String> = preSelectedDate.split("-").toTypedArray()
            val datePickerDialog = DatePickerDialog(
                    this,
                    R.style.MyDatePickerDialogTheme,
                    { datePicker, year, monthOfYear, dayOfMonth ->
                        val calendar = Calendar.getInstance()
                        calendar[year, monthOfYear] = dayOfMonth
                        preSelectedDate = Utility.formatDate(calendar.time, Constants.DOB_DATE_FORMAT)
                        registerUserReq.dob = preSelectedDate
                        binding?.txEtDate?.setText(preSelectedDate)
                        saveOSRFValueData(AppConstants.AjaxKeys.DOB.toInt(), preSelectedDate)
                    },
                    dateValues[2].toInt(),
                    dateValues[1].toInt() - 1,
                    dateValues[0].toInt()
            )
            val minDate = Utility.convertStringToDate("01-01-1960", Constants.DOB_DATE_FORMAT)
            val minDateCal = Calendar.getInstance()
            minDateCal.time = minDate
            datePickerDialog.setCancelable(false)
            datePickerDialog.datePicker.minDate = minDateCal.timeInMillis

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -20)
            val maxDate = calendar.time.time

            datePickerDialog.datePicker.maxDate = maxDate
            datePickerDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setData() {
        if (registerUserReq.firstName != null && !registerUserReq.firstName.equals("")) {
            binding?.txEtFirstName?.setText(registerUserReq.firstName)
        }
        /*if (registerUserReq.middleName != null && !registerUserReq.middleName.equals("")) {
            binding?.txEtMiddleName?.setText(registerUserReq.middleName)
        }*/
        if (registerUserReq.lastName != null && !registerUserReq.lastName.equals("")) {
            binding?.txEtLastName?.setText(registerUserReq.lastName)
        }
        if (registerUserReq.dob != null && !registerUserReq.dob.equals("")) {
            binding?.txEtDate?.setText(registerUserReq.dob)
        }
        if (registerUserReq.gender != null && !registerUserReq.gender.equals("")) {
            if (registerUserReq.gender.toString() == "1") {
                binding!!.tvMale.isChecked = true
            } else {
                binding!!.tvFemale.isChecked = true
            }
        }
        if (registerUserReq.maritalStatus != null && !registerUserReq.maritalStatus.equals("")) {
            if (registerUserReq.maritalStatus.toString() == "1") {
                binding!!.tvMarried.isChecked = true
            } else {
                binding!!.tvSingle.isChecked = true
            }
        }
        if (registerUserReq.secEmail != null && !registerUserReq.secEmail.equals("")) {
            binding?.txEtAlterEmail?.setText(registerUserReq.secEmail)
        }
        if (registerUserReq.altMobile != null && !registerUserReq.altMobile.equals("")) {
            binding?.txEtAlterMobile?.setText(registerUserReq.altMobile)
        }
        if (registerUserReq.refType != null && !registerUserReq.refType.equals("")) {
            val str: String = registerUserReq.refType!!
            val value: String? = promotionTypesMap?.let { getKeyFromValue(it, str) } as String?
            binding?.etHowYouKnow?.setText(value)
            if (registerUserReq.refType.equals("8")) {
                binding?.etHowYouKnow?.isEnabled = false
            }
        } else {
            binding?.etHowYouKnow?.isEnabled = true
        }

        if (registerUserReq.residence != null && !registerUserReq.residence.equals("")) {
            val str: String = registerUserReq.residence!!
            val value: String? = residenceTypesMap?.let { getKeyFromValue(it, str) } as String?
            binding?.etRecidenceType?.setText(value)
        }

        if ((registerUserReq.nativeCityId != null && !registerUserReq.nativeCityId.equals("0"))) {
            binding?.txEtSelectCity?.setText(registerUserReq.nativeCityId)
        }
        if (userDetails != null && userDetails.secEmailVerified == "1") {
            enableSecEmail(false)
        } else {
            enableSecEmail(true)
        }
    }

    private fun enableSecEmail(b: Boolean) {
        binding!!.txEtAlterEmail.isEnabled = b
        binding!!.tvVerifyEmail.isEnabled = b

        if (b) {

        } else {
            binding!!.tvVerifyEmail.text = "Verified"
            binding!!.tvVerifyEmail.setTextColor(ContextCompat.getColor(
                    this,
                    com.capitalnowapp.mobile.R.color.colorProfileProgress_1))
            userDetails.secEmailVerified = "1"
            sharedPreferences.putString(Constants.USER_DETAILS_DATA, Gson().toJson(userDetails))
        }
    }

    override fun afterTextChanged(s: Editable?) {
        when (s.toString()) {
            binding?.txEtFirstName?.editableText.toString() -> {
                registerUserReq.firstName = s.toString()
                saveData()
                if (s!!.isNotEmpty()) {
                    saveOSRFValueData(
                            AppConstants.AjaxKeys.FirstName.toInt(),
                            registerUserReq.firstName
                    )
                }
            }
            /*binding?.txEtMiddleName?.editableText.toString() -> {
                registerUserReq.middleName = s.toString()
                saveData()
                if(s!!.isNotEmpty()) {
                    saveOSRFValueData(
                        AppConstants.AjaxKeys.MiddleName.toInt(),
                        registerUserReq.middleName
                    )
                }
            }*/
            binding?.txEtLastName?.editableText.toString() -> {
                registerUserReq.lastName = s.toString()
                saveData()
                if (s!!.isNotEmpty()) {
                    saveOSRFValueData(
                            AppConstants.AjaxKeys.LastName.toInt(),
                            registerUserReq.lastName
                    )
                }
            }
            binding?.txEtAlterMobile?.editableText.toString() -> {
                registerUserReq.altMobile = s.toString()
                saveData()
                if (binding?.txEtAlterMobile?.editableText.toString().length == 10) {
                    saveOSRFValueData(
                            AppConstants.AjaxKeys.AltMob.toInt(),
                            registerUserReq.altMobile
                    )
                }
            }
            binding?.txEtAlterEmail?.editableText.toString() -> {
                registerUserReq.secEmail = s.toString()
                saveData()

            }

            binding?.txEtSelectCity?.editableText.toString() -> {
                registerUserReq.secEmail = s.toString()
                saveData()
            }

        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    private fun showCitiesDialog() {
        showCodesDialog(allCitiesList!!, this)
    }

    private fun showPromotionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(promotionTypesMapKeys) { _, which ->
            binding?.etHowYouKnow?.setText(promotionTypesMapKeys?.get(which))
            registerUserReq.refType = promotionTypesMap?.get(promotionTypesMapKeys?.get(which)!!)
            saveOSRFValueData(AppConstants.AjaxKeys.PromotionType.toInt(), registerUserReq.refType)
            saveData()
        }
        builder.show()
    }

    private fun showResidenceDialog() {
        val builder  = AlertDialog.Builder(this)
        builder.setItems(residenceTypesMapKeys) { _, which ->
            binding?.etRecidenceType?.setText(residenceTypesMapKeys?.get(which))
            registerUserReq.residence = residenceTypesMap?.get(residenceTypesMapKeys?.get(which)!!)
            saveData()
        }
        builder.show()
    }

    fun updateCity() {
        citiesArray = arrayOfNulls(allCitiesList!!.size)
        for (i in 0 until allCitiesList!!.size) {
            citiesArray!![i] =
                    allCitiesList?.get(i)?.name // Whichever string you wanna store here from custom object
        }
        if ((registerUserReq.nativeCityId != null && !registerUserReq.nativeCityId.equals(""))) {
            binding?.txEtSelectCity?.setText(
                    citiesArray!![(registerUserReq.nativeCityId)?.toInt()?.minus(1)!!]
            )
        }
        saveData()
    }

    private fun setMale() {
        registerUserReq.gender = "1"
        setTextViewDrawableColor(binding!!.tvMale, R.color.Primary2)
        setTextViewDrawableColor(binding!!.tvFemale, R.color.black)
        saveData()
        saveOSRFValueData(AppConstants.AjaxKeys.Gender.toInt(), registerUserReq.gender)
    }
    private fun setFemale() {
        registerUserReq.gender = "0"
        setTextViewDrawableColor(binding!!.tvMale, R.color.black)
        setTextViewDrawableColor(binding!!.tvFemale, R.color.Primary2)
        saveData()
        saveOSRFValueData(AppConstants.AjaxKeys.Gender.toInt(), registerUserReq.gender)
    }

    private fun setMarried() {
        registerUserReq.maritalStatus = "1"
        setTextViewDrawableColor(binding!!.tvMarried, R.color.Primary2)
        setTextViewDrawableColor(binding!!.tvSingle, R.color.black)
        saveData()
        //saveOSRFValueData(AppConstants.AjaxKeys.Gender.toInt(), registerUserReq.gender)
    }
    private fun setSingle() {
        registerUserReq.maritalStatus = "0"
        setTextViewDrawableColor(binding!!.tvMarried, R.color.black)
        setTextViewDrawableColor(binding!!.tvSingle, R.color.Primary2)
        saveData()
        //saveOSRFValueData(AppConstants.AjaxKeys.Gender.toInt(), registerUserReq.gender)
    }


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            binding?.txEtFirstName?.id -> {
                if (Validator.validateEditText(binding!!.txEtFirstName)) {
                    saveOSRFValueData(
                            AppConstants.AjaxKeys.FirstName.toInt(),
                            registerUserReq.firstName
                    )
                }
                saveData()
            }
            /*binding?.txEtMiddleName?.id -> {
                if (Validator.validateEditText(binding!!.txEtMiddleName)) {
                    saveOSRFValueData(
                        AppConstants.AjaxKeys.MiddleName.toInt(),
                        registerUserReq.middleName
                    )
                }
                saveData()
            }*/
            binding?.txEtLastName?.id -> {
                if (Validator.validateEditText(binding!!.txEtLastName)) {
                    saveOSRFValueData(
                            AppConstants.AjaxKeys.LastName.toInt(),
                            registerUserReq.lastName
                    )
                }
                saveData()
            }
            binding?.txEtAlterMobile?.id -> {
                if (Validator.validatePhoneNum(binding!!.txEtAlterMobile)) {
                    saveOSRFValueData(
                            AppConstants.AjaxKeys.AltMob.toInt(),
                            registerUserReq.altMobile
                    )
                }
                saveData()
            }
            binding?.txEtAlterEmail?.id -> {
                if (Utility.isValidEmail(binding!!.txEtAlterEmail.text.toString())) {
                    saveOSRFValueData(
                            AppConstants.AjaxKeys.AltMail.toInt(),
                            registerUserReq.secEmail
                    )
                }
                saveData()
            }
            binding?.txEtSelectCity?.id -> {
                if (Utility.isValidEmail(binding!!.txEtSelectCity.text.toString())) {
                    saveOSRFValueData(
                            AppConstants.AjaxKeys.City.toInt(),
                            registerUserReq.nativeCityId
                    )
                }
                saveData()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        refreshJson()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001 && resultCode == RESULT_OK) {

            state = data?.getStringExtra("state")!!
            //area = data.getStringExtra("area")!!
            pinCode = data.getStringExtra("pin")!!
            city = data.getStringExtra("city")!!
            adr1 = data.getStringExtra("addressLine1")!!

            val adr = "$adr1,$city,$state,$pinCode"
            binding?.txEtSelectCity?.setText(adr)
            registerUserReq.nativeCityId = adr
            saveData()
        }
    }
}

package com.capitalnowapp.mobile.kotlin.activities

//import io.branch.referral.util.BranchEvent
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appsflyer.AppsFlyerLib
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.activities.LoginActivity
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.constants.Constants.ButtonType
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNButton
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityAdditionalDetailsBinding
import com.capitalnowapp.mobile.databinding.ActivityBasicDetailsBinding
import com.capitalnowapp.mobile.databinding.ActivityProfessionalDetailsBinding
import com.capitalnowapp.mobile.databinding.ActivityRegistrationHomeBinding
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback
import com.capitalnowapp.mobile.kotlin.adapters.ListFilterAdapter
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.kotlin.utils.Validator
import com.capitalnowapp.mobile.models.DepartmentJsonResponse
import com.capitalnowapp.mobile.models.DesignationJsonResponse
import com.capitalnowapp.mobile.models.GenericRequest
import com.capitalnowapp.mobile.models.MasterJsonResponse
import com.capitalnowapp.mobile.models.userdetails.RegisterUserReq
import com.capitalnowapp.mobile.models.userdetails.UserDetails
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.facebook.appevents.AppEventsLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.Locale


open class RegistrationHomeActivity : BaseActivity() {

    var masterJsonResponse: MasterJsonResponse? = null
    var departmentJsonResponse: DepartmentJsonResponse? = null
    var designationJsonResponse: DesignationJsonResponse? = null
    var dialog: AlertDialog? = null
    private lateinit var adapter: ListFilterAdapter
    private var basicDetailsActivity: BasicDetailsActivity? = null
    private var additionalDetailsActivity: AdditionalDetailsActivity? = null
    private var activityProfessionalDetails: ProfessionalDetailsActivity? = null
    var graduationYearsListKeys: ArrayList<String>? = null
    var promotionTypesMapKeys: Array<String>? = null
    var experienceListKeys: Array<String>? = null
    var modeOfPayListKeys: Array<String>? = null
    var residenceTypesMapKeys: Array<String>? = null
    private var binding: ActivityRegistrationHomeBinding? = null
    private var bindingBasic: ActivityBasicDetailsBinding? = null
    private var bindingAdditional: ActivityAdditionalDetailsBinding? = null
    private var bindingProfessional: ActivityProfessionalDetailsBinding? = null
    var registerUserReq = RegisterUserReq()
    var allCitiesList: ArrayList<MasterData>? = null
    var allCollegesList: ArrayList<MasterData>? = null
    var allCreditCardsList: ArrayList<MasterData>? = null
    private var cityMasterData: MasterData? = null
    private val ctcListMap: Map<String, String>? = null
    var promotionTypesMap: LinkedHashMap<String, String>? = null
    var experienceListMap: LinkedHashMap<String, String>? = null
    var residenceTypesMap: LinkedHashMap<String, String>? = null
    var graduationYearsListMap: LinkedHashMap<String, String>? = null
    var savedList: ArrayList<String>? = null
    var validationMsg = ""

    var state = ""
    var pinCode = ""
    var adr1 = ""
    var city = ""
    var area = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView(binding)
    }

    private fun initView(binding: ActivityRegistrationHomeBinding?) {
        try {

            val obj = JSONObject()
            try {
                obj.put("cnid",userDetails.qcId)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.registration_dashboard_landed))
            if (userDetails == null) {
                userDetails = Gson().fromJson(
                        sharedPreferences.getString(Constants.USER_DETAILS_DATA),
                        UserDetails::class.java
                )
            }

            refreshJson()
            getMasterData()
            prepareGraduationYearsList()
            getMasterJson()

            binding?.tvBack?.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            binding!!.tvLogoutRegHome.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Logout Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.registration_dashboard_interacted))

                showLogoutDialog()
            }
            binding!!.llBasic.setOnClickListener {
                val obj = JSONObject()
                try {
                    obj.put("cnid",userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Basic Details Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.registration_dashboard_interacted))
                startActivity(Intent(this, BasicDetailsActivity::class.java))
            }
            binding.llProfessional.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Professional Details Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.registration_dashboard_interacted))

                startActivity(Intent(this, ProfessionalDetailsActivity::class.java))
            }
            /*binding.cardAddDetails.setOnClickListener {
                startActivity(Intent(this, AdditionalDetailsActivity::class.java))
            }*/

            promotionTypesMap = LinkedHashMap<String, String>()
            promotionTypesMap!!["Search Engine"] = "1"
            promotionTypesMap!!["Social Network"] = "2"
            promotionTypesMap!!["Google Ads"] = "6"
            promotionTypesMap!!["Partnered Network Ads"] = "7"
            promotionTypesMap!!["Friend"] = "3"
            promotionTypesMap!!["SMS/Email"] = "4"
            if (userDetails.howYouKnowCn != null && !userDetails.howYouKnowCn.equals("") && userDetails.howYouKnowCn.equals(
                            "8"
                    )
            ) {
                promotionTypesMap!!["BankBazaar"] = "8"
            }
            promotionTypesMap!!["Others"] = "5"

            promotionTypesMapKeys = promotionTypesMap!!.keys.toTypedArray()

            experienceListMap = LinkedHashMap<String, String>()

            experienceListMap!!["00-06 Months"] = "1"
            experienceListMap!!["07-12 Months"] = "2"
            experienceListMap!!["13-18 Months"] = "3"
            experienceListMap!!["19-24 Months"] = "4"
            experienceListMap!!["25-36 Months"] = "5"
            experienceListMap!!["36+ Months"] = "6"

            experienceListKeys = experienceListMap!!.keys.toTypedArray()

            residenceTypesMap = LinkedHashMap<String, String>()

            residenceTypesMap!!["Owned By Self"] = "1"
            residenceTypesMap!!["Owned By Parent"] = "2"
            residenceTypesMap!!["Rented With Family"] = "3"
            residenceTypesMap!!["Rented With Friends"] = "4"
            residenceTypesMap!!["Rented Staying Alone"] = "5"
            residenceTypesMap!!["Guest House"] = "6"
            residenceTypesMap!!["Hostel"] = "7"
            residenceTypesMapKeys = residenceTypesMap!!.keys.toTypedArray()

            modeOfPayListKeys = arrayOf("Cash", "Cheque", "Bank Transfer")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMasterJson() {
        cnModel.getMasterJson()
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
        alertDialog.findViewById<CNTextView>(R.id.tvUserName).text = "Hi Guest"
        button.setOnClickListener {
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
            alertDialog.dismiss()
        }

        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    fun getCompanies(str: String) {
        if (str.isNotEmpty()) {
            val req = GenericRequest()
            req.dataStr = str
            val token = userToken
            cnModel.getCompanies(req, token, this)
        }
    }

    fun updateMasterJson(response: JSONObject) {
        try {
            val strJson = response.toString()
            masterJsonResponse = Gson().fromJson(strJson, MasterJsonResponse::class.java)
            if (bindingProfessional != null) {
                activityProfessionalDetails?.updateEmpType()
                activityProfessionalDetails?.updateIndustry()
                //  activityProfessionalDetails?.updateCompanyAdapter()
            }
            if (bindingAdditional != null) {
                additionalDetailsActivity?.updateLoanPurpose()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }





    fun refreshJson() {
        try {
            val json = sharedPreferences.getString(Constants.USER_REGISTRATION_DATA)
            if (json != null && json != "") {
                registerUserReq = Gson().fromJson(json, RegisterUserReq::class.java)
            } else {
                loadDataFromUser()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadDataFromUser() {
        try {
            if (userDetails == null) {
                userDetails = Gson().fromJson(
                        sharedPreferences.getString(Constants.USER_DETAILS_DATA),
                        UserDetails::class.java
                )
            }

            if (userDetails.firstName != null && !userDetails.firstName.equals("")) {
                registerUserReq.firstName = userDetails.firstName
            }
            if (userDetails.middleName != null && !userDetails.middleName.equals("")) {
                registerUserReq.middleName = userDetails.middleName
            }
            if (userDetails.lastName != null && !userDetails.lastName.equals("")) {
                registerUserReq.lastName = userDetails.lastName
            }
            if (userDetails.dob != null && !userDetails.dob.equals("")) {
                registerUserReq.dob = userDetails.dob
            }
            if (userDetails.gender != null && !userDetails.gender.equals("")) {
                registerUserReq.gender = userDetails.gender
            }
            if (userDetails.altEmail != null && !userDetails.altEmail.equals("")) {
                registerUserReq.secEmail = userDetails.altEmail
            }
            if (userDetails.altMobile != null && !userDetails.altMobile.equals("")) {
                registerUserReq.altMobile = userDetails.altMobile
            }
            if (userDetails.howYouKnowCn != null && !userDetails.howYouKnowCn.equals("")) {
                registerUserReq.refType = userDetails.howYouKnowCn
            }
            if (userDetails.empType != null && !userDetails.empType.equals("")) {
                registerUserReq.empType = userDetails.empType
            }
            if (userDetails.companyId!! > 0) {
                registerUserReq.companyId = userDetails.companyId
            }
            if (userDetails.companyName != null && !userDetails.companyName.equals("")) {
                registerUserReq.companyName = userDetails.companyName
            }
            if (userDetails.department != null && !userDetails.department.equals("")) {
                registerUserReq.department = userDetails.department
            }
            if (userDetails.designation != null && !userDetails.designation.equals("")) {
                registerUserReq.designation = userDetails.designation
            }
            if (userDetails.monthlySal != null && !userDetails.monthlySal.equals("")) {
                registerUserReq.monthlySal = userDetails.monthlySal
            }
            if (userDetails.expInMonths != null && !userDetails.expInMonths.equals("")) {
                registerUserReq.experience = userDetails.expInMonths
            }
            if (userDetails.isMarried != null && !userDetails.isMarried.equals("")) {
                registerUserReq.maritalStatus = userDetails.isMarried
            }
            if (userDetails.pan != null && !userDetails.pan.equals("")) {
                registerUserReq.panNumber = userDetails.pan
            }
            if (userDetails.gradYear != null && !userDetails.gradYear.equals("")) {
                registerUserReq.yog = userDetails.gradYear
            }
            if (userDetails.collegeId != null && !userDetails.collegeId.equals("")) {
                registerUserReq.collegeName = userDetails.collegeId
            }
            if (userDetails.residenceTypeId != null && !userDetails.residenceTypeId.equals("")) {
                registerUserReq.residence = userDetails.residenceTypeId
            }
            if (userDetails.modeOfPay != null && !userDetails.modeOfPay.equals("")) {
                registerUserReq.modeOfPay = userDetails.modeOfPay
            }
            if (userDetails.frequentlyUsedApps != null && userDetails.frequentlyUsedApps!!.isNotEmpty() && userDetails.frequentlyUsedApps!!.size > 0) {
                savedList = userDetails.frequentlyUsedApps
                registerUserReq.frequentlyUsedApps = android.text.TextUtils.join(",", savedList!!)
            }
            if (userDetails.workCityId != null && !userDetails.workCityId.equals("")) {
                registerUserReq.workCityId = userDetails.workCityId
            }
            if (userDetails.nativeCityId != null && !userDetails.nativeCityId.equals("")) {
                registerUserReq.nativeCityId = userDetails.nativeCityId
            }
            if (userDetails.industry != null && !userDetails.industry.equals("")) {
                registerUserReq.industry = userDetails.industry
            }
            if (userDetails.loanCustomPurpose != null && !userDetails.loanCustomPurpose.equals("")) {
                registerUserReq.loanPurposeCustom = userDetails.loanCustomPurpose
            }
            if (userDetails.loanPurposeId!! > 0) {
                registerUserReq.loanPurposeId = userDetails.loanPurposeId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMasterData() {
        val token = userToken
        cnModel.getMasterData(Constants.MASTER_DATA_CITIES, token)
        cnModel.getMasterData(Constants.MASTER_DATA_COLLEGE_NAMES, token)
    }

    open fun saveOSRFValueData(type: Int, value: String?) {
        try {
            val token = userToken
            if (userDetails != null && userDetails.userId != null) {
                cnModel.saveOSRFValue(userDetails.userId, type, value, token)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    open fun saveCompanyOSR(type: Int, value: String?, id: Int?) {
        try {
            val token = userToken
            if (userDetails != null && userDetails.userId != null) {
                cnModel.saveCompanyOSRF(userDetails.userId, type, value, id!!, token)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun updateMasterDataResponse(response: JSONObject, masterDataType: String) {
        if (response.getString("status") == Constants.STATUS_SUCCESS) {
            val masterDataListType = object : TypeToken<ArrayList<MasterData?>?>() {}.type
            val masterDataList = Gson().fromJson<ArrayList<MasterData>>(
                    response.getString("table_data"),
                    masterDataListType
            )

            try {
                when (masterDataType) {
                    Constants.MASTER_DATA_COLLEGE_NAMES -> {
                        allCollegesList = ArrayList()
                        allCollegesList?.addAll(masterDataList)
                        if (bindingAdditional != null) {
                            additionalDetailsActivity?.updateCollege()
                        }
                    }
                    Constants.MASTER_DATA_CREDIT_CARD_TYPES -> {
                        allCreditCardsList = ArrayList()
                        allCreditCardsList?.addAll(masterDataList)
                        if (bindingAdditional != null) {
                            additionalDetailsActivity?.updateCard()
                        }
                    }
                    Constants.MASTER_DATA_CITIES -> {
                        try {
                            allCitiesList = ArrayList()
                            allCitiesList?.addAll(masterDataList)
                            if (bindingBasic != null) {
                                //   basicDetailsActivity?.updateCity()
                            }

                            if (bindingProfessional != null) {
                                activityProfessionalDetails?.updateCity()
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun prepareGraduationYearsList() {
        var startingYear = 2021
        graduationYearsListKeys = ArrayList()
        do {
            graduationYearsListKeys!!.add(startingYear.toString())
            startingYear--
        } while (startingYear > 2000)
        graduationYearsListMap = LinkedHashMap<String, String>()
        graduationYearsListMap!!["Select"] = "0"
        var totalYears: Int = graduationYearsListKeys!!.size
        for (i in graduationYearsListKeys!!.indices) {
            graduationYearsListMap!![graduationYearsListKeys!![i]] = totalYears.toString()
            totalYears--
        }
        graduationYearsListMap!!["Others"] = "99"
        graduationYearsListKeys!!.add("Others")
    }

    open fun updateSaveOSRFValueResponse(status: String, message: String?, fieldType: Int) {
        if (status == Constants.STATUS_SUCCESS) {
            //Value saved successfully in database & we are not showing any ack.
        } else {
            // We are showing error message only for Mobile & PAN if they are duplicate.

            // We are showing error message only for Mobile & PAN if they are duplicate.
            showAlertDialog(message)
            when (fieldType) {
                AppConstants.AjaxKeys.AltMob.toInt() -> {
                    bindingBasic?.txEtAlterMobile?.setText("")
                    registerUserReq.altMobile = ""
                }
                AppConstants.AjaxKeys.FirstName.toInt() -> {
                    bindingBasic?.txEtFirstName?.setText("")
                    registerUserReq.firstName = ""
                }
                /*AppConstants.AjaxKeys.MiddleName.toInt() -> {
                    bindingBasic?.txEtMiddleName?.setText("")
                    registerUserReq.middleName = ""
                }*/
                AppConstants.AjaxKeys.LastName.toInt() -> {
                    bindingBasic?.txEtLastName?.setText("")
                    registerUserReq.lastName = ""
                }
                AppConstants.AjaxKeys.PanNum.toInt() -> {
                    bindingAdditional?.txEtPanNumber?.setText("")
                    registerUserReq.panNumber = ""
                }
                AppConstants.AjaxKeys.AltMail.toInt() -> {
                    bindingBasic?.txEtAlterEmail?.setText("")
                    registerUserReq.secEmail = ""
                }
            }
        }
    }

    fun setBasicBinding(
            binding: ActivityBasicDetailsBinding,
            basicDetailsActivity: BasicDetailsActivity
    ) {
        bindingBasic = binding
        this.basicDetailsActivity = basicDetailsActivity
    }

    fun setAdditionalBinding(
            binding: ActivityAdditionalDetailsBinding,
            additionalDetailsActivity: AdditionalDetailsActivity
    ) {
        this.additionalDetailsActivity = additionalDetailsActivity
        bindingAdditional = binding
    }

    fun setProfessionBinding(
            binding: ActivityProfessionalDetailsBinding,
            activityProfessionalDetails: ProfessionalDetailsActivity
    ) {
        this.activityProfessionalDetails = activityProfessionalDetails
        bindingProfessional = binding
    }

    fun isBasicFilled(): Int {
        var count = 0
        if (registerUserReq.firstName?.trim()?.length!! < 2) {
            count++
            validationMsg = getString(R.string.first_name_validation_msg)
        }
        if (registerUserReq.lastName?.trim()?.length!! < 2) {
            count++
            validationMsg = getString(R.string.last_name_validation_msg)
        }
        if (!Validator.isValidInputDOB(registerUserReq.dob.toString())) {
            count++
            validationMsg = getString(R.string.dob_validation_msg)
        }
        if (registerUserReq.gender?.isEmpty()!!) {
            count++
            validationMsg = getString(R.string.gender_validation_msg)
        }
        if (registerUserReq.maritalStatus?.isEmpty()!!) {
            count++
            validationMsg = getString(R.string.marital_status_validation_msg)
        }
        if (!Utility.isValidEmail(registerUserReq.secEmail)) {
            count++
            validationMsg = getString(R.string.alt_email_validation_msg)
        }
        if (registerUserReq.altMobile?.trim()?.length!! != 10) {
            count++
            validationMsg = getString(R.string.alt_mobile_validation_msg)
        }
        if (registerUserReq.refType?.isEmpty()!! || registerUserReq.refType?.length!! < 1) {
            count++
            validationMsg = getString(R.string.promotion_validation_msg)
        }
        if (registerUserReq.residence?.isEmpty()!! || registerUserReq.residence?.length!! < 0) {
            count++
            validationMsg = getString(R.string.residence_validation_msg)
        }
        if (registerUserReq.nativeCityId?.isEmpty()!! || registerUserReq.nativeCityId?.length!! < 3) {
            count++
            validationMsg = getString(R.string.location_validation_msg)
        }
        if (userDetails != null && userDetails.secEmailVerified != null && userDetails.secEmailVerified!!.isEmpty() || userDetails.secEmailVerified.equals("0")) {
            count++
            validationMsg = "Please verify alternate email"
        }
        return count
    }

    fun isProfessionalFilled(): Int {
        var count = 0
        if (registerUserReq.empType?.isEmpty()!! && registerUserReq.empType?.length!! < 1 ) {
            count++
            validationMsg = getString(R.string.employement_type_validation_msg)
        }
        if (registerUserReq.companyName?.trim()?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.company_validation_msg)
        }
        if (registerUserReq.department?.trim()?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.department_validation_msg)
        }
        if (registerUserReq.designation?.trim()?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.designation_validation_msg)
        }
        if (registerUserReq.monthlySal!!.isEmpty() || registerUserReq.monthlySal?.length!! <= 2 || registerUserReq.monthlySal!!.trim()
                        .substring(0, 1) == "0"
        ) {
            count++
            validationMsg = getString(R.string.salary_validation_msg)
        }
        if (registerUserReq.modeOfPay?.isEmpty()!! && registerUserReq.modeOfPay?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.salary_mode_validation_msg)
        }
        if (registerUserReq.workCityId.isNullOrEmpty() || registerUserReq.workCityId?.length!! < 1 || registerUserReq.workCityId.equals(
                        "0"
                )
        ) {
            count++
            validationMsg = getString(R.string.city_validation_msg)
        }
        if (registerUserReq.experience?.isEmpty()!! && registerUserReq.experience?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.experience_validation_msg)
        }
        if (registerUserReq.industry?.isEmpty()!! && registerUserReq.industry?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.industry_validation_msg)
        }
        return count
    }

    fun isAdditionalFilled(): Int {
        var count = 0
        if (registerUserReq.panNumber?.trim()?.length!! != 10) {
            count++
            validationMsg = getString(R.string.pan_validation_msg)
        }
        if (registerUserReq.yog?.isEmpty()!! && registerUserReq.yog?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.graduation_year_validation_msg)
        }
        if (registerUserReq.collegeName?.isEmpty()!! && registerUserReq.collegeName?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.college_validation_msg)
        }
        if (registerUserReq.residence?.isEmpty()!! && registerUserReq.residence?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.residence_validation_msg)
        }
        if (registerUserReq.maritalStatus?.isEmpty()!! && registerUserReq.maritalStatus?.length!! <= 1) {
            count++
            validationMsg = getString(R.string.marital_status_validation_msg)
        }
        return count
    }

    fun validateApps(): Boolean {
        if (savedList != null && savedList!!.size >= 4) {
            registerUserReq.frequentlyUsedApps = android.text.TextUtils.join(",", savedList!!)
            return true
        }
        return false
    }

    fun saveData() {
        sharedPreferences.putString(
                Constants.USER_REGISTRATION_DATA,
                Gson().toJson(registerUserReq)
        )
    }
    fun getKeyFromValue(hm: Map<*, *>, value: Any): Any? {
        for (o in hm.keys) {
            if (hm[o] == value) {
                return o
            }
        }
        return ""
    }

    fun setTextViewDrawableColor(textView: RadioButton, color: Int) {
        textView.setTextColor(color)
    }

    fun submitData() {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
            val token = userToken
            cnModel.saveOneTimeRegistration(userDetails.userId, registerUserReq, userDetails, token)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun updateOneStepRegistrationResponse(
            user_id: String?,
            user_status_id: String?,
            message: String?, showPopup: Int?
    ) {

        adgydeCounting(getString(R.string.user_registration_completed))
        CNProgressDialog.hideProgressDialog()
        sharedPreferences.putString(Constants.USER_REGISTRATION_DATA, null)

        //  AdGyde.onSimpleEvent(getString(R.string.user_approved))
        val params = HashMap<String, Any>()
        val key = getString(R.string.user_approved)
        params[key] =
                getString(R.string.user_approved) //patrametre name,value change to event
        AppsFlyerLib.getInstance().logEvent(this@RegistrationHomeActivity, key, params)

        val logger = AppEventsLogger.newLogger(this@RegistrationHomeActivity)
        logger.logEvent(getString(R.string.user_approved), Bundle())

        val obj = JSONObject()
        try {
            obj.put("cnid",userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.user_approved))

        /*BranchEvent("UserApproved")
                .addCustomDataProperty("UserApproved", "User_Approved")
                .setCustomerEventAlias("User_Approved")
                .logEvent(this@RegistrationHomeActivity)*/

        if (user_status_id.equals("12")) {
            if (showPopup == 1) {
                showAlert(message, user_id)
            } else {
                getProfile(user_id)
            }
        } else {
            showAlert(message, user_id)
        }
    }

    private fun showAlert(message: String?, user_id: String?) {
        CNAlertDialog()
        CNAlertDialog.setRequestCode(Constants.ALERT_DIALOG_REQUEST_CODE_COMMON)
        CNAlertDialog.showMaterialAlertDialog(
                this,
                "",
                message,
                R.drawable.loan_success_icon,
                false,
                R.color.pop_up_color
        )
        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {
                getProfile(user_id)
            }

            override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {}
        })

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

            override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {}
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

    open fun adgydeCounting(value: String) {
        val params = HashMap<String, Any>()
        val key = getString(R.string.registration_key)
        params[key] = value //patrametre name,value change to event
        AppsFlyerLib.getInstance().logEvent(this, key, params)

        val logger = AppEventsLogger.newLogger(this)
        val bundle = Bundle()
        bundle.putString(key, value)
        logger.logEvent(getString(R.string.registration_key), bundle)

        //Mixpanel passing parmas 
        val obj = JSONObject()
        obj.put("cnid",userDetails.qcId)
        TrackingUtil.pushEvent(obj, getString(R.string.user_registered))

        /*BranchEvent("UserRegistration")
                .addCustomDataProperty("UserRegistration", "User_Registration")
                .setCustomerEventAlias("User_Registration")
                .logEvent(this@RegistrationHomeActivity)*/

    }

    fun showCodesDialog(codeArrayList: ArrayList<MasterData>, activity: Activity) {
        try {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.filter_dialog, null)
            val rvData: RecyclerView = view.findViewById(R.id.rvData)
            rvData.layoutManager = LinearLayoutManager(this)
            val etSearchCode = view.findViewById<EditText>(R.id.etSearch)
            etSearchCode.hint = getString(R.string.search_city)

            adapter = ListFilterAdapter(this, codeArrayList, SelectedIdCallback { selectedId ->
                try {
                    val data = codeArrayList[selectedId.toInt().minus(1)]
                    if (activity is BasicDetailsActivity) {
                        bindingBasic?.txEtSelectCity?.setText(data.name)
                        registerUserReq.nativeCityId = data.id
                        saveOSRFValueData(
                                AppConstants.AjaxKeys.NativeCity.toInt(),
                                registerUserReq.nativeCityId
                        )
                        saveData()
                    } else if (activity is ProfessionalDetailsActivity) {
                        bindingProfessional?.txEtCity?.setText(data.name)
                        registerUserReq.workCityId = data.id
                        /*bindingProfessional?.cbSameCity?.isChecked =
                            registerUserReq.nativeCityId == registerUserReq.workCityId
                        saveOSRFValueData(
                            AppConstants.AjaxKeys.City.toInt(),
                            registerUserReq.workCityId
                        )*/
                        saveData()
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

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s != "") {
                        filterData(s, codeArrayList)
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
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

    fun filterData(s: CharSequence, countryCodeArrayList: java.util.ArrayList<MasterData>) {
        val filterList: java.util.ArrayList<MasterData> = java.util.ArrayList<MasterData>()
        for (i in countryCodeArrayList.indices) {
            val item: MasterData = countryCodeArrayList[i]
            if (item.name.toLowerCase(Locale.ROOT).contains(s)) {
                filterList.add(item)
            }
        }
        adapter.updateList(filterList)
    }

    fun designDialog(builder: AlertDialog.Builder) {
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
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog?.window!!.attributes = layoutParams
    }

    fun updateCompanies(
            response: JSONArray,
            activity: Activity
    ) {
        val companies: ArrayList<MasterData>
        val listType: Type = object : TypeToken<List<MasterData?>>() {}.type
        companies = Gson().fromJson(response.toString(), listType)
        if (activity is CompanyListActivity) {
            activity.updateList(companies)
        } else {
            if (bindingProfessional != null) {
                activityProfessionalDetails?.updateCompanyAdapter(companies)
            }
        }
    }
}


package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Selection
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityReg1Binding
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback
import com.capitalnowapp.mobile.kotlin.adapters.CompanyNameAdapter1
import com.capitalnowapp.mobile.kotlin.adapters.ListFilterAdapter
import com.capitalnowapp.mobile.kotlin.utils.Validator
import com.capitalnowapp.mobile.models.ApplyLoanServiceDataReq
import com.capitalnowapp.mobile.models.ApplyLoanServiceDataResponse
import com.capitalnowapp.mobile.models.DepartmentJsonResponse
import com.capitalnowapp.mobile.models.DesignationJsonResponse
import com.capitalnowapp.mobile.models.GenericRequest
import com.capitalnowapp.mobile.models.MasterJsonResponse
import com.capitalnowapp.mobile.models.Registrations.DesignationData
import com.capitalnowapp.mobile.models.Registrations.DesignationListResponse
import com.capitalnowapp.mobile.models.Registrations.GetDesignationListReq
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationOneReq
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationOneResponse
import com.capitalnowapp.mobile.models.userdetails.UserDetails
import com.capitalnowapp.mobile.models.userdetails.UserDetailsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_professional_details.tvSearching
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class Reg1Activity : BaseActivity(), View.OnFocusChangeListener {
    private var applyLoanServiceDataResponse: ApplyLoanServiceDataResponse? = null
    private var saveRegistrationResponse: SaveRegistrationOneResponse? = null
    var masterJsonResponse: MasterJsonResponse? = null
    var departmentJsonResponse: DepartmentJsonResponse? = null
    var designationJsonResponse: DesignationJsonResponse? = null
    private var userDetailsResponse: UserDetailsResponse? = null
    private var binding: ActivityReg1Binding? = null
    private var currentDate: String = ""
    private var isFormatting = false
    private var deletingHyphen = false
    private var hyphenStart = 0
    private var deletingBackward = false
    private var DOB: String? = ""
    private var page: String? = ""
    var promotionTypesMapKeys: Array<String>? = null
    var promotionTypesMap: LinkedHashMap<String, String>? = null
    private var empTypesArray: Array<CharSequence?>? = null
    var GenderMap: LinkedHashMap<String, String>? = null
    var GenderMapKeys: Array<String>? = null
    var modeOfPayListKeys: Array<String>? = null
    private var panNumber: String? = ""
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var monthlySalary: String? = ""
    private var employmentType: String? = ""
    private var pinCode: String? = ""
    private var permanentPinCode: String? = ""
    private var officePinCode: String? = ""
    private var howDoYouKnow: String? = ""
    private var companyName: String? = ""
    private var companyId: Int? = -1
    private var gender: String? = ""
    private var modeOfSalary: String? = ""
    private var firstTimeLoad = true
    private var selectedCompany: MasterData? = null
    var validationMsg = ""
    private var activity: AppCompatActivity? = null
    var canRedirect: Boolean = false

    private var industryArray: Array<CharSequence?>? = null
    private var departmentArray: Array<CharSequence?>? = null
    private var designationArray: Array<CharSequence?>? = null
    private var industry: String? = ""
    private var department: String? = ""
    private var designation: String? = ""
    var dialog: AlertDialog? = null

    private var designationList: ArrayList<DesignationData>? = ArrayList()
    private var designationMasterList: ArrayList<MasterData>? = ArrayList()
    private var selectedDesignationId: String? = null
    private var selectedDesignationName: String? = null
    private lateinit var adapter: ListFilterAdapter

    @kotlin.jvm.JvmField
    var fromSelection: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReg1Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        try {
            getProfile(userId)
            getMasterJson()
            getMasterJson2()
            getDepartmentJson()
            //getDesignationJson()
            getDesignationList()
            fromSelection = false
            currentDate = Utility.formatDate(Date(), Constants.DOB_DATE_FORMAT)

            binding?.etIndType?.setOnClickListener {
                if (masterJsonResponse != null && masterJsonResponse?.industries != null && masterJsonResponse?.industries?.size!! > 0) {
                    showIndustryDialog()
                }
            }
            binding?.etDepartment?.setOnClickListener {
                if (departmentJsonResponse != null && departmentJsonResponse?.department != null && departmentJsonResponse?.department?.size!! > 0) {
                    showDepartmentDialog()
                }
            }
            binding?.etSearchDesignation?.setOnClickListener {
                if (designationMasterList != null && designationMasterList?.isNotEmpty()!!) {
                    showCodesDialog(designationMasterList!!)
                }
            }

            binding?.etCompanyName?.onFocusChangeListener = this

            binding?.etCompanyName?.addTextChangedListener(
                object : TextWatcher {
                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (!firstTimeLoad) {
                            tvSearching.visibility = View.VISIBLE
                        } else {
                            firstTimeLoad = false
                            tvSearching.visibility = View.GONE
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    private var timer = Timer()
                    private val DELAY: Long = 2000 // Milliseconds
                    override fun afterTextChanged(s: Editable) {
                        if (binding?.etCompanyName?.hasFocus()!!) {
                            if (s.toString().length >= 3 && !fromSelection) {
                                if (s.toString().isEmpty()) {
                                    tvSearching.visibility = View.GONE
                                }
                                companyName = s.toString()
                                timer.cancel()
                                timer = Timer()
                                timer.schedule(
                                    object : TimerTask() {
                                        override fun run() {
                                            getCompanies(s.toString())
                                        }
                                    },
                                    DELAY
                                )
                            } else {
                                tvSearching.visibility = View.GONE
                                fromSelection = false
                                selectedCompany = null
                                /*val companiesAdapter =
                                    CompanyNameAdapter(
                                        this@ProfessionalDetailsActivity,
                                        R.layout.item_company_name,
                                        ArrayList()
                                    )
                                binding.txEtCompanyName.setAdapter(companiesAdapter)*/
                            }
                        }
                    }
                }
            )

            binding?.etDOB?.addTextChangedListener(object : TextWatcher {

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
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
                        //do something
                        DOB = text.toString()
                    } else {
                        //do something
                    }
                }
            })
            binding?.etDOB?.setOnClickListener {
                showDatePicker(binding!!.etDOB)
            }

            modeOfPayListKeys = arrayOf("Direct Bank Transfer", "Cheque", "Cash")

            promotionTypesMap = LinkedHashMap<String, String>()
            promotionTypesMap!!["Search Engine"] = "Search Engine"
            promotionTypesMap!!["Social Network"] = "Social Network"
            promotionTypesMap!!["Social Media - Instagram"] = "Social Media - Instagram"
            promotionTypesMap!!["Google Ads"] = "Google Ads"
            promotionTypesMap!!["Partnered Network Ads"] = "Partnered Network Ads"
            promotionTypesMap!!["Friend"] = "Friend"
            promotionTypesMap!!["SMS/Email"] = "SMS/Email"
            promotionTypesMap!!["Others"] = "Others"
            promotionTypesMapKeys = promotionTypesMap!!.keys.toTypedArray()

            binding?.etHowDoYouKnow?.setOnClickListener {
                if (promotionTypesMapKeys != null && promotionTypesMapKeys!!.isNotEmpty()) {
                    showPromotionDialog()
                }
            }

            binding?.etEmpType?.setOnClickListener {
                if (masterJsonResponse != null && masterJsonResponse?.employmentTypes != null && masterJsonResponse?.employmentTypes?.size!! > 0) {
                    showEmpTypesDialog()
                }
            }

            GenderMap = LinkedHashMap<String, String>()
            GenderMap!!["Male"] = "1"
            GenderMap!!["Female"] = "0"
            GenderMapKeys = GenderMap!!.keys.toTypedArray()

            binding?.etGender?.setOnClickListener {
                if (GenderMapKeys != null && GenderMapKeys!!.isNotEmpty()) {
                    showGenderDialog()
                }
            }



            binding?.etPAN?.addTextChangedListener(object : TextWatcher {
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
                    if (s?.length == 10 && Validator.isValidPanNumber(s.toString())) {
                        panNumber = s.toString()

                        /*val obj = JSONObject()
                        try {
                            obj.put("cnid",userDetails.qcId)
                        } catch (e: JSONException) {
                            throw RuntimeException(e)
                        }
                        TrackingUtil.pushEvent(obj, getString(R.string.pan_card_number_entered))*/

                        //uploadPan()

                    } else if (s?.length!! > 0) {
                        panNumber = ""
                    } else {
                        panNumber = ""
                    }
                    binding?.etPAN?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            })

            binding?.etFirstName?.addTextChangedListener(object : TextWatcher {
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
                    firstName = s.toString()
                }
            })

            binding?.etLastName?.addTextChangedListener(object : TextWatcher {
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
                    lastName = s.toString()

                }
            })

            binding?.etMonthlySalary?.addTextChangedListener(object : TextWatcher {
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
                    monthlySalary = s.toString()
                }
            })

            binding?.etPincode?.addTextChangedListener(object : TextWatcher {
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
                    pinCode = s.toString()
                }
            })
            binding?.etPerPincode?.addTextChangedListener(object : TextWatcher {
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
                    permanentPinCode = s.toString()
                }
            })
            binding?.etOfficePincode?.addTextChangedListener(object : TextWatcher {
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
                    officePinCode = s.toString()
                }
            })


            binding?.etModeOfSalary?.setOnClickListener {
                if (modeOfPayListKeys != null && modeOfPayListKeys!!.isNotEmpty()) {
                    showModeOfSalaryDialog()
                }
            }

            val string1 = "By continuing the application process, I hereby agree/authorize the following:"
            val string2 = "1. Capital Now <a href=\"https://api.capitalnow.in/mpage/terms-and-conditions\"><b><font>Terms &amp; Conditions</font></b></a>"
            val string3 = "2. I am an Indian citizen and the PAN card number shared above belongs to me."
            val string4 = "3. I allow CAPITAL NOW and its lending partners to access my credit information."

            //binding?.tvPrivacyLink?.text = Html.fromHtml("By continuing the application process, I hereby agree/authorize the following: \n 1. Capital Now application <a href=\"https://api.capitalnow.in/mpage/terms-and-conditions\"><b><font>Terms &amp; Conditions</font></b></a> \n 2. I am an Indian citizen and the PAN card number shared above belongs to me. \n 3. I allow CAPITAL NOW and its lending partners to access my credit information.")
            binding?.tvPrivacyLink?.text = Html.fromHtml("$string1<br>$string2<br>$string3<br>$string4")
            binding?.tvPrivacyLink?.movementMethod = LinkMovementMethod.getInstance()
            binding?.tvProceed?.setOnClickListener {
                validateFields()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showCodesDialog(designationMasterList: ArrayList<MasterData>) {
        try{
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(com.capitalnowapp.mobile.R.layout.filter_dialog, null)
            val rvData: RecyclerView = view.findViewById(com.capitalnowapp.mobile.R.id.rvData)
            val tvCustom: TextView = view.findViewById(com.capitalnowapp.mobile.R.id.tvCustom)
            rvData.layoutManager = LinearLayoutManager(this)
            val etSearchCode = view.findViewById<EditText>(com.capitalnowapp.mobile.R.id.etSearch)
            etSearchCode.hint = "Search Designation"
           /* tvCustom.setOnClickListener {

                    selectedDesignationId = ""
                    selectedDesignationName = tvCustom.text.toString().trim()
                    binding?.etSearchDesignation?.setText(selectedDesignationName)

                dialog?.dismiss()
            }*/
            adapter = ListFilterAdapter(this, designationMasterList, SelectedIdCallback { selectedId ->
                for (item in 0 until designationList?.size!!) {
                    if (designationList!![item].id == selectedId) {
                        binding!!.etSearchDesignation.setText(designationList!![item].name)
                        selectedDesignationId = designationList!![item].id
                        selectedDesignationName = designationList!![item].name
                    }
                }
                dialog?.dismiss()
            })
            rvData.adapter = adapter
            filterData("", java.util.ArrayList(), tvCustom)
            etSearchCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        if (s != "" && s.length >= 2) {
                            filterData(s, designationMasterList, tvCustom)
                        }else{
                            filterData(s, ArrayList(), tvCustom)
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


        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun filterData(
        s: CharSequence,
        countryCodeArrayList: java.util.ArrayList<MasterData>,
        tvCustom: TextView
    ) {
        val filterList: java.util.ArrayList<MasterData> = java.util.ArrayList<MasterData>()
        for (i in countryCodeArrayList.indices) {
            val item: MasterData = countryCodeArrayList[i]
            if (item.name.lowercase(Locale.ROOT).contains(s.toString().lowercase(Locale.ROOT))) {
                filterList.add(item)
            }
        }
        adapter.updateList(filterList)
        if (filterList.size == 0) {
            tvCustom.visibility = View.GONE
            tvCustom.text = s
        } else {
            tvCustom.visibility = View.GONE
            tvCustom.text = ""
        }
    }

    private fun parseDesignationList(designationData: List<DesignationData>?) {
        if (designationData?.isNotEmpty()!!) {
            designationList = java.util.ArrayList()
            designationMasterList = java.util.ArrayList()
            for (item in designationData) {
                val masterData = MasterData()
                masterData.id = item.id
                masterData.name = item.name
                designationMasterList?.add(masterData)
                designationList?.addAll(designationData)
            }
        }
    }

    fun updateDepartmentJson(response: JSONObject) {
        try {
            val strJson = response.toString()
            departmentJsonResponse = Gson().fromJson(strJson, DepartmentJsonResponse::class.java)
            updateDepartmentType()
            //  activityProfessionalDetails?.updateCompanyAdapter()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateDepartmentType() {
        if (userDetailsResponse?.userDetails?.department != null && !userDetailsResponse?.userDetails?.department.equals("")) {
            binding?.etDepartment?.setText(userDetailsResponse?.userDetails?.department)
        }
    }

    private fun showIndustryDialog() {
        industryArray = arrayOfNulls(masterJsonResponse?.industries!!.size)
        for (i in 0 until masterJsonResponse?.industries!!.size) {
            industryArray!![i] =
                masterJsonResponse?.industries?.get(i) // Whichever string you wanna store here from custom object
        }

        val builder = AlertDialog.Builder(this)
        builder.setItems(industryArray) { _, which ->
            binding?.etIndType?.setText(industryArray!![which])
            industry = masterJsonResponse?.industries!![which]

        }
        designDialog(builder)
    }

    private fun showDepartmentDialog() {
        departmentArray = arrayOfNulls(departmentJsonResponse?.department!!.size)
        for (i in 0 until departmentJsonResponse?.department!!.size) {
            departmentArray!![i] =
                departmentJsonResponse?.department?.get(i) // Whichever string you wanna store here from custom object
        }

        val builder = AlertDialog.Builder(this)
        builder.setItems(departmentArray) { _, which ->
            binding?.etDepartment?.setText(departmentArray!![which])
            department = departmentJsonResponse?.department!![which]

        }
        designDialog(builder)
    }

    fun updateIndustry() {
        if (userDetailsResponse?.userDetails?.industry != null && !userDetailsResponse?.userDetails?.industry.equals("")) {
            binding?.etIndType?.setText(userDetailsResponse?.userDetails?.industry)
        }
    }
    private fun showDesignationDialog() {
        designationArray = arrayOfNulls(designationJsonResponse?.designation!!.size)
        for (i in 0 until designationJsonResponse?.designation!!.size) {
            designationArray!![i] =
                designationJsonResponse?.designation?.get(i) // Whichever string you wanna store here from custom object
        }
        val builder = AlertDialog.Builder(this)
        builder.setItems(designationArray) { _, which ->
            binding?.etSearchDesignation?.setText(designationArray!![which])
            designation = designationJsonResponse?.designation!![which]

        }
        designDialog(builder)
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


    @SuppressLint("SuspiciousIndentation")
    fun updateDesignationJson(response: JSONObject) {
        try {
            val strJson = response.toString()
            designationJsonResponse = Gson().fromJson(strJson, DesignationJsonResponse::class.java)

            updateDesignationType()
            //  activityProfessionalDetails?.updateCompanyAdapter()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateDesignationType() {
        if (userDetailsResponse?.userDetails?.designation != null && !userDetailsResponse?.userDetails?.designation.equals("")) {
            binding?.etSearchDesignation?.setText(userDetailsResponse?.userDetails?.designation)
        }
    }

    fun getCompanies(str: String) {
        if (str.isNotEmpty()) {
            val req = GenericRequest()
            req.dataStr = str
            val token = userToken
            cnModel.getCompanies(req, token, this)
        }
    }


    private fun getMasterJson2() {
        cnModel.getMasterJson2()
    }

    private fun getDepartmentJson() {
        cnModel.getDepatmentJson()
    }

    private fun getDesignationJson() {
        cnModel.getDesignationJson()
    }

    @SuppressLint("SuspiciousIndentation")
    fun updateMasterJson(response: JSONObject) {
        try {
            val strJson = response.toString()
            masterJsonResponse = Gson().fromJson(strJson, MasterJsonResponse::class.java)

            updateEmpType()
            updateIndustry()
            //  activityProfessionalDetails?.updateCompanyAdapter()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMasterJson() {
        cnModel.getMasterJson()
    }

    fun updateEmpType() {
        if (masterJsonResponse?.employmentTypes != null && masterJsonResponse?.employmentTypes!!.size > 0) {
            for (obj in masterJsonResponse?.employmentTypes!!) {
                if (obj.key == userDetailsResponse?.userDetails?.empType) {
                    binding?.etEmpType?.setText(obj.value)
                }
            }
        }
    }


    private fun validateFields() {
        try {
            firstName = binding?.etFirstName?.text.toString().trim { it <= ' ' }
            lastName = binding?.etLastName?.text.toString().trim { it <= ' ' }
            panNumber = binding?.etPAN?.text.toString().trim { it <= ' ' }
            DOB = binding?.etDOB?.text.toString().trim { it <= ' ' }
            gender = binding?.etGender?.text.toString().trim { it <= ' ' }
            industry = binding?.etIndType?.text.toString().trim { it <= ' ' }
            department = binding?.etDepartment?.text.toString().trim { it <= ' ' }
            designation = binding?.etSearchDesignation?.text.toString().trim { it <= ' ' }
            companyName = binding?.etCompanyName?.text.toString().trim { it <= ' ' }
            monthlySalary = binding?.etMonthlySalary?.text.toString().trim { it <= ' ' }
            modeOfSalary = binding?.etModeOfSalary?.text.toString().trim { it <= ' ' }
            employmentType = binding?.etEmpType?.text.toString().trim { it <= ' ' }
            pinCode = binding?.etPincode?.text.toString().trim { it <= ' ' }
            permanentPinCode = binding?.etPerPincode?.text.toString().trim { it <= ' ' }
            officePinCode = binding?.etOfficePincode?.text.toString().trim { it <= ' ' }
            howDoYouKnow = binding?.etHowDoYouKnow?.text.toString().trim { it <= ' ' }
            var count = 0
            if (firstName!!.isEmpty() || firstName!!.length < 2) {
                validationMsg = "First Name needs at least 2 characters."
                count++
            } else if (lastName!!.isEmpty() || lastName!!.length < 2) {
                validationMsg = "Last Name needs at least of 2 characters."
                count++
            } else if (gender!!.isEmpty()) {
                validationMsg = "Select Gender."
                count++
            }else if (industry!!.isEmpty()) {
                validationMsg = "Select Industry Type."
                count++
            }else if (department!!.isEmpty()) {
                validationMsg = "Select Department."
                count++
            }else if (designation!!.isEmpty()) {
                validationMsg = "Select Designation."
                count++
            } else if (panNumber!!.isEmpty() || panNumber!!.length < 10) {
                validationMsg = "PAN Number is required."
                count++
            } else if (DOB!!.isEmpty()) {
                validationMsg = "Enter a valid Date of Birth."
                count++
            } else if (companyName!!.isEmpty()) {
                validationMsg = "Select Company Name."
                count++
            } else if (monthlySalary!!.isEmpty()) {
                validationMsg = "Enter valid salary."
                count++
            } else if (modeOfSalary!!.isEmpty()) {
                validationMsg = "Select Mode of Salary."
                count++
            } else if (employmentType!!.isEmpty()) {
                validationMsg = "Select Employment Type."
                count++
            } else if (pinCode!!.isEmpty()) {
                validationMsg = "Present Pin code is required."
                count++
            }else if (permanentPinCode!!.isEmpty()) {
                validationMsg = "Permanent Pin code is required."
                count++
            } else if (howDoYouKnow!!.isEmpty()) {
                validationMsg = "Select How do you know about CapitalNow?"
                count++
            }else if(binding?.cbAgreeTerms?.isChecked == false){
                validationMsg = "Agree to the consent"
                count++
            }
            if (count > 0) {
                Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
            } else {
                saveRegistrationOne()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDesignationList() {
        try {
            val genericAPIService = GenericAPIService(activity, 0)
            val getDesignationListReq = GetDesignationListReq()
            getDesignationListReq.requestInput = ""
            val token = userToken
            genericAPIService.getDesignationList(getDesignationListReq, token)
            Log.d("req", Gson().toJson(getDesignationListReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
               val getDesignationListResponse = Gson().fromJson(
                    responseBody,
                    DesignationListResponse::class.java
                )
                if (getDesignationListResponse != null && getDesignationListResponse!!.status == "success") {
                    parseDesignationList(getDesignationListResponse.designationData)
                } else {

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

    private fun saveRegistrationOne() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val saveRegistrationOneReq = SaveRegistrationOneReq()
            saveRegistrationOneReq.firstName = firstName
            saveRegistrationOneReq.lastName = lastName
            saveRegistrationOneReq.gender = gender
            saveRegistrationOneReq.dob = DOB
            saveRegistrationOneReq.pancardNo = panNumber
            saveRegistrationOneReq.employementType = employmentType
            saveRegistrationOneReq.companyName = companyName
            saveRegistrationOneReq.industryType = industry
            saveRegistrationOneReq.department = department
            saveRegistrationOneReq.designation = selectedDesignationName
            saveRegistrationOneReq.companyId = companyId
            saveRegistrationOneReq.monthlySalary = monthlySalary
            saveRegistrationOneReq.modeOfPay = modeOfSalary
            saveRegistrationOneReq.presentPincode = pinCode
            saveRegistrationOneReq.permanentPincode = permanentPinCode
            saveRegistrationOneReq.officePincode = officePinCode
            saveRegistrationOneReq.howYouKnowCn = howDoYouKnow
            saveRegistrationOneReq.pageNo = "34"
            val token = userToken
            genericAPIService.saveRegistrationOne(saveRegistrationOneReq, token)
            Log.d("req", Gson().toJson(saveRegistrationOneReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                saveRegistrationResponse = Gson().fromJson(
                    responseBody,
                    SaveRegistrationOneResponse::class.java
                )
                if (saveRegistrationResponse != null && saveRegistrationResponse!!.status == true) {
                    getApplyLoanData(true)
                } else {
                    if (saveRegistrationResponse!!.statusCode == Constants.STATUS_CODE_UNAUTHORISED) {
                        (activity as BaseActivity).logout()

                    }else {
                        CNAlertDialog.showAlertDialog(
                            this,
                            resources.getString(R.string.title_alert),
                            saveRegistrationResponse!!.message
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


    private fun showEmpTypesDialog() {
        empTypesArray = arrayOfNulls(masterJsonResponse?.employmentTypes!!.size)
        for (i in 0 until masterJsonResponse?.employmentTypes!!.size) {
            empTypesArray!![i] =
                masterJsonResponse?.employmentTypes?.get(i)?.value // Whichever string you wanna store here from custom object
        }

        val builder = AlertDialog.Builder(this)
        builder.setItems(empTypesArray) { _, which ->
            binding?.etEmpType?.setText(empTypesArray!![which])
            employmentType = masterJsonResponse?.employmentTypes!![which].key
        }
        builder.show()
    }

    private fun showModeOfSalaryDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(modeOfPayListKeys) { _, which ->
            binding?.etModeOfSalary?.setText(modeOfPayListKeys?.get(which))
            modeOfSalary = modeOfPayListKeys?.get((which))
        }
        builder.show()
    }

    private fun showGenderDialog() {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setItems(GenderMapKeys) { _, which ->
                binding?.etGender?.setText(GenderMapKeys?.get(which))
                gender = GenderMap?.get(GenderMapKeys?.get(which))
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showPromotionDialog() {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setItems(promotionTypesMapKeys) { _, which ->
                binding?.etHowDoYouKnow?.setText(promotionTypesMapKeys?.get(which))
                howDoYouKnow = promotionTypesMap?.get(promotionTypesMapKeys?.get(which))

            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDatePicker(view: View?) {
        try {
            var preSelectedDate: String? = binding?.etDOB?.text.toString()
            if (preSelectedDate!!.isEmpty()) {
                preSelectedDate = currentDate
            }
            val dateValues: Array<String> = preSelectedDate!!.split("-").toTypedArray()
            val datePickerDialog = DatePickerDialog(
                this,
                R.style.MyDatePickerDialogTheme,
                { datePicker, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar[year, monthOfYear] = dayOfMonth
                    preSelectedDate = Utility.formatDate(calendar.time, Constants.DOB_DATE_FORMAT)
                    //registerUserReq.dob = preSelectedDate
                    binding?.etDOB?.setText(preSelectedDate)

                },
                dateValues[2].toInt(),
                dateValues[1].toInt() - 1,
                dateValues[0].toInt()
            )
            val minDate = Utility.convertStringToDate("01-01-1923", Constants.DOB_DATE_FORMAT)
            val minDateCal = Calendar.getInstance()
            minDateCal.time = minDate
            datePickerDialog.setCancelable(false)
            datePickerDialog.datePicker.minDate = minDateCal.timeInMillis

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -5)
            val maxDate = calendar.time.time

            datePickerDialog.datePicker.maxDate = maxDate
            datePickerDialog.show()

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
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.firstName != null) {
                binding?.etFirstName?.setText(userDetailsResponse?.userDetails?.firstName)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.lastName != null) {
                binding?.etLastName?.setText(userDetailsResponse?.userDetails?.lastName)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.gender != null) {
                binding?.etGender?.setText(if (userDetailsResponse?.userDetails?.gender == ("1")) "Male"
                else if(userDetailsResponse?.userDetails?.gender == ("0")) "Female" else "Select"
                )
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.dob != null) {
                binding?.etDOB?.setText(userDetailsResponse?.userDetails?.dob)
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.industry !=null){
                binding?.etIndType?.setText(userDetailsResponse?.userDetails?.industry)
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.department !=null){
                binding?.etDepartment?.setText(userDetailsResponse?.userDetails?.department)
            }
            if(userDetailsResponse?.userDetails !=null && userDetailsResponse?.userDetails?.designation !=null){
                binding?.etSearchDesignation?.setText(userDetailsResponse?.userDetails?.designation)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.companyName != null) {
                binding?.etCompanyName?.setText(userDetailsResponse?.userDetails?.companyName)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.monthlySal != null) {
                binding?.etMonthlySalary?.setText(userDetailsResponse?.userDetails?.monthlySal)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.modeOfPay != null) {
                binding?.etModeOfSalary?.setText(userDetailsResponse?.userDetails?.modeOfPay)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.pincode != null) {
                binding?.etPincode?.setText(userDetailsResponse?.userDetails?.pincode)
            }

            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.permanentPincode != null) {
                binding?.etPerPincode?.setText(userDetailsResponse?.userDetails?.permanentPincode)
            }
            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.offPincode != null) {
                binding?.etOfficePincode?.setText(userDetailsResponse?.userDetails?.offPincode)
            }

            if (userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.howYouKnowCn1 != null) {
                binding?.etHowDoYouKnow?.setText(userDetailsResponse?.userDetails?.howYouKnowCn1)
            }
            if(userDetailsResponse?.userDetails!= null && userDetailsResponse?.userDetails?.pan!= null){
                binding?.etPAN?.setText(userDetailsResponse?.userDetails?.pan)
            }
            if(userDetailsResponse?.userDetails != null && userDetailsResponse?.userDetails?.freezPan == 1){
                binding?.etPAN?.isEnabled = false
                binding?.etPAN?.isFocusable = false
            }else{
                binding?.etPAN?.isEnabled = true
                binding?.etPAN?.isFocusable = true
            }


        } catch (e: Exception) {

        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v?.id == binding?.etCompanyName?.id) {
            if (Validator.validateAutoComplete(binding!!.etCompanyName)) {
                if (selectedCompany != null && selectedCompany!!.id > 0.toString()) {
                    companyName = selectedCompany!!.name
                    companyId = selectedCompany!!.id.toInt()
                } else {
                    companyName = binding?.etCompanyName?.text.toString()
                    companyId = -1
                }
            }
        }
    }

    fun updateCompanyAdapter(list: ArrayList<MasterData>) {
        tvSearching.visibility = View.GONE
        fromSelection = false
        val companiesAdapter =
            CompanyNameAdapter1(this@Reg1Activity, R.layout.item_company_name, list)
        binding?.etCompanyName?.setAdapter(companiesAdapter)
        binding?.etCompanyName?.showDropDown()

        binding?.etCompanyName?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                tvSearching.visibility = View.GONE
                val item = parent.getItemAtPosition(position)
                if (item is MasterData) {
                    selectedCompany = item
                }
                tvSearching.visibility = View.GONE
            }
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
            updateCompanyAdapter(companies)
        }
    }


    fun getApplyLoanData(canRedirect: Boolean) {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity)
            val applyLoanServiceDataReq = ApplyLoanServiceDataReq()
            applyLoanServiceDataReq.currentScreen = "34"
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
                        35 -> {
                            val intent = Intent(this, Reg2Activity::class.java)
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
                    /*CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        "Something went wrong please try again later"
                    )*/
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

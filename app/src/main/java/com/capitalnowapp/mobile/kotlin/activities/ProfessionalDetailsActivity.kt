package com.capitalnowapp.mobile.kotlin.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView.OnItemClickListener
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityProfessionalDetailsBinding
import com.capitalnowapp.mobile.kotlin.adapters.CompanyNameAdapter
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.kotlin.utils.Validator
import com.capitalnowapp.mobile.util.Currency
import com.capitalnowapp.mobile.util.TrackingUtil
import kotlinx.android.synthetic.main.activity_professional_details.pb
import kotlinx.android.synthetic.main.activity_professional_details.tvSearching
import kotlinx.android.synthetic.main.activity_professional_details.tx_et_company_name
import org.json.JSONException
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask


class ProfessionalDetailsActivity : RegistrationHomeActivity(), TextWatcher,
    CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {

    @kotlin.jvm.JvmField
    var fromSelection: Boolean = false
    private var firstTimeLoad = true
    private var selectedCompany: MasterData? = null
    private var binding: ActivityProfessionalDetailsBinding? = null
    private var citiesArray: Array<CharSequence?>? = null
    private var empTypesArray: Array<CharSequence?>? = null
    private var industryArray: Array<CharSequence?>? = null
    private var companyValues: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfessionalDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        refreshJson()
        initView(binding)
        setProfessionBinding(binding!!, this)
    }

    private fun initView(binding: ActivityProfessionalDetailsBinding?) {

        val obj = JSONObject()
        try {
            obj.put("cnid",userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.professional_details_page_landed))

        fromSelection = false
        pb.indeterminateDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)

        binding!!.ivProfBack.setOnClickListener {
            onBackPressed()
        }
        binding.tvFinish.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid",userDetails.qcId)
                obj.put(getString(R.string.interaction_type),"FINISH Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.professional_details_page_submitted))

            when (isProfessionalFilled()) {
                0 -> {
                    when (isBasicFilled()) {
                        0 -> {
                            try {
                                val token = userToken
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return@setOnClickListener
                                }
                                mLastClickTime = SystemClock.elapsedRealtime()
                                CNProgressDialog.showProgressDialog(
                                    activityContext,
                                    Constants.LOADING_MESSAGE
                                )
                                cnModel.saveOneTimeRegistration(
                                    userDetails.userId,
                                    registerUserReq,
                                    userDetails, token
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        1 -> {
                            displayToast(validationMsg)
                            startActivity(Intent(this, BasicDetailsActivity::class.java))
                            finish()
                        }
                        else -> {
                            displayToast(getString(R.string.unique_validation_msg))
                            startActivity(Intent(this, BasicDetailsActivity::class.java))
                            finish()
                        }
                    }
                }
                1 -> {
                    displayToast(validationMsg)
                }
                else -> {
                    displayToast(getString(R.string.unique_validation_msg))
                }
            }
        }

        binding.txEtCity.setOnClickListener {
            if (allCitiesList != null && allCitiesList!!.size > 0) {
                showCitiesDialog()
            }


        }

        binding.txEtExperience.setOnClickListener {
            if (experienceListKeys != null && experienceListKeys!!.isNotEmpty()) {
                showExperienceDialog()
            }
        }
        binding.txEtModePay.setOnClickListener {
            if (modeOfPayListKeys != null && modeOfPayListKeys!!.isNotEmpty()) {
                showPaymentModeDialog()
            }
        }

        binding.etEmpType.setOnClickListener {
            if (masterJsonResponse != null && masterJsonResponse?.employmentTypes != null && masterJsonResponse?.employmentTypes?.size!! > 0) {
                showEmpTypesDialog()
            }
        }

        binding.etIndustry.setOnClickListener {
            if (masterJsonResponse != null && masterJsonResponse?.industries != null && masterJsonResponse?.industries?.size!! > 0) {
                showIndustryDialog()
            }
        }

        //binding.cbSameCity.setOnCheckedChangeListener(this)

        binding.txDept.addTextChangedListener(this)
        binding.txEtDesignation.addTextChangedListener(this)
        binding.txEtMonthlySalary.addTextChangedListener(this)

        binding.txDept.onFocusChangeListener = this
        binding.txEtDesignation.onFocusChangeListener = this
        binding.txEtMonthlySalary.onFocusChangeListener = this
        binding.txEtCompanyName.onFocusChangeListener = this

        //binding.txEtCompanyName.addTextChangedListener(this)

        /*binding.txEtCompanyName.setOnClickListener {
            val intent = Intent(this, CompanyListActivity::class.java)
            startActivityForResult(intent, 10002)
        }*/
        binding.txEtCompanyName.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (!firstTimeLoad) {
                        tvSearching.visibility = VISIBLE
                    } else {
                        firstTimeLoad = false
                        tvSearching.visibility = GONE
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
                    if (binding.txEtCompanyName.hasFocus()) {
                        if (s.toString().length >= 3 && !fromSelection) {
                            if (s.toString().isEmpty()) {
                                tvSearching.visibility = GONE
                            }
                            registerUserReq.companyName = s.toString()
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
                            tvSearching.visibility = GONE
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
        setData()
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
            registerUserReq.empType = masterJsonResponse?.employmentTypes!![which].key
            saveOSRFValueData(AppConstants.AjaxKeys.EmpSalType.toInt(), registerUserReq.empType)
            saveData()
        }
        builder.show()
    }

    private fun showIndustryDialog() {
        industryArray = arrayOfNulls(masterJsonResponse?.industries!!.size)
        for (i in 0 until masterJsonResponse?.industries!!.size) {
            industryArray!![i] =
                masterJsonResponse?.industries?.get(i) // Whichever string you wanna store here from custom object
        }

        val builder = AlertDialog.Builder(this)
        builder.setItems(industryArray) { _, which ->
            binding?.etIndustry?.setText(industryArray!![which])
            registerUserReq.industry = masterJsonResponse?.industries!![which]
            saveData()
        }
        designDialog(builder)
    }

    private fun setData() {
        if (registerUserReq.companyName != null && !registerUserReq.companyName.equals("")) {
            binding?.txEtCompanyName?.setText(registerUserReq.companyName)
        }
        if (registerUserReq.department != null && !registerUserReq.department.equals("")) {
            binding?.txDept?.setText(registerUserReq.department)
        }
        if (registerUserReq.designation != null && !registerUserReq.designation.equals("")) {
            binding?.txEtDesignation?.setText(registerUserReq.designation)
        }
        if (registerUserReq.monthlySal != null && !registerUserReq.monthlySal.equals("")) {
            binding?.txEtMonthlySalary?.setText(registerUserReq.monthlySal)
        }
        if (registerUserReq.modeOfPay != null && !registerUserReq.modeOfPay.equals("")) {
            binding?.txEtModePay?.setText(registerUserReq.modeOfPay)
        }
        if (registerUserReq.experience != null && !registerUserReq.experience.equals("")) {
            /*  val str: String = registerUserReq.experience!!
              val value: String? = experienceListMap?.let { getKeyFromValue(it, str) } as String?*/
            binding?.txEtExperience?.setText(
                experienceListKeys?.get(
                    registerUserReq.experience!!.toInt().minus(1)
                )
            )
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        when (s.toString()) {
            binding?.txEtCompanyName?.editableText.toString() -> {
                if (selectedCompany != null && selectedCompany!!.id > 0.toString()) {
                    registerUserReq.companyName = selectedCompany!!.name
                    registerUserReq.companyId = selectedCompany!!.id.toInt()
                } else {
                    registerUserReq.companyName = s.toString()
                    registerUserReq.companyId = -1
                }
                saveCompanyOSR(
                    AppConstants.AjaxKeys.Company.toInt(),
                    registerUserReq.companyName,
                    registerUserReq.companyId
                )
                saveData()
                //filterAdapter(s.toString())
            }
            binding?.txDept?.editableText.toString() -> {
                registerUserReq.department = s.toString()
            }
            binding?.txEtDesignation?.editableText.toString() -> {
                registerUserReq.designation = s.toString()
            }
            binding?.txEtMonthlySalary?.editableText.toString() -> {
                registerUserReq.monthlySal = s.toString()
                if (s.toString() != null && s.toString().isNotEmpty()) {
                    val words = Currency.convertToIndianCurrency(s.toString())
                    binding!!.tvCurrency.visibility = VISIBLE
                    binding!!.tvCurrency.text = words
                } else {
                    binding!!.tvCurrency.visibility = GONE
                }
            }

        }
        saveData()
    }

    private fun showCitiesDialog() {
        showCodesDialog(allCitiesList!!, this)
    }

    private fun showExperienceDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(experienceListKeys) { _, which ->
            binding?.txEtExperience?.setText(experienceListKeys?.get(which))
            registerUserReq.experience = experienceListMap?.get(experienceListKeys?.get(which)!!)
            saveData()
        }
        builder.show()
    }

    private fun showPaymentModeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(modeOfPayListKeys) { _, which ->
            binding?.txEtModePay?.setText(modeOfPayListKeys?.get(which))
            registerUserReq.modeOfPay = modeOfPayListKeys?.get((which))
            saveData()
            saveOSRFValueData(AppConstants.AjaxKeys.ModeOfPay.toInt(), registerUserReq.modeOfPay)
        }
        builder.show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        try {
            if (isChecked) {
                if (registerUserReq.nativeCityId != null && !registerUserReq.nativeCityId.equals("")) {
                    binding?.txEtCity?.setText(
                        allCitiesList?.get(
                            (registerUserReq.nativeCityId)!!.toInt().minus(1)
                        )?.name
                    )
                    registerUserReq.workCityId = registerUserReq.nativeCityId
                } else {
                    Toast.makeText(this, getString(R.string.select_native_city), Toast.LENGTH_SHORT)
                        .show()
                    //binding?.cbSameCity?.isChecked = false
                }
                saveOSRFValueData(AppConstants.AjaxKeys.City.toInt(), registerUserReq.workCityId)
                saveData()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            binding?.txDept?.id -> {
                if (Validator.validateEditText(binding!!.txDept)) {
                    saveOSRFValueData(
                        AppConstants.AjaxKeys.Department.toInt(),
                        registerUserReq.department
                    )
                }
                saveData()
            }
            binding?.txEtDesignation?.id -> {
                if (Validator.validateEditText(binding!!.txEtDesignation)) {
                    saveOSRFValueData(
                        AppConstants.AjaxKeys.Designation.toInt(),
                        registerUserReq.designation
                    )
                }
                saveData()
            }
            binding?.txEtCompanyName?.id -> {
                if (Validator.validateAutoComplete(binding!!.txEtCompanyName)) {
                    if (selectedCompany != null && selectedCompany!!.id > 0.toString()) {
                        registerUserReq.companyName = selectedCompany!!.name
                        registerUserReq.companyId = selectedCompany!!.id.toInt()
                    } else {
                        registerUserReq.companyName = tx_et_company_name.text.toString()
                        registerUserReq.companyId = -1
                    }
                    saveCompanyOSR(
                        AppConstants.AjaxKeys.Company.toInt(),
                        registerUserReq.companyName,
                        registerUserReq.companyId
                    )
                }
                saveData()
            }
            binding?.txEtMonthlySalary?.id -> {
                if (Validator.validateEditText(binding!!.txEtMonthlySalary) && !binding!!.txEtMonthlySalary.toString()[0].equals(
                        "0"
                    )
                ) {
                    saveOSRFValueData(
                        AppConstants.AjaxKeys.NetSal.toInt(),
                        registerUserReq.monthlySal
                    )
                }
                saveData()
            }
        }
    }

    fun updateCity() {
        citiesArray = arrayOfNulls(allCitiesList!!.size)
        for (i in 0 until allCitiesList!!.size) {
            citiesArray!![i] =
                allCitiesList?.get(i)?.name // Whichever string you wanna store here from custom object
        }
        if ((registerUserReq.workCityId != null && !registerUserReq.workCityId.equals(""))) {
            binding?.txEtCity?.setText(
                citiesArray!![(registerUserReq.workCityId)?.toInt()?.minus(1)!!]
            )
            if (registerUserReq.nativeCityId != null && !registerUserReq.nativeCityId.equals("") && registerUserReq.nativeCityId == registerUserReq.workCityId) {
                //binding?.cbSameCity?.isChecked = true
            }
        }
        saveData()
    }

    fun updateEmpType() {
        if (registerUserReq.empType != null && !registerUserReq.empType.equals("") && masterJsonResponse?.employmentTypes != null && masterJsonResponse?.employmentTypes!!.size > 0) {
            for (obj in masterJsonResponse?.employmentTypes!!) {
                if (obj.key == registerUserReq.empType) {
                    binding?.etEmpType?.setText(obj.value)
                    saveData()
                }
            }
        }
    }


    fun updateDesignationType() {
        if (registerUserReq.designation != null && !registerUserReq.designation.equals("")) {

        }
    }

    fun updateIndustry() {
        if (registerUserReq.industry != null && !registerUserReq.industry.equals("")) {
            binding?.etIndustry?.setText(registerUserReq.industry)
            saveData()
        }
    }

    /*public fun updateCompanyAdapter() {
        if (masterJsonResponse != null && masterJsonResponse?.companyNames != null && masterJsonResponse?.companyNames?.size!! > 0) {
            companyValues.addAll(masterJsonResponse?.companyNames!!)
            companyAdapter = ArrayAdapter(
                getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, companyValues
            )
            binding?.txEtCompanyName?.setAdapter(companyAdapter)
        }
    }*/

    /*private fun filterAdapter(toString: String) {
        if (toString.length > 2) {
            companyValues = ArrayList()
            if (masterJsonResponse != null && masterJsonResponse?.companyNames != null && masterJsonResponse?.companyNames?.size!! > 0) {
                for (obj in masterJsonResponse?.companyNames!!) {
                    if (obj.toString().toLowerCase(Locale.ROOT)
                            .startsWith(toString.toLowerCase(Locale.ROOT))
                    ) {
                        companyValues.add(obj)
                    }
                }
                companyAdapter = ArrayAdapter(
                    getApplicationContext(),
                    android.R.layout.simple_dropdown_item_1line, companyValues
                )
                binding?.txEtCompanyName?.setAdapter(companyAdapter)
            }
        } else {
            updateCompanyAdapter()
        }
    }*/

    fun updateCompanyAdapter(list: ArrayList<MasterData>) {
        tvSearching.visibility = GONE
        fromSelection = false
        val companiesAdapter =
            CompanyNameAdapter(this@ProfessionalDetailsActivity, R.layout.item_company_name, list)
        binding?.txEtCompanyName?.setAdapter(companiesAdapter)
        binding?.txEtCompanyName?.showDropDown()

        binding?.txEtCompanyName?.onItemClickListener =
            OnItemClickListener { parent, _, position, _ ->
                tvSearching.visibility = GONE
                val item = parent.getItemAtPosition(position)
                if (item is MasterData) {
                    selectedCompany = item
                }
                tvSearching.visibility = GONE
            }
    }

    override fun onDestroy() {
        saveCompanyToLocal()
        super.onDestroy()
    }

    private fun saveCompanyToLocal() {
        if (selectedCompany != null && selectedCompany!!.id > 0.toString()) {
            registerUserReq.companyName = selectedCompany!!.name
            registerUserReq.companyId = selectedCompany!!.id.toInt()
        } else {
            registerUserReq.companyName = tx_et_company_name.text.toString().trim()
            registerUserReq.companyId = -1
        }
        saveCompanyOSR(
            AppConstants.AjaxKeys.Company.toInt(),
            registerUserReq.companyName,
            registerUserReq.companyId
        )
        saveData()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        refreshJson()
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10002 && resultCode == RESULT_OK) {
            selectedCompany = data?.getSerializableExtra("selectedCompany") as MasterData
            binding!!.txEtCompanyName.setText(selectedCompany!!.name)
        }
    }*/
}

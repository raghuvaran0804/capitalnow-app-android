package com.capitalnowapp.mobile.kotlin.activities.offer

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityRegistrationPersonalBinding
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback
import com.capitalnowapp.mobile.kotlin.adapters.ListFilterAdapter
import com.capitalnowapp.mobile.models.offerModel.CSCityName
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.SavePersonalDetailsReq
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

//Screen 2

class RegistrationForm2Activity : BaseActivity() {
    private var binding: ActivityRegistrationPersonalBinding? = null
    private var activity: AppCompatActivity? = null
    private var validationMsg = ""
    private var pAdr1: String? = ""
    private var pAdr2: String? = ""
    private var city: String? = ""
    private var pincode: String? = ""
    private var stayDuration: String? = ""
    private var reason: String? = ""
    private var qualification: String? = ""
    private var selectedResidenceType: Int? = -1
    private var selectedMatretialaStatus: Int? = -1
    private var stayDurationListMap: LinkedHashMap<String, String>? = null
    private var higherQualificationListMap: LinkedHashMap<String, String>? = null
    private var stayDurationListKeys: Array<String>? = null
    var higherQualificationListKeys: Array<String>? = null
    var whyLoanListMap: LinkedHashMap<String, String>? = null
    var whyLoanListKeys: Array<String>? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private lateinit var adapter: ListFilterAdapter
    var dialog: AlertDialog? = null
    private var cityList: ArrayList<CSCityName>? = ArrayList()
    private var pincodeList: ArrayList<PinCodesData>? = ArrayList()
    private var cityMasterList: ArrayList<MasterData>? = ArrayList()
    private var pincodeMasterList: ArrayList<MasterData>? = ArrayList()
    private var selectedCity: String? = null
    private var selectedPincode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationPersonalBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()
        binding?.tvNext?.setOnClickListener {
            validateForm2()
        }
        binding?.ivBack?.setOnClickListener {
            val intent = Intent(this, RegistrationForm1Activity::class.java)
            startActivity(intent)
        }

        binding!!.etCity.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedCity = ""
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding!!.etPinCode.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedPincode = ""
            }

            override fun afterTextChanged(s: Editable) {}
        })


        binding!!.etCity.setOnClickListener {
            if (cityMasterList != null && cityMasterList?.isNotEmpty()!!) {
                showCodesDialog(cityMasterList!!)
            }
        }
        binding!!.etPinCode.setOnClickListener {
            if (pincodeMasterList != null && pincodeMasterList?.isNotEmpty()!!) {
                showPinCodesDialog(pincodeMasterList!!)
            }
        }

        binding?.rgResidency?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding!!.tvOwned.id -> {
                    setOwned()
                }
                binding!!.tvRented.id -> {
                    setRented()
                }
            }
        })
        binding?.rgMaritalStatus?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                binding!!.tvMarried.id -> {
                    setMarried()
                }
                binding!!.tvUnMarried.id -> {
                    setUnmarried()
                }
            }

        })
        stayDurationListMap = LinkedHashMap<String, String>()
        stayDurationListMap!!["Below 1 Year"] = "1"
        stayDurationListMap!!["1 Years"] = "2"
        stayDurationListMap!!["2 Years"] = "3"
        stayDurationListMap!!["3 Years"] = "4"
        stayDurationListMap!!["4 Years"] = "5"
        stayDurationListMap!!["Above 5 Years"] = "6"

        stayDurationListKeys = stayDurationListMap!!.keys.toTypedArray()

        binding?.etStayDuration?.setOnClickListener {
            if (stayDurationListKeys != null && stayDurationListKeys!!.isNotEmpty()) {
                showStayDurationDialog()
            }
        }

        higherQualificationListMap = LinkedHashMap<String, String>()
        higherQualificationListMap!!["Master's Degree"] = "MS"
        higherQualificationListMap!!["Post Graduation"] = "PG"
        higherQualificationListMap!!["BTech"] = "BTech"
        higherQualificationListMap!!["Graduation"] = "GRAD"
        higherQualificationListMap!!["HSC"] = "HSC"
        higherQualificationListMap!!["Other"] = "OTHER"
        higherQualificationListKeys = higherQualificationListMap!!.keys.toTypedArray()

        binding?.etQualification?.setOnClickListener {
            if (higherQualificationListKeys != null && higherQualificationListKeys!!.isNotEmpty()) {
                higherQualificationDialog()
            }
        }
        whyLoanListMap = LinkedHashMap<String, String>()
        whyLoanListMap!!["Wedding"] = "Wedding"
        whyLoanListMap!!["Travel"] = "Travel"
        whyLoanListMap!!["Home Renovation"] = "Home Renovation"
        whyLoanListMap!!["2 Wheeler/4 Wheeler"] = "2 Wheeler/4 Wheeler"
        whyLoanListMap!!["Personal Loan"] = "Personal Loan"
        whyLoanListMap!!["Online Shopping"] = "Online Shopping"
        whyLoanListMap!!["Instant Cash Loan"] = "Instant Cash Loan"
        whyLoanListMap!!["Education"] = "Education"
        whyLoanListMap!!["Medical Emergency"] = "Medical Emergency"
        whyLoanListMap!!["Refinancing"] = "Refinancing"
        whyLoanListKeys = whyLoanListMap!!.keys.toTypedArray()

        binding?.etWhyLoan?.setOnClickListener {
            if (whyLoanListKeys != null && whyLoanListKeys!!.isNotEmpty()) {
                whyLoanDialog()
            }
        }
    }

    private fun showPinCodesDialog(pincodeMasterList: ArrayList<MasterData>) {
        try {
            // flag 3= brand, 1= dealer, 2 = vehicle, 4 = city, 5 = area
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(com.capitalnowapp.mobile.R.layout.filter_dialog, null)
            val rvData: RecyclerView = view.findViewById(com.capitalnowapp.mobile.R.id.rvData)
            val tvCustom: TextView = view.findViewById(com.capitalnowapp.mobile.R.id.tvCustom)
            val tvEnterText: TextView = view.findViewById(com.capitalnowapp.mobile.R.id.tvEnterText)
            tvEnterText.visibility = View.GONE
            rvData.layoutManager = LinearLayoutManager(this)
            val etSearchCode = view.findViewById<EditText>(com.capitalnowapp.mobile.R.id.etSearch)
            etSearchCode.visibility = View.GONE

            adapter = ListFilterAdapter(this, pincodeMasterList, SelectedIdCallback { selectedId ->
                try {

                    for (item in 0 until pincodeList?.size!!) {
                        if (pincodeList!![item].psclPincode == selectedId) {
                            binding!!.etPinCode.setText(pincodeList!![item].psclPincode)
                            selectedPincode = pincodeList!![item].psclPincode
                            /*getPincodes(
                                selectedCity!!
                            )*/
                            break
                        }
                    }

                    dialog?.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            rvData.adapter = adapter
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

    private fun setCitiesData(csCityNames: ArrayList<CSCityName>?) {
        if (csCityNames?.isNotEmpty()!!) {
            for (item in csCityNames) {
                val masterData = MasterData()
                masterData.name = item.name
                cityMasterList?.add(masterData)
                cityList?.addAll(csCityNames)
            }
        }
    }

    private fun setPicodeData(pinCodesData: ArrayList<PinCodesData>?) {
        if (pinCodesData?.isNotEmpty()!!) {
            for (item in pinCodesData) {
                val masterData = MasterData()
                masterData.name = item.psclPincode
                pincodeMasterList?.add(masterData)
                pincodeList?.addAll(pinCodesData)
            }
        }
    }

    private fun showCodesDialog(cityMasterList: ArrayList<MasterData>) {
        try {
            // flag 3= brand, 1= dealer, 2 = vehicle, 4 = city, 5 = area
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(com.capitalnowapp.mobile.R.layout.filter_dialog, null)
            val rvData: RecyclerView = view.findViewById(com.capitalnowapp.mobile.R.id.rvData)
            val tvCustom: TextView = view.findViewById(com.capitalnowapp.mobile.R.id.tvCustom)
            val tvEnterText: TextView = view.findViewById(com.capitalnowapp.mobile.R.id.tvEnterText)
            tvEnterText.visibility = View.GONE
            rvData.layoutManager = LinearLayoutManager(this)
            val etSearchCode = view.findViewById<EditText>(com.capitalnowapp.mobile.R.id.etSearch)
            etSearchCode.visibility = View.GONE

            adapter = ListFilterAdapter(this, cityMasterList, SelectedIdCallback { selectedId ->
                try {
                    setPicodeData(ArrayList())
                    pincodeMasterList = ArrayList()
                    binding?.etPinCode?.setText("Pin Code")
                    selectedPincode = ""
                        for (item in 0 until cityList?.size!!) {
                            if (cityList!![item].name == selectedId) {
                                binding!!.etCity.setText(cityList!![item].name)
                                selectedCity = cityList!![item].name

                                getPincodes(
                                    selectedCity!!
                                )
                                break
                            }
                        }

                    dialog?.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            rvData.adapter = adapter
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


    private fun higherQualificationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(higherQualificationListKeys) { _, which ->
            binding?.etQualification?.setText(higherQualificationListKeys?.get(which))
            qualification =
                higherQualificationListMap?.get(higherQualificationListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun showStayDurationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(stayDurationListKeys) { _, which ->
            binding?.etStayDuration?.setText(stayDurationListKeys?.get(which))
            stayDuration = stayDurationListMap?.get(stayDurationListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun whyLoanDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(whyLoanListKeys) { _, which ->
            binding?.etWhyLoan?.setText(whyLoanListKeys?.get(which))
            reason = whyLoanListMap?.get(whyLoanListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun setUnmarried() {
        selectedMatretialaStatus = 8
        profileFormDataResponse.profileformData?.PPerMaritalStatus = selectedMatretialaStatus
        binding?.tvUnMarried?.isChecked = true
        binding?.tvMarried?.isChecked = false
    }

    private fun setMarried() {
        selectedMatretialaStatus = 6
        profileFormDataResponse.profileformData?.PPerMaritalStatus = selectedMatretialaStatus
        binding?.tvMarried?.isChecked = true
        binding?.tvUnMarried?.isChecked = false
    }

    private fun setOwned() {
        selectedResidenceType = 0
        profileFormDataResponse.profileformData?.pPerResidenceType = selectedResidenceType
        binding?.tvOwned?.isChecked = true
        binding?.tvRented?.isChecked = false
    }

    private fun setRented() {
        selectedResidenceType = 1
        profileFormDataResponse.profileformData?.pPerResidenceType = selectedResidenceType
        binding?.tvRented?.isChecked = true
        binding?.tvOwned?.isChecked = false
    }

    private fun getPincodes(selectedCity: String) {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val getPincodesReq = GetPinCodesReq()
            getPincodesReq.pCity = selectedCity
            val token = userToken
            genericAPIService.getPincodes(getPincodesReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val getPincodesResponse =
                    Gson().fromJson(responseBody, GetPinCodesResponse::class.java)
                if (getPincodesResponse.status == true) {
                    //getApplyLoanDataBase(true)
                    setPicodeData(getPincodesResponse.pincodeData)
                } else {
                    Toast.makeText(this, getPincodesResponse.message, Toast.LENGTH_SHORT).show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveForm2() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val savePersonalDetailsReq = SavePersonalDetailsReq()
            savePersonalDetailsReq.pageNo = "15"
            savePersonalDetailsReq.pAddressLine1 = pAdr1
            savePersonalDetailsReq.pAddressLine2 = pAdr2
            savePersonalDetailsReq.pCity = selectedCity
            savePersonalDetailsReq.pPincode = selectedPincode
            savePersonalDetailsReq.pPerResidenceType = selectedResidenceType.toString()
            savePersonalDetailsReq.pPerCurAddStayingDuration = stayDuration
            savePersonalDetailsReq.pPerHigestQualification = qualification
            savePersonalDetailsReq.pPerMaritalStatus = selectedMatretialaStatus.toString()
            savePersonalDetailsReq.pPerLoanReason = reason
            val token = userToken
            genericAPIService.savePersonalDetails(savePersonalDetailsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val csGenericResponse =
                    Gson().fromJson(responseBody, CSGenericResponse::class.java)
                if (csGenericResponse.status == true) {
                    getApplyLoanDataBase(true)
                } else {
                    Toast.makeText(this, csGenericResponse.message, Toast.LENGTH_SHORT).show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateForm2() {
        try {
            pAdr1 = binding?.etPAddressLine1?.text.toString().trim { it <= ' ' }
            pAdr2 = binding?.etPAddressLine2?.text.toString().trim { it <= ' ' }
            stayDuration = binding?.etStayDuration?.text.toString().trim { it <= ' ' }
            qualification = binding?.etQualification?.text.toString().trim { it <= ' ' }
            reason = binding?.etWhyLoan?.text.toString().trim { it <= ' ' }
            selectedCity = binding?.etCity?.text.toString().trim { it <= ' ' }
            selectedPincode = binding?.etPinCode?.text.toString().trim { it <= ' ' }
            var count = 0
            if (pAdr1!!.isEmpty()) {
                validationMsg = "AddressLine1 is required and can't be empty"
                count++
            } else if (pAdr2!!.isEmpty()) {
                validationMsg = "AddressLine2 is required and can't be empty"
                count++
            } else if (selectedCity!!.isEmpty()) {
                validationMsg = "City is required and can't be empty"
                count++
            } else if (stayDuration!!.isEmpty()) {
                validationMsg =
                    "Since How long you staying in current Address is required and can't be empty"
                count++
            } else if (qualification!!.isEmpty()) {
                validationMsg = "Your Highest Qualification is required and can't be empty"
                count++
            } else if (selectedResidenceType!! > 0) {
                validationMsg = "Select Residence Type"
            } else if (selectedMatretialaStatus!! > 0) {
                validationMsg = "Select Marital Status"
            } else if (selectedPincode!!.isEmpty()) {
                validationMsg = "PinCode is required and can't be empty"
                count++
            } else if (reason!!.isEmpty()) {
                validationMsg = "Why do you require loan? is required and can't be empty"
                count++
            }
            if (count > 0) {
                Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
            } else {
                saveForm2()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 15
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                    if (profileFormDataResponse.code == 2000) {
                        setCitiesData(profileFormDataResponse.profileformData?.csCityNames)
                        setData()
                    }
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setData() {
        try {
            binding?.etPAddressLine1?.setText(this.profileFormDataResponse.profileformData?.pAddressLine1)
            binding?.etPAddressLine2?.setText(this.profileFormDataResponse.profileformData?.pAddressLine2)
            binding?.etCity?.setText(this.profileFormDataResponse.profileformData?.pCity)
            binding?.etPinCode?.setText(this.profileFormDataResponse.profileformData?.pPincode)
            binding?.etStayDuration?.setText(this.profileFormDataResponse.profileformData?.pPerCurAddStayingDuration)
            binding?.etQualification?.setText(this.profileFormDataResponse.profileformData?.pPerHigestQualification)
            binding?.etWhyLoan?.setText(this.profileFormDataResponse.profileformData?.PPerLoanReason)
            when (this.profileFormDataResponse.profileformData?.pPerResidenceType) {
                0 -> {
                    setOwned()
                }
                1 -> {
                    setRented()
                }
            }
            val str: String? =
                this.profileFormDataResponse.profileformData?.pPerCurAddStayingDuration
            val value: String? = stayDurationListMap?.let { getKeyFromValue(it, str!!) } as String?
            binding?.etStayDuration?.setText(value)
            val str1: String? =
                this.profileFormDataResponse.profileformData?.pPerHigestQualification
            val value1: String? =
                higherQualificationListMap?.let { getKeyFromValue(it, str1!!) } as String?
            binding?.etQualification?.setText(value1)
            when (this.profileFormDataResponse.profileformData?.PPerMaritalStatus) {
                8 -> {
                    setUnmarried()
                }
                6 -> {
                    setMarried()
                }
            }
            val str2: String? = this.profileFormDataResponse.profileformData?.PPerLoanReason
            val value2: String? = whyLoanListMap?.let { getKeyFromValue(it, str2!!) } as String?
            binding?.etWhyLoan?.setText(value2)
            //binding?.etWhyLoan?.setText(whyLoanListMap?.get(profileFormDataResponse.profileformData?.PPerLoanReason))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getKeyFromValue(hm: Map<*, *>, value: Any): Any? {
        for (o in hm.keys) {
            if (hm[o].toString().lowercase() == value.toString().lowercase()) {
                return o
            }
        }
        return ""
    }
}
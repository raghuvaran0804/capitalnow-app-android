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
import com.capitalnowapp.mobile.databinding.ActivityRegistrationProfessionalBinding
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback
import com.capitalnowapp.mobile.kotlin.adapters.ListFilterAdapter
import com.capitalnowapp.mobile.models.offerModel.CSCityName
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.SaveProfessionalDetailsReq
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

//Screen 3

//Upwards flow also
class RegistrationForm3Activity : BaseActivity() {
    private var binding: ActivityRegistrationProfessionalBinding? = null
    private var activity: AppCompatActivity? = null
    private var employerName: String? = ""
    private var officialEmail: String? = ""
    private var workExperience: String? = ""
    private var corAddressLine1: String? = ""
    private var corAddressLine2: String? = ""
    private var city: String? = ""
    private var pincode: String? = ""
    private var validationMsg = ""
    private var selectedSalaryMode: Int? = -1
    var workExperienceListMap: LinkedHashMap<String, String>? = null
    var workExperienceListKeys: Array<String>? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var csGenericResponse = CSGenericResponse()
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
        binding = ActivityRegistrationProfessionalBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        profileFormData()

        binding?.tvNext?.setOnClickListener {
            validateForm3()
        }
        binding?.ivBack?.setOnClickListener {
            val intent = Intent(this, RegistrationForm2Activity::class.java)
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


        binding?.rgSalaryMode?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                binding!!.tvOnline.id -> {
                    setOnline()
                }
                binding!!.tvCash.id -> {
                    setCash()
                }
                binding!!.tvCheque.id -> {
                    setCheque()
                }
            }

        })

        workExperienceListMap = LinkedHashMap<String, String>()

        workExperienceListMap!!["Below 1 Year"] = "1"
        workExperienceListMap!!["1 Years"] = "2"
        workExperienceListMap!!["2 Years"] = "3"
        workExperienceListMap!!["3 Years"] = "4"
        workExperienceListMap!!["4 Years"] = "5"
        workExperienceListMap!!["Above 5 Years"] = "6"

        workExperienceListKeys = workExperienceListMap!!.keys.toTypedArray()

        binding?.etWorkExperience?.setOnClickListener {
            if (workExperienceListKeys != null && workExperienceListKeys!!.isNotEmpty()) {
                showWorkExperienceDialog()
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


    private fun showWorkExperienceDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(workExperienceListKeys) { _, which ->
            binding?.etWorkExperience?.setText(workExperienceListKeys?.get(which))
            workExperience = workExperienceListMap?.get(workExperienceListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun setOnline() {
        selectedSalaryMode = 1
        profileFormDataResponse.profileformData?.pSalaryMode = selectedSalaryMode
        binding?.tvOnline?.isChecked = true
        binding?.tvCash?.isChecked = false
        binding?.tvCheque?.isChecked = false
    }

    private fun setCash() {
        selectedSalaryMode = 2
        profileFormDataResponse.profileformData?.pSalaryMode = selectedSalaryMode
        binding?.tvCash?.isChecked = true
        binding?.tvOnline?.isChecked = false
        binding?.tvCheque?.isChecked = false
    }

    private fun setCheque() {
        selectedSalaryMode = 3
        profileFormDataResponse.profileformData?.pSalaryMode = selectedSalaryMode
        binding?.tvCheque?.isChecked = true
        binding?.tvCash?.isChecked = false
        binding?.tvOnline?.isChecked = false
    }

    private fun saveForm3() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val saveProfessionalDetailsReq = SaveProfessionalDetailsReq()
            saveProfessionalDetailsReq.pageNo = "17"
            saveProfessionalDetailsReq.pSalaryMode = selectedSalaryMode
            saveProfessionalDetailsReq.pProAddressLine1 = corAddressLine1
            saveProfessionalDetailsReq.pProAddressLine2 = corAddressLine2
            saveProfessionalDetailsReq.pProCity = selectedCity
            saveProfessionalDetailsReq.pProOfficePincode = selectedPincode
            saveProfessionalDetailsReq.pProWorkExperience = workExperience
            saveProfessionalDetailsReq.pCompanyName = employerName
            saveProfessionalDetailsReq.pOfficialEmail = officialEmail
            val token = userToken
            genericAPIService.saveWorkDetails(saveProfessionalDetailsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                csGenericResponse =
                    Gson().fromJson(responseBody, CSGenericResponse::class.java)
                if (csGenericResponse.status == true) {
                    getApplyLoanDataBase(true)
                }
                else {
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
            CNProgressDialog.hideProgressDialog()
            e.printStackTrace()
        }
    }

    private fun validateForm3() {
        try {
            employerName = binding?.etEmployerName?.text.toString().trim { it <= ' ' }
            officialEmail = binding?.etOfficialEmail?.text.toString().trim { it <= ' ' }
            workExperience = binding?.etWorkExperience?.text.toString().trim { it <= ' ' }
            corAddressLine1 = binding?.etCorAddressLine1?.text.toString().trim { it <= ' ' }
            corAddressLine2 = binding?.etCorAddressLine2?.text.toString().trim { it <= ' ' }
            selectedCity = binding?.etCity?.text.toString().trim { it <= ' ' }
            selectedPincode = binding?.etPinCode?.text.toString().trim { it <= ' ' }
            var count = 0
            if (employerName!!.isEmpty()) {
                validationMsg = "Employer Name is required and can't be empty"
                count++
            } else if (officialEmail!!.isEmpty()) {
                validationMsg = "Official Email ID is required and can't be empty"
                count++
            } else if (workExperience!!.isEmpty()) {
                validationMsg = "Work Experience is required and can't be empty"
                count++
            } else if (selectedSalaryMode!!.toString().isEmpty()) {
                validationMsg = "Select Salary Mode"
                count++
            } else if (corAddressLine1!!.isEmpty()) {
                validationMsg = "Corresponding Address Line 1 is required and can't be empty"
                count++
            } else if (corAddressLine2!!.isEmpty()) {
                validationMsg = "Corresponding Address Line 2 is required and can't be empty"
                count++
            }  else if (selectedCity!!.isEmpty()) {
                validationMsg = "City is required and can't be empty"
                count++
            } else if (selectedPincode!!.isEmpty()) {
                validationMsg = "Pincode is required and can't be empty"
                count++
            }
            if (count > 0) {
                Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
            } else {
                saveForm3()
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
            profileFormDataReq.pageNo = 17
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                    setCitiesData(profileFormDataResponse.profileformData?.csCityNames)
                    setData()

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
            if(profileFormDataResponse.profileformData?.pCompanyName !=null) {
                binding?.etEmployerName?.setText(profileFormDataResponse.profileformData?.pCompanyName)
            }
            if(profileFormDataResponse.profileformData?.pOfficialEmail != null) {
                binding?.etOfficialEmail?.setText(profileFormDataResponse.profileformData?.pOfficialEmail)
            }
            if(profileFormDataResponse.profileformData?.pProWorkExperience !=null) {
                val str: String? = profileFormDataResponse.profileformData?.pProWorkExperience
                val value: String? =
                    workExperienceListMap?.let { getKeyFromValue(it, str!!) } as String?
                binding?.etWorkExperience?.setText(value)
            }
            if(profileFormDataResponse.profileformData?.pSalaryMode != null) {
                when (profileFormDataResponse.profileformData?.pSalaryMode) {
                    1 -> {
                        setOnline()
                    }
                    2 -> {
                        setCash()
                    }
                    3 -> {
                        setCheque()
                    }
                }
            }
            if(profileFormDataResponse.profileformData?.pProAddressLine1 != null) {
                binding?.etCorAddressLine1?.setText(profileFormDataResponse.profileformData?.pProAddressLine1)
            }
            if(profileFormDataResponse.profileformData?.pProAddressLine2 != null) {
                binding?.etCorAddressLine2?.setText(profileFormDataResponse.profileformData?.pProAddressLine2)
            }

            if(profileFormDataResponse.profileformData?.pProCity != null) {
                binding?.etCity?.setText(profileFormDataResponse.profileformData?.pProCity)
            }
            if(profileFormDataResponse.profileformData?.pProOfficePincode != null) {
                binding?.etPinCode?.setText(profileFormDataResponse.profileformData?.pProOfficePincode!!.toString())
            }
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
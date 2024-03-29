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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityOfferReferencesBinding
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.ListFilterAdapter
import com.capitalnowapp.mobile.models.offerModel.CSCityName
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.SaveOfferReferenceDetailsReq
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_offer_references.tvSelectId
import kotlinx.android.synthetic.main.activity_offer_references.tvSelectId2

class OfferReferencesActivity : BaseActivity(), View.OnClickListener {
    private var referenceName1: String? = ""
    private var referenceMobileNo1: String? = ""
    private var referenceRelation1: String? = ""
    private var referenceAdr1: String? = ""
    private var referencePincode1: String? = ""
    private var referenceState1: String? = ""
    private var referenceCity1: String? = ""
    private var referenceName2: String? = ""
    private var referenceMobileNo2: String? = ""
    private var referenceRelation2: String? = ""
    private var referenceAdr2: String? = ""
    private var referencePincode2: String? = ""
    private var referenceState2: String? = ""
    private var referenceCity2: String? = ""
    private var validationMsg = ""
    private var activity: AppCompatActivity? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var binding: ActivityOfferReferencesBinding? = null
    var Relation1ListMap: LinkedHashMap<String, String>? = null
    var Relation2ListMap: LinkedHashMap<String, String>? = null
    private var Relation1ListKeys: Array<String>? = null
    private var Relation2ListKeys: Array<String>? = null
    private var relation1: String? = ""
    private var relation2: String? = ""
    private var cityList: ArrayList<CSCityName>? = ArrayList()
    private var cityMasterList: ArrayList<MasterData>? = ArrayList()
    private var selectedCity1: String? = null
    private var selectedCity2: String? = null
    private lateinit var adapter: ListFilterAdapter
    var dialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfferReferencesBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()

        binding!!.etCity.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedCity1 = ""
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.etCity.setOnClickListener {
            if (cityMasterList != null && cityMasterList?.isNotEmpty()!!) {
                showCodesDialog(cityMasterList!!)
            }
        }
        binding!!.etCity2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedCity2 = ""
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.etCity2.setOnClickListener {
            if (cityMasterList != null && cityMasterList?.isNotEmpty()!!) {
                showCodes2Dialog(cityMasterList!!)
            }
        }
        Relation1ListMap = LinkedHashMap<String, String>()

        Relation1ListMap!!["father"] = "father"
        Relation1ListMap!!["mother"] = "mother"
        Relation1ListMap!!["brother"] = "brother"
        Relation1ListMap!!["sister"] = "sister"
        Relation1ListMap!!["wife"] = "wife"
        Relation1ListMap!!["husband"] = "husband"
        Relation1ListMap!!["son"] = "son"
        Relation1ListMap!!["daughter"] = "daughter"
        Relation1ListKeys = Relation1ListMap!!.keys.toTypedArray()

        binding?.etRelation?.setOnClickListener {
            if (Relation1ListKeys != null && Relation1ListKeys!!.isNotEmpty()) {
                relation1Dialog()
            }
        }

        Relation2ListMap = LinkedHashMap<String, String>()

        Relation2ListMap!!["father"] = "father"
        Relation2ListMap!!["mother"] = "mother"
        Relation2ListMap!!["brother"] = "brother"
        Relation2ListMap!!["sister"] = "sister"
        Relation2ListMap!!["wife"] = "wife"
        Relation2ListMap!!["husband"] = "husband"
        Relation2ListMap!!["son"] = "son"
        Relation2ListMap!!["daughter"] = "daughter"
        Relation2ListMap!!["neighbour"] = "neighbour"
        Relation2ListMap!!["colleague"] = "colleague"
        Relation2ListMap!!["friend"] = "friend"
        Relation2ListMap!!["uncle"] = "uncle"
        Relation2ListMap!!["aunt"] = "aunt"
        Relation2ListMap!!["others"] = "others"
        Relation2ListKeys = Relation2ListMap!!.keys.toTypedArray()

        binding?.etRelation2?.setOnClickListener {
            if (Relation2ListKeys != null && Relation2ListKeys!!.isNotEmpty()) {
                relation2Dialog()
            }
        }
        binding?.llPrimaryReference?.setOnClickListener(this)
        binding?.llSecondaryReference?.setOnClickListener(this)

        binding?.tvNext?.setOnClickListener {
            validateReferences()
        }
        binding?.ivBack?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
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

                    for (item in 0 until cityList?.size!!) {
                        if (cityList!![item].name == selectedId) {
                            binding!!.etCity.setText(cityList!![item].name)
                            selectedCity1 = cityList!![item].name

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

    private fun showCodes2Dialog(cityMasterList: ArrayList<MasterData>) {
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

                    for (item in 0 until cityList?.size!!) {
                        if (cityList!![item].name == selectedId) {
                            binding!!.etCity2.setText(cityList!![item].name)
                            selectedCity2 = cityList!![item].name

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

    private fun validateReferences() {
        referenceName1 = binding?.etName?.text.toString().trim { it <= ' ' }
        referenceMobileNo1 = binding?.etMobileNumber?.text.toString().trim { it <= ' ' }
        referenceRelation1 = binding?.etRelation?.text.toString().trim { it <= ' ' }
        referenceAdr1 =  binding?.etAddressLine1?.text.toString().trim { it <= ' ' }
        referenceCity1 =  binding?.etCity?.text.toString().trim { it <= ' ' }

        referenceName2 = binding?.etName2?.text.toString().trim { it <= ' ' }
        referenceMobileNo2 = binding?.etMobileNumber2?.text.toString().trim { it <= ' ' }
        referenceRelation2 = binding?.etRelation2?.text.toString().trim { it <= ' ' }
        referenceAdr2 =  binding?.etAddressLine2?.text.toString().trim { it <= ' ' }
        referenceCity2 =  binding?.etCity2?.text.toString().trim { it <= ' ' }
        var count = 0
        if (referenceName1!!.isEmpty()) {
            validationMsg = "Primary reference Name is required and can't be empty"
            count++
        }else if (referenceMobileNo1!!.isEmpty()) {
            validationMsg = "Primary reference Mobile Number is required and can't be empty"
            count++
        } else if (referenceRelation1!!.isEmpty()) {
            validationMsg = "Primary reference Relation is required and can't be empty"
            count++
        } else if (referenceAdr1!!.isEmpty()) {
            validationMsg = "Primary reference Address is required and can't be empty"
            count++
        } else if (referenceCity1!!.isEmpty()) {
            validationMsg = "Primary reference City is required and can't be empty"
            count++
        } else if (referenceName2!!.isEmpty()) {
            validationMsg = "Secondary reference Name is required and can't be empty"
            count++
        }else if (referenceMobileNo2!!.isEmpty()) {
            validationMsg = "Secondary reference Mobile Number is required and can't be empty"
            count++
        } else if (referenceRelation2!!.isEmpty()) {
            validationMsg = "Secondary reference Relation is required and can't be empty"
            count++
        } else if (referenceAdr2!!.isEmpty()) {
            validationMsg = "Secondary reference Address is required and can't be empty"
            count++
        }  else if (referenceCity2!!.isEmpty()) {
            validationMsg = "Secondary reference City is required and can't be empty"
            count++
        }
        if (count > 0) {
            Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
        } else {
            saveReferences()
        }
    }

    private fun saveReferences() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val saveOfferReferenceDetailsReq = SaveOfferReferenceDetailsReq()
            saveOfferReferenceDetailsReq.pageNo = "28"
            saveOfferReferenceDetailsReq.crPName = referenceName1
            saveOfferReferenceDetailsReq.crPMobileNumber = referenceMobileNo1
            saveOfferReferenceDetailsReq.crPRelation = relation1
            saveOfferReferenceDetailsReq.crPAddress1 = referenceAdr1
            saveOfferReferenceDetailsReq.crPCity = selectedCity1
            saveOfferReferenceDetailsReq.crSName = referenceName2
            saveOfferReferenceDetailsReq.crSMobileNumber = referenceMobileNo2
            saveOfferReferenceDetailsReq.crSRelation = relation2
            saveOfferReferenceDetailsReq.crSAddress1 = referenceAdr2
            saveOfferReferenceDetailsReq.crSCity = selectedCity2
            val token = userToken
            genericAPIService.saveReferenceDetails(saveOfferReferenceDetailsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                    getApplyLoanDataBase(true)
                }else {
                    Toast.makeText(this, profileFormDataResponse.message, Toast.LENGTH_LONG).show()
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

    private fun relation1Dialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(Relation1ListKeys) { _, which ->
            binding?.etRelation?.setText(Relation1ListKeys?.get(which))
            relation1 = Relation1ListMap?.get(Relation1ListKeys?.get(which)!!)
        }
        builder.show()
    }
    private fun relation2Dialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(Relation2ListKeys) { _, which ->
            binding?.etRelation2?.setText(Relation2ListKeys?.get(which))
            relation2 = Relation2ListMap?.get(Relation2ListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 28
            val token =userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                    setCitiesData(profileFormDataResponse.profileformData?.csCityNames)
                    setReferenceData()
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

    private fun setReferenceData() {
        try{
            //Primary Contact Reference
            if(profileFormDataResponse.profileformData?.crPName != null) {
                binding?.etName?.setText(this.profileFormDataResponse.profileformData?.crPName)
            }
            if(profileFormDataResponse.profileformData?.crPMobileNumber !=null) {
                binding?.etMobileNumber?.setText(this.profileFormDataResponse.profileformData?.crPMobileNumber.toString())
            }
            val str: String? = this.profileFormDataResponse.profileformData?.crPRelation
            val value: String? = Relation1ListMap?.let { getKeyFromValue(it, str!!) } as String?
            binding?.etRelation?.setText(value)
            relation1 = value
            if(profileFormDataResponse.profileformData?.crPAddress1 !=null) {
                binding?.etAddressLine1?.setText(this.profileFormDataResponse.profileformData?.crPAddress1)
            }
            if(profileFormDataResponse.profileformData?.crPCity != null) {
                binding?.etCity?.setText(this.profileFormDataResponse.profileformData?.crPCity)
            }
            //Secondary Contact Reference
            if(profileFormDataResponse.profileformData?.crSName != null) {
                binding?.etName2?.setText(this.profileFormDataResponse.profileformData?.crSName)
            }
            if(profileFormDataResponse.profileformData?.crSMobileNumber != null) {
                binding?.etMobileNumber2?.setText(this.profileFormDataResponse.profileformData?.crSMobileNumber.toString())
            }
            val str1: String? = this.profileFormDataResponse.profileformData?.crSRelation
            val value1: String? = Relation2ListMap?.let { getKeyFromValue(it, str1!!) } as String?
            binding?.etRelation2?.setText(value1)
            relation2 = value1
            if(profileFormDataResponse.profileformData?.crSAddress1 !=null) {
                binding?.etAddressLine2?.setText(this.profileFormDataResponse.profileformData?.crSAddress1)
            }
            if(profileFormDataResponse.profileformData?.crSCity != null) {
                binding?.etCity2?.setText(this.profileFormDataResponse.profileformData?.crSCity)
            }

        }catch (e: Exception){
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llPrimaryReference -> {
                if (binding?.llId?.visibility == VISIBLE) {
                    binding?.llId?.visibility = GONE
                    setExpand(tvSelectId, true)
                } else {
                    binding?.llId?.visibility = VISIBLE
                    setExpand(tvSelectId, false)
                }
            }
            R.id.llSecondaryReference -> {
                if (binding?.llId2?.visibility == VISIBLE) {
                    binding?.llId2?.visibility = GONE
                    setExpand(tvSelectId2, true)
                } else {
                    binding?.llId2?.visibility = VISIBLE
                    setExpand(tvSelectId2, false)
                }
            }
        }
    }

    private fun setExpand(image: ImageView?, b: Boolean) {
        if (b) {
            image?.setImageResource(R.drawable.arrow_square_right)
        } else {
            image?.setImageResource(R.drawable.arrow_square_left)
        }
    }
}
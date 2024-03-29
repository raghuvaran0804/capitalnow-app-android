package com.capitalnowapp.mobile.kotlin.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.constants.Constants.ButtonType
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.databinding.ActivityVehicleDetailsBinding
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.models.GetVehicleDetailsReq
import com.capitalnowapp.mobile.models.GetVehicleDetailsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.google.gson.Gson

class VehicleDetailsActivity : BaseActivity() {
    private var getVehicleDetailsResponse: GetVehicleDetailsResponse? = null
    var binding: ActivityVehicleDetailsBinding? = null

    private var selectedCity = ""
    private var selectedArea = ""
    private var selectedBrand = ""
    private var selectedDealerId = ""
    private var selectedDealerName = ""
    private var selectedVehicleId = ""
    private var selectedVehiclePrice = ""
    private var selectedVarient = ""
    private var selectedColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding!!.ivBack.setOnClickListener {
            onBackPressed()
        }
        initView()
    }

    private fun initView() {
        if (intent.extras != null) {
            selectedCity = intent.getStringExtra("city")!!
            selectedArea = intent.getStringExtra("area")!!
            selectedDealerId = intent.getStringExtra("dealer")!!
            selectedDealerName = intent.getStringExtra("dealerName")!!
            selectedVehicleId = intent.getStringExtra("vehicle")!!
            selectedVarient = intent.getStringExtra("varient")!!
            selectedColor = intent.getStringExtra("color")!!

            getVehicleDetails()
        }
        binding!!.tvConfirm.setOnClickListener {
            val sharedPreferences = CNSharedPreferences(this)
            sharedPreferences.putBoolean(Constants.From_Vehicle_Details, true)
            sharedPreferences.putString(Constants.SelectedVehicleId, selectedVehicleId)
            sharedPreferences.putString(Constants.SelectedDealerId, selectedDealerId)
            sharedPreferences.putString(Constants.SelectedVehiclePrice, selectedVehiclePrice)
            sharedPreferences.putString(Constants.SelectedVehicleArea, getVehicleDetailsResponse?.tableData?.area)
            sharedPreferences.putString(Constants.SelectedVehicleCity, getVehicleDetailsResponse?.tableData?.city)
            sharedPreferences.putString(Constants.SelectedVehicleDealer, getVehicleDetailsResponse?.tableData?.dealerName)
            sharedPreferences.putString(Constants.SelectedVehicleBrand, getVehicleDetailsResponse?.tableData?.twlvModel)
            finish()
        }
    }


    private fun getVehicleDetails() {
        try {
            val genericAPIService = GenericAPIService(this,0)
            val getVehicleDetailsReq = GetVehicleDetailsReq()
            getVehicleDetailsReq.cityName = selectedCity
            getVehicleDetailsReq.areaName = selectedArea
            getVehicleDetailsReq.brand = selectedBrand
            getVehicleDetailsReq.dealerId = selectedDealerId
            getVehicleDetailsReq.dealerName = selectedDealerName
            getVehicleDetailsReq.vehicleId = selectedVehicleId
            getVehicleDetailsReq.varientName = selectedVarient
            getVehicleDetailsReq.color = selectedColor
            val token = userToken
            genericAPIService.getVehicleDetails(getVehicleDetailsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                getVehicleDetailsResponse = Gson().fromJson(
                    responseBody, GetVehicleDetailsResponse::class.java
                )
                if (getVehicleDetailsResponse != null && getVehicleDetailsResponse!!.status == Constants.STATUS_SUCCESS) {
                    setData(getVehicleDetailsResponse!!)
                } else {
                    showAlert(getVehicleDetailsResponse)
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

    private fun showAlert(vehicleDetailsResponse: GetVehicleDetailsResponse?) {
        CNAlertDialog.setRequestCode(1)
        CNAlertDialog.showAlertDialogWithCallback(
            this,
            "",
            vehicleDetailsResponse?.message,
            false,
            "",
            ""
        )

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {}
            override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {
                if (buttonType == ButtonType.POSITIVE) {
                    finish()
                }
            }
        })
    }

    private fun setData(vehicleDetailsResponse: GetVehicleDetailsResponse) {
        binding!!.tvDealer.text = vehicleDetailsResponse.tableData?.dealerName
        var adr = ""
        if (vehicleDetailsResponse.tableData?.address?.isNotEmpty()!!) {
            adr += vehicleDetailsResponse.tableData!!.address
        }

        if (vehicleDetailsResponse.tableData?.area?.isNotEmpty()!!) {
            if (adr.isNotEmpty()) {
                adr = adr + ", " + vehicleDetailsResponse.tableData!!.area
            } else {
                adr += vehicleDetailsResponse.tableData!!.area
            }
        }

        if (vehicleDetailsResponse.tableData?.city?.isNotEmpty()!!) {
            if (adr.isNotEmpty()) {
                adr = adr + ", " + vehicleDetailsResponse.tableData!!.city
            } else {
                adr += vehicleDetailsResponse.tableData!!.city
            }
        }
        if (vehicleDetailsResponse.tableData?.state?.isNotEmpty()!!) {
            if (adr.isNotEmpty()) {
                adr = adr + ", " + vehicleDetailsResponse.tableData!!.state
            } else {
                adr += vehicleDetailsResponse.tableData!!.state
            }
        }

        if (vehicleDetailsResponse.tableData?.pincode?.isNotEmpty()!!) {
            if (adr.isNotEmpty()) {
                adr = adr + ", " + vehicleDetailsResponse.tableData!!.pincode
            } else {
                adr += vehicleDetailsResponse.tableData!!.pincode
            }
        }
        binding!!.tvAddress.text = adr
        binding!!.tvBrand.text = ": " + vehicleDetailsResponse.tableData?.name
        binding!!.tvType.text = ": " + vehicleDetailsResponse.tableData?.vehicleType
        binding!!.tvPrice.text = "Rs. " + vehicleDetailsResponse.tableData?.twlvOnroadPrice
        selectedVehiclePrice = vehicleDetailsResponse.tableData?.twlvOnroadPrice!!
        binding!!.tvModel.text = ": " + vehicleDetailsResponse.tableData?.twlvModel
        binding!!.tvExshowroomPrice.text = ": Rs. " + vehicleDetailsResponse.tableData?.twlvExshowroomPrice
        binding!!.tvEligibility.text = "Rs. " + vehicleDetailsResponse.tableData?.eligibility
        binding!!.tvDownPayment.text = vehicleDetailsResponse.tableData?.downPayment.toString()
        Glide.with(getApplicationContext()).load(vehicleDetailsResponse.tableData?.twlvImage).into(binding!!.ivBike)
    }
}

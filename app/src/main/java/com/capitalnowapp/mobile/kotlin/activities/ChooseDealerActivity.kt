package com.capitalnowapp.mobile.kotlin.activities

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityChooseDealerBinding
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback
import com.capitalnowapp.mobile.kotlin.adapters.ListFilterAdapter
import com.capitalnowapp.mobile.models.AreaListResponse
import com.capitalnowapp.mobile.models.AreaTableData
import com.capitalnowapp.mobile.models.BrandTableData
import com.capitalnowapp.mobile.models.CityListResponse
import com.capitalnowapp.mobile.models.CityTableData
import com.capitalnowapp.mobile.models.ColorListResponse
import com.capitalnowapp.mobile.models.ColorTableData
import com.capitalnowapp.mobile.models.DealerListResponse
import com.capitalnowapp.mobile.models.DealerTableData
import com.capitalnowapp.mobile.models.GetAreaListReq
import com.capitalnowapp.mobile.models.GetBrandListReq
import com.capitalnowapp.mobile.models.GetBrandListResponse
import com.capitalnowapp.mobile.models.GetCityListReq
import com.capitalnowapp.mobile.models.GetColorListReq
import com.capitalnowapp.mobile.models.GetDealerListReq
import com.capitalnowapp.mobile.models.GetVarientListReq
import com.capitalnowapp.mobile.models.GetVehiclesListReq
import com.capitalnowapp.mobile.models.VarientListResponse
import com.capitalnowapp.mobile.models.VarientTableData
import com.capitalnowapp.mobile.models.VehiclesListResponse
import com.capitalnowapp.mobile.models.VehiclesTableData
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import java.util.Locale


class ChooseDealerActivity : BaseActivity() {

    var dialog: AlertDialog? = null

    private lateinit var area: String
    private lateinit var city: String

    private var cityList: ArrayList<CityTableData>? = ArrayList()
    private var areaList: ArrayList<AreaTableData>? = ArrayList()
    private var dealerList: ArrayList<DealerTableData>? = ArrayList()
    private var brandList: ArrayList<BrandTableData>? = ArrayList()
    private var vehicleList: ArrayList<VehiclesTableData>? = ArrayList()
    private var varientList: ArrayList<VarientTableData>? = null
    private var colorList: ArrayList<ColorTableData>? = null
    private var cityMasterList: ArrayList<MasterData>? = ArrayList()
    private var areaMasterList: ArrayList<MasterData>? = ArrayList()
    private var dealerMasterList: ArrayList<MasterData>? = ArrayList()
    private var vehicleMasterList: ArrayList<MasterData>? = ArrayList()
    private var brandMasterList: ArrayList<MasterData>? = ArrayList()

    private var cityAdapter: ArrayAdapter<CityTableData>? = null
    private var areaAdapter: ArrayAdapter<AreaTableData>? = null
    private var dealerAdapter: ArrayAdapter<DealerTableData>? = null
    private var brandAdapter: ArrayAdapter<BrandTableData>? = null
    private var vehicleAdapter: ArrayAdapter<VehiclesTableData>? = null
    private var varientAdapter: ArrayAdapter<VarientTableData>? = null
    private var colorAdapter: ArrayAdapter<ColorTableData>? = null

    private var selectedCity: String? = null
    private var selectedArea: String? = null
    private var selectedDealerId: String? = null
    private var selectedDealerName: String? = ""
    private var selectedVehicleId: String? = null
    private var selectedVarient: String? = null
    private var selectedColor: String? = null
    private var selectedBrand: String? = null
    private var selectedModel: String? = null
    private var vehicleIdToSend: String? = null


    private var cityListResponse: CityListResponse? = CityListResponse()
    var binding: ActivityChooseDealerBinding? = null
    private lateinit var adapter: ListFilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseDealerBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        binding?.ivBack?.setOnClickListener {
            onBackPressed()
        }
        binding?.tvPickFromMap?.setOnClickListener {
            val intent = Intent(this@ChooseDealerActivity, DealerLocationActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding!!.etCity.threshold = 2
        binding!!.etArea.threshold = 2
        binding!!.etSearchDealer.threshold = 2
        binding!!.etSearchTwoWheeler.threshold = 2
        binding!!.etBrand.threshold = 2

        binding!!.etCity.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedCity = ""
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding!!.etArea.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedArea = ""
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding!!.etSearchDealer.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedDealerId = ""
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding!!.etBrand.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedBrand = ""
            }

            override fun afterTextChanged(s: Editable) {}

        })

        binding!!.etSearchTwoWheeler.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedVehicleId = ""
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding!!.etSearchDealer.setOnClickListener {
            if (dealerMasterList != null && dealerMasterList?.isNotEmpty()!!) {
                showCodesDialog(dealerMasterList!!, 1)
            } else {
                if (selectedBrand != null) {
                    dealerMasterList = ArrayList()
                    showCodesDialog(dealerMasterList!!, 1)
                }
            }
        }

        binding!!.etSearchTwoWheeler.setOnClickListener {
            if (vehicleMasterList != null && vehicleMasterList?.isNotEmpty()!!) {
                showCodesDialog(vehicleMasterList!!, 2)
            }
        }
        binding!!.etBrand.setOnClickListener {
            if (brandMasterList != null && brandMasterList?.isNotEmpty()!!) {
                showCodesDialog(brandMasterList!!, 3)
            }
        }

        binding!!.etCity.setOnClickListener {
            if (cityMasterList != null && cityMasterList?.isNotEmpty()!!) {
                showCodesDialog(cityMasterList!!, 4)
            }
        }

        binding!!.etArea.setOnClickListener {
            if (areaMasterList != null && areaMasterList?.isNotEmpty()!!) {
                showCodesDialog(areaMasterList!!, 5)
            } else {
                if (selectedCity != null) {
                    areaMasterList = ArrayList()
                    showCodesDialog(areaMasterList!!, 5)
                }
            }
        }


        binding!!.tvFinish.setOnClickListener {
            when {
                selectedCity.isNullOrEmpty() -> {
                    displayToast(resources.getString(com.capitalnowapp.mobile.R.string.unique_validation_msg))
                }

                selectedArea.isNullOrEmpty() -> {
                    displayToast(resources.getString(com.capitalnowapp.mobile.R.string.unique_validation_msg))
                }

                selectedBrand.isNullOrEmpty() -> {
                    displayToast(resources.getString(com.capitalnowapp.mobile.R.string.unique_validation_msg))
                }

                selectedDealerId.isNullOrEmpty() && selectedDealerName.isNullOrEmpty() -> {
                    displayToast(resources.getString(com.capitalnowapp.mobile.R.string.unique_validation_msg))
                }

                selectedVehicleId.isNullOrEmpty() -> {
                    displayToast(resources.getString(com.capitalnowapp.mobile.R.string.unique_validation_msg))
                }

                selectedVarient.isNullOrEmpty() -> {
                    displayToast(resources.getString(com.capitalnowapp.mobile.R.string.unique_validation_msg))
                }

                selectedColor.isNullOrEmpty() -> {
                    displayToast("Please select Color")
                }

                else -> {
                    val intent = Intent(this, VehicleDetailsActivity::class.java)
                    intent.putExtra("city", selectedCity.toString().trim())
                    intent.putExtra("area", selectedArea.toString().trim())
                    intent.putExtra("brand", selectedBrand.toString().trim())
                    intent.putExtra("dealer", selectedDealerId.toString().trim())
                    intent.putExtra("dealerName", selectedDealerName)
                    intent.putExtra("vehicle", vehicleIdToSend)
                    intent.putExtra("varient", selectedVarient.toString().trim())
                    intent.putExtra("color", selectedColor.toString().trim())
                    startActivity(intent)
                }
            }
        }
        setEmptyData()
        getCityList()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (sharedPreferences.getBoolean(Constants.From_Vehicle_Details)) {
            finish()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                binding!!.etCity.setText(data?.getStringExtra("selectedCity"))
                binding!!.etArea.setText(data?.getStringExtra(("selectedArea")))

                selectedCity = data!!.getStringExtra("selectedCity")
                selectedArea = data.getStringExtra(("selectedArea"))

                setEmptyData(3)
                getBrandList(selectedCity!!, selectedArea!!)
            }
        }

    private fun setEmptyData() {
        binding!!.etCity.hint = getString(com.capitalnowapp.mobile.R.string.choose_city)
        binding!!.etArea.hint = getString(com.capitalnowapp.mobile.R.string.choose_area)
        binding!!.etSearchDealer.hint = getString(com.capitalnowapp.mobile.R.string.search_dealer)
        binding!!.etSearchTwoWheeler.hint =
            getString(com.capitalnowapp.mobile.R.string.search_two_wheeler)
        binding!!.etBrand.hint = getString(com.capitalnowapp.mobile.R.string.search_brand)

        setVarientList(ArrayList())
        setColorList(ArrayList())
    }


    private fun getColorList(selectedVehicleId: String) {
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this, 0)
        val getColorListReq = GetColorListReq()
        getColorListReq.dealerId = selectedDealerId
        getColorListReq.brand = selectedBrand
        getColorListReq.model = selectedModel
        getColorListReq.twlvVarient = selectedVarient
        val token = userToken
        genericAPIService.getColorList(getColorListReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val colorListResponse = Gson().fromJson(
                responseBody, ColorListResponse::class.java
            )
            if (colorListResponse != null && colorListResponse.status == Constants.STATUS_SUCCESS) {
                setColorList(colorListResponse.colorTableData)
            } else {

            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }
    }

    private fun setColorList(colorTableDataList: List<ColorTableData>?) {
        colorList = ArrayList()
        val colorTableData = ColorTableData()
        colorTableData.twlvColor = "Choose Color"
        colorList?.add(colorTableData)
        colorList?.addAll(colorTableDataList!!)
        colorAdapter = ArrayAdapter<ColorTableData>(
            this,
            R.layout.simple_spinner_item, colorList!!
        )
        colorAdapter?.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerColor.adapter = colorAdapter
        binding!!.spinnerColor.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val colorTableData: ColorTableData =
                        binding!!.spinnerColor.getItemAtPosition(position) as ColorTableData
                    selectedColor = colorTableData.twlvColor
                    vehicleIdToSend = colorTableData.vehicleId
                } else {
                    selectedColor = ""
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun getVarientList() {

        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this, 0)
        val getVarientListReq = GetVarientListReq()
        getVarientListReq.dealerId = selectedDealerId
        getVarientListReq.model = selectedModel
        getVarientListReq.brand = selectedBrand
        val token = userToken
        genericAPIService.getVarientList(getVarientListReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val varientListResponse = Gson().fromJson(
                responseBody, VarientListResponse::class.java
            )
            if (varientListResponse != null && varientListResponse.status == Constants.STATUS_SUCCESS) {
                setVarientList(varientListResponse.VarientTableData)

            } else {

            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }

    }

    private fun setVarientList(varientTableDataList: List<VarientTableData>?) {

        varientList = ArrayList()
        val varientTableData = VarientTableData()
        varientTableData.varientName = "Choose Variant"
        varientList?.add(varientTableData)
        varientList?.addAll(varientTableDataList!!)
        varientAdapter = ArrayAdapter<VarientTableData>(
            this,
            R.layout.simple_spinner_item, varientList!!
        )
        varientAdapter?.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerVarient.adapter = varientAdapter

        setColorList(ArrayList())

        binding!!.spinnerVarient.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val varientTableData: VarientTableData =
                        binding!!.spinnerVarient.getItemAtPosition(position) as VarientTableData
                    selectedVarient = varientTableData.varientName
                    getColorList(selectedVehicleId!!)
                } else {
                    selectedVarient = ""
                    setColorList(ArrayList())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun getVehiclesList(
        selectedCity: String,
        selectedArea: String,
        dealerId: String,
        selectedBrand: String
    ) {
        selectedDealerId = dealerId
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this, 0)
        val getVehiclesListReq = GetVehiclesListReq()
        getVehiclesListReq.dealerId = selectedDealerId
        getVehiclesListReq.brand = this.selectedBrand
        val token = userToken
        genericAPIService.getVehiclesList(getVehiclesListReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val vehiclesListResponse = Gson().fromJson(
                responseBody, VehiclesListResponse::class.java
            )
            if (vehiclesListResponse != null && vehiclesListResponse.status == Constants.STATUS_SUCCESS) {
                //setVehicleList(vehiclesListResponse.vehicleTableData)
                parseVehicleList(vehiclesListResponse.vehicleTableData)
            } else {

            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }
    }

    private fun parseVehicleList(vehicleTableData: List<VehiclesTableData>?) {
        if (vehicleTableData?.isNotEmpty()!!) {
            for (item in vehicleTableData) {
                val masterData = MasterData()
                masterData.id = item.vehicleId
                masterData.name = item.name
                vehicleMasterList?.add(masterData)
                vehicleList?.addAll(vehicleTableData)
            }
        }
    }

    private fun getDealerList(selectedCity: String, selectedArea: String, selectedBrand: String) {
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this, 0)
        val getDealerListReq = GetDealerListReq()
        getDealerListReq.userId = userId
        getDealerListReq.areaName = selectedArea
        getDealerListReq.cityName = selectedCity
        getDealerListReq.brand = this.selectedBrand
        getDealerListReq.requestInput = ""
        val token = userToken
        genericAPIService.getDealerList(getDealerListReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val dealerListResponse = Gson().fromJson(
                responseBody, DealerListResponse::class.java
            )
            if (dealerListResponse != null && dealerListResponse.status == Constants.STATUS_SUCCESS) {
                // setDealerList(dealerListResponse.dealerTableData)
                parseDealerList(dealerListResponse.dealerTableData)

            } else {

            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }
    }

    private fun parseDealerList(dealerTableData: List<DealerTableData>?) {
        if (dealerTableData?.isNotEmpty()!!) {
            for (item in dealerTableData) {
                val masterData = MasterData()
                masterData.id = item.dealerId
                masterData.name = item.name
                dealerMasterList?.add(masterData)
                dealerList?.addAll(dealerTableData)
            }
        }
    }

    private fun getBrandList(selectedCity: String, selectedArea: String) {
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this, 0)
        val getBrandListReq = GetBrandListReq()
        getBrandListReq.areaName = selectedArea
        getBrandListReq.cityName = selectedCity
        val token = userToken
        genericAPIService.getBrandList(getBrandListReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val brandListResponse = Gson().fromJson(
                responseBody, GetBrandListResponse::class.java
            )
            if (brandListResponse != null && brandListResponse.status == Constants.STATUS_SUCCESS) {
                // setDealerList(dealerListResponse.dealerTableData)
                parseBrandList(brandListResponse.brandTableData)

            } else {

            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }

    }

    private fun parseBrandList(brandTableData: List<BrandTableData>?) {
        if (brandTableData?.isNotEmpty()!!) {
            for (item in brandTableData) {
                val masterData = MasterData()
                masterData.name = item.twlvBrand
                brandMasterList?.add(masterData)
                brandList?.addAll(brandTableData)
            }
        }
    }

    private fun getAreaList(selectedCity: String) {
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this, 0)
        val getAreaListReq = GetAreaListReq()
        getAreaListReq.cityName = selectedCity
        getAreaListReq.dealerId
        val token = userToken
        genericAPIService.getAreaList(getAreaListReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val areaListResponse = Gson().fromJson(
                responseBody, AreaListResponse::class.java
            )
            if (areaListResponse != null && areaListResponse.status == Constants.STATUS_SUCCESS) {
                setAreaList(areaListResponse.areaTableData)
            } else {

            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }
    }

    private fun setAreaList(areaTableDataList: List<AreaTableData>?) {
        if (areaTableDataList?.isNotEmpty()!!) {
            for (item in areaTableDataList) {
                val masterData = MasterData()
                masterData.name = item.area
                areaMasterList?.add(masterData)
                areaList?.addAll(areaTableDataList)
            }
        }
    }

    private fun getCityList() {
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this, 0)
        val getCityListReq = GetCityListReq()
        getCityListReq.dealerId = ""
        val token = userToken
        genericAPIService.getCityList(getCityListReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val cityListResponse = Gson().fromJson(
                responseBody,
                CityListResponse::class.java
            )
            if (cityListResponse != null && cityListResponse.status == Constants.STATUS_SUCCESS) {
                setCitiesData(cityListResponse.cityTableData)
                if (cityListResponse.currentCity != null && cityListResponse.currentCity != "") {
                    binding!!.etCity.setText(cityListResponse.currentCity)
                    selectedCity = cityListResponse.currentCity
                } else {
                    Toast.makeText(activityContext, "Current city not found", Toast.LENGTH_SHORT)
                        .show()
                }
                if (selectedCity != null && selectedCity != "") {
                    getAreaList(selectedCity!!)
                } else {
                    Toast.makeText(activityContext, "Please Select City", Toast.LENGTH_SHORT).show()
                }

            } else {

            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }

    }

    private fun setCitiesData(cityTableDataList: List<CityTableData>?) {
        if (cityTableDataList?.isNotEmpty()!!) {
            for (item in cityTableDataList) {
                val masterData = MasterData()
                masterData.name = item.cityName
                cityMasterList?.add(masterData)
                cityList?.addAll(cityTableDataList)
            }
        }
    }

    private fun showCodesDialog(codeArrayList: java.util.ArrayList<MasterData>, flag: Int) {
        try {
            // flag 3= brand, 1= dealer, 2 = vehicle, 4 = city, 5 = area
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(com.capitalnowapp.mobile.R.layout.filter_dialog, null)
            val rvData: RecyclerView = view.findViewById(com.capitalnowapp.mobile.R.id.rvData)
            val tvCustom: TextView = view.findViewById(com.capitalnowapp.mobile.R.id.tvCustom)
            rvData.layoutManager = LinearLayoutManager(this)
            val etSearchCode = view.findViewById<EditText>(com.capitalnowapp.mobile.R.id.etSearch)
            val tvEnterText = view.findViewById<TextView>(com.capitalnowapp.mobile.R.id.tvEnterText)
            etSearchCode.setOnClickListener {

            }
            when (flag) {
                1 -> {
                    etSearchCode.hint = getString(com.capitalnowapp.mobile.R.string.search_dealer)
                }

                2 -> {
                    etSearchCode.hint =
                        getString(com.capitalnowapp.mobile.R.string.search_two_wheeler)
                }

                3 -> {
                    etSearchCode.hint = getString(com.capitalnowapp.mobile.R.string.search_brand)
                }

                4 -> {
                    etSearchCode.hint = getString(com.capitalnowapp.mobile.R.string.search_city)
                }

                5 -> {
                    etSearchCode.hint = getString(com.capitalnowapp.mobile.R.string.choose_area)
                }
            }

            tvCustom.setOnClickListener {
                if (flag == 1) {
                    selectedDealerId = ""
                    selectedDealerName = tvCustom.text.toString().trim()
                    binding?.etSearchDealer?.setText(selectedDealerName)
                    getVehiclesList(
                        selectedCity!!,
                        selectedArea!!,
                        selectedDealerId!!,
                        selectedBrand!!
                    )
                } else if (flag == 5) {
                    binding?.etArea?.setText(tvCustom.text.toString().trim())
                    selectedArea = tvCustom.text.toString().trim()
                    getBrandList(selectedCity!!, selectedArea!!)
                }
                dialog?.dismiss()
            }

            adapter = ListFilterAdapter(this, codeArrayList, SelectedIdCallback { selectedId ->
                try {
                    if (flag == 1) {
                        setEmptyData(5)
                        for (item in 0 until dealerList?.size!!) {
                            if (dealerList!![item].dealerId == selectedId) {
                                binding!!.etSearchDealer.setText(dealerList!![item].name)
                                getVehiclesList(
                                    selectedCity!!, selectedArea!!,
                                    dealerList!![item].dealerId!!,
                                    selectedBrand!!
                                )
                                break
                            }
                        }
                    } else if (flag == 2) {
                        setEmptyData(6)
                        for (item in 0 until vehicleList?.size!!) {
                            if (vehicleList!![item].vehicleId == selectedId) {
                                binding!!.etSearchTwoWheeler.setText(vehicleList!![item].name)
                                selectedVehicleId = vehicleList!![item].vehicleId!!
                                selectedBrand = vehicleList!![item].twlvBrand
                                selectedModel = vehicleList!![item].twlvModel
                                getVarientList()
                                break
                            }
                        }
                    } else if (flag == 3) {
                        setEmptyData(4)
                        dealerMasterList = ArrayList()
                        for (item in 0 until brandList?.size!!) {
                            if (brandList!![item].twlvBrand == selectedId) {
                                binding!!.etBrand.setText(brandList!![item].twlvBrand)
                                selectedBrand = brandList!![item].twlvBrand
                                getDealerList(selectedCity!!, selectedArea!!, selectedBrand!!)
                                break
                            }
                        }
                    } else if (flag == 4) {
                        setEmptyData(2)
                        for (item in 0 until cityList?.size!!) {
                            if (cityList!![item].cityName == selectedId) {
                                binding!!.etCity.setText(cityList!![item].cityName)
                                selectedCity = cityList!![item].cityName
                                getAreaList(selectedCity!!)
                                break
                            }
                        }
                    } else if (flag == 5) {
                        setEmptyData(3)
                        for (item in 0 until areaList?.size!!) {
                            if (areaList!![item].area == selectedId) {
                                binding!!.etArea.setText(areaList!![item].area)
                                selectedArea = areaList!![item].area
                                getBrandList(selectedCity!!, selectedArea!!)
                                break
                            }
                        }
                    }
                    dialog?.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            rvData.adapter = adapter
            if (flag != 3 && flag != 2) {
                filterData("", ArrayList(), flag, tvCustom)
            }
            etSearchCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (flag != 3 && flag != 2) {
                        if (s != "" && s.length >= 2) {
                            filterData(s, codeArrayList, flag, tvCustom)
                        } else {
                            filterData(s, ArrayList(), flag, tvCustom)
                        }
                    } else {
                        if (s != "") {
                            filterData(s, codeArrayList, flag, tvCustom)
                        }
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

    fun filterData(
        s: CharSequence,
        countryCodeArrayList: ArrayList<MasterData>,
        flag: Int,
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
        if ((flag == 1 && filterList.size == 0) || (flag == 5 && filterList.size == 0)) {
            tvCustom.visibility = View.VISIBLE
            tvCustom.text = s
        } else {
            tvCustom.visibility = View.GONE
            tvCustom.text = ""
        }
    }

    fun setEmptyData(flag: Int) {
        when (flag) {
            2 -> {

                setAreaList(ArrayList())
                areaMasterList = ArrayList()
                binding?.etArea?.setText(getString(com.capitalnowapp.mobile.R.string.choose_area))

                parseBrandList(ArrayList())
                brandMasterList = ArrayList()
                binding?.etBrand?.setText(getString(com.capitalnowapp.mobile.R.string.search_brand))

                parseDealerList(ArrayList())
                dealerMasterList = ArrayList()
                binding?.etSearchDealer?.setText(getString(com.capitalnowapp.mobile.R.string.search_dealer))

                parseVehicleList(ArrayList())
                vehicleMasterList = ArrayList()
                binding?.etSearchTwoWheeler?.setText(getString(com.capitalnowapp.mobile.R.string.search_two_wheeler))

                setVarientList(ArrayList())

                setColorList(ArrayList())
            }

            3 -> {
                parseBrandList(ArrayList())
                brandMasterList = ArrayList()
                binding?.etBrand?.setText(getString(com.capitalnowapp.mobile.R.string.search_brand))

                parseDealerList(ArrayList())
                dealerMasterList = ArrayList()
                binding?.etSearchDealer?.setText(getString(com.capitalnowapp.mobile.R.string.search_dealer))

                parseVehicleList(ArrayList())
                vehicleMasterList = ArrayList()
                binding?.etSearchTwoWheeler?.setText(getString(com.capitalnowapp.mobile.R.string.search_two_wheeler))

                setVarientList(ArrayList())

                setColorList(ArrayList())
            }

            4 -> {
                parseDealerList(ArrayList())
                dealerMasterList = ArrayList()
                binding?.etSearchDealer?.setText(getString(com.capitalnowapp.mobile.R.string.search_dealer))

                parseVehicleList(ArrayList())
                vehicleMasterList = ArrayList()
                binding?.etSearchTwoWheeler?.setText(getString(com.capitalnowapp.mobile.R.string.search_two_wheeler))

                setVarientList(ArrayList())

                setColorList(ArrayList())
            }

            5 -> {
                parseVehicleList(ArrayList())
                vehicleMasterList = ArrayList()
                binding?.etSearchTwoWheeler?.setText(getString(com.capitalnowapp.mobile.R.string.search_two_wheeler))

                setVarientList(ArrayList())

                setColorList(ArrayList())
            }

            6 -> {
                setVarientList(ArrayList())

                setColorList(ArrayList())

            }

            7 -> {
                setColorList(ArrayList())
            }
        }
    }
}





package com.capitalnowapp.mobile.kotlin.activities.offer

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View.GONE
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityRegistrationWorkBinding
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.ListFilterAdapter
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataBankListResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.SaveBankDetailsReq
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class RegistrationBankDetailsActivity : BaseActivity() {

    private var selectedBankId: String = ""
    private var selectedBankName: String = ""
    private var activity: AppCompatActivity? = null
    private var bankList: ArrayList<ProfileFormDataBankListResponse>? = ArrayList()
    private var bankMasterList: ArrayList<MasterData>? = ArrayList()
    private var binding: ActivityRegistrationWorkBinding? = null
    private var selectedBankAccount: String? = ""
    private var selectedAccountType: String? = ""
    private var accountHolderName: String? = ""
    private var accountNumber: String? = ""
    private var ifscCode: String? = ""
    private var validationMsg = ""
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var csGenericResponse = CSGenericResponse()
    private var accountType: String? = ""
    var accountTypeListMap: LinkedHashMap<String, String>? = null
    var accountTypeKeys: Array<String>? = null
    private lateinit var adapter: ListFilterAdapter

    var dialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationWorkBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()
        binding!!.etSelectBank.setOnClickListener {
            getBanksList()
            if (bankMasterList != null && bankMasterList?.isNotEmpty()!!) {
                showCodesDialog(bankMasterList!!, 1)
            }else{
                getBanksList()
            }
        }
        binding?.ivBack?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
        accountTypeListMap = LinkedHashMap<String, String>()
        accountTypeListMap!!["savings"] = "savings"
        accountTypeListMap!!["salary"] = "salary"
        accountTypeKeys = accountTypeListMap!!.keys.toTypedArray()

        binding?.etBankType?.setOnClickListener {
            if (accountTypeKeys != null && accountTypeKeys!!.isNotEmpty()) {
                accountTypeDialog()
            }
        }
        binding?.tvNext?.setOnClickListener {
            validateBankDetails()
        }

    }

    private fun accountTypeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(accountTypeKeys) { _, which ->
            binding?.etBankType?.setText(accountTypeKeys?.get(which))
            accountType = accountTypeListMap?.get(accountTypeKeys?.get(which)!!)
        }
        builder.show()
    }
    private fun showCodesDialog(codeArrayList: java.util.ArrayList<MasterData>, flag: Int) {
        try {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(com.capitalnowapp.mobile.R.layout.filter_dialog, null)
            val rvData: RecyclerView = view.findViewById(com.capitalnowapp.mobile.R.id.rvData)
            val tvCustom: TextView = view.findViewById(com.capitalnowapp.mobile.R.id.tvCustom)
            tvCustom.visibility = GONE
            rvData.layoutManager = LinearLayoutManager(this)
            val etSearchCode = view.findViewById<EditText>(com.capitalnowapp.mobile.R.id.etSearch)
            if (flag == 1) {
                etSearchCode.hint = getString(com.capitalnowapp.mobile.R.string.select_bank)
            }

            adapter = ListFilterAdapter(this, codeArrayList, SelectedIdCallback { selectedId ->
                try {
                    if (flag == 1) {
                        //setEmptyData(2)
                        for (item in 0 until bankList?.size!!) {
                            if (bankList!![item].id.toString() == selectedId) {
                                binding!!.etSelectBank.setText(bankList!![item].razorpayBankName)
                                selectedBankId = bankList!![item].id.toString()
                                selectedBankName = bankList!![item].razorpayBankName.toString()
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

    private fun getBanksList() {
        if (profileFormDataResponse.profileformData?.pBankLists?.isNotEmpty()!!) {
            bankList = ArrayList()
            bankMasterList = ArrayList()
            val vehicleTableData = profileFormDataResponse.profileformData?.pBankLists
            if (vehicleTableData != null) {
                for (item in vehicleTableData) {
                    val masterData = MasterData()
                    masterData.id = item?.id.toString()
                    masterData.name = item?.razorpayBankName
                    bankMasterList?.add(masterData)
                    bankList?.add(item!!)
                }
            }
        }
    }

    private fun validateBankDetails() {
        try{
            selectedBankAccount = binding?.etSelectBank?.text.toString().trim { it <= ' ' }
            selectedAccountType = binding?.etBankType?.text.toString().trim { it <= ' ' }
            accountHolderName = binding?.etHolderName?.text.toString().trim { it <= ' ' }
            accountNumber = binding?.etAccountNo?.text.toString().trim { it <= ' ' }
            ifscCode = binding?.etIfscCode?.text.toString().trim { it <= ' ' }
            var count = 0
            if (selectedBankAccount!!.isEmpty()) {
                validationMsg = "Select Bank Account"
                count++
            } else if (selectedAccountType!!.isEmpty()) {
                validationMsg = "Select Account Type"
                count++
            } else if (accountHolderName!!.isEmpty()) {
                validationMsg = "Account Holder Name is required and can't be empty"
                count++
            } else if (accountNumber!!.toString().isEmpty()) {
                validationMsg = "Account Number is required and can't be empty"
                count++
            } else if (ifscCode!!.toString().isEmpty()) {
                validationMsg = "IFSC is required and can't be empty"
                count++
            }
            if (count > 0) {
                Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
            } else {
                saveBankDetails()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun saveBankDetails() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val saveBankDetailsReq = SaveBankDetailsReq()
            saveBankDetailsReq.pageNo = "16"
            saveBankDetailsReq.pSalaryBankAccount = selectedBankAccount
            saveBankDetailsReq.pBankAccountType = accountType
            saveBankDetailsReq.pBankAccountHolder = accountHolderName
            saveBankDetailsReq.pAccountNo = accountNumber
            saveBankDetailsReq.pIfscCode = ifscCode
            val token = userToken
            genericAPIService.saveBankDetails(saveBankDetailsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                csGenericResponse =
                    Gson().fromJson(responseBody, CSGenericResponse::class.java)
                if (csGenericResponse.status == true) {
                    getApplyLoanDataBase(true)
                }else {
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

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 16
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                    setBankDetailsData()
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

    private fun setBankDetailsData() {
        try {
                binding?.etSelectBank?.setText(profileFormDataResponse.profileformData?.pSalaryBankAccount)
                binding?.etBankType?.setText(profileFormDataResponse.profileformData?.pBankAccountType)
                binding?.etHolderName?.setText(profileFormDataResponse.profileformData?.pBankAccountHolder)
                binding?.etAccountNo?.setText(profileFormDataResponse.profileformData?.pAccountNo!!.toString())
                binding?.etIfscCode?.setText(profileFormDataResponse.profileformData?.pIfscCode)

        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
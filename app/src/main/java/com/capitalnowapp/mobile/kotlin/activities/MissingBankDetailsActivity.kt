package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.databinding.ActivityMissingBankDetailsBinding
import com.capitalnowapp.mobile.models.GetUpdateBankDataReq
import com.capitalnowapp.mobile.models.UpdateBankDataResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class MissingBankDetailsActivity : BaseActivity() {
    var binding: ActivityMissingBankDetailsBinding? = null
    private var getUpdateBankDataReq = GetUpdateBankDataReq()
    var validationMsg = ""
    var dynamicText = ""
    var isAccountNumReq = false
    var isIFSCReq = false
    private var activity: AppCompatActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissingBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {

        binding!!.tvSubmit.setOnClickListener {
            if (validateData()) {
                updateBankData()
            }
        }
        binding?.tvBack?.setOnClickListener{
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        val intent = intent
        val bankAccountNumber = intent.getIntExtra("bank_account_number", -1)
        val ifcsCode = intent.getIntExtra("ifsc_code", -1)
        if (bankAccountNumber == 0 && ifcsCode == 0) {
            binding!!.etAccountNumber.visibility = View.VISIBLE
            binding!!.etReEnterAccountNumber.visibility = View.VISIBLE
            binding!!.etIFSC.visibility = View.VISIBLE
            isAccountNumReq = true
            isIFSCReq = true
        } else if (bankAccountNumber == 0) {
            isAccountNumReq = true
            isIFSCReq = false
            binding!!.etAccountNumber.visibility = View.VISIBLE
            binding!!.etReEnterAccountNumber.visibility = View.VISIBLE
        } else if (ifcsCode == 0) {
            binding!!.etIFSC.visibility = View.VISIBLE
            isAccountNumReq = false
            isIFSCReq = true
        }
        if (intent.extras != null) {
            dynamicText = intent.getStringExtra("bank_note")!!
            binding!!.tvMissingDetailsText.text = dynamicText
        }
    }

    private fun validateData(): Boolean {
        if (isAccountNumReq) {
            val accountNumber = binding!!.etAccountNumber!!.text.toString().trim { it <= ' ' }
            val reAccountNumber = binding!!.etReEnterAccountNumber!!.text.toString().trim { it <= ' ' }
            if (accountNumber.isEmpty()) {
                validationMsg = "Account Number is required and can't be empty"
                displayToast(validationMsg)
                return false
            } else if (reAccountNumber.isEmpty()) {
                validationMsg = "Re-Enter Account Number is required and can't be empty"
                displayToast(validationMsg)
                return false
            }else if(accountNumber != reAccountNumber){
                validationMsg = "Account Number & Re-Enter Account Number is not matching"
                displayToast(validationMsg)
                return false
            }
        }
        return if (isIFSCReq) {
            val ifscCode = binding!!.etIFSC!!.text.toString().trim { it <= ' ' }
            if (ifscCode.isEmpty() && ifscCode.length == 11) {
                validationMsg = "Please enter valid IFSC Code"
                displayToast(validationMsg)
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    private fun updateBankData() {
        try {
            val genericAPIService = GenericAPIService(this)
            val getUpdateBankDataReq = GetUpdateBankDataReq()
            val accountNumber = binding!!.etAccountNumber!!.text.toString().trim { it <= ' ' }
            val ifscCode = binding!!.etIFSC!!.text.toString().trim { it <= ' ' }
            getUpdateBankDataReq.bankAccountNumber = accountNumber
            getUpdateBankDataReq.ifscCode = ifscCode
            val token = userToken
            genericAPIService.updateBankData(getUpdateBankDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                val updateBankDataResponse = Gson().fromJson(
                        responseBody, UpdateBankDataResponse::class.java
                )
                if (updateBankDataResponse != null && updateBankDataResponse!!.status == Constants.STATUS_SUCCESS) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("from", "missingbankdetails")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                   /* val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)*/
                    /*(activity as DashboardActivity).getApplyLoanData(true)*/
                    /*startActivity(Intent(this@MissingBankDetailsActivity, DashboardActivity::class.java))
                    finishAffinity()*/

                } else {
                    //CNAlertDialog.showAlertDialog(this, resources.getString(R.string.title_alert), updateBankDataResponse.message)
                    CNAlertDialog.showStatusWithCallback(
                            this,
                            resources.getString(R.string.title_alert),
                            updateBankDataResponse.message,
                            R.drawable.failure_new, R.color.cb_errorRed
                    )
                    CNAlertDialog.setRequestCode(1)
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

    private fun isValidIfscCode(ifscCode: String, isOfficeNo: Boolean): Boolean {
        return if (ifscCode.isNotEmpty()) {
            ifscCode.length == 11
        } else {
            false
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
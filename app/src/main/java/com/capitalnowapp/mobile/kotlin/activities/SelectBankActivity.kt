package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivitySelectBankBinding
import com.capitalnowapp.mobile.kotlin.adapters.SelectBankAdapter
import com.capitalnowapp.mobile.models.GetMandateList
import com.capitalnowapp.mobile.models.GetMandateListResponse
import com.capitalnowapp.mobile.models.SavePrimaryMandateReq
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class SelectBankActivity : BaseActivity() {
    private var binding: ActivitySelectBankBinding? = null
    private lateinit var adapter: SelectBankAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBankBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView(){
        getMandateList()

        binding!!.tvContinue.setOnClickListener {
            if(adapter.getSelectedItem().cemBankAcId!=null && adapter.getSelectedItem().cemBankAcId!!.isNotEmpty()) {
                CNAlertDialog.dismiss()
                savePrimaryMandate()
            }
        }
        binding!!.rvSelectBank.layoutManager = LinearLayoutManager(this)

    }

    private fun savePrimaryMandate() {
        try{
            CNProgressDialog.showProgressDialog(this,Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this)
            val savePrimaryMandateReq = SavePrimaryMandateReq()
            savePrimaryMandateReq.cemId = adapter.getSelectedItem().cemId
            val token = userToken
            genericAPIService.savePrimaryMandate(savePrimaryMandateReq,token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val getMandateListResponse = Gson().fromJson(responseBody, GetMandateListResponse::class.java)
                if(getMandateListResponse != null && getMandateListResponse.status == Constants.STATUS_SUCCESS){
                    CNAlertDialog.dismiss()
                    sharedPreferences.putBoolean("shouldRefreshDashboardScreen", true)
                    startActivity(Intent(this@SelectBankActivity, DashboardActivity::class.java))
                    finishAffinity()
                }else{
                    CNAlertDialog.showAlertDialog(this, resources.getString(R.string.title_alert), getMandateListResponse.message)
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                    CNProgressDialog.hideProgressDialog()
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun getMandateList() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this)
            val getMandateListReq = GetMandateList()
            val token = userToken
            genericAPIService.getMandateList(getMandateListReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val getMandateListResponse = Gson().fromJson(
                    responseBody, GetMandateListResponse::class.java
                )
                if (getMandateListResponse != null && getMandateListResponse.status == Constants.STATUS_SUCCESS) {
                    if(getMandateListResponse.list!=null && getMandateListResponse.list!!.isNotEmpty()){
                        adapter = SelectBankAdapter(getMandateListResponse.list!!)
                   binding!!.rvSelectBank.adapter = adapter
                }
                }else {

                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                    CNProgressDialog.hideProgressDialog()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
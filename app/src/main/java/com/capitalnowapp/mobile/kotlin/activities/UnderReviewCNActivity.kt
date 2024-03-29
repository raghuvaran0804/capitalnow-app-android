package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityUnderReviewCnactivityBinding
import com.capitalnowapp.mobile.models.GenericRequest
import com.capitalnowapp.mobile.models.GetProcessPageContentResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class UnderReviewCNActivity : BaseActivity() {
    private var getProcessPageContentResponse: GetProcessPageContentResponse? = null
    private var binding : ActivityUnderReviewCnactivityBinding? = null
    private var activity: AppCompatActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnderReviewCnactivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        try {
            getProcessPageContent()
            binding?.tvNext?.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            binding?.tvBack?.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun getProcessPageContent() {
        try{
            //CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val genericRequest = GenericRequest()
            val token = userToken
            genericAPIService.getProcessPageContent(genericRequest, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getProcessPageContentResponse = Gson().fromJson(
                    responseBody,
                    GetProcessPageContentResponse::class.java
                )
                if (getProcessPageContentResponse != null && getProcessPageContentResponse!!.status == true) {
                    setData()
                } else {
                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(R.string.title_alert),
                        getProcessPageContentResponse!!.message
                    )
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun setData() {
        try{
            binding?.tvStatusText?.text  = getProcessPageContentResponse?.data?.title
            binding?.tvSubText?.text = getProcessPageContentResponse?.data?.subTitle
        }catch (e:Exception){
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
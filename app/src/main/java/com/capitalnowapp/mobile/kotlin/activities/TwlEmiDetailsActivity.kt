package com.capitalnowapp.mobile.kotlin.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.ActivityTwlEmiDetailsBinding
import com.capitalnowapp.mobile.kotlin.adapters.TwlEmiDetailsAdapter
import com.capitalnowapp.mobile.models.TwlEmiDetailsList
import com.capitalnowapp.mobile.models.TwlLoanDetailsReq
import com.capitalnowapp.mobile.models.TwlLoanDetailsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class TwlEmiDetailsActivity : BaseActivity() {
    private var binding: ActivityTwlEmiDetailsBinding? = null
    private var twlEmiDetailsAdapter: TwlEmiDetailsAdapter? = null
    private var twlEmiDetailsList: List<TwlEmiDetailsList> = ArrayList()
    private  var lid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTwlEmiDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initView()
    }

    private fun initView() {
        lid = intent.getStringExtra("lid")!!

        binding!!.rvEmiDetails.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding!!.ivEmiBack.setOnClickListener {

            onBackPressed()
        }
        twlEmiDetails()

    }

    private fun twlEmiDetails() {
        try {
            val genericAPIService = GenericAPIService(this)
            val twlLoanDetailsReq = TwlLoanDetailsReq()
            twlLoanDetailsReq.twlId = lid.toInt()
            twlLoanDetailsReq.userId = userDetails.userId
            val token = userToken
            genericAPIService.twlLoanDetails(twlLoanDetailsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                val twlLoanDetailsResponse = Gson().fromJson(
                    responseBody, TwlLoanDetailsResponse::class.java
                )
                if (twlLoanDetailsResponse != null && twlLoanDetailsResponse.status == Constants.STATUS_SUCCESS) {
                    twlEmiDetailsList = twlLoanDetailsResponse.twlLoanDetailData?.twlEmiDetailsList!!
                    twlEmiDetailsAdapter = TwlEmiDetailsAdapter(twlEmiDetailsList, TwlEmiDetailsActivity())
                    binding!!.rvEmiDetails.adapter = twlEmiDetailsAdapter
                } else {

                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}
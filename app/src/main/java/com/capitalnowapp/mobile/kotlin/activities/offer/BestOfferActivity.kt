package com.capitalnowapp.mobile.kotlin.activities.offer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityBestOfferBinding
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class BestOfferActivity : BaseActivity() {
    private var binding: ActivityBestOfferBinding? = null
    private var activity: AppCompatActivity? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var csGenericResponse = CSGenericResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBestOfferBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()
        generateOffer()
    }

    private fun generateOffer() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 30
            val token = userToken
            genericAPIService.generateOffer(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                csGenericResponse =
                    Gson().fromJson(responseBody, CSGenericResponse::class.java)
                if(csGenericResponse.status == true){
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
            profileFormDataReq.pageNo = 30
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true ) {
                    setBestOfferData()
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

    private fun setBestOfferData() {
        try {
            if(profileFormDataResponse.partnerInfo?.lpLogo !=null){
                Glide.with(this).load(profileFormDataResponse.partnerInfo?.lpLogo)
                    .into(binding?.ivCsLogo!!)
            }
            binding?.tvLoanPartnerText?.text = profileFormDataResponse.partnerInfo?.lpName
            binding?.tvText?.text = profileFormDataResponse.profileformData?.text
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


}
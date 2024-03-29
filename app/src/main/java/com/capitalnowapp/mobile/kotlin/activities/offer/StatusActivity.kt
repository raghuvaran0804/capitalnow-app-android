package com.capitalnowapp.mobile.kotlin.activities.offer

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityStatusBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.models.ContactUsResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class StatusActivity : BaseActivity() {
    private var activity: AppCompatActivity? = null
    private var binding: ActivityStatusBinding? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var genericResponse: ContactUsResponse = ContactUsResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        profileFormData()

        if(profileFormDataResponse.partnerInfo?.lpLogo !=null){
            Glide.with(this).load(profileFormDataResponse.partnerInfo?.lpLogo)
                .into(binding?.ivCsLogo!!)
        }
        binding?.ivHelp?.setOnClickListener {
            showHelpPopup()
        }
        binding?.tvNext?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showHelpPopup() {
        val alertDialog = Dialog(this)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.cn_help_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(true)
        val tvEmailText = alertDialog.findViewById<TextView>(R.id.tvEmailText)
        val ivCnLogo = alertDialog.findViewById<ImageView>(R.id.ivCnLogo)
        val tvCall = alertDialog.findViewById<TextView>(R.id.tvCall)
        val ivCancel = alertDialog.findViewById<ImageView>(R.id.ivCancel)
        tvEmailText.text = profileFormDataResponse.offerHelp?.email.toString()
        tvCall.text = profileFormDataResponse.offerHelp?.phone.toString()
        if(profileFormDataResponse.partnerInfo?.lpLogo !=null){
            Glide.with(this).load(profileFormDataResponse.partnerInfo?.lpLogo)
                .into(ivCnLogo)
        }
        tvEmailText.setOnClickListener {
            alertDialog.dismiss()
            composeEmail()
        }
        tvCall.setOnClickListener {
            callToNum()
            alertDialog.dismiss()
        }
        ivCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun callToNum() {
        try {
            if (genericResponse.phone != "") {
                val num = genericResponse.phone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$num")
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun composeEmail() {
        try {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", genericResponse.email, null))
            startActivity(Intent.createChooser(emailIntent, "Choose to send email..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 27
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
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
        try{

            if (profileFormDataResponse.profileformData?.status=="success") {
                binding?.llThankYou?.visibility = View.VISIBLE
                binding?.llFailure?.visibility = View.GONE
                binding?.llHold?.visibility = View.GONE
            } else if (profileFormDataResponse.profileformData?.status=="failed"){
                binding?.llThankYou?.visibility = View.GONE
                binding?.llFailure?.visibility = View.VISIBLE
                binding?.llHold?.visibility = View.GONE
            } else if (profileFormDataResponse.profileformData?.status=="hold"){
                binding?.llThankYou?.visibility = View.GONE
                binding?.llFailure?.visibility = View.GONE
                binding?.llHold?.visibility = View.VISIBLE
            }
            binding?.tvSubmitSuccessfully?.setText(profileFormDataResponse.profileformData?.thankYou1)
            binding?.tvCallText?.setText(profileFormDataResponse.profileformData?.thankYou2)
            binding?.tvDeniedText?.setText(profileFormDataResponse.profileformData?.denied1)
            binding?.tvReApply?.setText(profileFormDataResponse.profileformData?.denied2)
            binding?.tvHoldText?.setText(profileFormDataResponse.profileformData?.hold1)
            binding?.tvrReachOut?.setText(profileFormDataResponse.profileformData?.hold2)
            if(profileFormDataResponse.offerHelp?.icon !=null){
                Glide.with(this).load(profileFormDataResponse.offerHelp?.icon)
                    .into(binding?.ivHelp!!)
            }
            if(profileFormDataResponse.partnerInfo?.lpLogo !=null){
                Glide.with(this).load(profileFormDataResponse.partnerInfo?.lpLogo)
                    .into(binding?.ivCsLogo!!)
            }
            binding?.tvLoanPartnerText?.text = profileFormDataResponse.partnerInfo?.lpName
            binding?.llTop?.setBackgroundColor(profileFormDataResponse.partnerInfo?.lpThemeColor!!.toInt())

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}
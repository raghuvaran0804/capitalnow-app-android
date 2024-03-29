package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentRewardRedeemedCouponNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.models.rewardsNew.GetEmailCouponDetailsReq
import com.capitalnowapp.mobile.models.rewardsNew.GetEmailCouponDetailsResponse
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponDetailsReq
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponDetailsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_main.toolbar


class RewardRedeemedCouponNewFragment : Fragment() {
    private var binding: FragmentRewardRedeemedCouponNewBinding? = null
    private var myClipboard: ClipboardManager? = null
    private var activity: Activity? = null
    private var myClip: ClipData? = null
    private var redeemedLogId: String = ""
    private var email: String? = ""
    private var getRedeemCouponDetailsResponse = GetRedeemCouponDetailsResponse()
    private var getEmailCouponDetailsResponse = GetEmailCouponDetailsResponse()
    private var isEmailSent: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myClipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRewardRedeemedCouponNewBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as DashboardActivity).toolbar.visibility = View.GONE
        redeemedLogId = arguments?.getString("redeemedLogId")!!
        getRedeemCouponDetails()

        binding?.tvSendEmail?.setOnClickListener {
            validateEmail()
        }


        binding?.tvNext?.setOnClickListener {
            (activity as DashboardActivity).replaceFrag(RewardPointsNewFragment(), "", null)
        }
        binding?.tvCopy?.setOnClickListener {
           copyText()
        }
        binding?.ivBack?.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }

    }

    private fun copyText() {
        val myClip1 = ClipData.newPlainText("text", binding?.tvVoucherCode?.text.toString())
        myClipboard?.setPrimaryClip(myClip1)
        Toast.makeText(context, "Coupon Code Copied",
            Toast.LENGTH_LONG).show()

    }

    private fun validateEmail() {
        try{
            email = binding?.tvEmail?.text.toString().trim { it <= ' ' }

            if(email!!.isEmpty()){
                Toast.makeText(context, "Please enter Email", Toast.LENGTH_LONG).show()
            }else {
                emailCouponDetails()
            }

        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun emailCouponDetails() {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getEmailCouponDetailsReq = GetEmailCouponDetailsReq()
            getEmailCouponDetailsReq.redeemLogId = redeemedLogId
            getEmailCouponDetailsReq.mailId = email
            val token = (activity as BaseActivity).userToken
            genericAPIService.getEmailCouponDetails(getEmailCouponDetailsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getEmailCouponDetailsResponse =
                    Gson().fromJson(responseBody, GetEmailCouponDetailsResponse::class.java)
                if (getEmailCouponDetailsResponse != null && getEmailCouponDetailsResponse.status == true) {
                    Toast.makeText(context, "Email Sent Successfully", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(context, getRedeemCouponDetailsResponse.message, Toast.LENGTH_LONG).show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun getRedeemCouponDetails() {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getRedeemCouponDetailsReq = GetRedeemCouponDetailsReq()
            getRedeemCouponDetailsReq.redeemLogId = redeemedLogId
            val token = (activity as BaseActivity).userToken
            genericAPIService.getRedeemCouponDetails(getRedeemCouponDetailsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getRedeemCouponDetailsResponse =
                    Gson().fromJson(responseBody, GetRedeemCouponDetailsResponse::class.java)
                if (getRedeemCouponDetailsResponse != null && getRedeemCouponDetailsResponse.status == true) {
                    setData(getRedeemCouponDetailsResponse)
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                    CNProgressDialog.hideProgressDialog()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(redeemCouponDetailsResponse: GetRedeemCouponDetailsResponse?) {
        try {
            binding?.tvPoints?.text =
                redeemCouponDetailsResponse?.redeemCouponDetailsData?.rewardPointsAvailable
            binding?.tvVoucherCode?.text =
                redeemCouponDetailsResponse?.redeemCouponDetailsData?.cardNumber
            binding?.tvExpireText?.text =
                redeemCouponDetailsResponse?.redeemCouponDetailsData?.expirationDate
            binding?.tvRedmeed?.text = redeemCouponDetailsResponse?.redeemCouponDetailsData?.redeemedPoints +" were redeemed "
            binding?.tvWorth?.text = redeemCouponDetailsResponse?.redeemCouponDetailsData?.cupValue
            binding?.tvCouponPin?.text =
                redeemCouponDetailsResponse?.redeemCouponDetailsData?.cardPin
            Glide.with(this).load(redeemCouponDetailsResponse?.redeemCouponDetailsData?.cupImageUrl)
                .into(binding?.ivCouponImg!!)
            binding?.tvActivationUrl?.text = redeemCouponDetailsResponse?.redeemCouponDetailsData?.activationUrl
            binding?.tvActivationCode?.text = redeemCouponDetailsResponse?.redeemCouponDetailsData?.activationCode

            binding?.tvEmail?.setText(redeemCouponDetailsResponse?.redeemCouponDetailsData?.userEmail)
            binding?.tvDetailsText?.text = redeemCouponDetailsResponse?.redeemCouponDetailsData?.cupDescription

            binding?.tvTerms?.setOnClickListener {
                val termsData =
                    redeemCouponDetailsResponse?.redeemCouponDetailsData?.termsAndConditionUrl
                setTerms(termsData)
            }
            binding?.tvActivationUrl?.setOnClickListener {
                val activationUrl = redeemCouponDetailsResponse?.redeemCouponDetailsData?.activationUrl
                showActivationUrl(activationUrl)

            }
            if(redeemCouponDetailsResponse?.redeemCouponDetailsData?.reedemedAt!!.contains("WEBSITE")){
                binding?.tvWebsite?.visibility = View.VISIBLE
            } else {
                binding?.tvWebsite?.visibility = View.GONE
            }
            if(redeemCouponDetailsResponse?.redeemCouponDetailsData?.reedemedAt!!.contains("STORE")){
                binding?.tvStore?.visibility = View.VISIBLE
            }else {
                binding?.tvStore?.visibility = View.GONE
            }
            if(redeemCouponDetailsResponse?.redeemCouponDetailsData?.activationUrl == null || redeemCouponDetailsResponse?.redeemCouponDetailsData?.activationUrl.equals("")){
                binding?.tvActivationUrl?.visibility = View.GONE
                binding?.tvActivationUrlText?.visibility = View.GONE
            } else {
                binding?.tvActivationUrl?.visibility = View.VISIBLE
                binding?.tvActivationUrlText?.visibility = View.VISIBLE
            }
            if (redeemCouponDetailsResponse?.redeemCouponDetailsData?.activationCode == null || redeemCouponDetailsResponse?.redeemCouponDetailsData?.activationCode.equals("")){
                binding?.tvActivationCode?.visibility = View.GONE
                binding?.tvActivationCodeText?.visibility = View.GONE
            } else {
                binding?.tvActivationCode?.visibility = View.VISIBLE
                binding?.tvActivationCodeText?.visibility = View.VISIBLE
            }

            if (redeemCouponDetailsResponse?.redeemCouponDetailsData?.isEmailSent == true) {
                binding?.tvEmail?.isEnabled = false
                binding?.tvSendEmail?.isEnabled = false
            } else {
                binding?.tvEmail?.isEnabled = true
                binding?.tvSendEmail?.isEnabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showActivationUrl(activationUrl: String?) {
        try{
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse(activationUrl)
            startActivity(openURL)

        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun setTerms(termsData: String?) {

        try {
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse(termsData)
            startActivity(openURL)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
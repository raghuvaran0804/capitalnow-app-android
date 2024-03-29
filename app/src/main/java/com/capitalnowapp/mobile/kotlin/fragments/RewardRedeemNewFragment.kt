package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentRewardRedeemNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponInfoReq
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponInfoResponse
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponReq
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_main.toolbar


class RewardRedeemNewFragment : Fragment() {
    private var binding: FragmentRewardRedeemNewBinding? = null
    private lateinit var mActivityRef: BaseActivity
    private var activity: Activity? = null
    private var getCouponInfoResponse = GetCouponInfoResponse()
    private var getRedeemCouponResponse = GetRedeemCouponResponse()
    private var cupId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRewardRedeemNewBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).toolbar.visibility = View.GONE
        cupId = arguments?.getString("cupId")!!
        getCouponInfo()

        binding?.tvRedeem?.setOnClickListener {
            //(activity as DashboardActivity).replaceFrag(RewardRedeemedCouponNewFragment(), "", null)
            showPopup(getRedeemCouponResponse,getCouponInfoResponse)
        }
        binding?.ivBack?.setOnClickListener {
            (activity as DashboardActivity).replaceFrag(RewardPointsNewFragment(), "", null)
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivityRef = context as BaseActivity
    }

    private fun showPopup(
        getRedeemCouponResponse: GetRedeemCouponResponse,
        getCouponInfoResponse: GetCouponInfoResponse
    ) {
        val alertDialog = Dialog(requireActivity())
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.redeem_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        val tvOk = alertDialog.findViewById<TextView>(R.id.tvOk)
        val tvNotNow = alertDialog.findViewById<TextView>(R.id.tvNotNow)
        val tvPoints = alertDialog.findViewById<TextView>(R.id.tvPoints)
        tvPoints.text = getCouponInfoResponse.couponInfoData?.cupPoints.toString()
        tvOk.setOnClickListener { view: View? ->
            alertDialog.dismiss()
            redeemCoupon()
        }

        tvNotNow.setOnClickListener { view: View? ->
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun redeemCoupon() {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getRedeemCouponReq = GetRedeemCouponReq()
            getRedeemCouponReq.cupId = cupId
            val token = (activity as BaseActivity).userToken
            genericAPIService.getRedeemCoupon(getRedeemCouponReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getRedeemCouponResponse =
                    Gson().fromJson(responseBody, GetRedeemCouponResponse::class.java)
                if (getRedeemCouponResponse != null && getRedeemCouponResponse.status == true) {
                    (activity as DashboardActivity).rewardsRedirection = "fromRewardRedeem"
                    (activity as DashboardActivity).rewardsRedirectionId = cupId
                    val bundle = Bundle()
                    bundle.putString("redeemedLogId",
                        getRedeemCouponResponse.redeemCouponData?.redeemLogId.toString()
                    )
                    (activity as DashboardActivity).replaceFrag(
                        RewardRedeemedCouponNewFragment(),
                        "",
                        bundle
                    )
                } else {
                    showErrorPopup(getRedeemCouponResponse)
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

    private fun showErrorPopup(redeemCouponResponse: GetRedeemCouponResponse?) {

        val alertDialog = Dialog(requireActivity())
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.redeem_error_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        val tvOk = alertDialog.findViewById<TextView>(R.id.tvOk)
        val tvTitle = alertDialog.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = alertDialog.findViewById<TextView>(R.id.tvMessage)
        tvMessage.text = getRedeemCouponResponse.message
        if (getRedeemCouponResponse.code == 4130 || getRedeemCouponResponse.code == 4128){
            tvTitle.text = "Oops..."
        } else if (getRedeemCouponResponse.code == 4126){
            tvTitle.text = "Attention!"
        } else if (getRedeemCouponResponse.code == 4127){
            tvTitle.text = "Freezed!"
        }
        tvOk.setOnClickListener { view: View? ->
            alertDialog.dismiss()
        }

        alertDialog.show()

    }

    private fun getCouponInfo() {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getCouponInfoReq = GetCouponInfoReq()
            getCouponInfoReq.cupId = cupId
            val token = (activity as BaseActivity).userToken
            genericAPIService.getCouponInfo(getCouponInfoReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getCouponInfoResponse =
                    Gson().fromJson(responseBody, GetCouponInfoResponse::class.java)
                if (getCouponInfoResponse != null && getCouponInfoResponse.status == true) {
                    setCouponInfoData()
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

    @SuppressLint("SetTextI18n")
    private fun setCouponInfoData() {
        try {
            Glide.with(this).load(getCouponInfoResponse.couponInfoData?.cupImageUrl)
                .into(binding?.ivCouponImg!!)
            binding?.tvVoucherWorth?.text = getCouponInfoResponse.couponInfoData?.cupName
            binding?.tvPoints?.text = getCouponInfoResponse.couponInfoData?.cupUserAvailablePoints
            binding?.tvDetailsText?.text = getCouponInfoResponse.couponInfoData?.cupDescription
            binding?.tvTerms?.setOnClickListener {
                var termsData = getCouponInfoResponse.couponInfoData?.cupTermsConditions
                setTerms(termsData)
            }
            binding?.tvRedeem?.text = "REDEEM "+getCouponInfoResponse.couponInfoData?.cupPoints
            binding?.tvRedeem?.typeface?.isBold
            if(getCouponInfoResponse.couponInfoData?.reedemedAt!!.contains("WEBSITE")){
                binding?.tvWebsite?.visibility = View.VISIBLE
            } else {
                binding?.tvWebsite?.visibility = View.GONE
            }
            if(getCouponInfoResponse.couponInfoData?.reedemedAt!!.contains("STORE")) {
                binding?.tvStore?.visibility = View.VISIBLE
            }else {
                binding?.tvStore?.visibility = View.GONE
            }



        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showWebSite(webSiteLink: String?) {
        try {
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse(webSiteLink)
            startActivity(openURL)
        } catch (e: Exception) {
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
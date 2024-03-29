package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.kotlin.fragments.RedeemSuccessSheetFragment
import com.capitalnowapp.mobile.models.coupons.CouponsDetails
import com.capitalnowapp.mobile.models.coupons.CouponsResponse
import com.capitalnowapp.mobile.models.coupons.RedeemCouponReq
import com.capitalnowapp.mobile.models.coupons.RedeemCouponResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_coupon_details.ivBanner
import kotlinx.android.synthetic.main.activity_coupon_details.tvDesc
import kotlinx.android.synthetic.main.activity_coupon_details.tvExpireDate
import kotlinx.android.synthetic.main.activity_coupon_details.tvOn
import kotlinx.android.synthetic.main.activity_coupon_details.tvRedeem
import kotlinx.android.synthetic.main.activity_coupon_details.tvTermsLink
import kotlinx.android.synthetic.main.activity_coupon_details.tvTitle
import kotlinx.android.synthetic.main.toolbar_basic_rewards.iv
import kotlinx.android.synthetic.main.toolbar_basic_rewards.tvToolbarTitle

private var couponDetails: CouponsDetails? = null
private var couponsResponse: CouponsResponse? = null
private var shouldAllowBack: Boolean = true

class CouponDetailsActivity : BaseActivity() {

    private var redeemCouponResponse: RedeemCouponResponse? = RedeemCouponResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_details)

        try {
            initView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initView() {
        try {
            if (intent.extras != null) {
                couponDetails = intent.extras!!.getSerializable("coupons_data") as CouponsDetails?
                couponsResponse = intent.extras!!.getSerializable("response") as CouponsResponse?
                setData()
            }
            shouldAllowBack = true
            tvToolbarTitle.text = "Details"
            iv.setOnClickListener {
                onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setData() {
        try {
            Glide.with(this).load(couponDetails?.websiteImage).into(ivBanner)
            tvTitle.text = couponDetails?.couponTitle
            tvExpireDate.text = Utility.formatYYYYMMDD_DDMMMYYYY(couponDetails?.expiryDate)
            tvTermsLink.text = couponDetails?.websiteName
            tvTermsLink.paintFlags = tvTermsLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            tvTermsLink.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(couponDetails?.link)
                startActivity(browserIntent)
            }

            tvDesc.text = couponDetails?.couponDescription
            tvOn.text = "On " + couponDetails?.websiteName

            tvRedeem.text = "Redeem for " + couponDetails?.points + " Points"

            tvRedeem.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                takeConfirmationToRedeem()
            }
            if (couponsResponse?.rewardPoints?.toInt()!! < couponDetails?.points?.toInt()!!) {
                tvRedeem.isEnabled = false
                tvRedeem.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorGrey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takeConfirmationToRedeem() {
        try {
            CNAlertDialog.setRequestCode(1)
            CNAlertDialog.showAlertDialogWithCallback(this, "Confirm", resources.getString(R.string.redeem_confirmation), true, "", "")
            CNAlertDialog.setListener(object : AlertDialogSelectionListener {
                override fun alertDialogCallback() {
                }

                override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {
                    if (buttonType == Constants.ButtonType.POSITIVE) {
                        TrackingUtil.getInstance().logEvent(TrackingUtil.Event.APPLY_LOAN)
                        redeemCoupon()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun redeemCoupon() {
        try {

            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this)
            val redeemCouponReq = RedeemCouponReq()
            redeemCouponReq.userId = userDetails.userId
            redeemCouponReq.couponSelected = couponDetails?.couponID
            val token = userToken
            genericAPIService.redeemCoupon(redeemCouponReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                redeemCouponResponse = Gson().fromJson(responseBody, RedeemCouponResponse::class.java)
                if (redeemCouponResponse != null && redeemCouponResponse?.status == true) {
                    //showSuccessSheet(redeemCouponResponse, couponDetails?.points)
                    showSendEmail()
                } else {
                    Toast.makeText(this, redeemCouponResponse?.message, Toast.LENGTH_SHORT).show()
                }
            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                Toast.makeText(this, getString(R.string.error_failure), Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSuccessSheet(redeemCouponResponse: RedeemCouponResponse?, points: String?) {
        val fragment = RedeemSuccessSheetFragment(this, points)
        fragment.isCancelable = false
        fragment.show(supportFragmentManager, "TAG")
        shouldAllowBack = false
    }

    override fun onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed()
        }
    }

    fun showSendEmail() {
        startActivity(Intent(this, CheckCouponActivity::class.java).putExtra("data", couponDetails).putExtra("response", redeemCouponResponse))
        this.finish()
    }
}
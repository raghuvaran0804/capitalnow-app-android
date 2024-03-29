package com.capitalnowapp.mobile.kotlin.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.text.SpannableString
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.kotlin.fragments.CouponDescSheetFragment
import com.capitalnowapp.mobile.models.GenericResponse
import com.capitalnowapp.mobile.models.coupons.CouponsDetails
import com.capitalnowapp.mobile.models.coupons.CouponsResponse
import com.capitalnowapp.mobile.models.coupons.RedeemCouponResponse
import com.capitalnowapp.mobile.models.coupons.SendEmailReq
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_check_coupon.etEmail
import kotlinx.android.synthetic.main.activity_check_coupon.ivBanner
import kotlinx.android.synthetic.main.activity_check_coupon.toolbar
import kotlinx.android.synthetic.main.activity_check_coupon.tvCode
import kotlinx.android.synthetic.main.activity_check_coupon.tvCopy
import kotlinx.android.synthetic.main.activity_check_coupon.tvDesc
import kotlinx.android.synthetic.main.activity_check_coupon.tvExpireDate
import kotlinx.android.synthetic.main.activity_check_coupon.tvRedeemNow
import kotlinx.android.synthetic.main.activity_check_coupon.tvRedeemPoints
import kotlinx.android.synthetic.main.activity_check_coupon.tvSend
import kotlinx.android.synthetic.main.activity_check_coupon.tvTermsLink
import kotlinx.android.synthetic.main.activity_check_coupon.tvToolbarTitle1
import kotlinx.android.synthetic.main.toolbar_basic_rewards.view.iv
import kotlinx.android.synthetic.main.toolbar_basic_rewards.view.tvToolbarTitle


private var couponDetails: CouponsDetails? = null
private var redeemCouponResponse: RedeemCouponResponse? = null
private var couponsResponse: CouponsResponse? = null
private var myClipboard: ClipboardManager? = null

class CheckCouponActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_coupon)

        try {
            initView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initView() {
        try {
            myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

            if (intent.extras != null) {
                couponDetails = intent.extras!!.getSerializable("data") as CouponsDetails?
                if (intent.extras!!.containsKey("response")) {
                    redeemCouponResponse = intent.extras!!.getSerializable("response") as RedeemCouponResponse?
                }
                if (intent.extras!!.containsKey("coupon_response")) {
                    couponsResponse = intent.extras!!.getSerializable("coupon_response") as CouponsResponse?
                }
                setData()
            }

            tvSend.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()

                if (etEmail.text?.toString()?.trim()?.isNotEmpty() == true && Utility.isValidEmail(etEmail.text.toString().trim())) {
                    sendEmail()
                } else {
                    displayToast("Please enter a valid email")
                }
            }

            tvRedeemNow.setOnClickListener {
                onBackPressed()
            }

            toolbar.iv.setOnClickListener {
                onBackPressed()
            }

            tvDesc.setOnClickListener {
                showDescSheet()
            }

            toolbar.tvToolbarTitle.text = "Awesome!"
            toolbar.tvToolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.Primary1))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDescSheet() {
        val fragment = CouponDescSheetFragment(this, couponDetails)
        fragment.show(supportFragmentManager, "TAG")
    }

    private fun sendEmail() {

        try {

            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this)
            val sendEmailReq = SendEmailReq()
            sendEmailReq.userId = userDetails.userId
            if (redeemCouponResponse != null) {
                sendEmailReq.couponSelected = redeemCouponResponse?.couponCode
            } else {
                sendEmailReq.couponSelected = couponDetails?.couponCode
            }
            sendEmailReq.toEmail = etEmail.text.toString().trim()
            val token = userToken
            genericAPIService.sendEmail(sendEmailReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val genericResponse = Gson().fromJson(responseBody, GenericResponse::class.java)
                if (genericResponse != null && genericResponse?.status == true) {
                    etEmail.isEnabled = false
                    tvSend.isEnabled = false
                    tvSend.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_gray)
                }
                Toast.makeText(this, genericResponse?.message, Toast.LENGTH_SHORT).show()
            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                Toast.makeText(this, getString(R.string.error_failure), Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setData() {
        try {
            var points = 0
            points = if (redeemCouponResponse != null) {
                redeemCouponResponse?.rewardsPointsRemaining!!
            } else {
                couponsResponse?.rewardPoints?.toInt()!!
            }

            val s = "You have $points Reward Points to Redeem."
            val spannable = SpannableString(s)
            val spannable1 = Utility.increaseFontSizeForPath(spannable, (points.toString()), 1.3F) // make "big" text bigger 3 time than normal text
            tvToolbarTitle1.text = spannable1

            if (redeemCouponResponse != null) {
                tvCode.text = redeemCouponResponse?.couponCode
            } else {
                tvCode.text = couponDetails?.couponCode
            }
            tvExpireDate.text = "Expires On: " + Utility.formatYYYYMMDD_DDMMMYYYY(couponDetails?.expiryDate)
            tvTermsLink.text = couponDetails?.websiteName
            tvTermsLink.paintFlags = tvTermsLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            tvTermsLink.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(couponDetails?.link)
                startActivity(browserIntent)
            }

            tvRedeemPoints.text = "Redeemed " + couponDetails?.points + " Points"

            Glide.with(this).load(couponDetails?.websiteImage).into(ivBanner)

            tvCopy.setOnClickListener {
                copyText()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun copyText() {
        val myClip = ClipData.newPlainText("text", tvCode.text.toString().toString())
        myClipboard?.setPrimaryClip(myClip)
        Toast.makeText(this, "Code Copied",
                Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sharedPreferences.putBoolean("should_redeem_refresh", true)
    }
}
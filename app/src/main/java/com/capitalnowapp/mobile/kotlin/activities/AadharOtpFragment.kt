package com.capitalnowapp.mobile.kotlin.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityAadharOtpBinding
import com.capitalnowapp.mobile.models.AadharOtpReq
import com.capitalnowapp.mobile.models.AadharOtpResponse
import com.capitalnowapp.mobile.models.GenericResponse
import com.capitalnowapp.mobile.models.SkipAadharData
import com.capitalnowapp.mobile.models.VerifyAadharOtpReq
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.TrackingUtil
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject


class AadharOtpFragment : Fragment() {
    var userId: String? = null
    private var otp: String = ""
    private var binding: ActivityAadharOtpBinding? = null
    private var accessKey = ""
    private var aadharNum: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityAadharOtpBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.tvAadharVerifyOtp.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type),"VALIDATE Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.aadhar_OT_validation_page_interacted))

            otp = binding!!.etOtp.text.toString().trim()
            if (otp != ""
                && otp.length == 6
            ) {
                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put("isSuccess","true")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.aadhar_OTP_entered))
                verifyAadharOtp()
            } else {
                Toast.makeText(context, "Enter Valid Aadhar OTP Number", Toast.LENGTH_SHORT).show()

            }
        }

        if (arguments != null) {
            accessKey = requireArguments().getString("accessKey")!!
            aadharNum = requireArguments().getString("aadharNum")!!
        }

        binding!!.tvSkip.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type),"Skip for this step Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.aadhar_OT_validation_page_interacted))
            skipAadhar()
        }

        binding!!.tvResendOTP.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type),"Resend OTP Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.aadhar_OT_validation_page_interacted))

            reSendAadharOtp()
            binding!!.tvSkip.visibility = GONE
        }

        object : CountDownTimer(61000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    binding!!.tvTimer.text = "in " + millisUntilFinished / 1000 + " sec(s)"
                    //here you can have your logic to set text to edittext
                    if (millisUntilFinished < 1500) {
                        binding!!.tvResendOTP.isEnabled = false
                        //binding!!.tvResendOTP.setTextColor(ContextCompat.getColor(requireActivity(),R.color.Primary2))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }
            }

            override fun onFinish() {
                binding!!.tvResendOTP.isEnabled = true
                binding!!.tvTimer.visibility = View.INVISIBLE
            }

        }.start()

    }

    private fun reSendAadharOtp() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity,0)
        val aadharOtpReq = AadharOtpReq()
        if ((activity as DashboardActivity).mCurrentLocation != null) {
            aadharOtpReq.long =
                (activity as DashboardActivity).mCurrentLocation?.longitude.toString()
            aadharOtpReq.lat = (activity as DashboardActivity).mCurrentLocation?.latitude.toString()
        }
        aadharOtpReq.userId = (activity as DashboardActivity).userDetails.userId
        aadharOtpReq.aadharno = aadharNum
        val token = (activity as BaseActivity).userToken
        genericAPIService.sendAadharOtp(aadharOtpReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val aadharOtpResponse = Gson().fromJson(
                responseBody,
                AadharOtpResponse::class.java
            )
            if (aadharOtpResponse != null && aadharOtpResponse.status == true) {
                Toast.makeText(context, "OTP Sent successfully", Toast.LENGTH_SHORT).show()

            } else {
                //Failure
                Toast.makeText(context, "OTP Sent failed", Toast.LENGTH_SHORT).show()
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNProgressDialog.hideProgressDialog()
            }
        }
    }

    private fun verifyAadharOtp() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity,0)
        val verifyAadharOtpReq = VerifyAadharOtpReq()
        verifyAadharOtpReq.accesscode = accessKey
        verifyAadharOtpReq.otp = otp
        verifyAadharOtpReq.userId = (activity as DashboardActivity).userDetails.userId
        val token = (activity as BaseActivity).userToken
        genericAPIService.verifyAadharOtp(verifyAadharOtpReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val genericResponse = Gson().fromJson(
                responseBody,
                GenericResponse::class.java
            )
            if (genericResponse != null && genericResponse.status) {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put("isSuccess","true")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.aadhar_verification_success_server_event))

                //Success
                (activity as DashboardActivity).selectedTab =
                    (activity as DashboardActivity).getString(R.string.home)
                (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                (activity as DashboardActivity).isFromApply = false
                (activity as DashboardActivity).getApplyLoanData(true)

            } else {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put("isSuccess","false")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.aadhar_verification_success_server_event))
                CNAlertDialog.showAlertDialog(
                    context,
                    resources.getString(R.string.title_alert),
                    genericResponse.message
                )
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                //Failure
                CNProgressDialog.hideProgressDialog()
            }
        }

    }


    private fun skipAadhar() {
        val genericAPIService = GenericAPIService(activity as BaseActivity)
        val skipAadharData = SkipAadharData()
        skipAadharData.aadharSkip = "Yes"
        skipAadharData.userId = (activity as BaseActivity).userDetails.userId
        val token = (activity as BaseActivity).userToken
        genericAPIService.skipAadharData(skipAadharData, token)
        genericAPIService.setOnDataListener { responseBody ->
            val genericResponse = Gson().fromJson(
                responseBody,
                GenericResponse::class.java
            )
            if (genericResponse != null && genericResponse.status) {
                (activity as DashboardActivity).selectedTab =
                    (activity as DashboardActivity).getString(R.string.home)
                (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                (activity as DashboardActivity).isFromApply = false
                (activity as DashboardActivity).getApplyLoanData(true)
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
            }
        }
    }
}
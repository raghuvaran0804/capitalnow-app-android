package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.activities.LoginActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentDataDeletionBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.models.CheckLoanStatusForDeletionReq
import com.capitalnowapp.mobile.models.CheckLoanStatusForDeletionResponse
import com.capitalnowapp.mobile.models.DeleteConsentReq
import com.capitalnowapp.mobile.models.DeleteConsentResponse
import com.capitalnowapp.mobile.models.SendOTPDeletionReq
import com.capitalnowapp.mobile.models.SendOTPDeletionResponse
import com.capitalnowapp.mobile.models.VerifyOTPDeletionReq
import com.capitalnowapp.mobile.models.VerifyOTPDeletionResponse
import com.capitalnowapp.mobile.models.userdetails.UserDetails
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.google.gson.Gson


class DataDeletionFragment : Fragment() {
    private var cnId: String? = null
    private var mobileNumber: String? = null
    private var OTP: String? = null
    private var reason: String? = null
    private var binding: FragmentDataDeletionBinding? = null
    private var activity: Activity? = null
    var reasonTypesMapKeys: Array<String>? = null
    var reasonTypesMap: LinkedHashMap<String, String>? = null
    var dialog: AlertDialog? = null
    var sharedPreferences: CNSharedPreferences? = null
    var userDetails: UserDetails? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDataDeletionBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLoanStatus()

        binding?.tvBack?.setOnClickListener {
            if(binding?.llView1?.visibility == View.VISIBLE){
                val intent = Intent(context, DashboardActivity::class.java)
                intent.putExtra("from", "deleteAccount")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            if(binding?.llHaveLoan?.visibility == View.VISIBLE){
                val intent = Intent(context, DashboardActivity::class.java)
                intent.putExtra("from", "deleteAccount")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            if(binding?.llSendOtp?.visibility == View.VISIBLE){
                binding?.etReason?.text?.clear()
                binding?.llSendOtp?.visibility = View.GONE
                binding?.tvSendOTP?.visibility = View.GONE
                binding?.llView1?.visibility = View.VISIBLE
                binding?.tvContinue?.visibility = View.VISIBLE

            }
            if(binding?.llVerifyOtp?.visibility == View.VISIBLE){
                binding?.etOtp?.text?.clear()
                binding?.llVerifyOtp?.visibility = View.GONE
                binding?.tvVerifyOTP?.visibility = View.GONE
                binding?.llSendOtp?.visibility = View.VISIBLE
                binding?.tvSendOTP?.visibility = View.VISIBLE
            }
        }

        binding?.tvContinue?.setOnClickListener {
            reason = binding?.etReason?.text.toString().trim { it <= ' ' }
            if(reason?.isEmpty()!!){
                Toast.makeText(context, "Please Select Reason", Toast.LENGTH_SHORT).show()
            }else {
                binding?.llView1?.visibility = View.GONE
                binding?.tvContinue?.visibility = View.GONE
                binding?.llSendOtp?.visibility = View.VISIBLE
                binding?.tvSendOTP?.visibility = View.VISIBLE
            }
        }
        if((activity as BaseActivity).userDetails?.userMobile != null || (activity as BaseActivity).userDetails?.userMobile != ""){
            mobileNumber = (activity as BaseActivity).userDetails?.userMobile
            binding?.etMobileNumber?.setText((activity as BaseActivity).userDetails?.userMobile)
        }



        binding?.tvSendOTP?.setOnClickListener {
            mobileNumber = binding?.etMobileNumber?.text.toString().trim { it <= ' ' }
            if(mobileNumber?.isNotEmpty()!! && Patterns.PHONE.matcher(mobileNumber!!).matches() && mobileNumber?.length == 10){
                sendOTP()
            }else {
                Toast.makeText(context, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show()

            }

        }
        binding?.tvVerifyOTP?.setOnClickListener {
            OTP = binding?.etOtp?.text.toString().trim { it <= ' ' }
            if(OTP?.isNotEmpty()!! && OTP?.length == 6) {
                verifyOTP()

            }else{
                Toast.makeText(context, "Enter Valid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.tvHaveLoan?.setOnClickListener {
            val logInIntent = Intent(requireContext(), DashboardActivity::class.java)
            logInIntent.flags =
                Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logInIntent)
        }

        reasonTypesMap = LinkedHashMap<String, String>()
        reasonTypesMap!!["No Longer using the App"] = "No Longer using the App"
        reasonTypesMap!!["Financial Obligations Met"] = "Financial Obligations Met"
        reasonTypesMap!!["Switched to Another App"] = "Switched to Another App"
        reasonTypesMap!!["Unsatisfactory Experience"] = "Unsatisfactory Experience"
        reasonTypesMap!!["Not Eligible For Loans"] = "Not Eligible For Loans"
        reasonTypesMap!!["Concerns about Data Privacy"] = "Concerns about Data Privacy"
        reasonTypesMapKeys = reasonTypesMap!!.keys.toTypedArray()

        binding?.etReason?.setOnClickListener {
            if (reasonTypesMapKeys != null && reasonTypesMapKeys!!.isNotEmpty()) {
                showPromotionDialog()
            }
        }

    }

    private fun verifyOTP() {
        try{
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val verifyOTPDeletionReq = VerifyOTPDeletionReq()
            verifyOTPDeletionReq.mobileNo = mobileNumber
            verifyOTPDeletionReq.otp = OTP
            val token = (activity as BaseActivity).userToken
            genericAPIService.verifyOtpAccountDelete(verifyOTPDeletionReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val verifyOTPDeletionResponse =
                    Gson().fromJson(responseBody, VerifyOTPDeletionResponse::class.java)
                if (verifyOTPDeletionResponse != null && verifyOTPDeletionResponse.status == true) {
                        showDeleteConfirmPopUp()
                } else {
                    Toast.makeText(context, verifyOTPDeletionResponse.message, Toast.LENGTH_SHORT).show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun sendOTP() {
        try{
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val sendOTPDeletionReq = SendOTPDeletionReq()
            sendOTPDeletionReq.evuiMobile = mobileNumber
            val token = (activity as BaseActivity).userToken
            genericAPIService.evSendOTP(sendOTPDeletionReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val sendOTPDeletionResponse =
                    Gson().fromJson(responseBody, SendOTPDeletionResponse::class.java)
                if (sendOTPDeletionResponse != null && sendOTPDeletionResponse.status == true) {
                    binding?.llSendOtp?.visibility = View.GONE
                    binding?.tvSendOTP?.visibility = View.GONE
                    binding?.llVerifyOtp?.visibility = View.VISIBLE
                    binding?.tvVerifyOTP?.visibility = View.VISIBLE
                    binding?.tvMobileText?.text = "We just sent a OTP to your mobile number " + mobileNumber
                } else {
                    Toast.makeText(context, sendOTPDeletionResponse.message, Toast.LENGTH_SHORT).show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun checkLoanStatus() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val checkLoanStatusForDeletionReq = CheckLoanStatusForDeletionReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.checkLoanStatus(checkLoanStatusForDeletionReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val checkLoanStatusForDeletionResponse =
                    Gson().fromJson(responseBody, CheckLoanStatusForDeletionResponse::class.java)
                if (checkLoanStatusForDeletionResponse != null && checkLoanStatusForDeletionResponse.status == true) {
                    binding?.llView1?.visibility = View.VISIBLE
                    binding?.tvContinue?.visibility = View.VISIBLE
                } else {
                    binding?.llHaveLoan?.visibility = View.VISIBLE
                    binding?.tvHaveLoan?.visibility = View.VISIBLE
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showDeleteConfirmPopUp() {
        try {

            val builder = AlertDialog.Builder(context)
            val view = layoutInflater.inflate(R.layout.delete_confirm_popup, null)
            val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
            val tvConfirm = view.findViewById<TextView>(R.id.tvConfirm)

            tvCancel.setOnClickListener {
                dialog?.dismiss()
                binding?.llVerifyOtp?.visibility = View.GONE
                binding?.tvVerifyOTP?.visibility = View.GONE
                binding?.etOtp?.text?.clear()
                binding?.llView1?.visibility = View.VISIBLE
                binding?.tvContinue?.visibility = View.VISIBLE
                binding?.etReason?.text?.clear()
            }
            tvConfirm.setOnClickListener {
                    deleteConsent()

            }
            builder.setView(view)
            builder.setCancelable(true)
            dialog = builder.create()
            dialog?.show()
            dialog?.setCanceledOnTouchOutside(false)

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val displayWidth: Int = displayMetrics.widthPixels
            val displayHeight: Int = displayMetrics.heightPixels
            val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog?.window!!.attributes)
            val dialogWindowWidth = (displayWidth * 0.8f).toInt()
            val dialogWindowHeight = (displayHeight * 0.6f).toInt()
            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window!!.attributes = layoutParams

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteConsent() {
        try{
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val deleteConsentReq = DeleteConsentReq()
            deleteConsentReq.dcDeleteReason = reason
            val token = (activity as BaseActivity).userToken
            genericAPIService.deleteConsent(deleteConsentReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val deleteConsentResponse =
                    Gson().fromJson(responseBody, DeleteConsentResponse::class.java)
                if (deleteConsentResponse != null && deleteConsentResponse.status == true) {
                    dialog?.dismiss()
                    if ((activity as BaseActivity).userDetails.qcId != null &&  (activity as BaseActivity).userDetails.qcId != "") {
                        cnId = (activity as BaseActivity).userDetails.qcId
                        showMissYouPopUp(cnId)
                    }
                } else {
                    Toast.makeText(context, deleteConsentResponse.message, Toast.LENGTH_SHORT).show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showMissYouPopUp(cnId: String?) {
        try {
            val builder = AlertDialog.Builder(activity as BaseActivity)
            val view = layoutInflater.inflate(R.layout.miss_you_popup, null)
            val tvOk = view.findViewById<TextView>(R.id.tvOk)
            val deletionText = view.findViewById<TextView>(R.id.tvDeleteText)
            deletionText.text = "Account Deletion Request for " + cnId + " is Recorded."
            tvOk.setOnClickListener {
                logout()
            }
            builder.setView(view)
            builder.setCancelable(true)
            dialog = builder.create()
            dialog?.show()
            dialog?.setCanceledOnTouchOutside(false)

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val displayWidth: Int = displayMetrics.widthPixels
            val displayHeight: Int = displayMetrics.heightPixels
            val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog?.window!!.attributes)
            val dialogWindowWidth = (displayWidth * 0.8f).toInt()
            val dialogWindowHeight = (displayHeight * 0.6f).toInt()
            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window!!.attributes = layoutParams
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logout() {
        sharedPreferences?.putString(Constants.USER_DETAILS_DATA, Gson().toJson(userDetails))
        sharedPreferences?.putString(Constants.USER_REGISTRATION_DATA, null)
        val logInIntent = Intent(requireContext(), LoginActivity::class.java)
        logInIntent.flags =
            Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(logInIntent)
        //overridePendingTransition(R.anim.left_in, R.anim.right_out)

    }

    private fun showPromotionDialog() {
        try {
            val builder = AlertDialog.Builder(requireContext())
            builder.setItems(reasonTypesMapKeys) { _, which ->
                binding?.etReason?.setText(reasonTypesMapKeys?.get(which))
                reason = reasonTypesMap?.get(reasonTypesMapKeys?.get(which))
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
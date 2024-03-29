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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityEsignBinding
import com.capitalnowapp.mobile.models.ContactUsResponse
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.PrSaveSignReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_signature2.cbAgreeTerms

class ESignActivity : BaseActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private var result: String? = null
    private var activity: AppCompatActivity? = null
    private var isSignPadSelected: Boolean = false
    private var isSignUploaded: Boolean = false
    private var isSignSelected: Boolean = false
    private var cropFileName: String? = null
    private var cropGooglePhotosUri: Boolean? = false
    private val REQUEST_IMAGE_CAPTURE: Int = 102
    private var signUri: Uri? = null
    private var pageNumber: Int? = 26
    private var binding: ActivityEsignBinding? = null
    private lateinit var value: String
    private lateinit var url: String
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var csGenericResponse = CSGenericResponse()
    private var genericResponse: ContactUsResponse = ContactUsResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEsignBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()
        binding?.tvNext?.setOnClickListener {
            if (cbAgreeTerms.isChecked) {
                saveSignature()
            } else {
                if(!cbAgreeTerms.isChecked) {
                    Toast.makeText(this, "Please Check the Consent", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding?.ivHelp?.setOnClickListener {
            showHelpPopup()
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
        val tvCall = alertDialog.findViewById<TextView>(R.id.tvCall)
        val ivCancel = alertDialog.findViewById<ImageView>(R.id.ivCancel)
        tvEmailText.text = profileFormDataResponse.offerHelp?.email.toString()
        tvCall.text = profileFormDataResponse.offerHelp?.phone.toString()
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

    private fun saveSignature() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val prSaveSignReq = PrSaveSignReq()
            prSaveSignReq.pageNo = 26
            val token = userToken
            genericAPIService.prSaveSign(prSaveSignReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                csGenericResponse =
                    Gson().fromJson(responseBody, CSGenericResponse::class.java)
                if (csGenericResponse.status == true) {
                    getApplyLoanDataBase(true)
                } else {
                    Toast.makeText(this, csGenericResponse.message, Toast.LENGTH_SHORT).show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 26
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                    setData(profileFormDataResponse)
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

    private fun setData(profileFormDataResponse: ProfileFormDataResponse) {
        binding!!.webView.visibility = View.VISIBLE
        binding!!.webView.settings.javaScriptEnabled = true
        binding?.webView?.isVerticalScrollBarEnabled = true
        Glide.with(this).load(profileFormDataResponse.partnerInfo?.lpLogo)
            .into(binding?.ivCsLogo!!)
        Glide.with(this).load(profileFormDataResponse.offerHelp!!.icon)
            .into(binding?.ivHelp!!)
        url = profileFormDataResponse.profileformData?.tancText!![0].replaceLinks!!
        binding!!.webView.loadUrl(url)
        binding?.tvSignatureConfirm?.text = profileFormDataResponse.profileformData?.tancText!![0].message
    }


}
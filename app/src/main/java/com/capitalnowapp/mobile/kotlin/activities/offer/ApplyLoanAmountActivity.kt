package com.capitalnowapp.mobile.kotlin.activities.offer

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityApplyLoanAmountBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.SaveLoanAmountRequiredReq
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.Currency
import com.google.gson.Gson

class ApplyLoanAmountActivity : BaseActivity() {
    private lateinit var maxAmount: String
    private var activity: AppCompatActivity? = null
    private val seekInterval = 500
    private var amount: Int = 0
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var csGenericResponse = CSGenericResponse()
    private var binding: ActivityApplyLoanAmountBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplyLoanAmountBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()

        binding?.ivBack?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding?.seekBar?.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int,
                    fromUser: Boolean
                ) {
                    try {
                        if ((seekInterval * progress) >= profileFormDataResponse.profileformData!!.pclMinLoanAmount!!) {
                            if (progress > 0) {
                                amount = seekInterval * progress
                                binding?.etLoanAmount?.text = amount.toString()
                                setAmountInWords(amount)
                            } else {
                                binding?.seekBar?.progress = 1
                            }
                        } else {
                            binding?.seekBar?.progress = progress + 1
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )

        binding?.ivRemove?.setOnClickListener {
            binding?.seekBar?.progress = binding?.seekBar?.progress?.minus(1)!!
        }
        binding?.ivAdd?.setOnClickListener {
            binding?.seekBar?.progress = binding?.seekBar?.progress?.plus(1)!!
        }
        binding?.tvNext?.setOnClickListener {
            saveLoanAmount()
        }
    }

    private fun saveLoanAmount() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val saveLoanAmountRequiredReq = SaveLoanAmountRequiredReq()
            saveLoanAmountRequiredReq.pageNo = 19
            saveLoanAmountRequiredReq.loanAmount = amount.toString()
            val token = userToken
            genericAPIService.saveLoanAmount(saveLoanAmountRequiredReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                 csGenericResponse =
                    Gson().fromJson(responseBody, CSGenericResponse::class.java)
                if (csGenericResponse.status == true)  {
                    getApplyLoanDataBase(true)
                }
                else {
                    Toast.makeText(this, csGenericResponse.message, Toast.LENGTH_SHORT).show()
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

    private fun setAmountInWords(s: Int){
        val words = Currency.convertToIndianCurrency(s.toString())
        binding?.tvCurrency?.text = words
    }
    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 19
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse = Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                        setApplyLoanData(profileFormDataResponse)
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
    private fun setApplyLoanData(profileFormDataResponse: ProfileFormDataResponse) {
        try{
            maxAmount = profileFormDataResponse.profileformData?.pclMaxLoanAmount!!.toString()
            binding?.etLoanAmount?.text = maxAmount
            val quotient: Int = profileFormDataResponse.profileformData?.pclMaxLoanAmount!! / seekInterval
            binding?.seekBar?.max = quotient
            binding?.seekBar?.progress = quotient
            setAmountInWords(profileFormDataResponse.profileformData!!.pclMaxLoanAmount!!)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
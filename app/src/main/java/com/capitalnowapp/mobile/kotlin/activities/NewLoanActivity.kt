package com.capitalnowapp.mobile.kotlin.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.ApplyLoan
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityNewLoanBinding
import com.capitalnowapp.mobile.models.GetLoanRangeReq
import com.capitalnowapp.mobile.models.GetLoanRangeResponse
import com.capitalnowapp.mobile.models.MasterJsonResponse
import com.capitalnowapp.mobile.models.RegisterLoanReq
import com.capitalnowapp.mobile.models.RegisterLoanResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.Currency
import com.google.gson.Gson
import me.tankery.lib.circularseekbar.CircularSeekBar
import org.json.JSONObject
import kotlin.math.roundToInt


class NewLoanActivity : BaseActivity() {
    private var min: Int? = 0
    private var max: Int? = 0
    private var purposeOfLoan: String? = ""
    private var other: String? = ""
    private var referralCode: String? = ""
    var validationMsg = ""
    private var selectedPurposeOfLoan: String? = ""
    private var i: Int? = -1
    private var binding: ActivityNewLoanBinding? = null
    private var seekInterval = 0
    private var amount: Int? = -1
    private var activity: AppCompatActivity? = null
    var masterJsonResponse: MasterJsonResponse? = null
    private var loanPurposeArray: Array<CharSequence?>? = null
    private var applyNewLoanBean: ApplyLoan? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewLoanBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        try {

            getMasterJson()
            binding?.etPurposeOfLoan?.setOnClickListener {
                if (masterJsonResponse != null && masterJsonResponse?.purposeOfLoan != null && masterJsonResponse?.purposeOfLoan?.size!! > 0) {
                    showPurposeDialog()
                }
            }
            binding?.tvBack?.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            binding?.tvNext?.setOnClickListener {
                validateFields()

            }

            binding?.circularProgressBar?.setOnSeekBarChangeListener(object :
                CircularSeekBar.OnCircularSeekBarChangeListener {
                override fun onProgressChanged(
                    circularSeekBar: CircularSeekBar?,
                    progress: Float,
                    fromUser: Boolean
                ) {
                    if (progress.roundToInt() >= min?.div(seekInterval)!! ) {
                        amount = seekInterval * progress.toInt()
                        setAmountInWords(amount!!)
                    }else{
                        binding?.circularProgressBar?.progress = min!!.div(seekInterval).toFloat()
                        amount = min
                        setAmountInWords(min!!)
                    }
                }

                override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {

                }

            })

            getLoanRange()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateFields() {
        try {
            selectedPurposeOfLoan = binding?.etPurposeOfLoan?.text.toString().trim { it <= ' ' }
            if (selectedPurposeOfLoan == "Others" && binding?.etOther?.visibility == View.VISIBLE) {
                selectedPurposeOfLoan = binding?.etOther?.text.toString().trim { it <= ' ' }
            }
            referralCode = binding?.etReferralCode?.text.toString().trim { it <= ' ' }
            var count = 0
            if (selectedPurposeOfLoan!!.isEmpty()) {
                validationMsg = "Select Purpose of loan"
                count++
            }
            if (binding?.etOther?.visibility == View.VISIBLE) {
                if (selectedPurposeOfLoan!!.isEmpty() || selectedPurposeOfLoan!!.length < 3) {
                    validationMsg = "Others is Required"
                    count++
                }
            }
            if (count > 0) {
                Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
            } else {
                registerLoan()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerLoan() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val registerLoanReq = RegisterLoanReq()
            registerLoanReq.amount = amount.toString()
            registerLoanReq.purposeOfLoan = selectedPurposeOfLoan
            registerLoanReq.refercode = referralCode
            val token = userToken
            genericAPIService.registerLoan(registerLoanReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val registerLoanResponse =
                    Gson().fromJson(responseBody, RegisterLoanResponse::class.java)
                if (registerLoanResponse != null && registerLoanResponse.status == true) {
                    //setLoanRange(getLoanRangeResponse)
                    val intent = Intent(this, LoanRecordedActivity:: class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        registerLoanResponse.message,
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun setAmountInWords(s: Int) {
        val ss: Int = if(s> min!!){
            s
        }else{
            min!!
        }
        val words = Currency.convertToIndianCurrency(ss.toString())
        binding?.tvAmountTitle?.text = words
        binding?.tvLoanAmount?.text = amount.toString()
    }

    private fun getLoanRange() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val getLoanRangeReq = GetLoanRangeReq()
            val token = userToken
            genericAPIService.getLoanRange(getLoanRangeReq, token)
            //Log.d("save Salary req", Gson().toJson(saveSalaryReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val getLoanRangeResponse =
                    Gson().fromJson(responseBody, GetLoanRangeResponse::class.java)
                if (getLoanRangeResponse != null && getLoanRangeResponse.status == true) {
                    setLoanRange(getLoanRangeResponse)
                } else {
                    Toast.makeText(
                        this,
                        getLoanRangeResponse.message,
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun setLoanRange(loanRangeResponse: GetLoanRangeResponse?) {
        try {
            min = loanRangeResponse?.data?.min
            max = loanRangeResponse?.data?.max
            val increament = loanRangeResponse?.data?.increament
            //binding?.seekbar?.min = min!!.toInt()
            //binding?.seekbar?.max = max!!.toInt()
            seekInterval = increament!!.toInt()

            val diff = max!!.minus(min!!.toInt())
            val quotient: Int = diff / seekInterval

            binding?.circularProgressBar?.max = max!!.div(seekInterval).toFloat()
            binding?.circularProgressBar?.progress = min!!.div(seekInterval).toFloat()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMasterJson() {
        cnModel.getMasterJsonLoan1()
    }

    @SuppressLint("SuspiciousIndentation")
    fun updateMasterJson(response: JSONObject) {
        try {
            val strJson = response.toString()
            masterJsonResponse = Gson().fromJson(strJson, MasterJsonResponse::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showPurposeDialog() {
        //disableAgreeButton()
        loanPurposeArray = arrayOfNulls(masterJsonResponse?.purposeOfLoan!!.size)
        for (i in 0 until masterJsonResponse?.purposeOfLoan!!.size) {
            loanPurposeArray!![i] =
                masterJsonResponse?.purposeOfLoan?.get(i)?.value // Whichever string you wanna store here from custom object
        }
        val builder = android.app.AlertDialog.Builder(this)
        builder.setItems(loanPurposeArray) { _, which ->
            binding?.etPurposeOfLoan?.setText(loanPurposeArray!![which])
            if (masterJsonResponse?.purposeOfLoan!![which].id == 200) {
                binding?.tvOther?.visibility = View.VISIBLE
                binding?.etOther?.visibility = View.VISIBLE
                binding?.etOther?.setText("")
                //applyNewLoanBean!!.qcr_custom_purpose = ""
            } else {
                //applyNewLoanBean!!.qcr_custom_purpose = ""
                binding?.etOther?.visibility = View.GONE
                binding?.tvOther?.visibility = View.GONE
            }
            //applyNewLoanBean!!.qcr_purpose_of_loan = masterJsonResponse?.purposeOfLoan!![which].id
            selectedPurposeOfLoan = masterJsonResponse?.purposeOfLoan!![which].id.toString()
        }
        designDialog(builder)
    }

    private fun designDialog(builder: android.app.AlertDialog.Builder) {
        var dialog: android.app.AlertDialog? = null
        dialog = builder.create()
        dialog?.show()

        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.6f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window!!.attributes = layoutParams
    }
    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }
}
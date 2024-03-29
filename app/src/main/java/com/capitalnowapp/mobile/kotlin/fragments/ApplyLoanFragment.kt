package com.capitalnowapp.mobile.kotlin.fragments

//import com.appsflyer.AppsFlyerLib
//import io.branch.referral.util.BranchEvent
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.MailTo
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CompoundButton
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.ApplyLoan
import com.capitalnowapp.mobile.beans.ApplyLoanData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.constants.Constants.ButtonType
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.FragmentApplyLoanBinding
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.kotlin.utils.Validator
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

class ApplyLoanFragment : Fragment(), View.OnClickListener {
    private var scale: Float = 0.0f
    private val seekInterval = 500
    private var binding: FragmentApplyLoanBinding? = null
    private var amount: Int = 0
    private var noOfDays: Int = 0
    private var interestAmount: Int = 0
    private var totalAmount: Int = 0
    private var finalTotalAmount: Int = 0
    private var maxLoanAmount: Int = 0
    private var discount = 0

    private var AMOUNT_INTERVAL = 500
    private var AMOUNT_MINIMUM = 1000
    private var AMOUNT_MAXIMUM = 10000
    private var DAYS_INTERVAL = 1
    private var DAYS_MINIMUM = 1
    private var DAYS_MAXIMUM = 30

    private var applyLoanData: ApplyLoanData? = null
    private var applyNewLoanBean: ApplyLoan? = null

    private var limitExhaustedMessage: String? = ""
    private var processingFeeTitle: String? = ""
    private var processingFeeSubTitle: String? = ""

    private var promo_code: String? = null
    private var offer_promo_code: String? = null

    private var isLimitExhausted: Boolean = false

    private var processingCharges: Int = 0
    private var newProcessingCharges: Int = 0

    private var dueDate: Date? = null
    private var activity: Activity? = null
    private var amazonAmount = 0
    private var bankAmount = 0
    private var loanType = ""
    private var amazonNumber = ""
    var sharedPreferences: CNSharedPreferences? = null


    @SuppressLint("NotConstructor")
    fun ApplyLoanFragment() {
        // Required empty public constructor
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentApplyLoanBinding.inflate(inflater, container, false)

        activity = getActivity()
        val bundle = arguments
        if (bundle != null) {
            isLimitExhausted = bundle.getBoolean(Constants.SP_IS_LIMIT_EXHAUSTED)
            if (isLimitExhausted) {
                limitExhaustedMessage = bundle.getString(Constants.SP_LIMIT_EXHAUSTED_MESSAGE)
            } else {
                applyLoanData = bundle.getSerializable(Constants.SP_APPLY_LOAN_DATA) as ApplyLoanData?
            }
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(binding)
    }

    private fun initView(binding: FragmentApplyLoanBinding?) {
        try {
            (activity as BaseActivity).sharedPreferences = CNSharedPreferences(activity)

            (activity as BaseActivity).userId = (activity as BaseActivity).userDetails.userId
            (activity as BaseActivity).cnModel = CNModel(activity, activity, Constants.RequestFrom.APPLY_LOAN)

            applyNewLoanBean = ApplyLoan()


            // Loading with initial values.
            if (applyLoanData != null) {
                maxLoanAmount = applyLoanData!!.userInterestCharges.max_loan_amount
                processingCharges = applyLoanData!!.userInterestCharges.processing_charges.toFloat().toInt()
                binding?.tvMaxLoanLimit?.text = "Current Eligibility Limit  " + maxLoanAmount
                setTermsText(applyLoanData!!)
            }

            AMOUNT_MAXIMUM = maxLoanAmount.also { amount = it }
            noOfDays = 30

            processingFeeTitle = resources.getString(R.string.apply_loan_processing_fee_title)
            processingFeeSubTitle = resources.getString(R.string.apply_loan_processing_fee_sub_title)


            binding?.ivLoanMinus?.setOnClickListener {
                if (amount > AMOUNT_MINIMUM) {
                    amount -= AMOUNT_INTERVAL
                }
                calculateAndUpdateInterestAmount(false)
                setAmazonToDefault()
            }

            binding?.ivSubmit?.setOnClickListener {
                startActivity(Intent(activity, UploadDocsNewFrag::class.java))
            }

            binding?.ivLoanPlus?.setOnClickListener {
                if (amount < AMOUNT_MAXIMUM) {
                    amount += AMOUNT_INTERVAL
                }
                calculateAndUpdateInterestAmount(false)
                setAmazonToDefault()
            }
            binding?.ivDayMinus?.setOnClickListener {
                if (noOfDays > DAYS_MINIMUM) {
                    noOfDays -= DAYS_INTERVAL
                }
                calculateAndUpdateInterestAmount(false)
            }

            binding?.ivDayPlus?.setOnClickListener {
                if (noOfDays < DAYS_MAXIMUM) {
                    noOfDays += DAYS_INTERVAL
                }
                calculateAndUpdateInterestAmount(false)
            }

            setCardVisibility()

            binding?.tvReferral?.setOnClickListener {
                val slideUp: Animation = AnimationUtils.loadAnimation(activity, R.anim.slide_down_out)
                binding.etReferralCode.visibility = VISIBLE
                binding.etReferralCode.startAnimation(slideUp)
                setCardVisibility()
            }
            disableAgreeButton()
            binding?.ivSubmit?.setImageResource(R.drawable.login_button_img_grey)

            binding?.ivSubmit?.setOnClickListener { //requestOTP();
                val amt = binding.etLoanAmount.text.toString().replace("[^\\d.]".toRegex(), "")
                val day = binding.etDayCount.text.toString().replace("[^\\d.]".toRegex(), "")
                if (amt.toInt() in AMOUNT_MINIMUM..AMOUNT_MAXIMUM) {
                    if (day.isNotEmpty() && day.toInt() in DAYS_MINIMUM..DAYS_MAXIMUM) {
                        when (loanType) {
                            AppConstants.LoanTypes.BankTransfer -> {
                                takeConfirmationToApplyForLoan()
                            }
                            AppConstants.LoanTypes.APayTransfer -> {
                                if (Validator.validateMobileNum(binding.llAmazon.etMobileAmazon)) {
                                    amazonNumber = binding.llAmazon.etMobileAmazon.text.toString().trim()
                                    if (binding.cbAmazonTerms.isChecked) {
                                        takeConfirmationToApplyForLoan()
                                    } else {
                                        (activity as DashboardActivity).displayToast(getString(R.string.amazon_terms_select_validation))
                                    }
                                } else {
                                    (activity as DashboardActivity).displayToast(getString(R.string.enter_mobile))
                                }
                            }
                            AppConstants.LoanTypes.BankApay -> {
                                if (Validator.validateMobileNum(binding.llAmazon.etMobileBoth)) {
                                    amazonNumber = binding.llAmazon.etMobileBoth.text.toString().trim()
                                    if (binding.cbAmazonTerms.isChecked) {
                                        takeConfirmationToApplyForLoan()
                                    } else {
                                        (activity as DashboardActivity).displayToast(getString(R.string.amazon_terms_select_validation))
                                    }
                                } else {
                                    (activity as DashboardActivity).displayToast(getString(R.string.enter_mobile))
                                }
                            }
                        }
                    } else {
                        Toast.makeText(activity, "No. of days must be minimum $DAYS_MINIMUM to maximum $DAYS_MAXIMUM", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(activity, "Loan amount must be minimum $AMOUNT_MINIMUM to maximum $AMOUNT_MAXIMUM", Toast.LENGTH_SHORT).show()
                }
            }

            binding?.tvProcessAmount?.text = String.format(processingFeeTitle!!, processingCharges)

            if (processingCharges > 100) {
                binding?.tvProcessAmount?.text = String.format(processingFeeSubTitle!!, processingCharges / 2, processingCharges)
            } else {
                binding?.tvProcessAmount?.text = String.format(processingFeeSubTitle!!, processingCharges, processingCharges)
            }

            if (isLimitExhausted && limitExhaustedMessage!!.isNotEmpty()) {
                binding?.tvProcessAmount?.visibility = View.INVISIBLE
                CNAlertDialog.setRequestCode(Constants.ALERT_DIALOG_REQUEST_CODE_COMMON)
                CNAlertDialog.showAlertDialogWithCallback(activity, "", limitExhaustedMessage, false, "", "")
                CNAlertDialog.setListener(object : AlertDialogSelectionListener {
                    override fun alertDialogCallback() {
                        //(activity as BaseActivity).onBackPressed()
                    }

                    override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {}
                })
            } else {
                calculateAndUpdateInterestAmount(false)
            }

            binding?.cbAgreeTerms?.setOnClickListener {
                if (binding.cbAgreeTerms.isChecked) {
                    if (loanType.isNotEmpty()) {
                        when (loanType) {
                            AppConstants.LoanTypes.BankTransfer -> {
                                enableAgreeButton()
                            }
                            AppConstants.LoanTypes.APayTransfer -> {
                                if (binding.cbAmazonTerms.isChecked) {
                                    enableAgreeButton()
                                } else {
                                    disableAgreeButton()
                                }
                            }
                            AppConstants.LoanTypes.BankApay -> {
                                if (binding.cbAmazonTerms.isChecked) {
                                    enableAgreeButton()
                                } else {
                                    disableAgreeButton()
                                }
                            }
                        }
                    } else {
                        (activity as DashboardActivity).displayToast(getString(R.string.select_loan_transfer_type))
                        binding.cbAgreeTerms.isChecked = false
                    }
                } else
                    disableAgreeButton()
            }

            binding?.cbAmazonTerms?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked && binding.cbAgreeTerms.isChecked) {
                    enableAgreeButton()
                } else {
                    disableAgreeButton()
                }
            })

            binding?.etLoanAmount?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (s.isNotEmpty()) {
                        val amt = binding.etLoanAmount.text.toString().replace("[^\\d.]".toRegex(), "")
                        amount = amt.toInt()
                        calculateAndUpdateInterestAmount(true)
                    } else {
                        amount = 0
                        calculateAndUpdateInterestAmount(true)
                        Toast.makeText(activity, "Loan Amount Should not be blank ", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            binding?.etDayCount?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.isNotEmpty()) {
                        val day = binding.etDayCount.text.toString().replace("[^\\d.]".toRegex(), "")
                        noOfDays = day.toInt()
                        calculateAndUpdateInterestAmount(true)
                    } else {
                        noOfDays = 1
                        calculateAndUpdateInterestAmount(true)
                        Toast.makeText(activity, "No. of days must be minimum  $DAYS_MINIMUM to maximum $DAYS_MAXIMUM", Toast.LENGTH_SHORT).show()
                    }

                }
            })

            binding?.tvReferral?.paintFlags = binding?.tvReferral?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!

            binding.llAmazon.seekBar.setOnSeekBarChangeListener(
                    object : OnSeekBarChangeListener {
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                                       fromUser: Boolean) {
                            calculateSplitAmount(progress)
                        }
                    }
            )

            binding.llAmazon.rbBank.setOnClickListener(this)
            binding.llAmazon.rbAmazon.setOnClickListener(this)
            binding.llAmazon.rbBoth.setOnClickListener(this)

            if (applyLoanData?.loanTypes != null && applyLoanData?.loanTypes!!.size > 0) {
                binding.llAmazon.root.visibility = VISIBLE
                for ((index, type) in applyLoanData!!.loanTypes.withIndex()) {
                    when (type.loanType) {
                        AppConstants.LoanTypes.BankTransfer -> {
                            binding.llAmazon.llBank.visibility = VISIBLE
                            binding.llAmazon.rbBank.isChecked = type.check != "0"
                            if (type.offerImg != null && type.offerImg.isNotEmpty()) {
                                Glide.with(this).load(type.offerImg).into(binding.llAmazon.ivOfr1)
                                binding.llAmazon.ivOfr1.visibility = VISIBLE
                            } else {
                                binding.llAmazon.ivOfr1.visibility = GONE
                            }

                            binding.llAmazon.rbBank.text = type.loanText
                            binding.llAmazon.tvAmountInfoBank.text = type.loanSubText
                        }
                        AppConstants.LoanTypes.APayTransfer -> {
                            binding.llAmazon.llApay.visibility = VISIBLE
                            binding.llAmazon.rbAmazon.isChecked = type.check != "0"
                            if (type.offerImg != null && type.offerImg.isNotEmpty()) {
                                Glide.with(this).load(type.offerImg).into(binding.llAmazon.ivOfr2)
                                binding.llAmazon.ivOfr2.visibility = VISIBLE
                            } else {
                                binding.llAmazon.ivOfr2.visibility = GONE
                            }
                            binding.llAmazon.rbAmazon.text = type.loanText
                            binding.llAmazon.tvAmountInfoApay.text = type.loanSubText
                        }
                        AppConstants.LoanTypes.BankApay -> {
                            binding.llAmazon.llBothLayout.visibility = VISIBLE
                            binding.llAmazon.rbBoth.isChecked = type.check != "0"
                            if (type.offerImg != null && type.offerImg.isNotEmpty()) {
                                Glide.with(this).load(type.offerImg).into(binding.llAmazon.ivOfr3)
                                binding.llAmazon.ivOfr3.visibility = VISIBLE
                            } else {
                                binding.llAmazon.ivOfr3.visibility = GONE
                            }
                            binding.llAmazon.rbBoth.text = type.loanText
                            binding.llAmazon.tvAmountInfoBoth.text = type.loanSubText
                        }
                    }
                }
                setAmazonToDefault()
            } else {
                binding.llAmazon.root.visibility = GONE
                loanType = AppConstants.LoanTypes.BankTransfer
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCardVisibility() {
        if ((activity as BaseActivity).userDetails.hasTakenFirstLoan == 1) {
            binding?.etReferralCode?.visibility = GONE
            binding?.tvReferral?.visibility = GONE
        } else {
            if ((activity as BaseActivity).sharedPreferences.getBoolean(Constants.SP_REFER_CODE_IS_REGISTERED)) {
                val referalCode = (activity as BaseActivity).sharedPreferences.getString(Constants.SP_REFER_CODE)
                if (!TextUtils.isEmpty(referalCode) && !referalCode.contains("utm_source=google-play")) {
                    binding?.etReferralCode?.setText(referalCode)
                }
            }
            binding?.tvReferral?.visibility = VISIBLE
        }

        if (discount <= 0) {
            if (binding?.etReferralCode?.visibility === VISIBLE) {
                binding?.llBg?.layoutParams?.height = (310 * scale).toInt()
            } else {
                binding?.llBg?.layoutParams?.height = (260 * scale).toInt()
            }
        } else {
            if (binding?.etReferralCode?.visibility === VISIBLE) {
                binding?.llBg?.layoutParams?.height = (360 * scale).toInt()
            } else {
                binding?.llBg?.layoutParams?.height = (280 * scale).toInt()
            }
        }
    }

    private fun setAmazonToDefault() {
        val quotient: Int = amount / seekInterval
        binding?.llAmazon?.seekBar?.max = quotient
        binding?.llAmazon?.seekBar?.progress = quotient.minus(1)
    }

    private fun calculateSplitAmount(progress: Int) {
        val quotient = binding?.llAmazon?.seekBar?.max
        if (progress > 0 && progress < quotient!!) {
            when {
                0 == quotient % progress -> {
                    if (amount % 1000 == 0) {
                        if (progress == quotient.minus(2)) {
                            amazonAmount = amount / 2
                            bankAmount = amount / 2
                        } else {
                            if (progress < quotient.div(2)) {
                                amazonAmount = (quotient - progress) * seekInterval
                                bankAmount = progress * seekInterval
                            } else {
                                bankAmount = (quotient - progress) * seekInterval
                                amazonAmount = progress * seekInterval
                            }
                        }
                    } else {
                        if (quotient - progress == 1) {
                            bankAmount = (quotient - progress) * seekInterval
                            amazonAmount = progress * seekInterval
                        } else {
                            amazonAmount = (quotient - progress) * seekInterval
                            bankAmount = progress * seekInterval
                        }
                    }
                }
                progress < quotient.div(2) -> {
                    if (quotient - progress == 1) {
                        bankAmount = (quotient - progress) * seekInterval
                        amazonAmount = progress * seekInterval
                    } else {
                        amazonAmount = (quotient - progress) * seekInterval
                        bankAmount = progress * seekInterval
                    }

                }
                else -> {
                    amazonAmount = (quotient - progress) * seekInterval
                    bankAmount = progress * seekInterval
                }
            }
            binding?.llAmazon?.tvAmazonAmount?.text = amazonAmount.toString()
            binding?.llAmazon?.tvBankAmount?.text = bankAmount.toString()
        } else {
            if (progress == 0) {
                binding?.llAmazon?.seekBar?.progress = 1
            } else if (progress == binding?.llAmazon?.seekBar?.max) {
                binding?.llAmazon?.seekBar?.progress = binding?.llAmazon?.seekBar?.max?.minus(1)!!
            }
        }
        setAmountTransferInfo()
    }

    private fun disableAgreeButton() {
        binding?.ivSubmit?.setImageResource(R.drawable.login_button_img_grey)
        binding?.ivSubmit?.isEnabled = false
    }

    private fun enableAgreeButton() {
        binding?.ivSubmit?.setImageResource(R.drawable.login_button_img_green)
        binding?.ivSubmit?.isEnabled = true
    }

    fun showAlertDialog(message: String?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(activity, resources.getString(R.string.title_alert), message)
    }

    fun calculateAndUpdateInterestAmount(fromEdit: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, noOfDays)
        dueDate = calendar.time
        val rateOfInterest = getRateOfInterest(noOfDays)
        val fee = noOfDays * amount * rateOfInterest / 100
        val interest = 0f
        interestAmount = (fee + interest).roundToInt()
        totalAmount = (amount + interestAmount)
        finalTotalAmount = totalAmount + processingCharges
        if (!fromEdit) {
            binding?.etLoanAmount?.setText(amount.toString())
            binding?.etDayCount?.setText(noOfDays.toString())
        }
        binding?.tvBorrower?.text = String.format("%,d", amount)
        binding?.tvInterest?.text = String.format("%,d", interestAmount)
        binding?.tvTotalAmount?.text = String.format("%,d", totalAmount)
        binding?.tvDueDate?.text = Utility.formatDate(dueDate, Constants.APPLY_LOAN_DUE_DATE_DISPLAY_FORMAT)

        if (amount > 5000) {
            binding?.tvProcessAmount?.text = String.format(processingFeeTitle!!, processingCharges)
        } else {
            if (processingCharges > 100) binding?.tvProcessAmount?.text = String.format(processingFeeTitle!!, processingCharges / 2) else binding?.tvProcessAmount?.text = String.format(processingFeeTitle!!, processingCharges)
        }

        setAmountTransferInfo()
        setDiscount()
    }

    private fun setDiscount() {
        if (applyLoanData?.cashbackConditions != null && applyLoanData?.cashbackConditions!!.size > 0) {
            for ((index, cashback) in applyLoanData!!.cashbackConditions.withIndex()) {
                if (loanType == AppConstants.LoanTypes.BankApay) {
                    if (amazonAmount >= cashback.minAmount && amazonAmount <= cashback.maxAmount) {
                        discount = cashback.discount
                        break
                    } else {
                        discount = 0
                    }
                } else if (loanType == AppConstants.LoanTypes.APayTransfer) {
                    if (amount >= cashback.minAmount && amount <= cashback.maxAmount) {
                        discount = cashback.discount
                        break
                    } else {
                        discount = 0
                    }
                } else {
                    discount = 0
                    break
                }
            }
        }
        if (discount > 0) {
            binding?.tvTotalAmount?.text = String.format("%,d", totalAmount - discount)
            binding?.llDiscount?.visibility = VISIBLE
            binding?.tvDiscount?.text = "-$discount"
        } else {
            binding?.tvTotalAmount?.text = String.format("%,d", totalAmount)
            binding?.llDiscount?.visibility = GONE
        }
        setCardVisibility()
    }

    private fun getRateOfInterest(days: Int): Float {
        var rateOfInterest = 0.0f
        if (applyLoanData != null) {
            for (serviceCharges in applyLoanData!!.serviceChargesList) {
                val fromDay = serviceCharges.from_day.toInt()
                val toDay = serviceCharges.to_day.toInt()
                if (days in fromDay..toDay) {
                    rateOfInterest = serviceCharges.service_charges.toFloat()
                    break
                }
            }
        }
        return rateOfInterest
    }

    private fun takeConfirmationToApplyForLoan() {
        try {
            CNAlertDialog.setRequestCode(1)
            CNAlertDialog.showAlertDialogWithCallback(activity, "Confirm", resources.getString(R.string.apply_loan_confirmation), true, "", "")
            CNAlertDialog.setListener(object : AlertDialogSelectionListener {
                override fun alertDialogCallback() {
                }

                override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {
                    if (buttonType == ButtonType.POSITIVE) {
                        TrackingUtil.getInstance().logEvent(TrackingUtil.Event.APPLY_LOAN)
                        applyLoanWithoutOTP()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun applyLoanWithoutOTP() {
        if (loanType != AppConstants.LoanTypes.BankTransfer) {
            CNProgressDialog.showProgressDialogText(activity, getString(R.string.verifying_amazon_number))
        } else {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        }
        newProcessingCharges = if (processingCharges == 100) {
            processingCharges
        } else {
            if (amount > 5000) processingCharges else processingCharges / 2
        }

        promo_code = binding?.etReferralCode?.text.toString().trim()
        applyNewLoanBean!!.receivedOTP = ""
        applyNewLoanBean!!.amount = amount
        applyNewLoanBean!!.tenureDays = noOfDays
        applyNewLoanBean!!.serviceFee = interestAmount
        applyNewLoanBean!!.processingCharges = processingCharges
        applyNewLoanBean!!.newProcessingCharges = newProcessingCharges
        applyNewLoanBean!!.total = totalAmount
        applyNewLoanBean!!.promo_code = promo_code
        applyNewLoanBean!!.otpPassword = ""
        applyNewLoanBean!!.current_location = (activity as DashboardActivity).mCurrentLocation.toString()
        applyNewLoanBean!!.amazonNumber = amazonNumber
        applyNewLoanBean!!.amazonAmount = amazonAmount.toString()
        applyNewLoanBean!!.bankAmount = bankAmount.toString()
        applyNewLoanBean!!.loanType = loanType
        applyNewLoanBean!!.cashback_amt = discount.toString()
        val token = (activity as BaseActivity).userToken
        (activity as BaseActivity).cnModel.validateOTPAndApplyLoan(this, (activity as BaseActivity).userId, applyNewLoanBean,token)
    }

    fun updateApplyLoanStatus(response: JSONObject) {
        CNProgressDialog.hideProgressDialog()
        try {
           /* if ((activity as BaseActivity).userDetails.hasTakenFirstLoan != 1) {
              //  AdGyde.onSimpleEvent(getString(R.string.user_applied_loan))
                val params = HashMap<String, Any>()
                val key = getString(R.string.user_applied_loan)
                params[key] = getString(R.string.user_applied_loan) //patrametre name,value change to event
                AppsFlyerLib.getInstance().logEvent(activity as DashboardActivity,  key, params)
                val logger = AppEventsLogger.newLogger(this.context)
                logger.logEvent(getString(R.string.user_applied_loan), Bundle())
                // AdGyde.onCountingEvent(key, params) //eventid,params

                BranchEvent("FirstTimeAppliedLoan")
                    .addCustomDataProperty("FirstTimeAppliedLoan", "First_Time_Applied_Loan")
                    .setCustomerEventAlias("First_Time_Applied_Loan")
                    .logEvent(activity as DashboardActivity)

            }*/
            val message = response.getString("message")
            val statusRedirect = response.getInt("status_redirect")
            CNAlertDialog()
            CNAlertDialog.setRequestCode(Constants.ALERT_DIALOG_REQUEST_CODE_COMMON)
            if (statusRedirect > 0) {
                if (statusRedirect == Constants.STATUS_REDIRECT_CODE_PENDING_DOCUMENTS) { // Pending Documents
                    // Pending Documents
                    (activity as DashboardActivity).isFromApply = true
                    (activity as DashboardActivity).getApplyLoanData(true)
                } else if (statusRedirect == Constants.STATUS_REDIRECT_CODE_FIVE_REFERENCES) { // Five References
                    startActivity(Intent(activity, ReferencesNewFragment::class.java))
                }
            } else {
                binding?.tvReferral?.visibility = GONE
                binding?.etReferralCode?.visibility = GONE
                (activity as BaseActivity).sharedPreferences.putBoolean(Constants.SP_REFER_CODE_IS_REGISTERED, false)
                (activity as BaseActivity).sharedPreferences.putString(Constants.SP_REFER_CODE, "")
                showAlertDialog(message)
                (activity as DashboardActivity).getApplyLoanData(true)
                (activity as DashboardActivity).onBackPressed()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder((activity as BaseActivity).activityContext, R.style.RulesAlertDialogStyle)
        val inflater: LayoutInflater = this@ApplyLoanFragment.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_terms_conditions, null)
        alert.setView(dialogView)
        val tvTitle: CNTextView = dialogView.findViewById(R.id.et_title)
        val tvBack: CNTextView = dialogView.findViewById(R.id.tvBack)
        tvTitle.text = title
        val pb = dialogView.findViewById<ProgressBar>(R.id.pb)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        val dialog: Dialog = alert.create()
        val mActivityRef = WeakReference<Activity>(activity)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("mailto:")) {
                    val activity: Activity = mActivityRef.get()!!
                    val mt = MailTo.parse(url)
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(mt.to))
                    intent.putExtra(Intent.EXTRA_TEXT, mt.body)
                    intent.putExtra(Intent.EXTRA_SUBJECT, mt.subject)
                    intent.putExtra(Intent.EXTRA_CC, mt.cc)
                    intent.type = "message/rfc822"
                    activity.startActivity(intent)
                    view.reload()
                    return true
                } else {
                    view.loadUrl(url)
                }
                return true
            }
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                pb.visibility = VISIBLE
                view.visibility = GONE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                pb.visibility = GONE
                view.visibility = VISIBLE
                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl(link)
        tvBack.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun setAmountTransferInfo() {
        when (loanType) {
            AppConstants.LoanTypes.BankTransfer -> {
                binding?.llAmazon?.tvAmountInfoBank?.visibility = VISIBLE
                binding?.llAmazon?.tvAmountInfoApay?.visibility = GONE
                binding?.llAmazon?.tvAmountInfoBoth?.visibility = GONE
            }
            AppConstants.LoanTypes.APayTransfer -> {
                //    binding?.llAmazon?.tvAmountInfoApay?.text = getString(R.string.amazon_transfer_text, amount)
                binding?.llAmazon?.tvAmountInfoBank?.visibility = GONE
                binding?.llAmazon?.tvAmountInfoApay?.visibility = VISIBLE
                binding?.llAmazon?.tvAmountInfoBoth?.visibility = GONE
                setAmazonTermsText(2)
            }
            AppConstants.LoanTypes.BankApay -> {
                //   binding?.llAmazon?.tvAmountInfoBoth?.text = getString(R.string.both_transfer_text, bankAmount, amazonAmount)
                binding?.llAmazon?.tvAmountInfoBank?.visibility = GONE
                binding?.llAmazon?.tvAmountInfoApay?.visibility = GONE
                binding?.llAmazon?.tvAmountInfoBoth?.visibility = VISIBLE
                setAmazonTermsText(3)
            }
        }

        for ((index, type) in applyLoanData!!.loanTypes.withIndex()) {
            when (type.loanType) {
                AppConstants.LoanTypes.BankTransfer -> {
                    val s = type.loanSubText.toString().replace("{bank_amount}", amount.toString())
                    binding?.llAmazon?.tvAmountInfoBank?.text = s
                }
                AppConstants.LoanTypes.APayTransfer -> {
                    val s = type.loanSubText.toString().replace("{apay_amount}", amount.toString())
                    binding?.llAmazon?.tvAmountInfoApay?.text = s
                }
                AppConstants.LoanTypes.BankApay -> {
                    val s = type.loanSubText.toString().replace("{apay_amount}", amazonAmount.toString())
                    val ss = s.replace("{bank_amount}", bankAmount.toString())
                    binding?.llAmazon?.tvAmountInfoBoth?.text = ss
                }
            }
        }
        setDiscount()
    }

    override fun onClick(v: View?) {
        setAmazonToDefault()
        binding?.cbAgreeTerms?.isChecked = false
        binding?.cbAmazonTerms?.isChecked = false
        if (applyLoanData?.amazonPayNumber != null && applyLoanData?.amazonPayNumber.toString().isNotEmpty()) {
            amazonNumber = applyLoanData?.amazonPayNumber.toString()
        }
        when (v?.id) {
            R.id.rbBank -> {
                binding?.llAmazon?.rbAmazon?.isChecked = false
                binding?.llAmazon?.rbBoth?.isChecked = false
                loanType = AppConstants.LoanTypes.BankTransfer
                binding?.llAmazon?.llAmazonNum?.visibility = GONE
                binding?.llAmazon?.llBoth?.visibility = GONE
                binding?.llAmazonTerms?.visibility = GONE
                setAmountTransferInfo()
            }
            R.id.rbAmazon -> {
                binding?.llAmazon?.rbBank?.isChecked = false
                binding?.llAmazon?.rbBoth?.isChecked = false
                loanType = AppConstants.LoanTypes.APayTransfer
                binding?.llAmazon?.llAmazonNum?.visibility = VISIBLE
                binding?.llAmazon?.llBoth?.visibility = GONE
                binding?.llAmazonTerms?.visibility = VISIBLE
                binding?.llAmazon?.etMobileAmazon?.setText(amazonNumber)
                binding?.llAmazon?.etMobileAmazon?.isEnabled = applyLoanData?.isCanEdit!!
                setAmazonTermsText(2)
                setAmountTransferInfo()
            }
            R.id.rbBoth -> {
                binding?.llAmazon?.rbAmazon?.isChecked = false
                binding?.llAmazon?.rbBank?.isChecked = false
                loanType = AppConstants.LoanTypes.BankApay
                binding?.llAmazon?.llAmazonNum?.visibility = GONE
                binding?.llAmazon?.llBoth?.visibility = VISIBLE
                binding?.llAmazonTerms?.visibility = VISIBLE
                binding?.llAmazon?.etMobileBoth?.setText(amazonNumber)
                binding?.llAmazon?.etMobileBoth?.isEnabled = applyLoanData?.isCanEdit!!
                setAmazonTermsText(3)
                setAmountTransferInfo()
            }
        }
        binding?.cbAgreeTerms?.callOnClick()
        setCardVisibility()
    }

    private fun setAmazonTermsText(index: Int) {
        try {
            var click = 0
            val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    showTermsPolicyDialog(applyLoanData?.loanTypes?.get(click)?.loanTermsKeyword!!, applyLoanData?.apayTermsLink!!)
                }
            }
            val startIndex: Int
            val endIndex: Int
            var wordToFind = ""
            var ss: SpannableString? = null
            for ((ind, type) in applyLoanData!!.loanTypes.withIndex()) {
                when (type.loanType) {
                    AppConstants.LoanTypes.APayTransfer -> {
                        if (type.loanType == index.toString()) {
                            click = ind
                            ss = SpannableString(applyLoanData?.loanTypes?.get(ind)?.loanTerms?.replace("{apay_amount}", amount.toString()))
                            wordToFind = applyLoanData?.loanTypes?.get(ind)?.loanTermsKeyword!!
                        }
                    }
                    AppConstants.LoanTypes.BankApay -> {
                        if (type.loanType == index.toString()) {
                            click = ind
                            val s = applyLoanData?.loanTypes?.get(ind)?.loanTerms?.replace("{apay_amount}", amazonAmount.toString())
                            val sss = s?.replace("{bank_amount}", bankAmount.toString())
                            wordToFind = applyLoanData?.loanTypes?.get(ind)?.loanTermsKeyword!!
                            ss = SpannableString(sss)
                        }
                    }
                }
            }
            startIndex = ss?.indexOf(wordToFind, 0)!!
            endIndex = startIndex + wordToFind.length
            ss.setSpan(termsAndCondition, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            scale = resources.displayMetrics.density
            binding?.tvAmazonTerms?.text = ss
            binding?.tvAmazonTerms?.movementMethod = LinkMovementMethod.getInstance()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTermsText(applyLoanData: ApplyLoanData) {
        var startIndex: Int
        var endIndex: Int
        val ss: SpannableString?
        if (applyLoanData.userTermsData != null && applyLoanData.userTermsData.message != null) {
            val words: List<String> = applyLoanData.userTermsData.findText!!.split("||")
            val links: List<String> = applyLoanData.userTermsData.replaceLinks!!.split("||")
            ss = SpannableString(applyLoanData.userTermsData.message)
            for (w in words.withIndex()) {
                val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        showTermsPolicyDialog(w.value, links[w.index])
                    }
                }
                startIndex = ss.indexOf(w.value, 0)
                endIndex = startIndex + w.value.length
                ss.setSpan(termsAndCondition, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                scale = resources.displayMetrics.density
                binding?.tvPrivacyLink?.text = ss
                binding?.tvPrivacyLink?.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }
}
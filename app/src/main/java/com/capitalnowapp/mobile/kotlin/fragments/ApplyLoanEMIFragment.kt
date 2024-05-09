package com.capitalnowapp.mobile.kotlin.fragments

//import com.appsflyer.AppsFlyerLib
//import io.branch.referral.util.BranchEvent
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.MailTo
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.ApplyLoan
import com.capitalnowapp.mobile.beans.ApplyLoanData
import com.capitalnowapp.mobile.beans.UserTermsData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.constants.Constants.ButtonType
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.FragmentApplyLoanEmiBinding
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.LoanEmiAdapter
import com.capitalnowapp.mobile.kotlin.adapters.LoanInstallmentAdapter
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.CheckPromoCodeReq
import com.capitalnowapp.mobile.models.CheckPromoCodeResponse
import com.capitalnowapp.mobile.models.MasterJsonResponse
import com.capitalnowapp.mobile.models.loan.InstalmentData
import com.capitalnowapp.mobile.models.loan.TenureData
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.TrackingUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_apply_loan_emi.rvEmi
import kotlinx.android.synthetic.main.loan_emi_adapter.view.rbOption
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.Date
import kotlin.math.roundToInt


class ApplyLoanEMIFragment : Fragment(), View.OnClickListener {
    private var popuploanType: String = ""
    private var message3: String = ""
    private var message2: String = ""
    private var message1: String = ""
    private var percAmount: Float = 0.0f
    private var replacePartnerText: String = ""
    private var selectedTenure: TenureData? = null
    var selectedPos: Int = -1
    private var isCongGone: Boolean = false
    private var isSuccessUnlocked: Boolean = false
    private var loanPurposeArray: Array<CharSequence?>? = null
    public var installmentAdapter: LoanInstallmentAdapter? = null
    private var emiAdapter: LoanEmiAdapter? = null
    private var scale: Float = 0.0f
    private val seekInterval = 1000
    public var binding: FragmentApplyLoanEmiBinding? = null
    public var amount: Int = 0
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

    var applyLoanData: ApplyLoanData? = null
    private var applyNewLoanBean: ApplyLoan? = null

    private var limitExhaustedMessage: String? = ""
    private var processingFeeTitle: String? = ""
    private var processingFeeSubTitle: String? = ""
    var sharedPreferences: CNSharedPreferences? = null


    private var promo_code: String? = null

    private var offer_promo_code: String? = ""

    private var isLimitExhausted: Boolean = false

    private var processingCharges: Int = 0
    private var newProcessingCharges: Int = 0

    private var dueDate: Date? = null
    private var activity: Activity? = null
    private var amazonAmount = 0
    private var bankAmount = 0
    private var loanType = ""
    private var amazonNumber = ""
    private var tenureDataList: ArrayList<TenureData>? = null
    var tenureDays: Int = -1
    var isTenureSelected = false
    var masterJsonResponse: MasterJsonResponse? = null
    var instalmentsList: List<InstalmentData>? = null
    var dialog: AlertDialog? = null

    @SuppressLint("NotConstructor")
    fun ApplyLoanEMIFragment() {
        // Required empty public constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplyLoanEmiBinding.inflate(inflater, container, false)
        /*val s : String = "a"
        Log.d("qwerty", s!!.substring(0,10))*/
        activity = getActivity()
        val bundle = arguments
        if (bundle != null) {
            isLimitExhausted = bundle.getBoolean(Constants.SP_IS_LIMIT_EXHAUSTED)
            if (isLimitExhausted) {
                limitExhaustedMessage = bundle.getString(Constants.SP_LIMIT_EXHAUSTED_MESSAGE)
            } else {
                applyLoanData =
                    bundle.getSerializable(Constants.SP_APPLY_LOAN_DATA) as ApplyLoanData?
                tenureDataList = applyLoanData?.tenureDataList as ArrayList<TenureData>?
            }
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(binding)
    }

    private fun initView(binding: FragmentApplyLoanEmiBinding?) {
        try {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.personal_loan_apply_now_page_landed))

            (activity as BaseActivity).sharedPreferences = CNSharedPreferences(activity)

            (activity as BaseActivity).userId = (activity as BaseActivity).userDetails.userId
            (activity as BaseActivity).cnModel =
                CNModel(activity, activity, Constants.RequestFrom.APPLY_LOAN)

            applyNewLoanBean = ApplyLoan()


            // Loading with initial values.
            if (applyLoanData != null) {
                binding?.tveNachMessage?.text = applyLoanData!!.tenureDataList[0].enachMessage
                maxLoanAmount = applyLoanData!!.userInterestCharges.max_loan_amount
                processingCharges =
                    applyLoanData!!.userInterestCharges.processing_charges.toFloat().toInt()
                binding?.tvMaxLoanLimit?.text = "Current Eligibility Limit  $maxLoanAmount"
                //setTermsText(applyLoanData!!.userTermsData)

                if (applyLoanData!!.userTermsData.ba_replace_links != null && !applyLoanData!!.userTermsData.ba_replace_links.equals(
                        ""
                    )
                ) {
                    binding?.llBorrowerTerms?.visibility = VISIBLE
                    binding?.tveNachMessage?.text = applyLoanData!!.tenureDataList[0].enachMessage
                    setBorrowTerms(applyLoanData!!.userTermsData)
                } else {
                    binding?.llBorrowerTerms?.visibility = GONE
                    binding?.tveNachMessage?.text = applyLoanData!!.tenureDataList[0].enachMessage

                }
            }

            AMOUNT_MAXIMUM = maxLoanAmount.also { amount = it }
            noOfDays = 30

            processingFeeTitle = resources.getString(R.string.apply_loan_processing_fee_title)
            processingFeeSubTitle =
                resources.getString(R.string.apply_loan_processing_fee_sub_title)


            binding?.ivSubmit?.setOnClickListener {
                startActivity(Intent(activity, UploadDocsNewFrag::class.java))
            }

            setCardVisibility()

            binding?.cardReferral?.setOnClickListener {
                if (binding.etReferralCode.visibility == VISIBLE) {
                    binding.etReferralCode.visibility = GONE
                    binding.tvReferral.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(
                            activity as BaseActivity,
                            R.drawable.ic_docs_expand
                        ),
                        null
                    )
                } else {
                    binding.etReferralCode.visibility = VISIBLE
                    binding.tvReferral.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(
                            activity as BaseActivity,
                            R.drawable.ic_docs_collapse
                        ),
                        null
                    )
                }
            }
            //disableAgreeButton()
            //  binding?.ivSubmit?.setImageResource(R.drawable.login_button_img_grey)

            binding?.ivSubmit?.setOnClickListener { //requestOTP();
                val amt = binding.etLoanAmount.text.toString().replace("[^\\d.]".toRegex(), "")

                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type), "APPLY Button Clicked")
                    obj.put("Loan Amount", amt)
                    obj.put("Loan Duration", emiAdapter?.getSelectedPosition()?.emiCount)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.personal_loan_apply_now_clicked))


                if (amt.toInt() in AMOUNT_MINIMUM..AMOUNT_MAXIMUM) {
                    loanType = AppConstants.LoanTypes.BankTransfer
                    when (loanType) {
                        AppConstants.LoanTypes.BankTransfer -> {
                            if (isTenureSelected) {
                                //if both visible and both check
                                //checkPurpose()
                                var purposeStatus = checkPurpose()
                                if (instalmentsList.isNullOrEmpty()) {
                                    Toast.makeText(
                                        activity, "Select Tenure",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (binding?.llBorrowerTerms?.visibility == VISIBLE && binding?.llTerms?.visibility == VISIBLE) {
                                    if (binding?.cbBorrowerTerms?.isChecked && binding?.cbAgreeTerms?.isChecked && purposeStatus) {
                                        //takeConfirmationToApplyForLoan(true)
                                        checkCanApply()
                                    } else if (binding?.cbBorrowerTerms?.isChecked && !binding?.cbAgreeTerms?.isChecked) {
                                        Toast.makeText(
                                            activity,
                                            "Please check the Borrower Agreement Consent",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (!binding?.cbBorrowerTerms?.isChecked && binding?.cbAgreeTerms?.isChecked) {
                                        Toast.makeText(
                                            activity,
                                            "Please check the Borrower Agreement Consent",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "Please check Borrower Agreement Consent and Terms and Conditions Consent",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else if (binding?.llTerms?.visibility == VISIBLE && purposeStatus) {
                                    if (binding?.cbAgreeTerms?.isChecked) {
                                        //takeConfirmationToApplyForLoan(false)
                                        checkCanApply()
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "Please check the Borrower Agreement Consent",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else if (binding?.llBorrowerTerms?.visibility == VISIBLE && purposeStatus) {
                                    if (binding?.cbBorrowerTerms?.isChecked) {
                                        //takeConfirmationToApplyForLoan(true)
                                        checkCanApply()
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "Please check Borrower Agreement Consent",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else if (binding?.llBorrowerTerms?.visibility == GONE && binding?.llTerms?.visibility == GONE && purposeStatus) {
                                    //takeConfirmationToApplyForLoan(false)
                                    checkCanApply()
                                    /*if (applyNewLoanBean!!.qcr_purpose_of_loan > 0) {
                                        if (applyNewLoanBean!!.qcr_purpose_of_loan == 200) {
                                            if (applyNewLoanBean!!.qcr_custom_purpose.isNotEmpty() && applyNewLoanBean!!.qcr_custom_purpose.length >= 2) {
                                                takeConfirmationToApplyForLoan(false)
                                            } else {
                                                (activity as BaseActivity).displayToast(getString(R.string.loan_purpose_custom_validation_msg))
                                                disableAgreeButton()
                                            }
                                        } else {
                                            takeConfirmationToApplyForLoan(false)
                                        }
                                    } else {
                                        (activity as BaseActivity).displayToast(getString(R.string.loan_purpose_validation_msg))
                                        disableAgreeButton()
                                    }*/
                                }
                                // else if
                                /*if (llBorrowerTerms.visibility == VISIBLE) {
                                    takeConfirmationToApplyForLoan(true)
                                } else {
                                    takeConfirmationToApplyForLoan(false)
                                }*/
                            } else {
                                (activity as DashboardActivity).displayLongToast(getString(R.string.select_tenure))
                            }
                        }
                    }

                } else {
                    Toast.makeText(
                        activity,
                        "Loan amount must be minimum $AMOUNT_MINIMUM to maximum $AMOUNT_MAXIMUM",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            if (isLimitExhausted && limitExhaustedMessage!!.isNotEmpty()) {
                CNAlertDialog.setRequestCode(Constants.ALERT_DIALOG_REQUEST_CODE_COMMON)
                CNAlertDialog.showAlertDialogWithCallback(
                    activity,
                    "",
                    limitExhaustedMessage,
                    false,
                    "",
                    ""
                )
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
                    if (emiAdapter != null) {
                        if (isTenureSelected) {
                            val tenure = emiAdapter!!.getSelectedPosition()
                            if (tenure.type == AppConstants.LoanEMITypes.Days) {
                                if (tenureDays >= 0 && tenureDays <= tenure.maxDays!!) {
                                    checkPurpose()
                                } else {
                                    (activity as BaseActivity).displayToast(tenure.maxDaysMessage)
                                    /*CNAlertDialog.showAlertDialog(
                                        context,
                                        resources.getString(R.string.title_alert),
                                        tenure.maxDaysMessage
                                    )
                                   disableAgreeButton()*/
                                }
                            } else {
                                checkPurpose()
                            }
                        } else {
                            disableAgreeButton()
                            (activity as DashboardActivity).displayLongToast(getString(R.string.select_tenure))
                        }
                    } else {
                        disableAgreeButton()
                    }
                }
            }

            binding?.cbAmazonTerms?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked && binding.cbAgreeTerms.isChecked) {
                    enableAgreeButton()
                } else {
                    //disableAgreeButton()
                }
            })

            binding!!.llAmazon.seekBar.setOnSeekBarChangeListener(
                object : OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onProgressChanged(
                        seekBar: SeekBar, progress: Int,
                        fromUser: Boolean
                    ) {
                        //calculateSplitAmount(progress)
                    }
                }
            )
            binding.seekBar.setOnSeekBarChangeListener(
                object : OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onProgressChanged(
                        seekBar: SeekBar, progress: Int,
                        fromUser: Boolean
                    ) {
                        try {
                            //disableAgreeButton()
                            if ((seekInterval * progress) >= applyLoanData!!.applyLoanMinAmount) {
                                if (progress > 0) {
                                    if (progress == binding.seekBar.max && applyLoanData!!.userInterestCharges.max_loan_amount == 8500) {
                                        amount = applyLoanData!!.userInterestCharges.max_loan_amount
                                    } else if (progress == 1) {
                                        amount = applyLoanData!!.applyLoanMinAmount.toInt()
                                    } else {
                                        amount = seekInterval * progress
                                    }
                                    calculateAndUpdateInterestAmount(false)
                                } else {
                                    binding.seekBar.progress = 1
                                }
                            } else {
                                binding.seekBar.progress = progress + 1
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
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
            /* binding.tvSelectTenure.setOnClickListener {
                 if (binding.llTenure.visibility == VISIBLE) {
                     binding.llTenure.visibility = GONE
                     binding.tvSelectTenure.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(activity as BaseActivity, R.drawable.ic_emi_right_arrow), null)
                 } else {
                     binding.llTenure.visibility = VISIBLE
                     binding.tvSelectTenure.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(activity as BaseActivity, R.drawable.ic_emi_up_arrow), null)
                 }
             }*/

            binding.ivRemove.setOnClickListener {
                binding.seekBar.progress = binding.seekBar.progress.minus(1)
            }
            binding.ivAdd.setOnClickListener {
                binding.seekBar.progress = binding.seekBar.progress.plus(1)
            }
            calculateAndUpdateInterestAmount(false)

            getMasterJson()
            binding.etPurpose.setOnClickListener {
                if (masterJsonResponse != null && masterJsonResponse?.purposeOfLoan != null && masterJsonResponse?.purposeOfLoan?.size!! > 0) {
                    showPurposeDialog()
                }
            }

            binding?.etCustomPurpose?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().length >= 2) {
                        applyNewLoanBean!!.qcr_custom_purpose = s.toString()
                    } else {
                        applyNewLoanBean!!.qcr_custom_purpose = ""
                        disableAgreeButton()
                    }
                }

            })
            binding?.rlCong?.startAnimation(
                AnimationUtils.loadAnimation(
                    activity,
                    R.anim.down_slide
                )
            )

            Handler().postDelayed({
                try {
                    val anim = AnimationUtils.loadAnimation(activity, R.anim.up_slide)
                    anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            if (binding?.rlCong != null) {
                                binding?.rlCong?.visibility = GONE
                                isCongGone = true
                            }
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    if (binding?.rlCong != null) {
                        binding?.rlCong?.startAnimation(anim)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, (3 * 1000).toLong()) // 3 sec

            binding.etPromoCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    when (s?.length) {
                        0 -> {
                            offer_promo_code = ""
                            binding.tvApplyPromo.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.dark_gray
                                )
                            )
                            binding.tvApplyPromo.isEnabled = true
                            binding.tvPromoText.visibility = GONE
                            binding.tvCancel.visibility = GONE
                        }

                        in 0..4 -> {
                            // invalid
                            offer_promo_code = ""
                            binding.tvApplyPromo.isEnabled = true
                            binding.tvApplyPromo.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.dark_gray
                                )
                            )
                        }

                        in 5..10 -> {
                            // validcase
                            binding.tvApplyPromo.isEnabled = true
                            binding.tvApplyPromo.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.Primary1
                                )
                            )
                            offer_promo_code = s.toString().trim()
                        }
                    }
                }

            })

            binding.tvApplyPromo.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type), "Promo Code Apply Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.promo_code_apply_clicked))


                if (isTenureSelected) {
                    if (offer_promo_code?.length!! >= 5) {
                        checkPromoCode()
                    } else {
                        //showAlertDialog("Invalid Promo Code")
                    }
                } else {
                    (activity as DashboardActivity).displayLongToast(getString(R.string.select_tenure))
                }
            }
            binding.tvCancel.setOnClickListener {
                clearOfferData()
                installmentAdapter?.notifyDataSetChanged()

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearOfferData() {
        installmentAdapter?.setOfferAmountList(ArrayList())
        binding?.tvApplyPromo?.visibility = VISIBLE
        binding?.tvApplyPromo?.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.dark_gray
            )
        )
        binding?.tvCancel?.visibility = GONE
        binding?.etPromoCode?.setText("")
        binding?.tvPromoText?.visibility = GONE
        offer_promo_code = ""
    }

    private fun checkPromoCode() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val checkPromoCodeReq = CheckPromoCodeReq()
            checkPromoCodeReq.isPl = "1"
            checkPromoCodeReq.loanType = AppConstants.LoanEMITypes.EMI
            checkPromoCodeReq.platform = "Android"
            checkPromoCodeReq.amount = amount
            checkPromoCodeReq.code = offer_promo_code
            checkPromoCodeReq.emiCount = emiAdapter!!.getSelectedPosition().emiCount
            val token = (activity as BaseActivity).userToken
            genericAPIService.checkPromoCode(checkPromoCodeReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val checkPromoCodeResponse =
                    Gson().fromJson(responseBody, CheckPromoCodeResponse::class.java)
                if (checkPromoCodeResponse != null && checkPromoCodeResponse.status == true) {
                    showPromoSuccessPopUp(checkPromoCodeResponse)
                    binding?.tvApplyPromo?.visibility = GONE
                    binding?.tvCancel?.visibility = VISIBLE
                    binding?.tvCancel?.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.green
                        )
                    )
                    binding?.tvPromoText?.visibility = VISIBLE
                    binding?.tvPromoText?.text = checkPromoCodeResponse.message
                    binding?.tvPromoText?.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.green
                        )
                    )
                    setEmiOffer(checkPromoCodeResponse)
                } else {
                    clearOfferData()
                    CNAlertDialog.showAlertDialog(
                        context,
                        resources.getString(R.string.title_alert),
                        checkPromoCodeResponse.message
                    )
                }

            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                (activity as BaseActivity).displayToast(it.message.toString())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setEmiOffer(checkPromoCodeResponse: CheckPromoCodeResponse) {
        try {
            if (checkPromoCodeResponse.data?.array != null && checkPromoCodeResponse.data!!.array?.size!! > 0) {
                installmentAdapter?.setOfferAmountList(checkPromoCodeResponse.data!!.array!!)
            } else {
                installmentAdapter?.setOfferAmountList(ArrayList())
            }
            installmentAdapter?.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showPromoSuccessPopUp(checkPromoCodeResponse: CheckPromoCodeResponse) {
        val alertDialog = Dialog(requireActivity())
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.promo_custom_alert)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        val tvMessage = alertDialog.findViewById<TextView>(R.id.tvMessage)
        tvMessage.text = checkPromoCodeResponse.data?.message
        val tvGotIt = alertDialog.findViewById<TextView>(R.id.tvGotIt)
        tvGotIt.setOnClickListener { view: View? ->
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun checkPurpose(): Boolean {
        if (applyNewLoanBean!!.qcr_purpose_of_loan > 0) {
            if (applyNewLoanBean!!.qcr_purpose_of_loan == 200) {
                if (applyNewLoanBean!!.qcr_custom_purpose.isNotEmpty() && applyNewLoanBean!!.qcr_custom_purpose.length >= 2) {
                    enableAgreeButton()
                    return true
                    //takeConfirmationToApplyForLoan(false)
                } else {
                    (activity as BaseActivity).displayToast(getString(R.string.loan_purpose_custom_validation_msg))
                    //disableAgreeButton()
                    return false
                }
            } else {
                enableAgreeButton()
                return true
                //takeConfirmationToApplyForLoan(false)
            }
        } else {
            (activity as BaseActivity).displayToast(getString(R.string.loan_purpose_validation_msg))
            //disableAgreeButton()
            return false
        }
    }

    private fun setCardVisibility() {
        if ((activity as BaseActivity).userDetails.hasTakenFirstLoan == 1) {
            binding?.cardReferral?.visibility = GONE
            binding?.tvReferral?.visibility = GONE
            binding!!.inFoHeadsUp.visibility = GONE
        } else {
            if ((activity as BaseActivity).sharedPreferences.getBoolean(Constants.SP_REFER_CODE_IS_REGISTERED)) {
                val referalCode =
                    (activity as BaseActivity).sharedPreferences.getString(Constants.SP_REFER_CODE)
                if (!TextUtils.isEmpty(referalCode) && !referalCode.contains("utm_source=google-play")) {
                    binding?.etReferralCode?.setText(referalCode)
                }
            }
            binding?.cardReferral?.visibility = VISIBLE
            binding!!.inFoHeadsUp.visibility = GONE

        }
    }

    private fun setAmazonToDefault() {
        val quotient: Int = amount / seekInterval
        binding?.llAmazon?.seekBar?.max = quotient
        binding?.llAmazon?.seekBar?.progress = quotient.minus(1)
        if (amount == 8500) {
            binding?.seekBar?.max = quotient.plus(1)
            binding?.seekBar?.progress = quotient.plus(1)
        } else {
            binding?.seekBar?.max = quotient
            binding?.seekBar?.progress = quotient
        }
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
        //binding?.cbAgreeTerms?.isChecked = false
        //binding?.ivSubmit?.setImageResource(R.drawable.login_button_img_grey)
        //binding?.ivSubmit?.isEnabled = false
        //checkPurpose()

    }

    private fun enableAgreeButton() {
        //    binding?.ivSubmit?.setImageResource(R.drawable.login_button_img_green)
        /*binding?.ivSubmit?.isEnabled = true
        binding?.ivSubmit?.setTextColor(
            ContextCompat.getColor(
                activity as BaseActivity,
                R.color.white
            )
        )
        cbBorrowerTerms.isEnabled = true
        cbBorrowerTerms.isClickable = true*/
    }

    fun showAlertDialog(message: String?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(activity, resources.getString(R.string.title_alert), message)
    }

    @SuppressLint("MissingInflatedId")
    fun showNewAlertDialog(response: JSONObject) {
        try {
            val builder = AlertDialog.Builder(context)
            val view = layoutInflater.inflate(R.layout.apply_loan_new_popup, null)
            val tvmessage1 = view.findViewById<CNTextView>(R.id.tvmessage1)
            val tvmessage2 = view.findViewById<TextView>(R.id.tvmessage2)
            val tvmessage3 = view.findViewById<TextView>(R.id.tvmessage3)
            val tvNotNow = view.findViewById<TextView>(R.id.tvNotNow)
            val tvPayNow = view.findViewById<TextView>(R.id.tvPayNow)
            if (response.has("message1")) {
                message1 = response.getString("message1")
                tvmessage1.text = message1
            }
            if (response.has("message2") && !response.getString("message2")
                    .equals(null) && !response.getString("message2").equals("")
            ) {
                message2 = response.getString("message2")
                tvmessage2.text = message2
            } else {
                tvmessage2.visibility = View.GONE
            }
            if (response.has("message3")) {
                message3 = response.getString("message3")
                tvmessage3.text = message3
            }
            if (response.has("loan_type")) {
                popuploanType = response.getString("loan_type")
            }

            tvNotNow.setOnClickListener {
                val intent = Intent(context, DashboardActivity::class.java)
                startActivity(intent)
            }
            tvPayNow.setOnClickListener {
                dialog?.dismiss()
                if (popuploanType == "pl") {
                    (activity as DashboardActivity).replaceFrag(
                        ActiveLoansHomeFragment(),
                        "Active Personal Loan",
                        null
                    )

                }
                if (popuploanType == "twl") {
                    (activity as DashboardActivity).replaceFrag(
                        TwlActiveLoansFragment(),
                        "Active Two Wheeler Loan",
                        null
                    )
                }
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

    fun calculateAndUpdateInterestAmount(fromEdit: Boolean) {
        binding?.etLoanAmount?.text = amount.toString()
        binding?.rvEmi?.layoutManager = LinearLayoutManager(activity)

        val tenureList = ArrayList<TenureData>()
        tenureList.addAll(tenureDataList!!)

        mainLoop@
        for (tenure in tenureDataList!!.withIndex()) {
            if (tenure.value.type == AppConstants.LoanEMITypes.EMI) {
                if (amount >= tenure.value.minAmount!! && amount <= tenure.value.maxAmount!!) {
                    for (ten in tenureDataList!!.withIndex()) {
                        if (ten.value.type == AppConstants.LoanEMITypes.Days)
                            if ((amount >= ten.value.minAmount!! && amount <= ten.value.maxAmount!!)) {
                                setProcessingText(tenure.value)
                            } else {
                                tenureList.removeAt(ten.index)
                                break@mainLoop
                            }
                    }
                }
            }
        }

        emiAdapter = LoanEmiAdapter(tenureList, amount, this)
        binding?.rvEmi?.adapter = emiAdapter
        setAmountTransferInfo()
        setDiscount()
        setInstallments(emptyList(), "", TenureData())

        if (amount < applyLoanData?.applyLoanMinAmount!!) {
            binding?.rlCong?.visibility = GONE
        } else if (!isCongGone) {
            binding?.rlCong?.visibility = VISIBLE
        }
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

    private fun checkCanApply() {
        try {
            if (loanType != AppConstants.LoanTypes.BankTransfer) {
                CNProgressDialog.showProgressDialogText(
                    activity,
                    getString(R.string.verifying_amazon_number)
                )
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
            applyNewLoanBean!!.tenureDays = tenureDays
            applyNewLoanBean!!.serviceFee = interestAmount
            applyNewLoanBean!!.processingCharges = processingCharges
            applyNewLoanBean!!.newProcessingCharges = newProcessingCharges
            applyNewLoanBean!!.total = totalAmount
            applyNewLoanBean!!.promo_code = promo_code
            applyNewLoanBean!!.Qcr_req_promo_code = offer_promo_code
            applyNewLoanBean!!.otpPassword = ""
            applyNewLoanBean!!.amazonNumber = amazonNumber
            applyNewLoanBean!!.amazonAmount = amazonAmount.toString()
            applyNewLoanBean!!.bankAmount = bankAmount.toString()
            applyNewLoanBean!!.loanType = loanType
            applyNewLoanBean!!.cashback_amt = discount.toString()
            applyNewLoanBean!!.tenureType = emiAdapter?.getSelectedPosition()?.type
            applyNewLoanBean!!.emiCount = emiAdapter?.getSelectedPosition()?.emiCount
            applyNewLoanBean!!.current_location = (activity as DashboardActivity).currentLocation
            applyNewLoanBean!!.instalmentDataList = installmentAdapter?.getSelectedPosition()
            isSuccessUnlocked = false
            val token = (activity as BaseActivity).userToken
            (activity as BaseActivity).cnModel.checkCanApply(
                this,
                (activity as BaseActivity).userId,
                applyNewLoanBean,
                1,
                token
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun takeConfirmationToApplyForLoan(isBorrower: Boolean) {
        try {
            var msg = ""
            var cancelText = ""
            var okText = ""

            if (isBorrower && !binding?.cbBorrowerTerms?.isChecked!! && applyLoanData!!.userTermsData.ba_uncheck_info != null) {
                msg = applyLoanData!!.userTermsData.ba_uncheck_info
                cancelText = "Continue"
                okText = "Agree and Continue"
            } else {
                msg = resources.getString(R.string.apply_loan_confirmation)
                cancelText = "Cancel"
                okText = "OK"
            }

            CNAlertDialog.setRequestCode(1)
            if (isBorrower && !binding?.cbBorrowerTerms?.isChecked!!) {
                CNAlertDialog.showAlertDialogWithCallback(
                    activity,
                    "Confirm",
                    msg,
                    true,
                    okText,
                    cancelText
                )
            } else {
                CNAlertDialog.showAlertDialogWithCallback(
                    activity,
                    "Confirm",
                    msg,
                    true,
                    okText,
                    cancelText
                )
            }
            CNAlertDialog.setListener(object : AlertDialogSelectionListener {
                override fun alertDialogCallback() {
                }

                override fun alertDialogCallback(buttonType: ButtonType, requestCode: Int) {
                    if (buttonType == ButtonType.POSITIVE) {
                        TrackingUtil.getInstance().logEvent(TrackingUtil.Event.APPLY_LOAN)
                        CNAlertDialog.dismiss()
                        if (requestCode == 20) {
                            applyLoanWithoutOTP(0)
                        } else {
                            applyLoanWithoutOTP(1)
                        }
                    } else if (buttonType == ButtonType.NEGATIVE) {
                        CNAlertDialog.dismiss()
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun applyLoanWithoutOTP(s: Int) {
        if (loanType != AppConstants.LoanTypes.BankTransfer) {
            CNProgressDialog.showProgressDialogText(
                activity,
                getString(R.string.verifying_amazon_number)
            )
        }

        newProcessingCharges = if (processingCharges == 100) {
            processingCharges
        } else {
            if (amount > 5000) processingCharges else processingCharges / 2
        }

        promo_code = binding?.etReferralCode?.text.toString().trim()
        applyNewLoanBean!!.receivedOTP = ""
        applyNewLoanBean!!.amount = amount
        applyNewLoanBean!!.tenureDays = tenureDays
        applyNewLoanBean!!.serviceFee = interestAmount
        applyNewLoanBean!!.processingCharges = processingCharges
        applyNewLoanBean!!.newProcessingCharges = newProcessingCharges
        applyNewLoanBean!!.total = totalAmount
        applyNewLoanBean!!.promo_code = promo_code
        applyNewLoanBean!!.Qcr_req_promo_code = offer_promo_code
        applyNewLoanBean!!.otpPassword = ""
        applyNewLoanBean!!.amazonNumber = amazonNumber
        applyNewLoanBean!!.amazonAmount = amazonAmount.toString()
        applyNewLoanBean!!.bankAmount = bankAmount.toString()
        applyNewLoanBean!!.loanType = loanType
        applyNewLoanBean!!.cashback_amt = discount.toString()
        applyNewLoanBean!!.tenureType = emiAdapter?.getSelectedPosition()?.type
        applyNewLoanBean!!.emiCount = emiAdapter?.getSelectedPosition()?.emiCount
        applyNewLoanBean!!.current_location = (activity as DashboardActivity).currentLocation
        applyNewLoanBean!!.instalmentDataList = installmentAdapter?.getSelectedPosition()

        isSuccessUnlocked = false
        val token = (activity as BaseActivity).userToken
        (activity as BaseActivity).cnModel.validateOTPAndApplyLoanEMI(
            this,
            (activity as BaseActivity).userId,
            applyNewLoanBean,
            s,
            token
        )
    }

    fun updateApplyLoanStatus(response: JSONObject) {
        CNProgressDialog.hideProgressDialog()
        try {
            /* if ((activity as BaseActivity).userDetails.hasTakenFirstLoan != 1) {
                 //    AdGyde.onSimpleEvent(getString(R.string.user_applied_loan))
                 val params = HashMap<String, Any>()
                 val key = getString(R.string.user_applied_loan)
                 params[key] = getString(R.string.user_applied_loan) //patrametre name,value change to event
                 AppsFlyerLib.getInstance().logEvent(activity as DashboardActivity, key, params)

                 val logger = AppEventsLogger.newLogger(this.context)
                 logger.logEvent(getString(R.string.user_applied_loan), Bundle())

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
                    (activity as DashboardActivity).getApplyLoanData(false)
                } else if (statusRedirect == Constants.STATUS_REDIRECT_CODE_FIVE_REFERENCES) { // Five References
                    (activity as DashboardActivity).getApplyLoanData(false)
                    (activity as DashboardActivity).onBackPressed()
                }
            } else {
                showSuccess(message)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun showSuccess(message: String) {
        CNAlertDialog.showAlertDialogWithCallback(
            activity,
            "",
            message, false, "", ""
        )

        CNAlertDialog.setRequestCode(1)

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {

            }

            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {
                if (buttonType == Constants.ButtonType.POSITIVE) {
                    isSuccessUnlocked = true
                    binding?.tvReferral?.visibility = GONE
                    binding?.etReferralCode?.visibility = GONE
                    (activity as BaseActivity).sharedPreferences.putBoolean(
                        Constants.SP_REFER_CODE_IS_REGISTERED,
                        false
                    )
                    (activity as BaseActivity).sharedPreferences.putString(
                        Constants.SP_REFER_CODE,
                        ""
                    )
                    if ((activity as BaseActivity).userDetails.hasTakenFirstLoan == 1) {
                        (activity as DashboardActivity).getApplyLoanData(false)
                    } else {
                        (activity as DashboardActivity).getApplyLoanData(true)
                    }
                    (activity as DashboardActivity).onBackPressed()
                    CNAlertDialog.dismiss()
                    CNProgressDialog.hideProgressDialog()
                }
            }
        })
    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder(
            (activity as BaseActivity).activityContext,
            R.style.RulesAlertDialogStyle
        )
        val inflater: LayoutInflater = this@ApplyLoanEMIFragment.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_terms_conditions, null)
        alert.setView(dialogView)
        val tvTitle: CNTextView = dialogView.findViewById(R.id.et_title)
        val tvBack: CNTextView = dialogView.findViewById(R.id.tvBack)
        tvTitle.text = title
        val pb = dialogView.findViewById<ProgressBar>(R.id.pb)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.settings.loadWithOverviewMode = true
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
                    val s = type.loanSubText.toString()
                        .replace("{apay_amount}", amazonAmount.toString())
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
        if (applyLoanData?.amazonPayNumber != null && applyLoanData?.amazonPayNumber.toString()
                .isNotEmpty()
        ) {
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
                    showTermsPolicyDialog(
                        applyLoanData?.loanTypes?.get(click)?.loanTermsKeyword!!,
                        applyLoanData?.apayTermsLink!!
                    )
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
                            ss = SpannableString(
                                applyLoanData?.loanTypes?.get(ind)?.loanTerms?.replace(
                                    "{apay_amount}",
                                    amount.toString()
                                )
                            )
                            wordToFind = applyLoanData?.loanTypes?.get(ind)?.loanTermsKeyword!!
                        }
                    }

                    AppConstants.LoanTypes.BankApay -> {
                        if (type.loanType == index.toString()) {
                            click = ind
                            val s = applyLoanData?.loanTypes?.get(ind)?.loanTerms?.replace(
                                "{apay_amount}",
                                amazonAmount.toString()
                            )
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

    private fun setProcessingText(tenure: TenureData) {
        for (insurance in tenure.insurance!!.withIndex()) {
            if (amount >= insurance.value.min!!.toInt() && amount <= insurance.value.max!!.toInt()) {
                //text != empty
                if (insurance.value.canApplicable == true) {
                    binding?.llTerms?.visibility = View.VISIBLE
                    var PartnerText = tenure.userTermsData?.message.toString()
                    replacePartnerText =
                        PartnerText.replace("{{partner}}", insurance.value.partner.toString())

                    var startIndex: Int
                    var endIndex: Int
                    val ss: SpannableString?
                    ss = SpannableString(replacePartnerText)
                    val words: List<String> = tenure.userTermsData?.findText!!.split("|")
                    val links: List<String> = tenure.userTermsData.replaceLinks!!.split("|")

                    for (w in words.withIndex()) {
                        val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                            override fun onClick(textView: View) {
                                if (emiAdapter!!.getSelectedPosition().type.equals(AppConstants.LoanEMITypes.EMI)) {
                                    var url = links[w.index]
                                    val userid = (activity as BaseActivity).userDetails.userId
                                    url += "?emi_count=" + emiAdapter!!.getSelectedPosition().emiCount + "&user_id=" + userid + "&amount=" + amount
                                    Log.d("url", url)
                                    showTermsPolicyDialog(w.value, url)
                                } else {
                                    var url = links[w.index]
                                    val userid = (activity as BaseActivity).userDetails.userId
                                    url += "?emi_count=1&user_id=$userid&amount=$amount"
                                    showTermsPolicyDialog(w.value, url)
                                }
                                //showTermsPolicyDialog(w.value, links[w.index])
                            }
                        }
                        startIndex = ss.indexOf(w.value, 0)
                        endIndex = startIndex + w.value.length
                        ss.setSpan(
                            termsAndCondition,
                            startIndex,
                            endIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        ss.setSpan(
                            ForegroundColorSpan(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.Primary1
                                )
                            ),
                            startIndex,
                            endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        scale = resources.displayMetrics.density
                        binding?.tvPrivacyLink?.text = ss
                        binding?.tvPrivacyLink?.movementMethod = LinkMovementMethod.getInstance()
                    }

                } else {
                    binding?.llTerms?.visibility = View.GONE
                }
                // insurance calculation

                var pAmount = (amount * (tenure.processingFee)!!.toFloat())
                if (insurance.value.percType == 1) {
                    percAmount = (amount * (tenure.insurancePerc)!!.toFloat())
                } else if (insurance.value.percType == 2) {
                    percAmount = insurance.value.perc!!.toFloat()
                } else if (insurance.value.percType == 0) {
                    percAmount = (amount * (tenure.insurancePerc)!!.toFloat())
                }
                val tancStr1 = insurance.value.tncStr1
                val tancStr2 = insurance.value.tncStr2

                if (pAmount < tenure.minProcessingFee!!) {
                    pAmount = tenure.minProcessingFee!!.toFloat()
                }
                val noteText2 = tenure.processingText!!.replace("{{tnc_str1}}", tancStr1.toString())
                val noteText = noteText2.replace("{{tnc_str2}}", tancStr2.toString())
                val percText = noteText.replace("{insurance}", percAmount.toString())
                val pText = percText.replace("{{perc}}", pAmount.roundToInt().toString())
                if (pText.contains("*")) {
                    binding?.tvProcessAmount?.text = Html.fromHtml(
                        "<font color=#FA7300>" + pText.substring(0, 1) + "</font>"
                                + pText.substring(1, pText.length)
                    )
                } else {
                    binding?.tvProcessAmount?.text = pText
                }
                //gone
            }
        }

    }

    private fun setTermsText(userTermsData: UserTermsData) {
        var startIndex: Int
        var endIndex: Int
        val ss: SpannableString?
        if (userTermsData.message != null) {
            val words: List<String> = userTermsData.findText!!.split("||")
            val links: List<String> = userTermsData.replaceLinks!!.split("||")
            ss = SpannableString(userTermsData.message)
            for (w in words.withIndex()) {
                val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        showTermsPolicyDialog(w.value, links[w.index])
                    }
                }
                startIndex = ss.indexOf(w.value, 0)
                endIndex = startIndex + w.value.length
                ss.setSpan(
                    termsAndCondition,
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                scale = resources.displayMetrics.density
                binding?.tvPrivacyLink?.text = ss
                binding?.tvPrivacyLink?.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    fun setInstallments(instalments: List<InstalmentData>, type: String, tenure: TenureData) {
        if (instalments.isEmpty()) {
            clearOfferData()
        }
        if (!instalments.isNullOrEmpty()) {
            val displayMetrics = DisplayMetrics()
            instalmentsList = instalments
            selectedTenure = tenure
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            installmentAdapter =
                LoanInstallmentAdapter(instalments, amount, displayMetrics.widthPixels, type, this)
            binding?.rvInstallments?.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            binding?.rvInstallments?.adapter = installmentAdapter
            binding?.pageIndicatorView?.count = instalments.size
            binding?.pageIndicatorView?.selection = 0

            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
                    super.onScrolled(recyclerView, dx, dy)
                    val firstItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    binding?.pageIndicatorView?.selection = firstItem
                }
            }
            binding?.rvInstallments?.addOnScrollListener(scrollListener)
            setProcessingText(tenure)
            binding?.pageIndicatorView?.visibility = VISIBLE
            binding?.rvInstallments?.visibility = VISIBLE
            binding?.tvProcessAmount?.visibility = VISIBLE
        } else {
            binding?.pageIndicatorView?.visibility = GONE
            binding?.rvInstallments?.visibility = GONE
            binding?.tvProcessAmount?.visibility = GONE
        }
        if (instalments.size > 1) {
            binding?.pageIndicatorView?.visibility = VISIBLE
        } else {
            binding?.pageIndicatorView?.visibility = GONE
        }

        if (tenure.userTermsData != null) {
            //setTermsText(tenure.userTermsData)
            if (tenure.userTermsData.ba_replace_links != null && !tenure.userTermsData.ba_replace_links.equals(
                    ""
                )
            ) {
                binding?.llBorrowerTerms?.visibility = VISIBLE
                setBorrowTerms(tenure.userTermsData)
            } else {
                binding?.llBorrowerTerms?.visibility = GONE
            }
        }
    }

    fun setBorrowTerms(userTermsData: UserTermsData) {
        var startIndex: Int
        var endIndex: Int
        val ss: SpannableString?
        if (userTermsData.ba_message != null) {
            val words: List<String> = userTermsData.ba_find_text!!.split("||")
            val links: List<String> = userTermsData.ba_replace_links!!.split("||")
            ss = SpannableString(userTermsData.ba_message)
            for (w in words.withIndex()) {
                val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) =
                        if (emiAdapter!!.getSelectedPosition().type.equals(AppConstants.LoanEMITypes.EMI)) {
                            var url = links[w.index]
                            val userid = (activity as BaseActivity).userDetails.userId
                            url += "?emi_count=" + emiAdapter!!.getSelectedPosition().emiCount + "&user_id=" + userid + "&amount=" + amount
                            Log.d("url", url)
                            showTermsPolicyDialog(w.value, url)
                        } else {
                            var url = links[w.index]
                            val userid = (activity as BaseActivity).userDetails.userId
                            url += "?emi_count=$tenureDays&user_id=$userid&amount=$amount"
                            showTermsPolicyDialog(w.value, url)
                        }
                }
                startIndex = ss.indexOf(w.value, 0)
                endIndex = startIndex + w.value.length
                ss.setSpan(
                    termsAndCondition,
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                scale = resources.displayMetrics.density
                binding?.tvBorrowerTerms?.text = ss
                binding?.tvBorrowerTerms?.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    fun setDays(days: Int, instalments: List<InstalmentData>, tenure: TenureData) {
        tenureDays = days
        disableAgreeButton()
        instalments[0].tenureDays = tenureDays.toDouble()
        if (days >= 0) {
            setInstallments(instalments, AppConstants.LoanEMITypes.Days, tenure)

        } else {
            setInstallments(emptyList(), "", tenure)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSuccessUnlocked) {
            (activity as DashboardActivity).getApplyLoanData(false)
            (activity as DashboardActivity).onBackPressed()
        }
    }

    private fun showPurposeDialog() {
        //disableAgreeButton()
        loanPurposeArray = arrayOfNulls(masterJsonResponse?.purposeOfLoan!!.size)
        for (i in 0 until masterJsonResponse?.purposeOfLoan!!.size) {
            loanPurposeArray!![i] =
                masterJsonResponse?.purposeOfLoan?.get(i)?.value // Whichever string you wanna store here from custom object
        }
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setItems(loanPurposeArray) { _, which ->
            binding?.etPurpose?.setText(loanPurposeArray!![which])
            if (masterJsonResponse?.purposeOfLoan!![which].id == 200) {
                binding?.etCustomPurpose?.visibility = VISIBLE
                binding?.etCustomPurpose?.setText("")
                applyNewLoanBean!!.qcr_custom_purpose = ""
            } else {
                applyNewLoanBean!!.qcr_custom_purpose = ""
                binding?.etCustomPurpose?.visibility = GONE
            }
            binding?.cbBorrowerTerms?.isEnabled = true
            binding?.cbBorrowerTerms?.isClickable = true
            applyNewLoanBean!!.qcr_purpose_of_loan = masterJsonResponse?.purposeOfLoan!![which].id
        }
        designDialog(builder)
    }

    private fun getMasterJson() {
        (activity as DashboardActivity).cnModel.getMasterJsonLoan(this)
    }

    fun updateMasterJson(response: JSONObject) {
        try {
            val strJson = response.toString()
            masterJsonResponse = Gson().fromJson(strJson, MasterJsonResponse::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun designDialog(builder: android.app.AlertDialog.Builder) {
        var dialog: android.app.AlertDialog? = null
        dialog = builder.create()
        dialog?.show()

        val displayMetrics = DisplayMetrics()
        (activity as BaseActivity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
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

    fun uncheckPosition() {
        rvEmi.findViewHolderForAdapterPosition(selectedPos)?.itemView?.rbOption?.isChecked = false
        //binding?.rvEmi?.findViewHolderForAdapterPosition(selectedPos)?.itemView?.rootView?.rbOption?.isChecked = false
    }
}
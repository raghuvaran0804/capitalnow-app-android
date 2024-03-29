package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.UserTermsData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.FragmentVehicleApplyLoanBinding
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.VehicleLoanInstallmentAdapter
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.kotlin.utils.AppConstants.LoanEMITypes.Companion.EMI
import com.capitalnowapp.mobile.models.ApplyTWLoanReq
import com.capitalnowapp.mobile.models.ApplyTWLoanResponse
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.GetTwlTenureDataReq
import com.capitalnowapp.mobile.models.TwlTenureData
import com.capitalnowapp.mobile.models.TwlTenureDataResponse
import com.capitalnowapp.mobile.models.loan.EmiDateRanges
import com.capitalnowapp.mobile.models.loan.InstalmentData
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_vehicle_apply_loan.cbBorrowerTerms
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.lang.ref.WeakReference
import kotlin.math.roundToInt


class VehicleApplyLoanFragment : Fragment() {

    private var twlTenureDataResponse: TwlTenureDataResponse? = null
    private var selectedInstallments: List<InstalmentData>? = null
    private var tenureDataList: List<TwlTenureData>? = null
    private var selectedTenure: TwlTenureData? = null
    private var installmentAdapter: VehicleLoanInstallmentAdapter? = null
    private var scale: Float = 0.0f
    private val seekInterval = 1
    private var dateRange = 0
    private var binding: FragmentVehicleApplyLoanBinding? = null
    private var amount: Int = 0
    //private var offer_promo_code: String? = ""


    private var processingFeeTitle: String? = ""
    private var processingFeeSubTitle: String? = ""
    var sharedPreferences: CNSharedPreferences? = null
    private var loanType = ""
    var tenureDays: Int = -1
    var isTenureSelected = false
    private var activity: Activity? = null
    private var AMOUNT_MAXIMUM = 10000
    private var AMOUNT_MINIMUM = 1000
    private var selectedDealerId: String? = null
    private var selectedVehicleId = 0
    private var selectedVehiclePrice = 0
    private var selectedVehicleArea: String? = null
    private var selectedVehicleCity: String? = null
    private var selectedVehicleDealer: String? = null
    private var selectedVehicleBrand: String? = null
    private var selectedStartDate: String? = null
    //var instalmentsList: List<InstalmentData>? = null

    @SuppressLint("NotConstructor")
    fun VehicleApplyLoanFragment() {
        // empty constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVehicleApplyLoanBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            sharedPreferences = CNSharedPreferences(activity)
            sharedPreferences!!.putBoolean(Constants.From_Vehicle_Details, false)
            (activity as BaseActivity).userId = (activity as BaseActivity).userDetails.userId
            (activity as BaseActivity).cnModel =
                CNModel(context, activity, Constants.RequestFrom.APPLY_LOAN)

            selectedDealerId = arguments?.getString("selectedDealerId")!!
            selectedVehicleArea = arguments?.getString("selectedVehicleArea")!!
            selectedVehicleCity = arguments?.getString("selectedVehicleCity")!!
            selectedVehicleDealer = arguments?.getString("selectedVehicleDealer")!!
            selectedVehicleBrand = arguments?.getString("selectedVehicleBrand")!!
            selectedVehicleId = arguments?.getInt("selectedVehicleId")!!
            selectedVehiclePrice = arguments?.getInt("selectedVehiclePrice")!!

            //binding?.tvAmountText?.text =

            getTwlTenureData()

            processingFeeTitle = resources.getString(R.string.apply_loan_processing_fee_title)
            processingFeeSubTitle =
                resources.getString(R.string.apply_loan_processing_fee_sub_title)


            //disableAgreeButton()
            //  binding?.ivSubmit?.setImageResource(R.drawable.login_button_img_grey)


            binding?.ivSubmit?.setOnClickListener { //requestOTP();
                if(binding?.llTerms?.visibility == VISIBLE && !binding!!.cbAgreeTerms.isChecked){
                    Toast.makeText(
                        activity,
                        "Please accept Terms & Conditions",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if (binding!!.llBorrowerTerms.visibility == VISIBLE && !binding!!.cbBorrowerTerms.isChecked) {
                    Toast.makeText(
                        activity,
                        "Please accept Borrower Agreement",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val amt =
                        binding!!.etLoanAmount.text.toString().replace("[^\\d.]".toRegex(), "")
                    /*AMOUNT_MAXIMUM = selectedTenure?.maxAmount?.toFloat()?.toInt()!!
                AMOUNT_MINIMUM = selectedTenure?.minAmount!!*/
                    if (amt.isNotEmpty()) {
                        if (amt.toInt() in AMOUNT_MINIMUM..AMOUNT_MAXIMUM) {
                            loanType = AppConstants.LoanTypes.BankTransfer
                            when (loanType) {
                                AppConstants.LoanTypes.BankTransfer -> {
                                    takeConfirmationToApplyForLoan()
                                }
                            }

                        } else {
                            if(twlTenureDataResponse != null && !twlTenureDataResponse!!.eligibilityMessage.isNullOrEmpty()) {
                                Toast.makeText(
                                    activity,
                                    twlTenureDataResponse?.eligibilityMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else{
                                Toast.makeText(
                                    activity,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        if(twlTenureDataResponse != null && !twlTenureDataResponse!!.eligibilityMessage.isNullOrEmpty()) {
                            Toast.makeText(
                                activity,
                                twlTenureDataResponse?.eligibilityMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }else{
                            Toast.makeText(
                                activity,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }


            binding?.etLoanAmount!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    val text = binding!!.etLoanAmount.text.trim().toString()
                    if (text.isNotEmpty()) {
                        if (text.toInt() > AMOUNT_MAXIMUM) {
                            binding!!.etLoanAmount.setText("")
                            if (twlTenureDataResponse != null && !twlTenureDataResponse!!.eligibilityMessage.isNullOrEmpty()) {
                                Toast.makeText(
                                    activity,
                                    twlTenureDataResponse?.eligibilityMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else {
                                Toast.makeText(
                                    activity,
                                    "Something went wrong ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            if (!selectedInstallments.isNullOrEmpty()) {
                                amount = text.toInt()
                                setInstallments(
                                    selectedInstallments,
                                    EMI,
                                    selectedTenure!!,
                                    dateRange
                                )
                            }
                        }
                    }
                }

            })

            binding!!.seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onProgressChanged(
                        seekBar: SeekBar, progress: Int,
                        fromUser: Boolean
                    ) {
                        try {
                            //disableAgreeButton()
                            amount = (AMOUNT_MINIMUM) + (seekInterval * progress)
                            calculateAndUpdateInterestAmount(false)
                            setTenureSeekBar(tenureDataList, binding!!.seekBarTenure.progress)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )

            binding!!.seekBarTenure.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onProgressChanged(
                        seekBar: SeekBar, progress: Int,
                        fromUser: Boolean
                    ) {
                        try {
                            //disableAgreeButton()
                            setTenureSeekBar(tenureDataList, progress)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )

            binding!!.seekBarDate.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onProgressChanged(
                        seekBar: SeekBar, progress: Int,
                        fromUser: Boolean
                    ) {
                        try {
                            //disableAgreeButton()
                            dateRange = progress
                            setTenureDays(selectedTenure?.emiDateRanges, progress)
                            setInstallments(
                                tenureDataList!![binding!!.seekBarTenure.progress].instalments,
                                EMI,
                                tenureDataList!![binding!!.seekBarTenure.progress],
                                dateRange.plus(1)
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )

            binding?.cbAgreeTerms?.setOnClickListener {
                if (binding!!.cbAgreeTerms.isChecked) {
                    binding!!.ivSubmit.isEnabled = true
                }
                //else
                    //disableAgreeButton()
            }

            loanType = AppConstants.LoanTypes.BankTransfer

            binding!!.ivRemove.setOnClickListener {
                binding!!.seekBar.progress = binding!!.seekBar.progress.minus(1)
            }
            binding!!.ivAdd.setOnClickListener {
                binding!!.seekBar.progress = binding!!.seekBar.progress.plus(1)
            }
            binding!!.tvinFoHeadsUp.visibility = GONE



            KeyboardVisibilityEvent.setEventListener(
                activity as BaseActivity,
                object : KeyboardVisibilityEventListener {
                    override fun onVisibilityChanged(isOpen: Boolean) {
                        if (isOpen) {
                        } else {
                            validateEditAmount()
                        }
                    }
                })
            calculateAndUpdateInterestAmount(false)

            /*binding!!.etPromoCode.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    when(s?.length){
                        0 ->{
                            offer_promo_code = ""
                            binding!!.tvApplyPromo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_gray))
                            binding!!.tvApplyPromo.isEnabled = false
                        }
                        in 1..4 ->{
                            // invalid
                            offer_promo_code = ""
                            binding!!.tvApplyPromo.isEnabled = true
                            binding!!.tvApplyPromo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.Primary1))
                        }
                        in 5..10 -> {
                            // validcase
                            binding!!.tvApplyPromo.isEnabled = true
                            binding!!.tvApplyPromo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.Primary1))
                            offer_promo_code = s.toString().trim()
                        }
                    }
                }

            })*/

            /*binding!!.tvApplyPromo.setOnClickListener {
                if(offer_promo_code?.length!! >=5) {
                    checkPromoCode()
                }else{
                    showAlertDialog("Invalid Promo Code")
                }
            }
            binding!!.ivDismiss.setOnClickListener {
                binding!!.tvApplyPromo.visibility = VISIBLE
                binding!!.tvApplyPromo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_gray))
                binding!!.ivDismiss.visibility = GONE
                binding!!.etPromoCode.setText("")
            }
            binding!!.ivDismissRed.setOnClickListener {
                binding!!.tvApplyPromo.visibility = VISIBLE
                binding!!.tvApplyPromo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_gray))
                binding!!.ivDismissRed.visibility = GONE
                binding!!.etPromoCode.setText("")
            }*/


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*private fun checkPromoCode(){
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val checkPromoCodeReq = CheckPromoCodeReq()
            checkPromoCodeReq.isPl = "1"
            checkPromoCodeReq.loanType = AppConstants.LoanEMITypes.EMI
            checkPromoCodeReq.platform = "Android"
            checkPromoCodeReq.amount = amount
            checkPromoCodeReq.code = offer_promo_code
            checkPromoCodeReq.emiCount = selectedTenure?.emiCount
            val token = (activity as BaseActivity).userToken
            genericAPIService.checkPromoCode(checkPromoCodeReq,token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val checkPromoCodeResponse =
                    Gson().fromJson(responseBody, CheckPromoCodeResponse::class.java)
                if (checkPromoCodeResponse != null && checkPromoCodeResponse.status == true) {
                    showPromoSuccessPopUp(checkPromoCodeResponse)
                    binding?.tvApplyPromo?.visibility = GONE
                    binding?.ivDismiss?.visibility = VISIBLE
                    setEmiOffer(checkPromoCodeResponse)
                }else{
                    binding?.tvApplyPromo?.visibility = GONE
                    binding?.ivDismissRed?.visibility = VISIBLE
                    CNAlertDialog.showAlertDialog(context, resources.getString(R.string.title_alert), checkPromoCodeResponse.message)
                }

            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                (activity as BaseActivity).displayToast(it.message.toString())
            }

        }catch (e: Exception){
            e.printStackTrace()
        }

    }*/

    /*private fun setEmiOffer(checkPromoCodeResponse: CheckPromoCodeResponse) {
        try {
            if(checkPromoCodeResponse.data?.array!=null && checkPromoCodeResponse.data!!.array?.size!! >0){
                installmentAdapter?.setOfferAmountList(checkPromoCodeResponse.data!!.array!!)
            }else{
                installmentAdapter?.setOfferAmountList(ArrayList())
            }
            installmentAdapter?.notifyDataSetChanged()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }*/

    /*private fun showPromoSuccessPopUp(checkPromoCodeResponse: CheckPromoCodeResponse) {
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
    }*/

    private fun validateEditAmount() {
        val text = binding!!.etLoanAmount.text.trim().toString()
        if (text.isNotEmpty()) {
            if (text.toInt() > AMOUNT_MAXIMUM || text.toInt() < AMOUNT_MINIMUM) {
                binding!!.etLoanAmount.setText("")
                if(twlTenureDataResponse != null && !twlTenureDataResponse!!.eligibilityMessage.isNullOrEmpty()) {
                    Toast.makeText(
                        activity,
                        twlTenureDataResponse?.eligibilityMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    Toast.makeText(
                        activity,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                amount = 0
                setInstallments(selectedInstallments, EMI, selectedTenure!!, dateRange)
            } else {
                if (!selectedInstallments.isNullOrEmpty()) {
                    amount = text.toInt()
                    setInstallments(selectedInstallments, EMI, selectedTenure!!, dateRange)
                }
            }
        } else {
            if(twlTenureDataResponse != null && !twlTenureDataResponse!!.eligibilityMessage.isNullOrEmpty()) {
                Toast.makeText(
                    activity,
                    twlTenureDataResponse?.eligibilityMessage,
                    Toast.LENGTH_SHORT
                ).show()
            } else{
                Toast.makeText(
                    activity,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
            amount = 0
            setInstallments(selectedInstallments, EMI, selectedTenure!!, dateRange)
        }
    }

    private fun takeConfirmationToApplyForLoan() {
        try {
            var msg = ""
            var cancelText = ""
            var okText = ""

            msg = resources.getString(R.string.apply_loan_confirmation)
            cancelText = "Cancel"
            okText = "OK"

            CNAlertDialog.setRequestCode(1)

            CNAlertDialog.showAlertDialogWithCallback(
                activity,
                "Confirm",
                msg,
                true,
                okText,
                cancelText
            )
            CNAlertDialog.setListener(object : AlertDialogSelectionListener {
                override fun alertDialogCallback() {
                }

                override fun alertDialogCallback(
                    buttonType: Constants.ButtonType,
                    requestCode: Int
                ) {
                    if (buttonType == Constants.ButtonType.POSITIVE) {
                        CNAlertDialog.dismiss()
                        applyTWLoan()
                    } else if (buttonType == Constants.ButtonType.NEGATIVE) {
                        CNAlertDialog.dismiss()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAmountToDefault() {
        val diff = this.AMOUNT_MAXIMUM - this.AMOUNT_MINIMUM
        val quotient: Int = diff / seekInterval
        binding?.seekBar?.max = quotient
        binding?.seekBar?.progress = quotient
    }

    private fun enableAgreeButton() {
        //    binding?.ivSubmit?.setImageResource(R.drawable.login_button_img_green)
        binding?.ivSubmit?.isEnabled = true
        binding?.ivSubmit?.setTextColor(
            ContextCompat.getColor(
                activity as BaseActivity,
                R.color.white
            )
        )
        cbBorrowerTerms.isEnabled = true
        cbBorrowerTerms.isClickable = true
    }

    private fun disableAgreeButton() {
        binding?.cbAgreeTerms?.isChecked = false
        //   binding?.ivSubmit?.setImageResource(R.drawable.login_button_img_grey)
        binding?.ivSubmit?.isEnabled = false

    }

    fun calculateAndUpdateInterestAmount(fromEdit: Boolean) {
        binding?.etLoanAmount?.setText(amount.toString())
        if (tenureDataList != null && tenureDataList?.size!! > 0) {
            val tenureList = ArrayList<TwlTenureData>()
            tenureList.addAll(tenureDataList!!)
        }
    }

    private fun setTermsText(userTermsData: UserTermsData) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder(
            (activity as BaseActivity).activityContext,
            R.style.RulesAlertDialogStyle
        )
        val inflater: LayoutInflater = this.layoutInflater
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
                    startActivity(intent)
                    view.reload()
                    return true
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                pb.visibility = View.VISIBLE
                view.visibility = View.GONE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                pb.visibility = View.GONE
                view.visibility = View.VISIBLE
                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl(link)
        tvBack.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun setBorrowTerms(userTermsData: UserTermsData) {
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
                        if(true) {
                            var url = links[w.index]
                            val userid = (activity as BaseActivity).userDetails.userId
                            url += "?emi_count=" + selectedTenure?.emiCount+ "&user_id=" + userid + "&amount=" + amount
                            Log.d("url", url)
                            showTermsPolicyDialog(w.value, url)
                        }else{

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

                scale = (activity as DashboardActivity).resources.displayMetrics.density
                binding?.tvBorrowerTerms?.text = ss
                binding?.tvBorrowerTerms?.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    /*private fun showAlert(vehicleDetailsResponse: GetVehicleDetailsResponse?) {
        CNAlertDialog.setRequestCode(1)
        CNAlertDialog.showAlertDialogWithCallback(context, "", vehicleDetailsResponse?.message, false, "", "")

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {}
            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {
                if (buttonType == Constants.ButtonType.POSITIVE) {

                }
            }
        })
    }*/

    fun showAlertDialog(message: String?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(activity, resources.getString(R.string.title_alert), message)
    }

    private fun applyTWLoan() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity,0)
        val applyTWLoanReq = ApplyTWLoanReq()
        val token = (activity as BaseActivity).userToken
        applyTWLoanReq.twlDealerId = selectedDealerId
        applyTWLoanReq.twlBikeId = selectedVehicleId
        applyTWLoanReq.twlBikeRate = selectedVehiclePrice
        applyTWLoanReq.twlBrand = selectedVehicleBrand.toString()
        applyTWLoanReq.twlAreaName = selectedVehicleArea.toString()
        applyTWLoanReq.twlCityName = selectedVehicleCity.toString()
        applyTWLoanReq.twlDealerName = selectedVehicleDealer.toString()
        applyTWLoanReq.twlInstalments = installmentAdapter?.getSelectedPosition()
        applyTWLoanReq.twlEmiCount = selectedTenure?.emiCount?.toInt()
        applyTWLoanReq.startDate = selectedStartDate.toString()
        applyTWLoanReq.startMonth = selectedTenure?.startMonth.toString()
        applyTWLoanReq.twlAmount = amount
        applyTWLoanReq.twlTotal = amount
        applyTWLoanReq.twlAcceptPreAgreement = 1
        var location = ""
        if ((activity as DashboardActivity).mCurrentLocation != null) {
            location =
                (activity as DashboardActivity).mCurrentLocation?.latitude.toString() + "," + (activity as DashboardActivity).mCurrentLocation?.longitude
        }
        applyTWLoanReq.currentLocation = location
        genericAPIService.applyTWLoan(applyTWLoanReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val applyTWLoanResponse = Gson().fromJson(
                responseBody,
                ApplyTWLoanResponse::class.java
            )
            if (applyTWLoanResponse != null && applyTWLoanResponse.status == Constants.STATUS_SUCCESS) {
                (activity as DashboardActivity).selectedTab =
                    (activity as DashboardActivity).getString(R.string.home)
                (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                (activity as DashboardActivity).isFromApply = false
                (activity as DashboardActivity).getApplyLoanData(false)
            } else {
                showAlertDialog(applyTWLoanResponse.message)
            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }
    }

    private fun getTwlTenureData() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(context,0)
            val getTwlTenureDataReq = GetTwlTenureDataReq()
            getTwlTenureDataReq.vehicleId = selectedVehicleId.toString()
            val token = (activity as BaseActivity).userToken
            genericAPIService.getTwlTenureData(getTwlTenureDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                twlTenureDataResponse = Gson().fromJson(
                    responseBody,
                    TwlTenureDataResponse::class.java
                )
                Handler().postDelayed(this::setDefaultSelection, 500)
                if (twlTenureDataResponse != null && twlTenureDataResponse!!.status == Constants.STATUS_SUCCESS) {
                    if (twlTenureDataResponse!!.twlTenureData?.size!! > 0) {
                        AMOUNT_MINIMUM = twlTenureDataResponse!!.minAmount!!
                        AMOUNT_MAXIMUM = twlTenureDataResponse!!.maxAmount!!
                        tenureDataList = twlTenureDataResponse!!.twlTenureData
                        amount = AMOUNT_MAXIMUM
                        setAmountToDefault()
                        binding!!.tveNachMessage.text =
                            twlTenureDataResponse!!.twlTenureData!![0].eNachMessage
                        binding?.tvMaxLoanLimit?.text =
                            "Current Eligibility Limit " + twlTenureDataResponse!!.eligibilityAmount!!
                        binding?.tvAmountText?.text = twlTenureDataResponse!!.amountText
                        setTenureSeekBar(tenureDataList, binding!!.seekBarTenure.progress)
                    }

                } else {
                    //Error
                }
            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun setTenureSeekBar(tenureDataList: List<TwlTenureData>?, progress: Int) {
        try {
            val size = tenureDataList?.size // 4
            val max = size?.minus(1)
            binding!!.seekBarTenure.max = max!! // 0-3, 1-6, 2-9, 3-12
            binding!!.seekBarTenure.progress = progress

            setTenureMonths(tenureDataList[progress].emiCount?.toInt()!!)

            setTenureDays(
                tenureDataList[progress].emiDateRanges,
                tenureDataList[progress].emiDateRanges?.interval!!
            )
            setInstallments(
                tenureDataList[progress].instalments,
                EMI,
                tenureDataList[progress], dateRange.plus(1)
            )
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun getDayNumberSuffix(day: Int): String? {
        return if (day in 11..13) {
            "th"
        } else {
            when (day % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
    }

    private fun setDefaultSelection(){
        try {
            binding!!.seekBarTenure.progress = twlTenureDataResponse?.preferredMonth!!.minus(1)
            binding!!.seekBarDate.progress = selectedTenure?.preferredDate!!.minus(1)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun setTenureDays(emiDateRanges: EmiDateRanges?, progress: Int) {
        val max = emiDateRanges?.max?.minus(1)  // 6 steps
        binding!!.seekBarDate.max = max!!
        binding!!.seekBarDate.progress = progress
        val text = "Every month of "
        var progresstext = progress
        progresstext = progresstext.plus(1)
        if (progresstext > emiDateRanges.max!!) {
            progresstext = progresstext.minus(1)
        }
        binding!!.etTenureDate.text =
            ("${text} $progresstext${getDayNumberSuffix(day = progresstext)}" )
    }

    private fun setTenureMonths(size: Int) {
        var text = ""
        text = if (size == 1) {
            "Month"
        } else {
            "Months"
        }
        binding!!.etTenureMonths.text =
            ("$size $text")
    }

     private fun setInstallments(
        instalments: List<InstalmentData>?,
        type: String,
        tenure: TwlTenureData, startDateInAMonth: Int
    ) {
        val startMonth = selectedTenure?.startMonth
        selectedTenure = tenure
        selectedInstallments = instalments
        if (!instalments.isNullOrEmpty()) {
            val displayMetrics = DisplayMetrics()
            //instalmentsList = instalments
            selectedTenure = tenure
            (activity as BaseActivity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            selectedStartDate = startDateInAMonth.toString()
            installmentAdapter =
                VehicleLoanInstallmentAdapter(
                    instalments,
                    amount,
                    displayMetrics.widthPixels,
                    type,
                    this,
                    startMonth!!.toInt(),
                    startDateInAMonth
                )
            binding?.rvInstallments?.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding!!.rvInstallments.adapter = installmentAdapter
            binding?.pageIndicatorView?.count = instalments.size
            binding?.pageIndicatorView?.selection = 0
            //binding?.pageIndicatorView?.scrolltoPosition(position)

            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
                    super.onScrolled(recyclerView, dx, dy)
                    val firstItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (firstItem >= 0) {
                        binding?.pageIndicatorView?.selection = firstItem
                    }
                }
            }
            binding!!.rvInstallments.addOnScrollListener(scrollListener)


            var pAmount = 0.0F
            if (tenure.processingFeeType.equals("0")) {
                pAmount = tenure.processingFee?.toFloat()!!
            } else {
                pAmount = (amount * (tenure.processingFee)!!.toFloat())
            }
            if (pAmount < tenure.minProcessingFee!!) {
                pAmount = tenure.minProcessingFee.toFloat()
            }

            val pText = tenure.processingText!!.replace("{{perc}}", pAmount.roundToInt().toString())
            if (pText.contains("*")) {
                binding?.tvProcessAmount?.text = Html.fromHtml(
                    "<font color=#FA7300>" + pText.substring(0, 1) + "</font>"
                            + pText.substring(1, pText.length)
                )
            } else {
                binding?.tvProcessAmount?.text = pText
            }
            binding?.pageIndicatorView?.visibility = View.VISIBLE
            binding?.rvInstallments?.visibility = View.VISIBLE
            binding?.tvProcessAmount?.visibility = View.VISIBLE
        } else {
            binding?.pageIndicatorView?.visibility = View.GONE
            binding?.rvInstallments?.visibility = View.GONE
            binding?.tvProcessAmount?.visibility = View.GONE
        }
        if (instalments!!.size > 1) {
            binding!!.pageIndicatorView.visibility = View.VISIBLE
        } else {
            binding!!.pageIndicatorView.visibility = View.GONE
        }

        if (tenure.userTermsData != null) {
            setTermsText(tenure.userTermsData)
            if (tenure.userTermsData.ba_replace_links != null && !tenure.userTermsData.ba_replace_links.equals(
                    ""
                )
            ) {
                binding!!.llBorrowerTerms.visibility = View.VISIBLE
                setBorrowTerms(tenure.userTermsData)
            } else {
                binding!!.llBorrowerTerms.visibility = View.GONE
            }
        }
    }
}
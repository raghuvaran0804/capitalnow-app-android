package com.capitalnowapp.mobile.kotlin.activities.offer

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityRegistrationBasicBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.SaveUserDetailsReq
import com.capitalnowapp.mobile.models.offerModel.TancText
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.Currency
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.Date

//Screen 1
class RegistrationForm1Activity : BaseActivity(), TextWatcher {
    private var binding: ActivityRegistrationBasicBinding? = null
    var validationMsg = ""
    private var activity: AppCompatActivity? = null
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var fatherName: String? = ""
    private var email: String? = ""
    private var monthlyNetSalary: String? = ""
    private var isFormatting = false
    private var deletingHyphen = false
    private var hyphenStart = 0
    private var deletingBackward = false
    private var currentDate: String = ""
    private var dob: String? = ""
    private var selectedGender: String? = ""
    private var selectedEmploymentType: Int? = 0
    private var profileFormDataResponse = ProfileFormDataResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBasicBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()
        currentDate = Utility.formatDate(Date(), Constants.DOB_DATE_FORMAT)
        binding?.rgGender?.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                binding!!.tvMale.id -> {
                    setMale()
                }
                binding!!.tvFemale.id -> {
                    setFemale()
                }
                binding!!.tvTransgender.id -> {
                    setTransgender()
                }
            }

        }
        binding?.etMonthlySalary?.addTextChangedListener(this)

        binding?.etDOB?.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (isFormatting) return
                // Make sure user is deleting one char, without a selection
                val selStart = Selection.getSelectionStart(s)
                val selEnd = Selection.getSelectionEnd(s)
                if (s.length > 1 // Can delete another character
                    && count == 1 // Deleting only one character
                    && after == 0 // Deleting
                    && s[start] == '-' // a hyphen
                    && selStart == selEnd
                ) { // no selection
                    deletingHyphen = true
                    hyphenStart = start
                    // Check if the user is deleting forward or backward
                    deletingBackward = selStart == start + 1
                } else {
                    deletingHyphen = false
                }
            }

            override fun afterTextChanged(text: Editable) {
                if (isFormatting) return
                isFormatting = true
                // If deleting hyphen, also delete character before or after it
                if (deletingHyphen && hyphenStart > 0) {
                    if (deletingBackward) {
                        if (hyphenStart - 1 < text.length) {
                            text.delete(hyphenStart - 1, hyphenStart)
                        }
                    } else if (hyphenStart < text.length) {
                        text.delete(hyphenStart, hyphenStart + 1)
                    }
                }
                if (text.length == 2 || text.length == 5) {
                    text.append('-')
                }
                isFormatting = false

                if (text.toString().length == 10) {
                    dob = text.toString()
                } else {
                    dob = text.toString()

                }
            }
        })
        binding?.etDOB?.setOnClickListener {
            showDatePicker(binding?.etDOB)
        }
        binding?.rgEmpType?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding!!.tvSalaried.id -> {
                    setSalaried()
                }
                binding!!.tvSelf.id -> {
                    setSelfEmployed()
                }
            }

        }
        binding?.tvNext?.setOnClickListener {
            if(binding?.cbAgreeTerms?.isChecked!!) {
                validateForm1()
            }else{
                Toast.makeText(this, "Please Check the Consent", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showDatePicker(view: View?) {
        try {
            var preSelectedDate: String? = binding?.etDOB?.text.toString()
            if (preSelectedDate!!.isEmpty()) {
                preSelectedDate = currentDate
            }
            val dateValues: Array<String> = preSelectedDate.split("-").toTypedArray()
            val datePickerDialog = DatePickerDialog(
                this,
                R.style.MyDatePickerDialogTheme,
                { datePicker, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar[year, monthOfYear] = dayOfMonth
                    preSelectedDate = Utility.formatDate(calendar.time, Constants.DOB_DATE_FORMAT)
                    //registerUserReq.dob = preSelectedDate
                    binding?.etDOB?.setText(preSelectedDate)
                    //saveOSRFValueData(AppConstants.AjaxKeys.DOB.toInt(), preSelectedDate)
                },
                dateValues[2].toInt(),
                dateValues[1].toInt() - 1,
                dateValues[0].toInt()
            )
            val minDate = Utility.convertStringToDate("01-01-1960", Constants.DOB_DATE_FORMAT)
            val minDateCal = Calendar.getInstance()
            minDateCal.time = minDate
            datePickerDialog.setCancelable(false)
            datePickerDialog.datePicker.minDate = minDateCal.timeInMillis

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -20)
            val maxDate = calendar.time.time

            datePickerDialog.datePicker.maxDate = maxDate
            datePickerDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setMale() {
        selectedGender = "M"
        profileFormDataResponse.profileformData?.pGender = selectedGender
        binding?.tvMale?.isChecked = true
        binding?.tvTransgender?.isChecked = false
        binding?.tvFemale?.isChecked = false

    }

    private fun setFemale() {
        selectedGender = "F"
        profileFormDataResponse.profileformData?.pGender = selectedGender
        binding?.tvFemale?.isChecked = true
        binding?.tvTransgender?.isChecked = false
        binding?.tvMale?.isChecked = false
        /*saveData()
        saveOSRFValueData(AppConstants.AjaxKeys.Gender.toInt(), registerUserReq.gender)*/
    }

    private fun setTransgender() {
        selectedGender = "T"
        profileFormDataResponse.profileformData?.pGender = selectedGender
        binding?.tvTransgender?.isChecked = true
        binding?.tvFemale?.isChecked = false
        binding?.tvMale?.isChecked = false
        /*saveData()
        saveOSRFValueData(AppConstants.AjaxKeys.Gender.toInt(), registerUserReq.gender)*/
    }

    private fun setSalaried() {
        selectedEmploymentType = 6
        profileFormDataResponse.profileformData?.pEmploymentType = selectedEmploymentType!!.toInt()
        binding?.tvSalaried?.isChecked = true
        binding?.tvSelf?.isChecked = false


    }

    private fun setSelfEmployed() {
        selectedEmploymentType = 8
        profileFormDataResponse.profileformData?.pEmploymentType = selectedEmploymentType!!.toInt()
        binding?.tvSelf?.isChecked = true
        binding?.tvSalaried?.isChecked = false
    }

    private fun setTextViewDrawableColor(textView: RadioButton, color: Int) {
        textView.setTextColor(color)
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 14
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                    setData()

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

    private fun setData() {
        try {
            binding?.etFirstName?.setText(this.profileFormDataResponse.profileformData?.pFirstName)
            binding?.etLastName?.setText(this.profileFormDataResponse.profileformData?.pLastName)
            binding?.etFatherName?.setText(this.profileFormDataResponse.profileformData?.pPerFatherName)
            when (this.profileFormDataResponse.profileformData?.pGender) {
                "M" -> {
                    setMale()
                }
                "F" -> {
                    setFemale()
                }
                "T" -> {
                    setTransgender()
                }
            }
            binding?.etAlterEmail?.setText(this.profileFormDataResponse.profileformData?.pEmail)
            binding?.etDOB?.setText(this.profileFormDataResponse.profileformData?.pDob)
            when (this.profileFormDataResponse.profileformData?.pEmploymentType) {
                6 -> {
                    setSalaried()
                }
                8 -> {
                    setSelfEmployed()
                }
            }
            binding?.etMonthlySalary?.setText(this.profileFormDataResponse.profileformData?.pMonthlySalary!!.toString())
            var kfsLinkData = profileFormDataResponse.profileformData?.tancText?.get(0)
            setBorrowTerms(kfsLinkData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBorrowTerms(kfsLinkData: TancText?) {
        try {

            var startIndex: Int
            var endIndex: Int
            val ss: SpannableString?
            if (kfsLinkData?.message != null) {
                val words: List<String> = kfsLinkData.findText!!.split("||")
                val links: List<String> = kfsLinkData.replaceLinks!!.split("||")
                ss = SpannableString(kfsLinkData.message)
                for (w in words.withIndex()) {
                    val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(textView: View) {
                            var url = links[w.index]
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

                    binding?.tvBorrowerTerms?.text = ss
                    binding?.tvBorrowerTerms?.movementMethod = LinkMovementMethod.getInstance()
                }
            }

        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder(
            (activity as BaseActivity).activityContext,
            R.style.RulesAlertDialogStyle
        )
        val inflater: LayoutInflater = this@RegistrationForm1Activity.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.cs_dialog_terms_conditions, null)
        alert.setView(dialogView)
        val tvTitle: CNTextView = dialogView.findViewById(R.id.et_title)
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
                }else if(url.contains("https://www.capitalnow.in/clsoeapp")) {
                    webView.visibility = View.GONE
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
        dialog.show()
    }

    private fun validateForm1() {
        try {
            firstName = binding?.etFirstName?.text.toString().trim { it <= ' ' }
            lastName = binding?.etLastName?.text.toString().trim { it <= ' ' }
            fatherName = binding?.etFatherName?.text.toString().trim { it <= ' ' }
            email = binding?.etAlterEmail?.text.toString().trim { it <= ' ' }
            dob = binding?.etDOB?.text.toString().trim { it <= ' ' }
            monthlyNetSalary = binding?.etMonthlySalary?.text.toString().trim { it <= ' ' }
            var count = 0
            if (firstName!!.isEmpty()) {
                validationMsg = "First Name is required and can't be empty"
                count++
            } else if (lastName!!.isEmpty()) {
                validationMsg = "Last Name is required and can't be empty"
                count++
            } else if (fatherName!!.isEmpty()) {
                validationMsg = "Father's Name is required and can't be empty"
                count++
            } else  if (email!!.isEmpty()) {
                validationMsg = "Email is required and can't be empty"
                count++
            } else if (dob!!.isEmpty()) {
                validationMsg = "Date of Birth is required and can't be empty"
                count++
            } else if (selectedEmploymentType!! == 0) {
                validationMsg = "Employment Type is required and can't be empty"
                count++
            } else if (selectedGender!! == "") {
                validationMsg = "Gender is required and can't be empty"
                count++
            } else if (monthlyNetSalary!!.isEmpty()) {
                validationMsg = "Monthly Net Salary is required and can't be empty"
                count++
            }
            if (count > 0) {
                Toast.makeText(this, validationMsg, Toast.LENGTH_LONG).show()
            } else {
                saveForm1()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveForm1() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val saveUserDetailsReq = SaveUserDetailsReq()
            saveUserDetailsReq.pageNo = "14"
            saveUserDetailsReq.pFirstName = firstName
            saveUserDetailsReq.pLastName = lastName
            saveUserDetailsReq.pPerFatherName = fatherName
            saveUserDetailsReq.pGender = selectedGender
            saveUserDetailsReq.pEmail = email
            var map = HashMap<String, String>()
            map.put(profileFormDataResponse.profileformData?.tancText?.get(0)?.consentKey!!,"1")
            saveUserDetailsReq.additionalProperties = map
            saveUserDetailsReq.pDob = dob
            saveUserDetailsReq.pEmploymentType = selectedEmploymentType
            saveUserDetailsReq.pMonthlySalary = monthlyNetSalary
            val token = userToken
            genericAPIService.saveUserDetails(saveUserDetailsReq, token)
            Log.d("promo code req", Gson().toJson(saveUserDetailsReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val csGenericResponse =
                    Gson().fromJson(responseBody, CSGenericResponse::class.java)
                if (csGenericResponse.status == true) {
                    getApplyLoanDataBase(true)
                }
                else{
                    Toast.makeText(this, csGenericResponse.message, Toast.LENGTH_SHORT).show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }


        } catch (e: Exception) {
            CNProgressDialog.hideProgressDialog()
            e.printStackTrace()

        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        when (s.toString()) {
            binding?.etMonthlySalary?.editableText.toString() -> {
                if (s.toString() != null && s.toString().isNotEmpty()) {
                    val words = Currency.convertToIndianCurrency(s.toString())
                    binding!!.tvCurrency.visibility = View.VISIBLE
                    binding!!.tvCurrency.text = words
                } else {
                    binding!!.tvCurrency.visibility = View.GONE
                }
            }
        }
    }


}
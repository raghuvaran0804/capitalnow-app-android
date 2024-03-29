package com.capitalnowapp.mobile.kotlin.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityAdditionalDetailsBinding
import com.capitalnowapp.mobile.kotlin.utils.AppConstants

class AdditionalDetailsActivity : RegistrationHomeActivity(), TextWatcher, View.OnClickListener {

    private var binding: ActivityAdditionalDetailsBinding? = null
    private var collegesArray: Array<CharSequence?>? = null
    private var cardsArray: Array<CharSequence?>? = null
    private var loanPurposeArray: Array<CharSequence?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdditionalDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        refreshJson()
        initView(binding!!)
        setAdditionalBinding(binding!!, this)
    }

    private fun initView(binding: ActivityAdditionalDetailsBinding) {

        binding.tvFinish.setOnClickListener {

            when (isAdditionalFilled()) {
                0 -> {
                    when (isBasicFilled()) {
                        0 -> {
                            when (isProfessionalFilled()) {
                                0 -> {
                                    try {
                                        val token = (currentActivity as BaseActivity).userToken
                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                            return@setOnClickListener
                                        }
                                        mLastClickTime = SystemClock.elapsedRealtime()
                                        CNProgressDialog.showProgressDialog(
                                            activityContext,
                                            Constants.LOADING_MESSAGE
                                        )
                                        cnModel.saveOneTimeRegistration(
                                            userDetails.userId,
                                            registerUserReq,
                                            userDetails,token
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                1 -> {
                                    displayToast(validationMsg)
                                    startActivity(
                                        Intent(
                                            this,
                                            ProfessionalDetailsActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                                else -> {
                                    displayToast(getString(R.string.unique_validation_msg))
                                    startActivity(
                                        Intent(
                                            this,
                                            ProfessionalDetailsActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }
                        }
                        1 -> {
                            displayToast(validationMsg)
                            startActivity(Intent(this, BasicDetailsActivity::class.java))
                            finish()
                        }
                        else -> {
                            displayToast(getString(R.string.unique_validation_msg))
                            startActivity(Intent(this, BasicDetailsActivity::class.java))
                            finish()
                        }
                    }
                }
                1 -> {
                    displayToast(validationMsg)
                }
                else -> {
                    displayToast(getString(R.string.unique_validation_msg))
                }
            }
        }
        binding.ivBackAddDetails.setOnClickListener {
            onBackPressed()
        }

        binding.txEtCollegeName.setOnClickListener {
            if (allCollegesList != null && allCollegesList!!.size > 0) {
                showCollegesDialog()
                hideKeyboard(this)
            }
        }
        binding.txEtCreditCard.setOnClickListener {
            if (allCreditCardsList != null && allCreditCardsList!!.size > 0) {
                showCardsDialog()
                hideKeyboard(this)
            }
        }

        binding.txEtResidenceType.setOnClickListener {
            if (residenceTypesMapKeys != null && residenceTypesMapKeys!!.isNotEmpty()) {
                showResidenceDialog()
                hideKeyboard(this)
            }
        }

        binding.txEtYear.setOnClickListener {
            if (graduationYearsListKeys != null && graduationYearsListKeys!!.size > 0) {
                showYearDialog()
                hideKeyboard(this)
            }
        }

        binding.etPurpose.setOnClickListener {
            if (masterJsonResponse != null && masterJsonResponse?.purposeOfLoan != null && masterJsonResponse?.purposeOfLoan?.size!! > 0) {
                showPurposeDialog()
            }
        }

        binding.txEtPanNumber.addTextChangedListener(this)
        binding.etCustomPurpose.addTextChangedListener(this)
        binding.tvMarried.setOnClickListener(this)
        binding.tvUnmarried.setOnClickListener(this)

        setData()
    }

    private fun setData() {
        if (registerUserReq.panNumber != null && !registerUserReq.panNumber.equals("")) {
            binding?.txEtPanNumber?.setText(registerUserReq.panNumber)
        }
        if (registerUserReq.maritalStatus != null && !registerUserReq.maritalStatus.equals("")) {
            if (registerUserReq.maritalStatus.equals("1")) {
                setMarried()
            } else {
                setUnMarried()
            }
        }
        if (registerUserReq.yog != null && !registerUserReq.yog.equals("")) {
            val str: String = registerUserReq.yog!!
            val value: String? = graduationYearsListMap?.let { getKeyFromValue(it, str) } as String?
            binding?.txEtYear?.setText(value)
        }
        if (registerUserReq.residence != null && !registerUserReq.residence.equals("")) {
            val str: String = registerUserReq.residence!!
            val value: String? = residenceTypesMap?.let { getKeyFromValue(it, str) } as String?
            binding?.txEtResidenceType?.setText(value)
        }
    }

    private fun showYearDialog() {
        val builder = AlertDialog.Builder(this)
        val myArray = arrayOfNulls<CharSequence>(graduationYearsListKeys!!.size)
        for (i in 0 until graduationYearsListKeys!!.size) {
            myArray[i] = graduationYearsListKeys?.get(i)
                .toString() // Whichever string you wanna store here from custom object
        }
        builder.setItems(myArray) { _, which ->
            binding?.txEtYear?.setText(graduationYearsListKeys?.get(which))
            registerUserReq.yog = graduationYearsListMap?.get(graduationYearsListKeys?.get(which)!!)
        }
        designDialog(builder)
    }

    private fun showResidenceDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setItems(residenceTypesMapKeys) { _, which ->
            binding?.txEtResidenceType?.setText(residenceTypesMapKeys?.get(which))
            registerUserReq.residence = residenceTypesMap?.get(residenceTypesMapKeys?.get(which)!!)
            saveData()
        }
        builder.show()
    }

    private fun showCollegesDialog() {
        collegesArray = arrayOfNulls(allCollegesList!!.size)
        for (i in 0 until allCollegesList!!.size) {
            collegesArray!![i] =
                allCollegesList?.get(i)?.name // Whichever string you wanna store here from custom object
        }
        val builder = AlertDialog.Builder(this)
        builder.setItems(collegesArray) { _, which ->
            binding?.txEtCollegeName?.setText(collegesArray!![which])
            registerUserReq.collegeName = allCollegesList!![which].id
            saveData()
        }
        designDialog(builder)
    }

    private fun showCardsDialog() {
        cardsArray = arrayOfNulls(allCreditCardsList!!.size)
        for (i in 0 until allCreditCardsList!!.size) {
            cardsArray!![i] =
                allCreditCardsList?.get(i)?.name // Whichever string you wanna store here from custom object
        }
        val builder = AlertDialog.Builder(this)
        builder.setItems(cardsArray) { _, which ->
            binding?.txEtCreditCard?.setText(cardsArray!![which])
            registerUserReq.cardType = allCreditCardsList!![which].id
            saveData()
        }
        designDialog(builder)
    }

    private fun showPurposeDialog() {
        loanPurposeArray = arrayOfNulls(masterJsonResponse?.purposeOfLoan!!.size)
        for (i in 0 until masterJsonResponse?.purposeOfLoan!!.size) {
            loanPurposeArray!![i] =
                masterJsonResponse?.purposeOfLoan?.get(i)?.value // Whichever string you wanna store here from custom object
        }

        val builder = AlertDialog.Builder(this)
        builder.setItems(loanPurposeArray) { _, which ->
            binding?.etPurpose?.setText(loanPurposeArray!![which])
            if (masterJsonResponse?.purposeOfLoan!![which].id == 200) {
                binding?.etCustomPurpose?.visibility = View.VISIBLE
                binding?.etCustomPurpose?.setText("")
                registerUserReq.loanPurposeCustom = ""
            } else {
                registerUserReq.loanPurposeCustom = ""
                binding?.etCustomPurpose?.visibility = View.GONE
            }
            registerUserReq.loanPurposeId = masterJsonResponse?.purposeOfLoan!![which].id
            saveData()
        }
        designDialog(builder)
    }

    override fun afterTextChanged(s: Editable?) {
        when (s.toString()) {
            binding?.txEtPanNumber?.editableText.toString() -> {
                registerUserReq.panNumber = s.toString()
                if (binding!!.txEtPanNumber.text.toString().length == 10) {
                    saveOSRFValueData(AppConstants.AjaxKeys.PanNum.toInt(), s.toString())
                    saveData()
                } else {
                    saveData()
                }
            }

            binding?.etCustomPurpose?.editableText.toString() -> {
                registerUserReq.loanPurposeCustom = s.toString()
                if (binding!!.etCustomPurpose.text.toString().length >= 10) {
                    saveData()
                } else {
                    saveData()
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding?.tvMarried?.id -> {
                setMarried()
            }
            binding?.tvUnmarried?.id -> {
                setUnMarried()
            }
        }
        saveData()
    }

    private fun setUnMarried() {
        registerUserReq.maritalStatus = "0"
        binding!!.tvUnmarried.background =
            ContextCompat.getDrawable(this, com.capitalnowapp.mobile.R.color.Secondary2)
        binding!!.tvMarried.background =
            ContextCompat.getDrawable(this, com.capitalnowapp.mobile.R.drawable.single_rounded_square)

        binding!!.tvUnmarried.setTextColor(
            ContextCompat.getColor(
                this,
                com.capitalnowapp.mobile.R.color.black
            )
        )
        binding!!.tvMarried.setTextColor(
            ContextCompat.getColor(
                this,
                com.capitalnowapp.mobile.R.color.black
            )
        )

        /*setTextViewDrawableColor(binding!!.tvUnmarried, com.capitalnow.mobile.R.color.black)
        setTextViewDrawableColor(binding!!.tvMarried, com.capitalnow.mobile.R.color.black)*/
    }

    private fun setMarried() {
        registerUserReq.maritalStatus = "1"

        binding!!.tvMarried.background =
            ContextCompat.getDrawable(this, com.capitalnowapp.mobile.R.color.Secondary2)
        binding!!.tvUnmarried.background =
            ContextCompat.getDrawable(this, com.capitalnowapp.mobile.R.drawable.single_rounded_square)

        binding!!.tvMarried.setTextColor(
            ContextCompat.getColor(
                this,
                com.capitalnowapp.mobile.R.color.black
            )
        )
        binding!!.tvUnmarried.setTextColor(
            ContextCompat.getColor(
                this,
                com.capitalnowapp.mobile.R.color.black
            )
        )

        /*setTextViewDrawableColor(binding!!.tvMarried, com.capitalnow.mobile.R.color.black)
        setTextViewDrawableColor(binding!!.tvUnmarried, com.capitalnow.mobile.R.color.black)*/
    }

    fun updateCollege() {
        if (allCollegesList != null && allCollegesList!!.size > 0) {
            collegesArray = arrayOfNulls(allCollegesList!!.size)
            for (i in 0 until allCollegesList!!.size) {
                collegesArray!![i] =
                    allCollegesList?.get(i)?.name // Whichever string you wanna store here from custom object
            }
        }
        if (userDetails.collegeId != null && !userDetails.collegeId.equals("")) {
            registerUserReq.collegeName = userDetails.collegeId
            binding?.txEtCollegeName?.setText(collegesArray!![userDetails.collegeId!!.toInt()])
            saveData()
        } else if ((registerUserReq.collegeName != null && !registerUserReq.collegeName.equals(""))) {
            for (clgId in allCollegesList!!.iterator()) {
                if (clgId.id.toInt() == registerUserReq.collegeName!!.toInt()) {
                    binding?.txEtCollegeName?.setText(clgId.name)
                    break
                }
            }
        }
    }

    fun updateCard() {
        if (allCreditCardsList != null && allCreditCardsList!!.size > 0) {
            cardsArray = arrayOfNulls<CharSequence>(allCreditCardsList!!.size)
            for (i in 0 until allCreditCardsList!!.size) {
                cardsArray!![i] =
                    allCreditCardsList?.get(i)?.name // Whichever string you wanna store here from custom object
            }
        }
        if (userDetails.creditCardId != null && !userDetails.creditCardId.equals("")) {
            registerUserReq.cardType = userDetails.creditCardId
            binding?.txEtCreditCard?.setText(cardsArray!![userDetails.creditCardId!!.toInt()])
            saveData()
        } else if ((registerUserReq.cardType != null && !registerUserReq.cardType.equals(""))) {
            for (cardId in allCreditCardsList!!.iterator()) {
                if (cardId.id.toInt() == registerUserReq.cardType!!.toInt()) {
                    binding?.txEtCreditCard?.setText(cardId.name)
                    break
                }
            }
        }
    }

    fun updateLoanPurpose() {
        if (registerUserReq.loanPurposeId != null && registerUserReq.loanPurposeId!! > 0 && masterJsonResponse?.purposeOfLoan != null && masterJsonResponse?.purposeOfLoan!!.size > 0) {
            for (obj in masterJsonResponse?.purposeOfLoan!!) {
                if (obj.id == registerUserReq.loanPurposeId) {
                    binding?.etPurpose?.setText(obj.value)
                    if (obj.id == 200) {
                        binding?.etCustomPurpose?.visibility = View.VISIBLE
                        binding?.etCustomPurpose?.setText(registerUserReq.loanPurposeCustom)
                    }
                    saveData()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        refreshJson()
    }
}

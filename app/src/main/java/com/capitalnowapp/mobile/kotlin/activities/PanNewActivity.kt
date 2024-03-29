package com.capitalnowapp.mobile.kotlin.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.ActivityPanNewBinding
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.kotlin.utils.Validator
import com.capitalnowapp.mobile.util.Utility
import java.util.Calendar
import java.util.Date

class PanNewActivity : BaseActivity() {

    private var panNumber: String? = ""
    private var DOB: Int? = -1
    private var currentDate: String = ""
    private var isFormatting = false
    private var deletingHyphen = false
    private var hyphenStart = 0
    private var deletingBackward = false
    private var binding: ActivityPanNewBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanNewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        try {
            currentDate = Utility.formatDate(Date(), Constants.DOB_DATE_FORMAT)

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
                        //do something
                    } else {
                       //do something
                    }
                }
            })
            binding?.etDOB?.setOnClickListener {
                showDatePicker(binding!!.etDOB)
            }

            binding?.tvValidate?.setOnClickListener {
                validate()
            }

            binding?.etPan?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 10 && Validator.isValidPanNumber(s.toString())) {
                        panNumber = s.toString()

                        /*val obj = JSONObject()
                        try {
                            obj.put("cnid",userDetails.qcId)
                        } catch (e: JSONException) {
                            throw RuntimeException(e)
                        }
                        TrackingUtil.pushEvent(obj, getString(R.string.pan_card_number_entered))*/

                        //uploadPan()

                    } else if (s?.length!! > 0) {
                        panNumber = ""
                    } else {
                        panNumber = ""
                    }
                    binding?.etPan?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            })

        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    private fun validate() {
        try{
            if(binding?.etFirstName?.length()!! < 3){
                Toast.makeText(this, "Enter Valid First Name", Toast.LENGTH_SHORT).show()
            }else if(binding?.etLastName?.length()!! < 3){
                Toast.makeText(this, "Enter Valid Last Name", Toast.LENGTH_SHORT).show()
            }else if(binding?.etDOB?.length()!! <= 1){
                Toast.makeText(this, "Please select DOB", Toast.LENGTH_SHORT).show()
            }else if (binding?.etPan?.length()!! < 10 || !Validator.isValidPanNumber(panNumber.toString())) {
                Toast.makeText(this, "Enter Valid PAN Card Number", Toast.LENGTH_SHORT).show()
            }else if(binding?.etPinCode?.length()!! <6){
                Toast.makeText(this, "Enter Valid Pin Code", Toast.LENGTH_SHORT).show()
            } else {

            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun showDatePicker(view : View?) {
        try{
            var preSelectedDate: String? = binding?.etDOB?.text.toString()
            if (preSelectedDate!!.isEmpty()) {
                preSelectedDate = currentDate
            }
            val dateValues: Array<String> = preSelectedDate!!.split("-").toTypedArray()
            val datePickerDialog = DatePickerDialog(
                this,
                R.style.MyDatePickerDialogTheme,
                { datePicker, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar[year, monthOfYear] = dayOfMonth
                    preSelectedDate = Utility.formatDate(calendar.time, Constants.DOB_DATE_FORMAT)
                    //registerUserReq.dob = preSelectedDate
                    binding?.etDOB?.setText(preSelectedDate)
                    //DOB = binding?.etDOB?.setText(preSelectedDate).toString().toInt()
                    saveOSRFValueData(AppConstants.AjaxKeys.DOB.toInt(), preSelectedDate)
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

        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    open fun saveOSRFValueData(type: Int, value: String?) {
        try {
            val token = userToken
            if (userDetails != null && userDetails.userId != null) {
                cnModel.saveOSRFValue(userDetails.userId, type, value, token)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
package com.capitalnowapp.mobile.kotlin.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.FragmentPanHardPullBinding
import com.capitalnowapp.mobile.util.Utility
import java.util.Calendar
import java.util.Date

class PanHardPullFragment : Fragment() {
    private var binding: FragmentPanHardPullBinding? = null
    private var activity: Activity? = null
    var validationMsg = ""
    private var panNumber: String = ""
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var currentDate: String = ""
    private var isFormatting = false
    private var deletingHyphen = false
    private var hyphenStart = 0
    private var deletingBackward = false
    private var dob: String? = ""
    private var pincode: String? = ""
    private var selectedGender: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPanHardPullBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        try {
            binding?.tvNext?.setOnClickListener {
                validateDetails()
                if (binding?.cbAgreeTerms1?.isChecked!! && binding?.cbAgreeTerms2?.isChecked!!) {

                } else {
                    Toast.makeText(activity, "Please Check the Consent", Toast.LENGTH_SHORT).show()
                }
            }
            currentDate = Utility.formatDate(Date(), Constants.DOB_DATE_FORMAT)
            binding?.etDOB?.addTextChangedListener(object : TextWatcher {

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
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
            binding?.rgGender?.setOnCheckedChangeListener { _, checkedId ->

                when (checkedId) {
                    binding!!.tvMale.id -> {
                        setMale()
                    }
                    binding!!.tvFemale.id -> {
                        setFemale()
                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
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
                requireContext(),
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

    private fun validateDetails() {
        try {
            panNumber = binding?.etPanNumber?.text.toString().trim { it <= ' ' }
            firstName = binding?.etFirstName?.text.toString().trim { it <= ' ' }
            lastName = binding?.etLastName?.text.toString().trim { it <= ' ' }
            dob = binding?.etDOB?.text.toString().trim { it <= ' ' }
            pincode = binding?.etPinCode.toString().trim { it <= ' ' }
            var count = 0
            if (panNumber!!.isEmpty()) {
                validationMsg = "PAN Number is required and can't be empty"
                count++
            } else if (firstName!!.isEmpty()) {
                validationMsg = "First Name is required and can't be empty"
                count++
            } else if (lastName!!.isEmpty()) {
                validationMsg = "Last Name is required and can't be empty"
                count++
            } else if (dob!!.isEmpty()) {
                validationMsg = "Date of Birth is required and can't be empty"
                count++
            } else if (pincode!!.isEmpty()) {
                validationMsg = "PinCode is required and can't be empty"
                count++
            } else if (selectedGender!! == "") {
                validationMsg = "Gender is required and can't be empty"
                count++
            }
            if (count > 0) {
                Toast.makeText(activity, validationMsg, Toast.LENGTH_SHORT)
                    .show()
            } else {
                //saveForm1()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setFemale() {
        selectedGender = "F"
        //profileFormDataResponse.profileformData?.pGender = selectedGender
        binding?.tvMale?.isChecked = false
        binding?.tvFemale?.isChecked = true
    }

    private fun setMale() {
        selectedGender = "M"
        //profileFormDataResponse.profileformData?.pGender = selectedGender
        binding?.tvMale?.isChecked = true
        binding?.tvFemale?.isChecked = false
    }
}
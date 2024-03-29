package com.capitalnowapp.mobile.kotlin.utils

import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class Validator {

    companion object {
        @JvmStatic
        fun validatePhoneNum(et: TextInputEditText): Boolean {
            return !(et.editableText.isEmpty() || et.editableText.toString()[0].equals("0") || et.editableText.length != 10)
        }

        @JvmStatic
        fun validateMobileNum(et: EditText): Boolean {
            return !(et.editableText.isEmpty() || et.editableText.toString()[0].equals("0") || et.editableText.length != 10)
        }

        @JvmStatic
        fun validateEditText(et: TextInputEditText): Boolean {
            return !(et.editableText.isEmpty() || et.editableText.toString().length < 2)
        }

        @JvmStatic
        fun validateAutoComplete(et: AutoCompleteTextView): Boolean {
            return !(et.editableText.isEmpty() || et.editableText.toString().length < 2)
        }

        fun isValidInputDOB(dobStr: String): Boolean {
            return if (dobStr.length == 10) {
                val sdf: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                try {
                    sdf.isLenient = false
                    sdf.parse(dobStr)
                    val dob: Calendar = Calendar.getInstance()
                    val today: Calendar = Calendar.getInstance()
                    dob.set(dobStr.substring(0, 2).toInt(), dobStr.substring(3, 5).toInt(), dobStr.substring(6, 10).toInt())
                    (today.get(Calendar.YEAR) >= dobStr.substring(6, 10).toInt() && (dobStr.substring(6, 10).toInt() >= 1960))
                } catch (e: ParseException) {
                    false
                }
            } else {
                false
            }
        }

        fun isValidPanNumber(pan : String): Boolean {
            val pattern: Pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")
            val matcher: Matcher = pattern.matcher(pan)
            if (matcher.matches()) {
                return true
            }
            return false
        }
    }
}
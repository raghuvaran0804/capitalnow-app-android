package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.otp_sheet.btnLogin
import kotlinx.android.synthetic.main.otp_sheet.otp


class OTPSheetFragment(private val loginActivity: LoginActivity) : BottomSheetDialogFragment() {

    @SuppressLint("NotConstructor")
    fun OTPSheetFragment() {
        // empty constructor
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.otp_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        try {
            val contentView = View.inflate(context, R.layout.otp_sheet, null)
            dialog.setContentView(contentView)
            (contentView.parent as View).setBackgroundColor(ContextCompat.getColor(contentView.context, R.color.transparent))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            btnLogin.setOnClickListener {
                loginActivity.otpStr = otp.text.toString().trim()
                loginActivity.btnLogin.callOnClick()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            dialog!!.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // To dismiss the fragment when the back-button is pressed.
                    //loginActivity.openKey()
                    dismiss()
                    true
                } else false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setOtp(otpStr: String) {
        otp.setText(otpStr)
    }
}
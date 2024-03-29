package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.kotlin.activities.CouponDetailsActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.redeem_success_sheet.view.tvDesc
import kotlinx.android.synthetic.main.redeem_success_sheet.view.tvSuccess


class RedeemSuccessSheetFragment(private val activity: CouponDetailsActivity, private val points: String?) : BottomSheetDialogFragment() {

    @SuppressLint("NotConstructor")
    fun RedeemSuccessSheetFragment() {
        // empty constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        try {
            val contentView = View.inflate(context, R.layout.redeem_success_sheet, null)
            dialog.setContentView(contentView)
            (contentView.parent as View).setBackgroundColor(ContextCompat.getColor(contentView.context, R.color.transparent))

            try {
                contentView.tvDesc.text = "You have successfully \nRedeemed $points Points"
                contentView.tvSuccess.setOnClickListener {
                    activity.showSendEmail()
                    this.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
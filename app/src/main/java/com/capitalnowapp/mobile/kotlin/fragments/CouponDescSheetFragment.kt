package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.kotlin.activities.CheckCouponActivity
import com.capitalnowapp.mobile.models.coupons.CouponsDetails
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.coupon_desc_sheet.view.tvTitle
import kotlinx.android.synthetic.main.redeem_success_sheet.view.tvDesc


class CouponDescSheetFragment(private val checkCouponActivity: CheckCouponActivity, private val couponDetails: CouponsDetails?) : BottomSheetDialogFragment() {

    @SuppressLint("NotConstructor")
    fun CouponDescSheetFragment() {
        // empty constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        try {
            val contentView = View.inflate(context, R.layout.coupon_desc_sheet, null)
            dialog.setContentView(contentView)
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            dialog.window?.statusBarColor = ContextCompat.getColor(checkCouponActivity, R.color.black)
            (contentView.parent as View).setBackgroundColor(ContextCompat.getColor(contentView.context, R.color.transparent))

            try {
                contentView.tvDesc.text = couponDetails?.couponDescription
            } catch (e: Exception) {
                e.printStackTrace()
            }

            contentView.tvTitle?.setOnClickListener {
                dialog.dismiss()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
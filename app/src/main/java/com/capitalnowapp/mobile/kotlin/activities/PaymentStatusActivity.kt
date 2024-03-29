package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.PaymentClearData
import com.capitalnowapp.mobile.constants.Constants

class PaymentStatusActivity : BaseActivity() {
    private var tvPaymentStatus: TextView? = null
    private var tvPaymentMessage: TextView? = null
    private var tvStatus: TextView? = null
    private var tvTransactionReference: TextView? = null
    private var tvAmount: TextView? = null
    private var tvTransactionStart: TextView? = null
    private var tvTransactionEnd: TextView? = null
    private var tv_redirect_to_home_page: TextView? = null
    private var paymentClearData: PaymentClearData? = null
    private var ivPaymentStatus: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_status)
        val bundle = intent.extras
        if (bundle != null) {
            paymentClearData = bundle[Constants.BUNDLE_PAYMENT_STATUS] as PaymentClearData?
        }
        initViews()
        bindData()
        //  handler = Handler()
        //  handler!!.postDelayed(runnable, 7000)
        tv_redirect_to_home_page!!.setOnClickListener { goTOHomePage() }
    }

    //var runnable = Runnable { goTOHomePage() }
    private fun initViews() {
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus)
        tvPaymentMessage = findViewById(R.id.tvPaymentMessage)
        tvStatus = findViewById(R.id.tvStatus)
        tvTransactionReference = findViewById(R.id.tvTransactionReference)
        tvAmount = findViewById(R.id.tvAmount)
        tvTransactionStart = findViewById(R.id.tvTransactionStart)
        tvTransactionEnd = findViewById(R.id.tvTransactionEnd)
        ivPaymentStatus = findViewById(R.id.ivPaymentStatus)
        tv_redirect_to_home_page = findViewById(R.id.tv_redirect_to_home_page)
    }

    private fun bindData() {
        if (paymentClearData != null) {
            if (paymentClearData!!.transaction_status == 0) {
                ivPaymentStatus!!.setImageResource(R.drawable.pay_fail)
                tvStatus!!.setTextColor(ContextCompat.getColor(this, R.color.orange_alert))
            } else {
                ivPaymentStatus!!.setImageResource(R.drawable.pay_success)
                tvStatus!!.setTextColor(ContextCompat.getColor(this, R.color.colorPaymentStatus))
            }
            tvPaymentStatus!!.text = "Payment " + paymentClearData!!.transactionStatusMessage
            tvPaymentMessage!!.text = paymentClearData!!.transactionMessage
            tvStatus!!.text = paymentClearData!!.transactionStatusMessage
            tvTransactionReference!!.text = paymentClearData!!.paymentId
            tvAmount!!.text = paymentClearData!!.amount
            tvTransactionStart!!.text = paymentClearData!!.transaction_initiated_at
            tvTransactionEnd!!.text = paymentClearData!!.transaction_ended_at
        }
    }

    private fun goTOHomePage() {
        val intent = Intent(this@PaymentStatusActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goTOHomePage()
    }
}
package com.capitalnowapp.mobile.kotlin.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.OrderData
import com.capitalnowapp.mobile.beans.PaymentClearData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.interfaces.SelectedToPayCallback
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.activities.PaymentStatusActivity
import com.capitalnowapp.mobile.kotlin.adapters.ActiveLoansAdapter
import com.capitalnowapp.mobile.kotlin.adapters.GatewayInfoAdapter
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.loan.AmtPayable
import com.capitalnowapp.mobile.models.loan.Gatewaydowntime
import com.capitalnowapp.mobile.models.loan.LoansToPay
import com.capitalnowapp.mobile.models.loan.MyLoansResponse
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.facebook.FacebookSdk
import com.razorpay.Checkout
import kotlinx.android.synthetic.main.fragment_active_loans.img
import kotlinx.android.synthetic.main.fragment_active_loans.llButton
import kotlinx.android.synthetic.main.fragment_active_loans.llNoLoanContent
import kotlinx.android.synthetic.main.fragment_active_loans.llPay
import kotlinx.android.synthetic.main.fragment_active_loans.rvLoans
import kotlinx.android.synthetic.main.fragment_active_loans.text
import kotlinx.android.synthetic.main.fragment_active_loans.tvBreakUpTitle
import kotlinx.android.synthetic.main.fragment_active_loans.view.tvTotalAmount
import org.json.JSONException
import org.json.JSONObject


class ActiveLoansFragment(private val myLoansResponse: MyLoansResponse, private val flag: Int) :
    Fragment(),
    SelectedToPayCallback {

    val LOCK = "LOCK"
    private var razorPayLoanData: List<LoansToPay> = ArrayList()
    private var gatewayDownTimeData: Gatewaydowntime? = null
    private var activeLoansAdapter: ActiveLoansAdapter? = null
    private var tvTotalAmount: TextView? = null
    private var tvBreakUp: TextView? = null
    private var llBreakUp: LinearLayout? = null
    private var cnModel: CNModel? = null
    var sharedPreferences: CNSharedPreferences? = null

    private var receiptId = ""
    private var startTime = ""
    private var endTime = ""
    var totalPaidAmount = 0

    var all_selected_ids: String? = null

    var amount_value: String? = null
    private var currentActivity: Activity? = null
    private var userId: String? = null
    private var amtPayableList: ArrayList<AmtPayable>? = ArrayList()


    // Empty constructor (required by Android)

    // Required empty public constructor


    companion object {
        fun newInstance(myLoansResponse: MyLoansResponse, flag: Int): ActiveLoansFragment {
            return ActiveLoansFragment(myLoansResponse, flag)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_loans, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CNProgressDialog.hideProgressDialog()
        currentActivity = activity
        userId = (currentActivity as BaseActivity).userDetails.userId
        cnModel = CNModel(context, currentActivity, Constants.RequestFrom.MY_LOANS)
        val rvLoans = view.findViewById<RecyclerView>(R.id.rvLoans)
        llBreakUp = view.findViewById(R.id.llBreakUp)
        tvBreakUp = view.findViewById(R.id.tvBreakUp)
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
        rvLoans.layoutManager = LinearLayoutManager(activity)

        activeLoansAdapter = ActiveLoansAdapter(razorPayLoanData, activity as BaseActivity, this)
        rvLoans.adapter = activeLoansAdapter
        activeLoansAdapter?.setOnItemClickListener(object :
            ActiveLoansAdapter.LoanSelectionListener {
            override fun onItemClick() {
                updateAmountValue(activeLoansAdapter?.getSelectedLoansList())
            }

        })

        llButton?.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Pay Now Button Clicked")
                if (razorPayLoanData[0].lid != null) {
                    obj.put("CNLID", razorPayLoanData[0].lid)
                } else {
                    obj.put("CNLID", "")
                }
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.active_personal_loans_page_interacted))
            showPopup()
        }

        llPay.visibility = VISIBLE
        tvBreakUpTitle.text = "Select loan(s) to pay"
        tvTotalAmount?.visibility = INVISIBLE
        llPay.isEnabled = false
        llButton.isEnabled = false
        updateMyLoansResponse(myLoansResponse)
    }

    private fun showPopup() {
        val alertDialog = Dialog(requireActivity())
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.payment_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        val tvOk = alertDialog.findViewById<TextView>(R.id.tvOk)
        tvOk.setOnClickListener { view: View? ->
            alertDialog.dismiss()
            if (amtPayableList?.size != 0) {

                if (gatewayDownTimeData != null) {
                    showBankDetailsDialog()
                } else {
                    (activity as DashboardActivity).startPayment(totalPaidAmount, amtPayableList)
                }
            }
        }
        alertDialog.show()
    }

    private fun updateMyLoansResponse(myLoansData: MyLoansResponse) {
            if (myLoansData.status == true && myLoansData.userData != null) {
                if (flag == 0) {
                    if (myLoansData.userData!!.loansToPay!!.isNotEmpty()) {
                        updateLoanPayList(myLoansData.userData!!.loansToPay!!)
                        if (myLoansData.userData!!.loansToPay?.get(0)?.amtPayable?.get(0)?.isRecommended == true || myLoansData.userData!!.loansToPay?.get(0)?.amtPayable?.get(0)?.isRecommended == false) {
                            selectedObj(
                                myLoansData.userData!!.loansToPay?.get(0)?.amtPayable?.get(0),
                                myLoansData.userData!!.loansToPay?.get(0)!!
                            )
                        }
                    } else {
                        bindEmptyData(myLoansData)
                    }
                } else {
                    if (myLoansData.userData!!.twlLoansToPay!!.isNotEmpty()) {
                        updateLoanPayList(myLoansData.userData!!.twlLoansToPay!!)
                        if (myLoansData.userData!!.loansToPay?.get(0)?.amtPayable?.get(0)?.isRecommended == true || myLoansData.userData!!.loansToPay?.get(0)?.amtPayable?.get(0)?.isRecommended == false) {
                            selectedObj(
                                myLoansData.userData!!.loansToPay?.get(0)?.amtPayable?.get(0),
                                myLoansData.userData!!.loansToPay?.get(0)!!
                            )
                        }
                    } else {
                        bindEmptyData(myLoansData)
                    }
                }
                if (myLoansData.userData!!.gatewaydowntime != null && (myLoansData.userData!!.gatewaydowntime?.isBank!! || myLoansData.userData!!.gatewaydowntime?.isUpi!!)) {
                    updateGateWayDetails(myLoansData.userData!!.gatewaydowntime)
                }
            } else {
                bindEmptyData(myLoansData)
            }
            CNProgressDialog.hideProgressDialog()

    }

    private fun bindEmptyData(myLoansData: MyLoansResponse) {

            llNoLoanContent.visibility = View.VISIBLE
            llPay.visibility = View.GONE
            rvLoans.visibility = View.GONE
            if (myLoansData.defaultImg?.isNotEmpty() == true) {
                img.visibility = View.VISIBLE
                Glide.with(currentActivity!!).load(myLoansData.defaultImg).into(img)
            } else {
                img.visibility = View.GONE
            }
            if (myLoansData.message?.isNotEmpty() == true) {
                text.visibility = View.VISIBLE
                text.text = myLoansData.message
            } else {
                text.visibility = View.GONE
            }

    }

    private fun showBankDetailsDialog() {
            val dialog = Dialog(currentActivity!!)
            dialog.setContentView(R.layout.dialog_bank_info)
            val window = dialog.window
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 40)
            window!!.setBackgroundDrawable(inset)
            val width = (currentActivity!!.resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = (currentActivity!!.resources.displayMetrics.heightPixels * 0.30).toInt()
            dialog.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvAmount = dialog.findViewById<TextView>(R.id.tvAmount)
            val tvAccountTitle = dialog.findViewById<TextView>(R.id.tvAccountTitle)
            val llBank = dialog.findViewById<LinearLayout>(R.id.llBank)
            val llUPI = dialog.findViewById<LinearLayout>(R.id.llUPI)
            val rvUPI = dialog.findViewById<RecyclerView>(R.id.rvUPI)
            val rvBank = dialog.findViewById<RecyclerView>(R.id.rvBank)
            val btnPay = dialog.findViewById<TextView>(R.id.btnPay)

            tvTitle.text = gatewayDownTimeData?.message
            tvAmount.text = getString(R.string.total_payable_amount, totalPaidAmount)

            if (gatewayDownTimeData!!.isUpi!!) {
                llUPI.visibility = View.VISIBLE
                rvUPI.layoutManager = LinearLayoutManager(activity)
                rvUPI.adapter = GatewayInfoAdapter(gatewayDownTimeData?.upiArray!!, 0)
            } else {
                llUPI.visibility = View.GONE
            }

            if (gatewayDownTimeData!!.isBank!!) {
                llBank.visibility = View.VISIBLE
                rvBank.layoutManager = LinearLayoutManager(activity)
                tvAccountTitle.text = gatewayDownTimeData?.bankArray?.title
                rvBank.adapter = GatewayInfoAdapter(gatewayDownTimeData?.bankArray?.values!!, 1)
            } else {
                llBank.visibility = View.GONE
            }
            btnPay.setOnClickListener {
                (activity as DashboardActivity).startPayment(totalPaidAmount, amtPayableList)
                dialog.dismiss()
            }
            dialog.show()

    }

    fun updateAmountValue(selectedList: java.util.ArrayList<LoansToPay>?) {
            var breakUpAmount = ""
            totalPaidAmount = 0
            all_selected_ids = ""
            if (selectedList != null && selectedList.size > 0) {
                llButton!!.isEnabled = true
                for (razorLoanData in selectedList) {
                    val amountValue = 0 // floor(razorLoanData.amtPayableToday!!.toDouble()).toInt()
                    val loanIds = razorLoanData.lid as String
                    breakUpAmount = "$breakUpAmount+ Rs $amountValue\n"
                    totalPaidAmount += amountValue
                    all_selected_ids = "$all_selected_ids$loanIds,"
                }
                llBreakUp?.visibility = View.VISIBLE
                tvBreakUp?.text = breakUpAmount.trim()
                tvTotalAmount?.text = "Rs $totalPaidAmount"
            } else {
                llBreakUp?.visibility = View.GONE
                llButton!!.isEnabled = false
            }
            amount_value = totalPaidAmount.toString()
            if (all_selected_ids!!.isNotEmpty()) all_selected_ids =
                all_selected_ids!!.substring(0, all_selected_ids!!.length - 1)

    }

    fun showAlertDialog(message: String?) {

            if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
            CNAlertDialog.showAlertDialog(
                context,
                resources.getString(R.string.title_alert),
                message
            )
    }

    fun invokeRazorPay(orderData: OrderData) {

            val checkout = Checkout()
            checkout.setImage(R.mipmap.ic_launcher)
            val activity = currentActivity!!
            try {
                receiptId = orderData.receiptId
                val options = JSONObject()
                options.put("name", orderData.checkoutName)
                options.put("description", orderData.checkoutDescription)
                options.put("order_id", orderData.razorPayOrderId)
                options.put("currency", "INR")
                options.put("amount", orderData.amount)
                options.put("prefill.email", orderData.userEmail)
                options.put("prefill.contact", orderData.userMobile)
                checkout.setKeyID(orderData.razorPayApiKey)
                //checkout.setKeyID("rzp_test_NcADfERIZYbXHp")
                checkout.open(currentActivity, options)
            } catch (e: Exception) {
                Log.e("Tag", "Error in starting Razorpay Checkout", e)
            }
    }

    fun updateLoanPayList(loansToPay: List<LoansToPay>) {

            if (loansToPay.isNotEmpty()) {
                rvLoans.visibility = VISIBLE
                razorPayLoanData = loansToPay
                activeLoansAdapter?.setMyRazorpayLoanData(razorPayLoanData)
                activeLoansAdapter?.notifyDataSetChanged()
                llNoLoanContent.visibility = GONE
                llButton.visibility = VISIBLE
                rvLoans.visibility = VISIBLE
            } else {
                llNoLoanContent.visibility = VISIBLE
                llButton.visibility = GONE
                rvLoans.visibility = GONE
                llPay.visibility = GONE
            }
    }

    fun onPaymentError(description: String) {

            CNProgressDialog.hideProgressDialog()
            endTime = Utility.formatTime(System.currentTimeMillis(), Constants.YYYY_MM_DD_HH_MM_SS)
            Utility.displayToast(
                FacebookSdk.getApplicationContext(),
                "onPaymentError $description",
                Toast.LENGTH_LONG
            )
            val paymentClearData = PaymentClearData()
            paymentClearData.amount = amount_value
            paymentClearData.paymentId = receiptId
            paymentClearData.transaction_initiated_at = startTime
            paymentClearData.transaction_ended_at = endTime
            paymentClearData.transaction_status = 0
            navigateToPaymentStatusPage(paymentClearData)
    }

    fun navigateToPaymentStatusPage(paymentClearData: PaymentClearData) {
            llButton!!.isEnabled = false
            val intent = Intent(activity, PaymentStatusActivity::class.java)
            intent.putExtra(Constants.BUNDLE_PAYMENT_STATUS, paymentClearData)
            startActivity(intent)

    }

    fun updateGateWayDetails(gatewayDownTimeData: Gatewaydowntime?) {

            this.gatewayDownTimeData = gatewayDownTimeData
    }

    override fun onResume() {

            super.onResume()
            llButton!!.isEnabled = true

    }

    override fun selectedObj(selectedEmi: AmtPayable?, loanToPay: LoansToPay) {
        val flag = !selectedEmi?.isSelected!!
        selectedEmi.lid = loanToPay.lid!!
        if (!amtPayableList.isNullOrEmpty()) {
            for (amt in amtPayableList!!.withIndex()) {
                if (amt.value.lid == loanToPay.lid) {
                    if (amt.value.id == selectedEmi.id) {
                        amtPayableList!!.removeAt(amt.index)
                        setSelectionValue(amt.value, flag)
                    } else {
                        setSelectionValue(amt.value, false)
                        amtPayableList!!.removeAt(amt.index)
                        amtPayableList!!.add(selectedEmi)
                        setSelectionValue(selectedEmi, flag)
                    }
                    break
                } else {
                    if (flag && amt.index == amtPayableList!!.size - 1) {
                        if (flag) {
                            amtPayableList!!.add(selectedEmi)
                            setSelectionValue(selectedEmi, flag)
                        }
                    }
                }
            }
        } else {
            if (flag) {
                amtPayableList?.add(selectedEmi)
                setSelectionValue(selectedEmi, flag)
            }
        }
        refreshData()
    }

    private fun refreshData() {
        activeLoansAdapter?.setMyRazorpayLoanData(razorPayLoanData)
        activeLoansAdapter?.notifyDataSetChanged()

        setBottomView()
    }

    private fun setBottomView() {
        totalPaidAmount = 0
        if (!amtPayableList.isNullOrEmpty()) {
            llPay.isEnabled = true
            llButton.isEnabled = true
            llPay.visibility = VISIBLE
            tvTotalAmount?.visibility = VISIBLE
            tvBreakUpTitle.text = "Total Amount to be paid"
            for (pay in amtPayableList!!.withIndex()) {
                totalPaidAmount += pay.value.dueAmount!!
            }
            llPay.tvTotalAmount.text =
                getString(R.string.indian_currency) + " " + totalPaidAmount.toString()
            llButton.background =
                ContextCompat.getDrawable(
                    activity as BaseActivity,
                    R.drawable.rounded_corner_gradient
                )
            llButton.backgroundTintList = null
            llButton.setTextColor(
                ContextCompat.getColor(
                    activity as BaseActivity,
                    R.color.white
                )
            )
        } else {
            llPay.visibility = VISIBLE
            tvBreakUpTitle.text = "Select loan(s) to pay"
            tvTotalAmount?.visibility = INVISIBLE
            llPay.isEnabled = false
            llButton.isEnabled = false
            llButton.backgroundTintList =
                ContextCompat.getColorStateList(activity as BaseActivity, R.color.dark_gray)
            llButton.setTextColor(ContextCompat.getColor(activity as BaseActivity, R.color.white))
        }
    }

    private fun setSelectionValue(selectedEmi: AmtPayable, isSelected: Boolean) {
        for (loan in razorPayLoanData.withIndex()) {
            if (loan.value.lid.toString() == selectedEmi.lid.toString()) {
                selectedEmi.isSelected = isSelected
            }
        }
    }
}

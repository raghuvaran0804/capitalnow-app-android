package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.activities.LoginActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.FragmentTwlActiveLoansBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.activities.TwlEmiDetailsActivity
import com.capitalnowapp.mobile.kotlin.adapters.TwlLoanAdapter
import com.capitalnowapp.mobile.models.TwlActiveLoanData
import com.capitalnowapp.mobile.models.TwlActiveLoansReq
import com.capitalnowapp.mobile.models.TwlActiveLoansResponse
import com.capitalnowapp.mobile.models.TwlAmtPayable
import com.capitalnowapp.mobile.models.loan.Gatewaydowntime
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson


class TwlActiveLoansFragment : Fragment() {

    private var twlActiveLoansResponse: TwlActiveLoansResponse? = null
    private var twlActiveLoanData: TwlActiveLoanData? = null
    private var binding: FragmentTwlActiveLoansBinding? = null
    private var activity: Activity? = null
    private var twlLoanAdapter: TwlLoanAdapter? = null
    private var gatewayDownTimeData: Gatewaydowntime? = null
    private var twlAmtPayable: List<TwlAmtPayable> = ArrayList()

    @SuppressLint("NotConstructor")
    fun TwlActiveLoansFragment() {
        // empty constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTwlActiveLoansBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private fun initView() {

        binding!!.tvEmiDetails.setOnClickListener {
            val intent = Intent(context, TwlEmiDetailsActivity::class.java)
            intent.putExtra("lid", twlActiveLoansResponse?.twlActiveLoanData?.lid)
            startActivity(intent)
        }

        binding!!.rvPayLoan.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        twlActiveLoans()

    }

    private fun twlActiveLoans() {
        try {
            val genericAPIService = GenericAPIService(context)
            val twlActiveLoansReq = TwlActiveLoansReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.twlActiveLoans(twlActiveLoansReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                 twlActiveLoansResponse = Gson().fromJson(
                    responseBody, TwlActiveLoansResponse::class.java
                )
                if (twlActiveLoansResponse != null && twlActiveLoansResponse!!.status == Constants.STATUS_SUCCESS) {
                    if (twlActiveLoansResponse!!.twlActiveLoanData != null
                        && twlActiveLoansResponse!!.twlActiveLoanData?.lid!=null){
                        binding!!.llNoLoanContent.visibility = View.GONE
                        binding!!.llDetails.visibility = View.VISIBLE
                        binding!!.rvPayLoan.visibility = View.VISIBLE
                        twlAmtPayable = twlActiveLoansResponse!!.twlActiveLoanData!!.twlamtPayable!!
                        twlLoanAdapter = TwlLoanAdapter(
                            twlAmtPayable, this,
                            twlActiveLoansResponse!!.twlActiveLoanData!!.lid
                        )
                        binding!!.llNoLoanContent.visibility = View.GONE
                        binding!!.rvPayLoan.visibility = View.VISIBLE
                        binding!!.llDetails.visibility = View.VISIBLE
                        binding!!.rvPayLoan.adapter = twlLoanAdapter
                        binding!!.tvTwlBorrowerAmt.text =
                            "Rs." + twlActiveLoansResponse!!.twlActiveLoanData?.borrowAmount
                        binding!!.tvTwlLoanId.text =
                            twlActiveLoansResponse!!.twlActiveLoanData?.twlId
                        binding!!.tvTwlStartDate.text =
                            twlActiveLoansResponse!!.twlActiveLoanData?.loanIssueDate
                        binding!!.tvTwlEndDate.text =
                            twlActiveLoansResponse!!.twlActiveLoanData?.loanDueDate
                    }
                    else {

                            binding!!.llNoLoanContent.visibility = View.VISIBLE
                            binding!!.llDetails.visibility = View.GONE
                            binding!!.rvPayLoan.visibility = View.GONE

                    }
                } else {
                    if(twlActiveLoansResponse!!.statusCode == Constants.STATUS_CODE_UNAUTHORISED){
                        logout()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logout() {
        try{
            val logInIntent = Intent(context, LoginActivity::class.java)
            logInIntent.flags =
                Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logInIntent)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun callPayment(twlAmtPayable: TwlAmtPayable) {
        showPopup(twlAmtPayable)
    }
    private fun showPopup(twlAmtPayable: TwlAmtPayable) {
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
            if (this.twlAmtPayable.isNotEmpty()) {

                if (gatewayDownTimeData != null) {
                    //showBankDetailsDialog()
                } else {
                    (activity as DashboardActivity).startTwlPayment(twlAmtPayable)
                }
            }
        }
        alertDialog.show()
    }

}
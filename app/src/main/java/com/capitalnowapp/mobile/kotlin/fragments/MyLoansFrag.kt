package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.BankDetails
import com.capitalnowapp.mobile.beans.PaymentClearData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.loan.MyLoansResponse
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.Utility
import com.facebook.FacebookSdk
import com.google.android.material.tabs.TabLayout
import com.razorpay.PaymentData


class MyLoansFrag : Fragment() {
    private var context = null
    var loanStatus = 0
    var check_no_loans = 0
    private var customer_relationship_details = ""
    var sharedPreferences: CNSharedPreferences? = null


    private var bankDetails: List<BankDetails> = java.util.ArrayList()
    private var currentActivity: Activity? = null
    private var userId: String? = null
    private var CNModel: CNModel? = null
    private var myLoansData: MyLoansResponse = MyLoansResponse()
    private var activeLoansFragment: ActiveLoansFragment? = null
    private var closeLoansFragment: CloseLoansFragment = CloseLoansFragment(myLoansData, 0)

    @SuppressLint("NotConstructor")
    fun MyLoansFrag() {
        // Required empty public constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewpager?.adapter = MyAdapter(childFragmentManager, lifecycle)
        context = context
        currentActivity = activity

        userId = (currentActivity as BaseActivity).userDetails.userId
        CNModel = CNModel(context, currentActivity, Constants.RequestFrom.MY_LOANS)
        refreshData()
        val viewpager = view.findViewById<ViewPager>(R.id.viewpager)
        val tabs = view.findViewById<TabLayout>(R.id.tabs)

        // attach tablayout with viewpager

        // attach tablayout with viewpager
        tabs.setupWithViewPager(viewpager)

        val adapter = ViewPagerAdapter(childFragmentManager)

        // add your fragments

        // add your fragments
        adapter.addFrag(activeLoansFragment!!, "ACTIVE")
        adapter.addFrag(closeLoansFragment, "CLEARED")

        // set adapter on viewpager

        // set adapter on viewpager
        viewpager.setAdapter(adapter)
    }

    private fun refreshData() {
        CNProgressDialog.showProgressDialog(currentActivity, Constants.LOADING_MESSAGE)
        val token = (currentActivity as BaseActivity).userToken
        CNModel!!.getMyLoansData(this, userId, token)
    }

    fun showAlertDialog(message: String?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(context, resources.getString(R.string.title_alert), message)
    }

    fun updateMyLoansResponse(myLoansData: MyLoansResponse) {
        this.myLoansData = myLoansData
        if (myLoansData.userData != null) {
            if (myLoansData.userData!!.loanHistoryData!!.isNotEmpty() || myLoansData.userData!!.loansToPay!!.isNotEmpty()) {
                activeLoansFragment?.updateLoanPayList(myLoansData.userData!!.loansToPay!!)
                closeLoansFragment.updateList(myLoansData.userData!!.loanHistoryData!!)
            }
            if (myLoansData.userData!!.gatewaydowntime != null && (myLoansData.userData!!.gatewaydowntime?.isBank!! || myLoansData.userData!!.gatewaydowntime?.isUpi!!)) {
                activeLoansFragment?.updateGateWayDetails(myLoansData.userData!!.gatewaydowntime)
            }
        }
        CNProgressDialog.hideProgressDialog()
    }

    class ViewPagerAdapter(manager: FragmentManager?) : FragmentStatePagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

        fun addFrag(fragment: Fragment) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add("")
        }

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
    }

    private fun showBankDetails() {
        if (bankDetails.isEmpty()) {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val token = (activity as BaseActivity).userToken
            CNModel?.getBankDetails(this, userId, token);
        } else {
            if (bankDetails.isNotEmpty()) {
                /* val contentView = layoutInflater.inflate(R.layout.custom_dialog_with_list_view, null)
                 val customListView = contentView.findViewById<View>(R.id.custom_list_view) as ListView
                 val customListAdapter = CustomDialogListAdapter(context, R.layout.bank_details_list_row_item, bankDetails)
                 customListView.adapter = customListAdapter
                 val relationship_details_title = contentView.findViewById<View>(R.id.custom_dialog_sub_title) as QCTextView
                 if (customer_relationship_details.isEmpty() || customer_relationship_details == null) relationship_details_title.visibility = View.GONE else {
                     relationship_details_title.setText(customer_relationship_details)
                     relationship_details_title.visibility = View.VISIBLE
                 }
                 val dialogWithListView = QCDialogWithCustomView(context, "Account Details", "OK", contentView)
                 dialogWithListView.setCancelable(true)
                 dialogWithListView.show()*/
            } else {
                showAlertDialog("No Bank Details(s) Found.")
            }
        }
    }

    fun updateBankDetails(bankDetailsList: List<BankDetails>, relationship_details: String) {
        this.bankDetails = bankDetailsList
        this.customer_relationship_details = relationship_details
        CNProgressDialog.hideProgressDialog()
        showBankDetails()
    }

    fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        Utility.displayToast(
            FacebookSdk.getApplicationContext(),
            "PaymentSuccess ",
            Toast.LENGTH_LONG
        )
        storePaymentData(paymentData!!)
    }

    fun onPaymentError(code: Int, description: String, paymentData: PaymentData?) {
        CNProgressDialog.hideProgressDialog()
        activeLoansFragment?.onPaymentError(description)
    }

    private fun storePaymentData(paymentData: PaymentData) {
        CNProgressDialog.showProgressDialog(currentActivity, Constants.LOADING_MESSAGE)
        val token = (activity as BaseActivity).userToken
        CNModel!!.savePaymentData(
            activity as DashboardActivity,
            userId,
            paymentData.paymentId,
            paymentData.orderId,
            paymentData.signature,
            token
        )
    }

    private fun storeTwlPaymentData(paymentData: PaymentData) {
        CNProgressDialog.showProgressDialog(currentActivity, Constants.LOADING_MESSAGE)
        val token = (activity as BaseActivity).userToken
        CNModel!!.saveTwlPaymentData(
            activity as DashboardActivity,
            userId,
            paymentData.paymentId,
            paymentData.orderId,
            paymentData.signature,
            token
        )
    }



    fun updatePaymentData(paymentClearData: PaymentClearData) {
        CNProgressDialog.hideProgressDialog()
        if (paymentClearData.transaction_status == 0) {
            activeLoansFragment?.onPaymentError("Payment Failed at CN Api")
        } else {
            activeLoansFragment?.navigateToPaymentStatusPage(paymentClearData)
        }
    }

    fun onError(msg: String) {
        activeLoansFragment?.onPaymentError(msg)
    }
}

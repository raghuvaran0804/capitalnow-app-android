package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.kotlin.adapters.CloseLoanAdapter
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.loan.LoanHistoryDatum
import com.capitalnowapp.mobile.models.loan.MyLoansResponse
import com.capitalnowapp.mobile.util.CNSharedPreferences
import kotlinx.android.synthetic.main.fragment_active_loans.llNoLoanContent
import kotlinx.android.synthetic.main.fragment_close_loans.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CloseLoansFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CloseLoansFragment(private val myLoansData: MyLoansResponse, private val flag: Int) :
    Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var closedLoanData: List<LoanHistoryDatum> = ArrayList()
    private var closeLoanAdapter: CloseLoanAdapter? = null
    private var CNModel: CNModel? = null
    private var userId: String? = null
    var sharedPreferences: CNSharedPreferences? = null

    @SuppressLint("NotConstructor")
    fun CloseLoansFragment() {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_close_loans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_close_loan = view.findViewById<RecyclerView>(R.id.rv_close_loan)
        rv_close_loan.layoutManager = LinearLayoutManager(activity)
        userId = (activity as BaseActivity).userDetails.userId
        CNModel = CNModel(context, activity, Constants.RequestFrom.MY_LOANS)

        closeLoanAdapter = CloseLoanAdapter(closedLoanData)
        rv_close_loan.adapter = closeLoanAdapter

        if (flag == 1) {
            getData()
        } else {
            updateMyLoansResponse(myLoansData)
        }
    }

    private fun getData() {
     //   CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val token = (activity as BaseActivity).userToken
        CNModel!!.getClearedLoans(this, userId, token, flag)
    }

    fun updateMyLoansResponse(myLoansData: MyLoansResponse) {
        try {
            if (myLoansData.status == true && myLoansData.userData != null) {
                if (myLoansData.userData!!.loanHistoryData!!.isNotEmpty()) {
                    updateList(myLoansData.userData!!.loanHistoryData!!)
                }
            } else {
                llNoLoanContent.visibility = View.VISIBLE
                rv_close_loan.visibility = View.GONE
                if (myLoansData.defaultImg?.isNotEmpty() == true) {
                    img.visibility = View.VISIBLE
                    Glide.with((activity as BaseActivity)).load(myLoansData.defaultImg).into(img)
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
        }catch (e: Exception){
            e.printStackTrace()
        }
        CNProgressDialog.hideProgressDialog()
    }

    fun showAlertDialog(message: String?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(context, resources.getString(R.string.title_alert), message)
    }

    fun updateList(loanHistoryData: List<LoanHistoryDatum>) {
        if (loanHistoryData.size > 0) {
            closedLoanData = loanHistoryData
            closeLoanAdapter?.setClosedLoanData(closedLoanData)
            closeLoanAdapter?.notifyDataSetChanged()
            llNoLoanContent.visibility = View.GONE
            rv_close_loan.visibility = View.VISIBLE
        } else {
            llNoLoanContent.visibility = View.VISIBLE
            rv_close_loan.visibility = View.GONE
        }
    }
}

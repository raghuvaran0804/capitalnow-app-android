package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.activities.LoginActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.CloseLoansHomeAdapter
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.loan.MyLoansResponse
import com.capitalnowapp.mobile.models.loan.UserData
import com.google.android.material.tabs.TabLayout

class CloseLoansHomeFragment : Fragment() {
    private lateinit var pager: ViewPager
    private lateinit var tab: TabLayout
    private var cnModel: CNModel? = null
    private var userId: String? = null

    @SuppressLint("NotConstructor")
    fun CloseLoansHomeFragment() {
        // Required empty public constructor
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_close_loans_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = (activity as BaseActivity).userDetails.userId
        cnModel = CNModel(context, activity, Constants.RequestFrom.MY_LOANS)

        pager = requireView().findViewById(R.id.viewPager)
        tab = requireView().findViewById(R.id.tabLayout)
        getData()
    }

    private fun getData() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val token = (activity as BaseActivity).userToken
        cnModel!!.getClearedLoans(this, userId, token, 0)
    }

    fun updateMyLoansResponse(myLoansData: MyLoansResponse) {
        if (myLoansData.status == true) {
            setViewPager(myLoansData)
        } else if (myLoansData.statusCode == Constants.STATUS_CODE_UNAUTHORISED) {
            logout()
        } else {
            myLoansData.userData = UserData()
            myLoansData.userData!!.loansToPay = ArrayList()
            myLoansData.userData!!.twlLoansToPay = ArrayList()
            setViewPager(myLoansData)
        }
    }

    private fun logout() {
        try{
            val logInIntent = Intent(requireContext(), LoginActivity::class.java)
            logInIntent.flags =
                Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logInIntent)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun setViewPager(myLoansData: MyLoansResponse) {
    val adapter = CloseLoansHomeAdapter(parentFragmentManager)
        val frag = CloseLoansFragment(myLoansData, 0)
        adapter.addFragment(frag,"Personal Loan")
    /*adapter.addFragment(
        CloseLoansFragment(myLoansData, 0),
        "Personal Loan"
    )*/
    /*adapter.addFragment(
        CloseLoansFragment(myLoansData, 1),
        "Two Wheeler Loan"
    )*/
        val frag1 = CloseLoansFragment(myLoansData, 1)
        adapter.addFragment(frag1,"Two Wheeler Loan")
    pager.adapter = adapter
    tab.setupWithViewPager(pager)

    //tab.visibility = View.GONE
}

private fun showError() {
    (activity as DashboardActivity).displayToast(resources.getString(R.string.error_failure))
}

}


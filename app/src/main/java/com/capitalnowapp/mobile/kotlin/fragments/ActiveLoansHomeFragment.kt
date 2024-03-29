package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.ActiveLoansHomeAdapter
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.loan.MyLoansResponse
import com.capitalnowapp.mobile.models.loan.UserData
import com.capitalnowapp.mobile.util.TrackingUtil
import com.google.android.material.tabs.TabLayout
import org.json.JSONException
import org.json.JSONObject

class ActiveLoansHomeFragment : Fragment() {
    private lateinit var pager: ViewPager
    private lateinit var tab: TabLayout
    private var cnModel: CNModel? = null
    private var userId: String? = null

    @SuppressLint("NotConstructor")
    fun ActiveLoansHomeFragment() {
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
        return inflater.inflate(R.layout.fragment_active_loans_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val obj = JSONObject()
        try {
            obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.active_personal_loans_page_landed))

        userId = (activity as BaseActivity).userDetails.userId
        cnModel = CNModel(context, activity, Constants.RequestFrom.MY_LOANS)

        pager = requireView().findViewById(R.id.viewPager)
        tab = requireView().findViewById(R.id.tabLayout)
        getData()
    }

    private fun getData() {
        //CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val token = (activity as BaseActivity).userToken
        cnModel!!.getActiveLoans(this, userId, token)
    }

    fun updateMyLoansResponse(myLoansData: MyLoansResponse) {
        if (myLoansData.status == true && myLoansData.userData != null) {
            setViewPager(myLoansData)
        } else {
            myLoansData.userData = UserData()
            myLoansData.userData!!.loansToPay = ArrayList()
            myLoansData.userData!!.twlLoansToPay = ArrayList()
            setViewPager(myLoansData)
        }
    }

    private fun setViewPager(myLoansData: MyLoansResponse) {
        if (myLoansData.userData != null) {
            if (myLoansData.userData?.loansToPay != null || myLoansData.userData?.twlLoansToPay != null) {
                val adapter = ActiveLoansHomeAdapter(parentFragmentManager)
                val frag = ActiveLoansFragment.newInstance(myLoansData, 0)
                adapter.addFragment(frag,"Personal Loan")
                /*adapter.addFragment(
                    ActiveLoansFragment(myLoansData, 1),
                    "Two Wheeler Loan"
                )*/
                pager.adapter = adapter
                tab.setupWithViewPager(pager)
            } else {
                showError()
            }
        } else {
            showError()
        }

        tab.visibility = GONE
    }

    private fun showError() {
        (activity as DashboardActivity).displayToast(resources.getString(R.string.error_failure))
    }
}


package com.capitalnowapp.mobile.kotlin.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentRewardRedeemHistoryBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.RedeemedHistoryAdapter
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemedCouponsReq
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemedCouponsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_main.toolbar

class RewardRedeemHistoryFragment : Fragment() {
    private var binding: FragmentRewardRedeemHistoryBinding? = null
    private var activity: Activity? = null
    private lateinit var mActivityRef: BaseActivity
    private var getRedeemedCouponsResponse = GetRedeemedCouponsResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardRedeemHistoryBinding.inflate(inflater,container,false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as DashboardActivity).toolbar.visibility = GONE
        getRedeemedCoupons()

        binding?.ivBack?.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivityRef = context as BaseActivity
    }

    private fun getRedeemedCoupons() {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getRedeemedCouponsReq = GetRedeemedCouponsReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.getRedeemedCoupons(getRedeemedCouponsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getRedeemedCouponsResponse =
                    Gson().fromJson(responseBody, GetRedeemedCouponsResponse::class.java)
                if (getRedeemedCouponsResponse != null && getRedeemedCouponsResponse.status == true) {
                    setHistoryData(getRedeemedCouponsResponse)
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun setHistoryData(redeemedCouponsResponse: GetRedeemedCouponsResponse?) {
        binding?.tvPoints?.text = redeemedCouponsResponse?.redeemedCouponsData?.rewardPointsAvailable
        binding!!.rvData.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter =
            RedeemedHistoryAdapter(redeemedCouponsResponse?.redeemedCouponsData?.redeemedCoupons, activity, redeemedCouponsResponse)
        binding?.rvData?.adapter = adapter
    }

}
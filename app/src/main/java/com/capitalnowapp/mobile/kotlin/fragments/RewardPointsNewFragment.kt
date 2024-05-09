package com.capitalnowapp.mobile.kotlin.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.activities.LoginActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentRewardPointsNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.RewardPointsNewAdapter
import com.capitalnowapp.mobile.kotlin.adapters.RewardPointsNewBasicAdapter
import com.capitalnowapp.mobile.models.CouponsList
import com.capitalnowapp.mobile.models.CouponsListData
import com.capitalnowapp.mobile.models.GetCouponsReq
import com.capitalnowapp.mobile.models.GetCouponsResponse
import com.capitalnowapp.mobile.models.rewardsNew.CouponData
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponCategoriesReq
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponCategoriesResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dash_new.drawer_layout
import kotlinx.android.synthetic.main.content_main.toolbar


class RewardPointsNewFragment : Fragment() {
    private var selectedCouponCat: String? = null
    private var getCouponCategoriesResponse: GetCouponCategoriesResponse? = null
    private var getCouponsResponse: GetCouponsResponse? = null
    private var tabPosition: Int = -1
    private lateinit var mActivityRef: BaseActivity
    private var binding: FragmentRewardPointsNewBinding? = null
    private var activity: Activity? = null
    private var cupCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRewardPointsNewBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCouponCategories()

        binding?.ivMenu?.setOnClickListener {
            (activity as DashboardActivity).drawer_layout.openDrawer(GravityCompat.START)
        }
        binding?.tvHistory?.setOnClickListener {
            (activity as DashboardActivity).replaceFrag(
                RewardRedeemHistoryFragment(),
                "History",
                null
            )

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivityRef = context as BaseActivity
    }

    private fun getCoupons(getCouponCategoriesResponse: GetCouponCategoriesResponse) {
        try {
            //CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getCouponsReq = GetCouponsReq()
            getCouponsReq.pageNo = "1"
            getCouponsReq.category = selectedCouponCat
            getCouponsReq.limit = "1000"
            val token = (activity as BaseActivity).userToken
            genericAPIService.getCoupons(getCouponsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getCouponsResponse =
                    Gson().fromJson(responseBody, GetCouponsResponse::class.java)
                if (getCouponsResponse != null && getCouponsResponse!!.status == true) {
                    if (!getCouponsResponse!!.couponsData?.couponsList.isNullOrEmpty()) {
                        setCouponData(
                            getCouponsResponse!!.couponsData,
                            getCouponCategoriesResponse.couponData
                        )
                    } else {
                        CNProgressDialog.hideProgressDialog()
                        Toast.makeText(
                            context,
                            getCouponsResponse!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }
        } catch (e: Exception) {
            CNProgressDialog.hideProgressDialog()
            e.printStackTrace()
        }
    }

    private fun getCouponCategories() {
        try {
            //CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getCouponCategoriesReq = GetCouponCategoriesReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.getCouponCategories(getCouponCategoriesReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                getCouponCategoriesResponse =
                    Gson().fromJson(responseBody, GetCouponCategoriesResponse::class.java)
                if (getCouponCategoriesResponse != null && getCouponCategoriesResponse!!.status == true) {
                    if (!getCouponCategoriesResponse!!.couponData?.couponCategories.isNullOrEmpty()) {
                        binding?.llData?.visibility = View.VISIBLE
                        binding?.llNoLoan?.visibility = View.GONE
                        setCouponCategoryData(getCouponCategoriesResponse)
                        cupCategory =
                            getCouponCategoriesResponse!!.couponData?.couponCategories?.get(0)?.cupCategory
                        getCoupons(getCouponCategoriesResponse!!)
                    } else {
                        binding?.llData?.visibility = View.GONE
                        binding?.llNoLoan?.visibility = View.VISIBLE
                    }
                } else {
                    if (getCouponCategoriesResponse!!.statusCode == Constants.STATUS_CODE_UNAUTHORISED) {
                        logout()
                    }
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                    }
                }
            }
        } catch (e: Exception) {
            CNProgressDialog.hideProgressDialog()
            e.printStackTrace()
        }
    }

    private fun logout() {
        try {
            val logInIntent = Intent(context, LoginActivity::class.java)
            logInIntent.flags =
                Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logInIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCouponCategoryData(couponCategoriesResponse: GetCouponCategoriesResponse?) {
        try {
            val layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding?.rvData?.layoutManager = layoutManager
            val adapter =
                RewardPointsNewBasicAdapter(
                    couponCategoriesResponse?.couponData!!,
                    activity,
                    couponCategoriesResponse,
                    0
                )
            binding?.rvData?.adapter = adapter
            binding?.pageIndicatorView?.count = 2
            binding?.pageIndicatorView?.selection = 0
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(binding?.rvData)

            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
                    super.onScrolled(recyclerView, dx, dy)
                    val firstItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (firstItem >= 0) {
                        adapter.setPosition(firstItem)
                        binding?.pageIndicatorView?.selection = firstItem
                    }
                }
            }
            binding!!.rvData.addOnScrollListener(scrollListener)

            val handler = Handler()
            var count = 0
            var flag = true
            handler.postDelayed(object : Runnable {
                override fun run() {
                    //  adapter.setPosition()
                    if (count < adapter.getItemCount()) {
                        if (count == adapter.getItemCount() - 1) {
                            flag = false;
                        } else if (count == 0) {
                            flag = true;
                        }
                        if (flag) count++;
                        else count--;

                        binding?.rvData?.smoothScrollToPosition(count)
                        handler.postDelayed(this, 6000)
                    }
                }
            }, 3000) //the time is in miliseconds

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCouponData(
        couponsData: CouponsListData?,
        couponData: CouponData?,
    ) {
        try {

            for (cat in couponData?.couponCategories?.withIndex()!!) {
                binding?.tabs?.addTab(binding?.tabs!!.newTab().setText(cat.value.cupCategory));
            }

            val data = filterCouponsData(
                couponData.couponCategories!![0].cupCategory.toString(),
                couponsData?.couponsList!!
            )

            var adapter =
                RewardPointsNewAdapter(data, activity, couponData.rewardPointsAvailable)
            binding?.recyclerView?.layoutManager = LinearLayoutManager(activity)
            binding?.recyclerView?.adapter = adapter


            binding?.tabs?.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    tabPosition = tab.position
                    selectedCouponCat = couponData.couponCategories!![tabPosition].cupCategory
                    val data = filterCouponsData(tab.text.toString(), couponsData.couponsList!!)
                    adapter =
                        RewardPointsNewAdapter(data, activity, couponData.rewardPointsAvailable)
                    binding?.recyclerView?.layoutManager = LinearLayoutManager(activity)
                    binding?.recyclerView?.adapter = adapter
                    //getCoupons(getCouponCategoriesResponse!!)

                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filterCouponsData(
        cat: String?,
        couponsList: List<CouponsList>
    ): List<CouponsList> {
        val list = ArrayList<CouponsList>()
        /*if (cat?.lowercase() == "all") {
            return couponsList
        } else {*/
            for (data in couponsList) {
                if (data.cupCategory.toString() == cat) {
                    list.add(data)
                }
            }
        //}
        return list
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).toolbar.visibility = View.GONE
    }

}
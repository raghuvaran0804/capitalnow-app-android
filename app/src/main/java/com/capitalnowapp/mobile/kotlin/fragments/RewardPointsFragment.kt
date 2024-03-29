package com.capitalnowapp.mobile.kotlin.fragments

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentRewardPointsBinding
import com.capitalnowapp.mobile.kotlin.adapters.RewardPointsBasicAdapter
import com.capitalnowapp.mobile.models.GenericRequest
import com.capitalnowapp.mobile.models.coupons.CouponsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.Utility
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dash_new.drawer_layout
import kotlinx.android.synthetic.main.fragment_reward_points.anim
import kotlinx.android.synthetic.main.fragment_reward_points.ivMenu
import kotlinx.android.synthetic.main.fragment_reward_points.ivMenu1
import kotlinx.android.synthetic.main.fragment_reward_points.llBg
import kotlinx.android.synthetic.main.fragment_reward_points.llData
import kotlinx.android.synthetic.main.fragment_reward_points.llNoRewards
import kotlinx.android.synthetic.main.fragment_reward_points.tabs
import kotlinx.android.synthetic.main.fragment_reward_points.tvInfo
import kotlinx.android.synthetic.main.fragment_reward_points.tvNoPoints
import kotlinx.android.synthetic.main.fragment_reward_points.tvPoints
import kotlinx.android.synthetic.main.fragment_reward_points.viewPager


class RewardPointsFragment : Fragment() {

    private lateinit var mActivityRef: BaseActivity
    private lateinit var binding: FragmentRewardPointsBinding

    @SuppressLint("NotConstructor")
    fun RewardPointsFragment() {
        // empty constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardPointsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*  binding.viewPager.rootView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
          binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL*/

        getLogicalBanyaData()

        ivMenu.setOnClickListener {
            (mActivityRef).drawer_layout.openDrawer(GravityCompat.START)
        }
        ivMenu1.setOnClickListener {
            (mActivityRef).drawer_layout.openDrawer(GravityCompat.START)
        }
    }
    private fun getLogicalBanyaData() {
        CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity)
        val genericRequest = GenericRequest()
        genericRequest.userId = (mActivityRef).userId
        genericRequest.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
        val token = (activity as BaseActivity).userToken
        genericAPIService.getLogicalBanyaData(genericRequest, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val couponsResponse = Gson().fromJson(responseBody, CouponsResponse::class.java)
            //   couponsResponse.status = false
            if (couponsResponse != null && couponsResponse.status) {
                if (couponsResponse.couponsList.isNotEmpty()) {
                    (mActivityRef).sharedPreferences.putBoolean(
                        "should_redeem_refresh",
                        false
                    )
                    setCouponsData(couponsResponse)
                    if (!couponsResponse.rewardPoints.equals("0")) {
                        if (anim != null) {
                            anim.visibility = VISIBLE
                            anim.addAnimatorListener(object : Animator.AnimatorListener {

                                override fun onAnimationStart(animation: Animator) {

                                }

                                override fun onAnimationEnd(animation: Animator) {
                                    try {
                                        anim.visibility = GONE
                                    } catch (ex: java.lang.Exception) {
                                        ex.toString()
                                    }
                                }

                                override fun onAnimationCancel(animation: Animator) {

                                }

                                override fun onAnimationRepeat(animation: Animator) {

                                }
                            })
                        }
                    }
                } else {
                    setEmptyData(couponsResponse)
                }
            } else {
                setEmptyData(couponsResponse)
            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
            Toast.makeText(context, getString(R.string.error_failure), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setEmptyData(couponsResponse: CouponsResponse) {
        llBg.visibility = GONE
        llData.visibility = GONE
        llNoRewards.visibility = VISIBLE
        val points = couponsResponse.rewardPoints
        val s = "Hi ${couponsResponse.username}, \n You have " + points + " Points to Redeem."
        if (!couponsResponse.rewardPoints.equals("0")) {
            tvInfo.text = "No coupons available"
        }
        val spannable = SpannableString(s)
        val spannable1 = Utility.increaseFontSizeForPath(
            spannable,
            ("$points Points"),
            1.3F
        ) // make "big" text bigger 3 time than normal text
        tvNoPoints.text = spannable1
    }

    private fun setCouponsData(couponsResponse: CouponsResponse) {
        try {
            val points = couponsResponse.rewardPoints?.toInt()!!

            val s =
                "Hi " + couponsResponse.username + ", \n You have " + points.toString() + " Points to Redeem."
            val spannable = SpannableString(s)
            val spannable1 = Utility.increaseFontSizeForPath(
                spannable,
                ("$points Points"),
                1.3F
            ) // make "big" text bigger 3 time than normal text
            tvPoints.text = spannable1

            viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
            //  val couponsList = getSortedList(couponsResponse.couponsList)
            val adapter =
                RewardPointsBasicAdapter(couponsResponse.couponsList, activity, couponsResponse)
            viewPager.adapter = adapter

            TabLayoutMediator(
                tabs, viewPager
            ) { tab, position ->
                tab.text = couponsResponse.couponsList[position].category
                /*adapter = RewardPointsBasicAdapter(couponsResponse.couponsList[position])
                viewPager.adapter = adapter*/
            }.attach()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*  private fun getSortedList(couponsList: List<CouponsData>): List<CouponsData> {
          Collections.sort(couponsList) { abc1, abc2 -> Boolean.compare(abc1.isRedeemed!!, abc2.isRedeemed!!) }
          return couponsList
      }
  */

    override fun onResume() {
        super.onResume()
        if ((mActivityRef).sharedPreferences.getBoolean("should_redeem_refresh")) {
            getLogicalBanyaData()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivityRef = context as BaseActivity
    }
}
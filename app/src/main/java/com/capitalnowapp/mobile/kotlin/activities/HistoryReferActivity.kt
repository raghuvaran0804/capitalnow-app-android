package com.capitalnowapp.mobile.kotlin.activities


import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.CashBackData
import com.capitalnowapp.mobile.beans.RewardPointsData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityHistoryReferBinding
import com.capitalnowapp.mobile.kotlin.adapters.CashBackAdapter
import com.capitalnowapp.mobile.kotlin.adapters.HistoryRewardAdapter
import com.capitalnowapp.mobile.models.CNModel
import org.json.JSONObject


class HistoryReferActivity : BaseActivity() {
    private var rewardPointsData: MutableList<RewardPointsData> = ArrayList()
    private var cashBackData: MutableList<CashBackData> = ArrayList()
    private var historyRewardAdapter: HistoryRewardAdapter? = null
    private var cashBackAdapter: CashBackAdapter? = null
    private var balance_history: String? = null
    private var reward_points: String? = null
    var binding: ActivityHistoryReferBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryReferBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding?.ivBack?.setOnClickListener(View.OnClickListener {
            finish()
        })

        binding?.tvApplyLoan?.setOnClickListener(View.OnClickListener {
            setResult(Activity.RESULT_OK, null)
            finish()
        })

        binding?.tvInfoReward?.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding?.tvInfoReward?.right!! - binding?.tvInfoReward?.totalPaddingRight!!)
                    alertInfo(getString(R.string.reward_balance_info), binding?.tvInfoReward!!)
                return@OnTouchListener true
            }
            true
        })

        binding?.tvRewardPoints?.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding?.tvRewardPoints?.right!! - binding?.tvRewardPoints?.totalPaddingRight!!)
                    alertInfo(getString(R.string.reward_points_info), binding?.tvRewardPoints!!)
                return@OnTouchListener true
            }
            true
        })

        userId = userDetails.userId
        cnModel = CNModel(this, this, Constants.RequestFrom.MY_LOANS)

        val rvRewardHistory = view.findViewById<RecyclerView>(R.id.rvRewardHistory)
        rvRewardHistory.layoutManager = LinearLayoutManager(this)
        historyRewardAdapter = HistoryRewardAdapter(rewardPointsData)
        rvRewardHistory.adapter = historyRewardAdapter

        val rvCashBack = view.findViewById<RecyclerView>(R.id.rvCashBack)
        rvCashBack.layoutManager = LinearLayoutManager(this)
        cashBackAdapter = CashBackAdapter(cashBackData)
        rvCashBack.adapter = cashBackAdapter
        getRewardPointsAndCashBackData()
    }

    private fun alertInfo(s: String, tv: CNTextView) {
       /* val balloon = createBalloon(baseContext) {
            setArrowSize(10)
            setWidthRatio(0.5f)
            setHeight(50)
            setArrowPosition(0.5f)
            setCornerRadius(8f)
            setAlpha(0.9f)
            setArrowOrientation(ArrowOrientation.TOP)
            setText(s)
            setTextColorResource(R.color.intro_page_body_color)
            setBackgroundColorResource(R.color.white)
            setLifecycleOwner(lifecycleOwner)
        }
        balloon.dismissWithDelay(5000)
        balloon.showAlignBottom(tv)*/
    }

    private fun alertInfoPoints(s: String) {
        /* SimpleTooltip.Builder(this)
                 .anchorView(binding?.tvRewardPoints)
                 .text(s)
                 .gravity(Gravity.TOP)
                 .animated(true)
                 .margin(10f)
                 .arrowColor(getColor(R.color.white))
                 .backgroundColor(getColor(R.color.white))
                 .textColor(getColor(R.color.intro_page_body_color))
                 .transparentOverlay(true)
                 .build()
                 .show()*/
    }

    fun getRewardPointsAndCashBackData() {
        CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
        val token = userToken

        cnModel.getReferralHistory(this, userId,token)
    }

    fun updateRewardPointsAndCashBackData(rewardPointsDataList: MutableList<RewardPointsData>, response: JSONObject) {
        rewardPointsData = rewardPointsDataList
        if (rewardPointsData.size > 0) {
            updateHistoryReferAndEarn(response)
            historyRewardAdapter?.setRewardPointsDataList(rewardPointsData)
            historyRewardAdapter?.notifyDataSetChanged()

            binding?.llDataNotFound?.visibility = GONE
            binding?.llData?.visibility = VISIBLE
        } else {
            binding?.llDataNotFound?.visibility = VISIBLE
            binding?.llData?.visibility = GONE
        }
        CNProgressDialog.hideProgressDialog()
    }

    fun updateHistoryReferAndEarn(response: JSONObject) {
        balance_history = response.getInt("balance").toString()
        reward_points = response.getInt("reward_points").toString()
        binding?.tvTotalBalance?.text = balance_history
        binding?.tvRewardTotal?.text = reward_points
    }
}

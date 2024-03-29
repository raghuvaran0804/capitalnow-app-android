package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.beans.RewardPointsData
import com.capitalnowapp.mobile.databinding.ItemHistoryRewardBinding

class HistoryRewardAdapter(private var rewardPointsData: MutableList<RewardPointsData>) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var binding: ItemHistoryRewardBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemHistoryRewardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryRewardVH(binding)
    }

    fun setRewardPointsDataList(rewardPointsData: MutableList<RewardPointsData>) {
        this.rewardPointsData = rewardPointsData
    }

    private inner class HistoryRewardVH internal constructor(binding: ItemHistoryRewardBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        init {
            binding!!.root.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val tvRewardType = binding?.tvRewardType
        val tvRewardId = binding?.tvRewardId
        val tvAmountReward = binding?.tvAmountReward
        val tvRewardDate = binding?.tvRewardDate
        val ivRewardType = binding?.ivRewardType
        val tvRewardReference = binding?.tvRewardReference

    }

    override fun getItemCount(): Int {
        return rewardPointsData!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HistoryRewardVH) {
            val rewardPointData = getRewardPointData(position)
            if (rewardPointData?.type.equals("C") && rewardPointData?.rewardType.equals("referal")) {

                holder.tvRewardType?.text = "Referral Rewards"
                holder.tvRewardId?.text = "QCID - "
                holder.tvRewardReference?.text = rewardPointData?.loanId
                holder.tvAmountReward?.text = rewardPointData?.points + " pts"

            } else if (rewardPointData?.type.equals("C") && rewardPointData?.rewardType.equals("clearloan")) {
                holder.tvRewardType?.text = "Rewards"
                holder.tvRewardId?.text = "LoanID - "
                holder.tvRewardReference?.text = rewardPointData?.loanId
                holder.tvAmountReward?.text = rewardPointData?.points + " pts"

            } else if (rewardPointData?.type.equals("D")) {
                holder.ivRewardType?.setImageResource(R.drawable.ic_redeem)
                holder.tvRewardType?.text = "Redeem " + "-" + rewardPointData?.points + " pts"
                holder.tvRewardType?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.orange_alert))
                holder.tvAmountReward?.text = "₹" + rewardPointData?.amount
                holder.tvRewardId?.text = "Trnx ID - "
                holder.tvRewardReference?.text = rewardPointData?.transId

            } else if (rewardPointData?.type.equals("C") && rewardPointData?.rewardType.equals("cashback")) {
                holder.tvRewardType?.text = "Cashback"
                holder.ivRewardType?.setImageResource(R.drawable.ic_cashback)

                if (!rewardPointData?.amount.equals(null)) {
                    holder.tvAmountReward?.text = "₹" + rewardPointData?.amount
                } else {
                    holder.tvAmountReward?.text = "₹ 0"
                }
                holder.tvRewardId?.text = rewardPointData?.referText
                holder.tvRewardReference?.text = rewardPointData?.referId
            }

            holder.tvRewardDate?.text = rewardPointData?.date
        }
    }

    fun getRewardPointData(position: Int): RewardPointsData? {
        return if (rewardPointsData.size > 0) {
            rewardPointsData[position]
        } else null
    }
}
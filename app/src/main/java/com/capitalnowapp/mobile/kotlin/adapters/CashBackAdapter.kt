package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.beans.CashBackData
import com.capitalnowapp.mobile.databinding.ItemCashBackBinding

class CashBackAdapter(private var cashBackData : MutableList<CashBackData>) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var binding: ItemCashBackBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding=ItemCashBackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryRewardVH(binding)
    }

    fun setCashBackDataList(rewardPointsData: MutableList<CashBackData>) {
        this.cashBackData = rewardPointsData
    }

    private inner class HistoryRewardVH internal constructor(binding: ItemCashBackBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        init {
            binding!!.root.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val tvRewardType = binding?.tvRewardType
        val tvRewardId = binding?.tvRewardId
        val tvAmountReward = binding?.tvAmountReward
        val tvCashBackDate=binding?.tvCashBackDate

    }

    override fun getItemCount(): Int {
        return cashBackData!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HistoryRewardVH) {
            val cashBackData = getCashBackData(position)
            if (cashBackData?.type.equals("C") ){
                holder.tvRewardType?.text = "Cashback"
            }
            if (!cashBackData?.amount.equals(null)){
                holder.tvAmountReward?.text = "₹" + cashBackData?.amount
            }else{
                holder.tvAmountReward?.text = "₹ 0"
            }
            holder.tvRewardId?.text ="Referred by CN id "+ cashBackData?.referBy
            holder.tvCashBackDate?.text = cashBackData?.date
        }
    }

    fun getCashBackData(position: Int): CashBackData? {
        return if (cashBackData.size > 0) {
            cashBackData[position]
        } else null
    }
}
package com.capitalnowapp.mobile.kotlin.adapters

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.databinding.ItemRewardHistoryNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.fragments.RewardRedeemedCouponNewFragment
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemedCouponsResponse
import com.capitalnowapp.mobile.models.rewardsNew.RedeemedCoupon

class RedeemedHistoryAdapter(private var redeemedCoupons: List<RedeemedCoupon>?, private var activity: Activity?,private var redeemedCouponsResponse: GetRedeemedCouponsResponse?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: ItemRewardHistoryNewBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemRewardHistoryNewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RedeemHistoryVH(binding)
    }

    private inner class RedeemHistoryVH(binding: ItemRewardHistoryNewBinding?) : RecyclerView.ViewHolder(binding!!.root) {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val couponImage = redeemedCouponsResponse?.redeemedCouponsData?.redeemedCoupons!![position].imageUrl
        Glide.with(holder.itemView.context).load(couponImage)
            .into(binding!!.ivCouponImage)
        binding?.tvPoints?.text = redeemedCoupons!![position].redeemedPoints.toString()
        binding?.tvExpireDate?.text = redeemedCoupons!![position].cuprlExpiryDate
        binding?.tvWorth?.text = redeemedCoupons!![position].cupValue
        binding?.llCard?.setOnClickListener {
            (activity as DashboardActivity).rewardsRedirection = "fromRewardHistory"
            (activity as DashboardActivity).rewardsRedirectionId = redeemedCoupons?.get(holder.absoluteAdapterPosition)?.redeemLogId.toString()
            val bundle = Bundle()
            bundle.putString("redeemedLogId", redeemedCoupons?.get(holder.absoluteAdapterPosition)?.redeemLogId.toString())
            (activity as DashboardActivity).replaceFrag(
                RewardRedeemedCouponNewFragment(),
                "Redeemed Coupons",
                bundle
            )
        }

    }

    override fun getItemCount(): Int {
        return redeemedCoupons?.size!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

package com.capitalnowapp.mobile.kotlin.adapters

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemRewardPointsNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.fragments.RewardRedeemNewFragment
import com.capitalnowapp.mobile.models.CouponsList

class RewardPointsNewAdapter(
    private var couponsList: List<CouponsList>,
    private val activity: Activity?,
    private val rewardPointsAvailable: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: ItemRewardPointsNewBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding =
            ItemRewardPointsNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsVH(binding)
    }

    private inner class DetailsVH(binding: ItemRewardPointsNewBinding?) :
        RecyclerView.ViewHolder(binding!!.root) {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = couponsList.size

        val couponImage = couponsList[position].cupImageUrl
        Glide.with(holder.itemView.context).load(couponImage)
            .into(binding!!.ivCouponImage)
        binding?.tvCouponName?.text = couponsList[position].cupBrand
        binding?.tvWorthText?.text = couponsList[position].cupName
        binding?.tvPoints?.text = couponsList[position].cupPoints.toString()

        if (rewardPointsAvailable?.toInt()!! >= couponsList[position].cupPoints?.toInt()!!) {
            binding?.tvRedeem?.isClickable = true
            binding?.tvRedeem?.isEnabled = true

        } else {
            binding?.tvRedeem?.backgroundTintList =
                ContextCompat.getColorStateList(activity!!, R.color.light_gray)
            binding?.tvRedeem?.isClickable = false
            binding?.tvRedeem?.isFocusable = false
            binding?.tvRedeem?.isEnabled = false

        }

        binding?.tvRedeem?.setOnClickListener {

            val bundle = Bundle()
            bundle.putString(
                "cupId",
                couponsList[holder.absoluteAdapterPosition].cupId.toString()
            )
            (activity as DashboardActivity).replaceFrag(
                RewardRedeemNewFragment(),
                (activity as DashboardActivity).getString(R.string.add_references),
                bundle
            )
            // activity?.startActivity(Intent(activity, RewardRedeemNewFragment::class.java).putExtra("data", data).putExtra("coupon_response", couponsResponse))
        }
    }

    override fun getItemCount(): Int {
        return couponsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun refreshList(data: List<CouponsList>) {
        this.couponsList = ArrayList()
        this.couponsList = data
        notifyDataSetChanged()
    }
}

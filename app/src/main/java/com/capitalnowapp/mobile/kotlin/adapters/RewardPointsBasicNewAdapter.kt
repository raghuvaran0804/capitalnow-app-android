package com.capitalnowapp.mobile.kotlin.adapters

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemRewardPointsBasicNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.fragments.RewardRedeemNewFragment
import com.capitalnowapp.mobile.models.CouponsList

class RewardPointsBasicNewAdapter(private var couponsList: List<CouponsList>?, private var cupImageUrl: String?, private var cupPoints: Int?, private var cupBrand: String?, private var cupName: String?,
                                  private var rewardPointsAvailable: String?, private var activity: Activity?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: ItemRewardPointsBasicNewBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemRewardPointsBasicNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsVH(binding)
    }

    private inner class DetailsVH(binding: ItemRewardPointsBasicNewBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        val ivCouponImage = binding?.ivCouponImage
        val tvCouponName = binding?.tvCouponName
        val tvWorthText = binding?.tvWorthText
        val tvPoints = binding?.tvPoints
        val tvRedeem = binding?.tvRedeem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DetailsVH) {
            val data = couponsList?.size

            val couponImage = couponsList!![position].cupImageUrl
            Glide.with(holder.itemView.context).load(couponImage)
                .into(holder.ivCouponImage!!)
            holder.tvCouponName?.text = couponsList!![position].cupBrand
            holder.tvWorthText?.text = couponsList!![position].cupName
            holder.tvPoints?.text = couponsList!![position].cupPoints.toString()

            if (rewardPointsAvailable?.toString()!! >= couponsList!![position].cupPoints?.toString()
                    .toString()
            ) {
                holder.tvRedeem?.isClickable = true
                holder.tvRedeem?.isEnabled = true

            } else {
                holder.tvRedeem?.backgroundTintList =
                    ContextCompat.getColorStateList(activity!!, R.color.light_gray)
                holder.tvRedeem?.isClickable = false
                holder.tvRedeem?.isFocusable = false
                holder.tvRedeem?.isEnabled = false

            }

            holder.tvRedeem?.setOnClickListener {

                val bundle = Bundle()
                bundle.putString(
                    "cupId",
                    couponsList?.get(holder.absoluteAdapterPosition)?.cupId.toString()
                )
                (activity as DashboardActivity).replaceFrag(
                    RewardRedeemNewFragment(),
                    (activity as DashboardActivity).getString(R.string.add_references),
                    bundle
                )
                // activity?.startActivity(Intent(activity, RewardRedeemNewFragment::class.java).putExtra("data", data).putExtra("coupon_response", couponsResponse))
            }
        }
    }

    override fun getItemCount(): Int {
        return couponsList?.size!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

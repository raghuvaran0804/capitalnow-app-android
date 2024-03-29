package com.capitalnowapp.mobile.kotlin.adapters

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.databinding.ItemRewardPointsBinding
import com.capitalnowapp.mobile.kotlin.activities.CheckCouponActivity
import com.capitalnowapp.mobile.kotlin.activities.CouponDetailsActivity
import com.capitalnowapp.mobile.models.coupons.CouponsData
import com.capitalnowapp.mobile.models.coupons.CouponsResponse


class RewardPointsAdapter(private val couponsList: CouponsData, private val bgColors: String?, private var activity: FragmentActivity?, private val couponsResponse: CouponsResponse) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var binding: ItemRewardPointsBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemRewardPointsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsVH(binding)
    }

    private inner class DetailsVH(binding: ItemRewardPointsBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        val ivLogo = binding?.ivLogo
        val tvTitle = binding?.tvTitle
        val llData = binding?.llData
        val tvPoints = binding?.tvPoints
        val tvDetails = binding?.tvDetails
    }

    override fun getItemCount(): Int {
        return couponsList.list!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DetailsVH) {
            try {
                val data = couponsList.list?.get(position)
                if (!data?.websiteLogo.isNullOrEmpty()) {
                    Glide.with(holder.itemView.context).load(data?.websiteLogo).into(holder.ivLogo!!)
                }
                holder.tvTitle?.text = data?.couponTitle
                holder.tvPoints?.text = data?.points + "\n" + "Points"


                val colorsList: MutableList<List<String>> = mutableListOf(bgColors!!.split(","))
                val color1 = colorsList[0][0]
                val color2 = colorsList[0][1]

                val colors = intArrayOf(Color.parseColor(color1), Color.parseColor(color2))
                val gd = GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT, colors)
                gd.cornerRadius = 0f
                holder.llData?.background = gd

                if (data!!.redeemed) {
                    holder.tvDetails?.text = "View Coupon >"
                } else {
                    holder.tvDetails?.text = "Details >"
                }

                holder.tvDetails?.setOnClickListener {
                    if (data!!.redeemed) {
                        activity?.startActivity(Intent(activity, CheckCouponActivity::class.java)
                                .putExtra("data", data).putExtra("coupon_response", couponsResponse))
                    } else {
                        activity?.startActivity(Intent(activity, CouponDetailsActivity::class.java)
                                .putExtra("coupons_data", data).putExtra("response", couponsResponse))
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
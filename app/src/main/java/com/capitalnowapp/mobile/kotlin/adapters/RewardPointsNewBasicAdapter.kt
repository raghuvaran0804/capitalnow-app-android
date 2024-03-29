package com.capitalnowapp.mobile.kotlin.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.models.rewardsNew.CouponData
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponCategoriesResponse
import kotlinx.android.synthetic.main.item_reward_points_new_basic_0.view.tvPoints
import kotlinx.android.synthetic.main.item_reward_points_new_basic_1.view.ivShare
import kotlinx.android.synthetic.main.item_reward_points_new_basic_1.view.tvCoupon
import kotlinx.android.synthetic.main.item_reward_points_new_basic_1.view.tvPointsText

class RewardPointsNewBasicAdapter(
    private var couponData: CouponData,
    private var activity: Activity?,
    private var couponCategoriesResponse: GetCouponCategoriesResponse?, private var pos: Int?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var view: View? = null
        view = if (pos == 0) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reward_points_new_basic_0, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reward_points_new_basic_1, parent, false)
        }

        return RewardsCatVH(view, pos)
    }

    private class RewardsCatVH(view: View, pos: Int?) : RecyclerView.ViewHolder(view) {
        init {
            var tvPoints: CNTextView? = null
            var tvCoupon: CNTextView? = null

            if (pos == 0) {
                tvPoints = view.findViewById<CNTextView>(R.id.tvPoints)
            } else {
                tvCoupon = view.findViewById<CNTextView>(R.id.tvCoupon)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RewardsCatVH) {
            if (pos == 0) {
                //couponData.rewardPointsAvailable // binding.tvFullPoints.settext
                holder.itemView.tvPoints.text = couponData.rewardPointsAvailable
            } else {
                holder.itemView.tvCoupon.text = couponData.referralCode
                holder.itemView.tvPointsText.text = "Get "+couponData.getPoints+" Points"
                holder.itemView.ivShare?.setOnClickListener(View.OnClickListener {
                    try {
                        val intent= Intent()
                        val couponCode = couponData.referralCode
                        val shareText = couponData.shareText
                        intent.action=Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT, "$shareText Redeem Code - $couponCode.")
                        intent.type="text/plain"
                        activity?.startActivity(Intent.createChooser(intent,"Share To:"))


                    } catch (e: Exception) {
                        //e.toString();
                        e.printStackTrace()
                    }
                })
            }
        }

    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return this.pos!!;
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setPosition(pos: Int) {
        this.pos = pos;
       // notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}

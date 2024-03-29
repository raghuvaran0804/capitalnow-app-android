package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.databinding.ItemRewardPointsBasicBinding
import com.capitalnowapp.mobile.models.coupons.CouponsData
import com.capitalnowapp.mobile.models.coupons.CouponsResponse

class RewardPointsBasicAdapter(private val couponsList: List<CouponsData>, private var activity: FragmentActivity?,private val couponsResponse: CouponsResponse) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var binding: ItemRewardPointsBasicBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemRewardPointsBasicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsVH(binding)
    }

    private inner class DetailsVH internal constructor(binding: ItemRewardPointsBasicBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        val rvData: RecyclerView = binding?.rvData!!
    }

    override fun getItemCount(): Int {
        return couponsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DetailsVH) {
            holder.rvData.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.rvData.adapter = RewardPointsAdapter(couponsList[position], couponsList[position].bgColors, activity, couponsResponse)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
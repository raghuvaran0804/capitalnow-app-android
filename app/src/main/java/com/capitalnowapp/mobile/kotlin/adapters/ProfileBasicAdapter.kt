package com.capitalnowapp.mobile.kotlin.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.databinding.ItemProfileBasicBinding
import com.capitalnowapp.mobile.models.profile.ProfileBasic
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou


class ProfileBasicAdapter(private var profileBasicList: List<ProfileBasic>, private var expandedItem: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var binding: ItemProfileBasicBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemProfileBasicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasicVH(binding)
    }

    private inner class BasicVH(binding: ItemProfileBasicBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        val rvData = binding?.rvData
        val ivBasic = binding?.ivBasic
        val tvTitle = binding?.tvTitle
        val ivDrop = binding?.ivDrop
        val rl = binding?.rl
        val view = binding?.view
    }

    override fun getItemCount(): Int {
        return profileBasicList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BasicVH) {
            val data = profileBasicList[position]
            holder.tvTitle?.text = data.title
            if (!data.image.isNullOrEmpty()) {
                if (data.image.contains(".svg")) {
                    GlideToVectorYou.justLoadImage((holder.itemView.context as BaseActivity), Uri.parse(data.image), holder.ivBasic)
                } else {
                    Glide.with(holder.itemView.context).load(data.image).into(holder.ivBasic!!)
                }
            }
            holder.rvData?.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.rvData?.adapter = ProfileDetailsAdapter(data.details!!)

            if (data.expand == true) {
                holder.rvData?.visibility = VISIBLE
                holder.view?.visibility = VISIBLE
                holder.ivDrop?.setImageResource(R.drawable.ic_profile_up_arrow)
            } else {
                holder.rvData?.visibility = GONE
                holder.view?.visibility = INVISIBLE
                holder.ivDrop?.setImageResource(R.drawable.ic_profile_down_arrow)
            }

            holder.rl?.setOnClickListener {
                /*expandedItem = position
                notifyDataSetChanged()*/
                if (holder.rvData?.visibility == VISIBLE) {
                    holder.rvData.visibility = GONE
                    holder.ivDrop?.setImageResource(R.drawable.ic_profile_down_arrow)
                } else {
                    val slide: Animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_up)
                    holder.rvData?.visibility = VISIBLE
                  //  holder.rvData?.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.down_slide))
                    holder.ivDrop?.setImageResource(R.drawable.ic_profile_up_arrow)
                }
            }
        }
    }
}
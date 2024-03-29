package com.capitalnowapp.mobile.kotlin.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.GetStartedViewPagerItemBinding


class GetStartedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var binding: GetStartedViewPagerItemBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = GetStartedViewPagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GetSTartedVH(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            var title = ""
            var content = ""
            var image: Drawable? = null
            when (position) {
                0 -> {
                    //  image = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_intro_1)
                    //title = holder.itemView.context.getString(R.string.get_started_title1)
                    //content = holder.itemView.context.getString(R.string.get_started_content1)
                    binding?.frameText?.visibility = View.GONE
                }
                1 -> {
                    //   image = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_intro_2)
                    //title = holder.itemView.context.getString(R.string.get_started_title2)
                    //content = holder.itemView.context.getString(R.string.get_started_content2)
                    binding?.frameText?.visibility = View.GONE
                }
            }
            binding?.tvTitle?.text = title
            binding?.tvContent?.text = content
            if (position == 0) {
                binding?.viewPagerImage?.setImageResource( R.drawable.cn_intro)
                binding?.tvTitle1?.text = "Quick . Digital . Safe"
            }
            if (position == 1) {
                binding?.viewPagerImage?.setImageResource( R.drawable.cs_intro)
                binding?.tvTitle1?.text = "Offerings"

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    private inner class GetSTartedVH(binding: GetStartedViewPagerItemBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        init {
            binding!!.root.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}
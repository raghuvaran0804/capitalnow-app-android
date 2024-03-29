package com.capitalnowapp.mobile.kotlin.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.fragments.HomeFragment
import com.capitalnowapp.mobile.models.HomeBannerImage
import com.smarteist.autoimageslider.SliderViewAdapter

class HomeImageSliderAdapter(
    private var images: MutableList<HomeBannerImage>,
    private var homeFragment: HomeFragment,
    private var currentActivity: Activity
) : SliderViewAdapter<SliderAdapterVH>() {
    private var mSliderItems: MutableList<HomeBannerImage> = ArrayList()

    override fun getCount(): Int {
        return mSliderItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate =
            LayoutInflater.from(parent?.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        val sliderItem = mSliderItems[position]
        Glide.with(viewHolder?.itemView?.context!!)
            .load(sliderItem.bannerLink)
            .fitCenter()
            .into(viewHolder.imageViewBackground)

        viewHolder.imageViewBackground.setOnClickListener {
            val redirectUrl = sliderItem.redirectionUrl
            val bannerType = sliderItem.bType
            val sharetext = sliderItem.shareMsg
            homeFragment.bannerView(bannerType,redirectUrl,sharetext,currentActivity)
        }
    }

    fun renewItems(images: MutableList<HomeBannerImage>) {
        mSliderItems = images
        notifyDataSetChanged()
    }
}

class SliderAdapterVH (itemView: View) :
    SliderViewAdapter.ViewHolder(itemView) {
    var imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)

}


package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.kotlin.adapters.ImageSliderAdapter.SliderAdapterVH
import com.capitalnowapp.mobile.models.Image
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderAdapter() : SliderViewAdapter<SliderAdapterVH>() {
    private var mSliderItems: MutableList<Image> = ArrayList()

    fun renewItems(sliderItems: MutableList<Image>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: Image) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {

        val sliderItem = mSliderItems[position]
        Glide.with(viewHolder.itemView.context)
            .load(sliderItem.twlBannerLink)
            .fitCenter()
            .into(viewHolder.imageViewBackground)
        viewHolder.itemView.setOnClickListener { }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    class SliderAdapterVH(itemView: View) :
        ViewHolder(itemView) {
        var imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
    }
}
package com.capitalnowapp.mobile.kotlin.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemActiveLoanSheetDetailsBinding
import com.capitalnowapp.mobile.models.loan.PriceBreakup

class SheetBreakUpAdapter(private val priceBreakup: List<PriceBreakup>?, private val recommended: Boolean?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: ItemActiveLoanSheetDetailsBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemActiveLoanSheetDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsVH(binding!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DetailsVH) {
            holder.tvTitle.text = priceBreakup?.get(position)?.title
            holder.tvValue.text = priceBreakup?.get(position)?.value.toString()

            if (recommended == true) {
                holder.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.tvValue.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            } else {
                if (!priceBreakup!![position].color.isNullOrEmpty()) {
                    val col = priceBreakup[position].color
                    holder.tvTitle.setTextColor(Color.parseColor(col))
                    holder.tvValue.setTextColor(Color.parseColor(col))
                } else {
                    holder.tvValue.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    holder.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return priceBreakup?.size!!
    }
}

class DetailsVH(binding: ItemActiveLoanSheetDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
    val tvTitle = binding?.tvTitle
    val tvValue = binding?.tvValue
}

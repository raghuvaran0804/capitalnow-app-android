package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.databinding.ItemProfileDetailsBinding
import com.capitalnowapp.mobile.models.loan.Value

class ProfileDetailsAdapter(private var children: List<Value>) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var binding: ItemProfileDetailsBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemProfileDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsVH(binding)
    }

    private inner class DetailsVH internal constructor(binding: ItemProfileDetailsBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        val tvTitle = binding?.tvTitle
        val tvValue = binding?.tvValue
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DetailsVH) {
            val data = children[position]
            holder.tvTitle?.text = data.title
            holder.tvValue?.text = data.value
        }
    }
}
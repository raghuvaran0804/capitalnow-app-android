package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.databinding.ItemLimitBinding
import com.capitalnowapp.mobile.models.IdTextData

class LoanOptionsAdapter(private var list: ArrayList<IdTextData>) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var binding: ItemLimitBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemLimitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LimitVH(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LimitVH) {

            holder.cbLimit?.text = list[position].text
            holder.cbLimit?.isChecked = list[position].isChecked

            holder.cbLimit?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val tempList = list
                    for (limit in tempList.withIndex()) {
                        if (tempList[limit.index].isChecked) {
                            tempList[limit.index].isChecked = false
                            break
                        }
                    }
                    tempList[position].isChecked = true
                    this.list = tempList
                    notifyDataSetChanged()
                }else{
                    this.list[position].isChecked = false
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getSelectedItem(): IdTextData {
        for (item in list) {
            if (item.isChecked) {
                return item
            }
        }
        return IdTextData()
    }
}

class LimitVH(binding: ItemLimitBinding?) : RecyclerView.ViewHolder(binding!!.root) {
    val cbLimit = binding?.cbLimit
}

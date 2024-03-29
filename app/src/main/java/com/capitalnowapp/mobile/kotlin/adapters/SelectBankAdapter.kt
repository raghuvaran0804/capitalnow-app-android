package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.databinding.ItemSelectBankBinding
import com.capitalnowapp.mobile.models.BankList

class SelectBankAdapter (private var list: List<BankList>): RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var binding: ItemSelectBankBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemSelectBankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectBankVH(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SelectBankVH) {
            holder.tvBankName.text = list[position].cemBankName
            holder.tvAccountNo.text = list[position].cemBankAcId
            holder.rbBank?.isChecked = list[position].isChecked

            holder.rbBank.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val tempList = list
                    for (bankChange in tempList.withIndex()) {
                        if (tempList[bankChange.index].isChecked) {
                            tempList[bankChange.index].isChecked = false
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

    override fun getItemCount(): Int {
        return list.size
    }
    fun getSelectedItem(): BankList {
        for (item in list) {
            if (item.isChecked) {
                return item
            }
        }
        return BankList()
    }

    class SelectBankVH(binding: ItemSelectBankBinding?): RecyclerView.ViewHolder(binding!!.root) {
        val tvBankName = binding!!.tvBankName
        val tvAccountNo = binding!!.tvAccountNo
        val rbBank = binding!!.rbBank
    }
}
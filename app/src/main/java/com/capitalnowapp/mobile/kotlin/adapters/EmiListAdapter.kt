package com.capitalnowapp.mobile.kotlin.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.databinding.ItemCsActiveLoansInsBinding
import com.capitalnowapp.mobile.models.offerModel.CSInstallment

class EmiListAdapter(
    private var csEmiDetailsList: List<CSInstallment>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var binding: ItemCsActiveLoansInsBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding =
            ItemCsActiveLoansInsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmiDetailsVH(binding)
    }

    private inner class EmiDetailsVH(binding: ItemCsActiveLoansInsBinding?) :
        RecyclerView.ViewHolder(binding!!.root) {
        init {
            binding!!.root.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try{
            if(csEmiDetailsList.size >=0){
                binding?.tvEmiNo?.text = csEmiDetailsList[position].instalmentNo.toString()
                binding?.tvAmount?.text = "Rs. "+csEmiDetailsList[position].instalment.toString()
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return csEmiDetailsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
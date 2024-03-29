package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemCustomerIdBinding
import java.util.Locale

class CustomerIdAdapter(private val qcId: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var binding: ItemCustomerIdBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemCustomerIdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyIdVH(binding!!)
    }

    override fun getItemCount(): Int {
        return qcId!!.length
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyIdVH) {
            val value = qcId?.get(position)
            if (value.toString().toLowerCase(Locale.ROOT) == "c") {
                holder.ivId.setImageResource(R.drawable.c)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "n") {
                holder.ivId.setImageResource(R.drawable.n)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "0") {
                holder.ivId.setImageResource(R.drawable.two)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "1") {
                holder.ivId.setImageResource(R.drawable.one)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "2") {
                holder.ivId.setImageResource(R.drawable.two)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "3") {
                holder.ivId.setImageResource(R.drawable.three)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "4") {
                holder.ivId.setImageResource(R.drawable.four)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "5") {
                holder.ivId.setImageResource(R.drawable.five)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "6") {
                holder.ivId.setImageResource(R.drawable.six)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "7") {
                holder.ivId.setImageResource(R.drawable.seven)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "8") {
                holder.ivId.setImageResource(R.drawable.eight)
            } else if (value.toString().toLowerCase(Locale.ROOT) == "9") {
                holder.ivId.setImageResource(R.drawable.nine)
            }
        }
    }
}

class MyIdVH(binding: ItemCustomerIdBinding) : RecyclerView.ViewHolder(binding.root) {
    val ivId = binding.ivId
}

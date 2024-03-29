package com.capitalnowapp.mobile.kotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.databinding.ItemBankNameBinding
import com.capitalnowapp.mobile.kotlin.fragments.RequestBankChangeFragment

class BankNameAdapter(private var bankData: List<String>?, var listener: RequestBankChangeFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var fragment: Fragment? = null
    var context: Context? = null

    class BankNameVH(binding: ItemBankNameBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        val tvBankName = binding?.tvBankName
        val llBankItem = binding?.llBankItem
        val viewSelected = binding?.viewSelected
    }

    override fun getItemCount(): Int {
        return bankData!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BankNameVH) {
            holder.tvBankName?.text = bankData!![position]

            holder.llBankItem?.setOnClickListener {
                listener.onBankSelected(bankData!![position])
            }
        }
    }

    fun updateData(bankDataList: ArrayList<String>) {
        bankData = bankDataList
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val  binding=ItemBankNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return BankNameVH(binding)
    }
}
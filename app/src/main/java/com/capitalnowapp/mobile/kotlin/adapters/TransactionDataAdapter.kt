package com.capitalnowapp.mobile.kotlin.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemTransactionsBinding
import com.capitalnowapp.mobile.kotlin.fragments.TransactionsFragment
import com.capitalnowapp.mobile.models.TransactionData

class TransactionDataAdapter(
    private var transactionData: List<TransactionData>?,
    private var transactionsFragment: TransactionsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var finalTransactionData: TransactionData? = null
    private var binding: ItemTransactionsBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemTransactionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionVH(binding!!)
    }

    override fun getItemCount(): Int {
        return transactionData!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TransactionVH){
            finalTransactionData = transactionData!![position]
            val image = finalTransactionData?.image
            if(finalTransactionData?.image == null || finalTransactionData?.image == ""){
                if(finalTransactionData?.type == "cn"){
                    Glide.with(holder.itemView.context).load(R.drawable.cn_logo_transactions).into(holder.ivTranLogo!!)
                }else if (finalTransactionData?.type == "bbps"){
                    Glide.with(holder.itemView.context).load(R.drawable.bbps_logo).into(holder.ivTranLogo!!)
                }
            }else {
                Glide.with(holder.itemView.context).load(image).into(holder.ivTranLogo!!)
            }

            holder.tvTitle.text = finalTransactionData?.title
            holder.tvDescription.text = finalTransactionData?.description
            holder.tvAmount.text = "Rs. "+finalTransactionData?.amount.toString()
            holder.tvTransactionId.text = "Txn ID: "+finalTransactionData?.transactionId
            holder.tvDate.text = finalTransactionData?.updatedAt
        }

    }

    class TransactionVH(binding: ItemTransactionsBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivTranLogo = binding.ivTranLogo
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
        val tvAmount = binding.tvAmount
        val tvTransactionId = binding.tvTransactionId
        val tvDate = binding.tvDate
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}



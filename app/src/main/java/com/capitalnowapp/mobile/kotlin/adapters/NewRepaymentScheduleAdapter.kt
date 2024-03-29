package com.capitalnowapp.mobile.kotlin.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemRepaymentScheduleBinding
import com.capitalnowapp.mobile.kotlin.activities.NewApplyLoanActivity
import com.capitalnowapp.mobile.models.EligibleOfferDetailsInstalment
import com.capitalnowapp.mobile.models.GetEligibleOfferDetailsResponse


class NewRepaymentScheduleAdapter(
    private var instalments: GetEligibleOfferDetailsResponse?,
    private var newApplyLoanActivity: NewApplyLoanActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var totalInstallments: List<EligibleOfferDetailsInstalment>? = null
    private var installments: EligibleOfferDetailsInstalment? = null
    private var binding: ItemRepaymentScheduleBinding? = null
    var clickedPos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemRepaymentScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewRepaymentScheduleVH(binding!!)
    }

    override fun getItemCount(): Int {
        return instalments!!.data!!.instalments!!.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is NewRepaymentScheduleVH){
            totalInstallments = instalments?.data?.instalments
            installments = instalments!!.data!!.instalments!![position]
            holder.tvEmiAmount.text = installments?.emiAmount
            holder.tvEmiDate.text = installments?.dueDate
            if(instalments?.data?.discountMessage != null && instalments?.data?.discountMessage != ""){
                holder.tvEmiAmount.setTextColor(R.color.green)
                holder.tvAmount.paintFlags = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                holder.tvAmount.visibility = View.VISIBLE
                holder.tvAmount.text = installments?.emiAmount
                holder.tvEmiAmount.text = installments?.discountAmount

            }
            holder.ivDetails.setOnClickListener {

                    newApplyLoanActivity.showAmountBreakUp(instalments?.data?.instalments!![holder.absoluteAdapterPosition])
            }

        }
    }

    class NewRepaymentScheduleVH(binding: ItemRepaymentScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvEmiAmount = binding?.tvEmiAmount
        val tvEmiDate = binding?.tvEmiDate
        val tvAmount = binding?.tvAmount
        val ivDetails = binding?.ivDetails
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}



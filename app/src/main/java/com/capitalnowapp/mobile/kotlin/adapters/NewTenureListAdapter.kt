package com.capitalnowapp.mobile.kotlin.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemNewTenureListBinding
import com.capitalnowapp.mobile.kotlin.activities.NewApplyLoanActivity
import com.capitalnowapp.mobile.models.GetEligibleOffersLoanType


class NewTenureListAdapter(
    private val loanTypes: List<GetEligibleOffersLoanType>?,
    private val newApplyLoanActivity: NewApplyLoanActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var selectedloantype: String? = null
    var amount: Int? = -1
    private var loanType: GetEligibleOffersLoanType? = null
    private var binding: ItemNewTenureListBinding? = null
    var clickedPos = -1
    var utcId = -1
    private var fromClick = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding =
            ItemNewTenureListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewTenureListVH(binding!!)
    }

    override fun getItemCount(): Int {
        return loanTypes?.size!!
    }

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewTenureListVH) {
            loanType = loanTypes!![position]

            holder.tvEmiAmount.text = loanType!!.emiAmount
            holder.tvEmiName.text = loanType!!.emiNumberText
            //holder.rbOption.isChecked = loanTypes.isLocked!!

            holder.rbOptions.setOnCheckedChangeListener { _, isChecked ->

            }



            holder.llMain.setOnClickListener {
                fromClick = true
                clickedPos = holder.absoluteAdapterPosition
                for (lt in loanTypes.withIndex()) {
                    loanTypes[lt.index].checked = lt.index == clickedPos
                }
                notifyDataSetChanged()
                //newApplyLoanActivity.etReferralCode.text!!.clear()
            }

            if (!fromClick && loanType?.isChecked == "1") {
                loanTypes[position].checked = true
            }

            if (loanType!!.isLocked!!) {
                holder.rbOptions.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.lock_primary2)
                holder.llMain.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.cb_input_gray)
                holder.tvEmiAmount.setTextColor(R.color.cb_input_gray)
                holder.tvEmiName.setTextColor(R.color.cb_input_gray)
                holder.rbOptions.visibility = View.GONE
                holder.ivLocked.visibility = View.VISIBLE
                holder.llMain.isEnabled = false
                holder.llMain.isClickable = false
            } else {
                holder.llMain.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.light_gray)
                holder.rbOptions.visibility = View.VISIBLE
                holder.ivLocked.visibility = View.GONE
            }
            if (loanType!!.checked == true) {
                utcId = loanType!!.uctId!!
                amount = loanType!!.emiAmount?.toInt()
                selectedloantype = loanType!!.loanType
                holder.rbOptions.isChecked = true
                holder.llMain.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.Primary2)
                newApplyLoanActivity.getEligibleOfferDetails()
            } else {
                holder.rbOptions.isChecked = false
                holder.llMain.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.light_gray)
            }

        }
    }


    class NewTenureListVH(binding: ItemNewTenureListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rbOptions = binding.rbOptions
        val tvEmiAmount = binding.tvEmiAmount
        val tvEmiName = binding.tvEmiName
        val llMain = binding.llMain
        val ivLocked = binding.ivLocked

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

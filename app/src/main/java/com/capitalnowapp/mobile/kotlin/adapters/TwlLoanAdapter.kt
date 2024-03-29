package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemTwlLoanBinding
import com.capitalnowapp.mobile.kotlin.fragments.TwlActiveLoansFragment
import com.capitalnowapp.mobile.models.TwlAmtPayable

class TwlLoanAdapter(
    private var twlAmtPayable: List<TwlAmtPayable>,
    private var twlActiveLoansFragment: TwlActiveLoansFragment,
    private var lid: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: ItemTwlLoanBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemTwlLoanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TwlEmiLoanVH(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            when (twlAmtPayable[position].dueType) {
                1 -> {
                    binding!!.llPreclosuer.background = ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.ic_preclosure_bg
                    )
                    binding!!.tvTitle.text = twlAmtPayable[position].title
                    Glide.with(holder.itemView.context).load(twlAmtPayable[position].recommendedImg)
                        .into(binding!!.ivNocharges)
                    binding!!.tvAmount.text = "Rs. "+twlAmtPayable[position].dueAmount.toString()
                    binding!!.tvPrePay.background = ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.rectangle_border_rounded
                    )
                    binding!!.tvPrePay.background = ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.rounded_corner_gradient_green
                    )
                    binding!!.tvPrePay.setPadding(20,20,20,20)
                    binding!!.tvPrePay.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.white
                        )
                    )
                    binding!!.ivNocharges.visibility = View.VISIBLE
                }
                2 -> {
                    binding!!.llPreclosuer.background = ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.ic_twl_whitebg
                    )
                    binding!!.tvTitle.text = twlAmtPayable[position].title
                    binding!!.tvDueText.text = twlAmtPayable[position].dueText
                    binding!!.tvDueText.setTextColor(ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.dark_gray
                    ))
                    binding!!.tvAmount.text = "Rs."+twlAmtPayable[position].dueAmount.toString()
                    binding!!.tvAmount.setTextColor(ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.Primary2
                    ))
                    binding!!.tvPrePay.background = ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.rounded_corner_gradient
                    )
                    binding!!.tvPrePay.setPadding(20,20,20,20)
                    binding!!.tvPrePay.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.white
                        )
                    )
                    binding!!.ivNocharges.visibility = View.GONE

                }
                3 -> {

                    binding!!.llPreclosuer.background =
                        ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_overdue_bg)
                    binding!!.tvTitle.text = twlAmtPayable[position].title
                    binding!!.tvDueText.text = twlAmtPayable[position].dueText
                    binding!!.tvDueText.setTextColor(ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.dark_gray
                    ))
                    binding!!.tvAmount.text = "Rs."+twlAmtPayable[position].dueAmount.toString()
                    binding!!.tvAmount.setTextColor(ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.cb_errorRed
                    ))
                    binding!!.tvPrePay.background = ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.rectangle_border_rounded_red
                    )
                    binding!!.tvPrePay.setPadding(20,20,20,20)
                    binding!!.tvPrePay.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.cb_errorRed
                        )
                    )
                }
            }
            binding!!.tvPrePay.setOnClickListener {
                twlAmtPayable[holder.absoluteAdapterPosition].lid = this.lid
                twlActiveLoansFragment.callPayment(twlAmtPayable[holder.absoluteAdapterPosition])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private inner class TwlEmiLoanVH(binding: ItemTwlLoanBinding?) :
        RecyclerView.ViewHolder(binding!!.root) {
        init {
            binding!!.root.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun getItemCount(): Int {
        return twlAmtPayable.size
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}



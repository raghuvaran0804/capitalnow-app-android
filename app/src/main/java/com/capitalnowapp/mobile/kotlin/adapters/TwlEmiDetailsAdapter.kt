package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemTwlEmiDetailsBinding
import com.capitalnowapp.mobile.kotlin.activities.TwlEmiDetailsActivity
import com.capitalnowapp.mobile.models.TwlEmiDetailsList

class TwlEmiDetailsAdapter(
    private var twlEmiDetailsList: List<TwlEmiDetailsList>,
    private var twlEmiDetailsActivity: TwlEmiDetailsActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: ItemTwlEmiDetailsBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding =
            ItemTwlEmiDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TwlEmiDetailsVH(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {

            if (twlEmiDetailsList[position].insStatusId == 20) {
                binding!!.cvEmi.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.cardEmiPaidBg
                    )
                )
                binding!!.tvDueDateText.text = twlEmiDetailsList[position].title
                binding!!.tvEmiDueDate.text = twlEmiDetailsList[position].insDueDate
                binding!!.tvPaidDate.text = twlEmiDetailsList[position].paidText
                binding!!.tvAmount.text = "Rs."+twlEmiDetailsList[position].insDueAmount.toString()
                binding!!.ivPaid.visibility = View.VISIBLE
                binding!!.llStatus.visibility = View.GONE
                binding!!.tvEnach.visibility = View.GONE
                binding!!.llEnach.visibility = View.GONE

            } else if (twlEmiDetailsList[position].insStatusId != 20) {
                binding!!.cvEmi.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.cardEmiBg
                    )
                )
                binding!!.tvDueDateText.text = twlEmiDetailsList[position].title
                binding!!.tvEmiDueDate.text = twlEmiDetailsList[position].insDueDate
                binding!!.tvPaidDate.text = twlEmiDetailsList[position].insRepayDate
                binding!!.tvAmount.text = "Rs."+twlEmiDetailsList[position].insDueAmount.toString()
                binding!!.tvStatus.text = twlEmiDetailsList[position].insStatus
                binding!!.tvEnach.text = twlEmiDetailsList[position].enachText
                binding!!.rvPaidDate.visibility = View.GONE
                binding!!.ivPaid.visibility = View.GONE
                binding!!.tvEnach.visibility = View.VISIBLE
                binding!!.llEnach.visibility = View.VISIBLE
            }
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return twlEmiDetailsList.size
    }

    private inner class TwlEmiDetailsVH(binding: ItemTwlEmiDetailsBinding?) :
        RecyclerView.ViewHolder(binding!!.root) {
        init {
            binding!!.root.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
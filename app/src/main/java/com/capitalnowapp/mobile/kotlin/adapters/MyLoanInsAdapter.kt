package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.databinding.ItemLoansSheetBinding
import com.capitalnowapp.mobile.interfaces.SelectedToPayCallback
import com.capitalnowapp.mobile.kotlin.fragments.ActiveLoansFragment
import com.capitalnowapp.mobile.kotlin.fragments.LoanBottomSheetFragment
import com.capitalnowapp.mobile.models.loan.AmtPayable
import com.capitalnowapp.mobile.models.loan.LoansToPay
import com.capitalnowapp.mobile.util.TrackingUtil
import org.json.JSONException
import org.json.JSONObject

class MyLoanInsAdapter(private val loanToPay: LoansToPay,private val activity: BaseActivity, private val activeLoansFragment: ActiveLoansFragment, private val loanBottomSheetFragment: LoanBottomSheetFragment, private val amtPayable: AmtPayable?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var binding: ItemLoansSheetBinding? = null
    private var callBack: SelectedToPayCallback? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemLoansSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyInsVH(binding!!)
    }


    override fun getItemCount(): Int {
        return if (amtPayable != null) {
            1
        } else {
            loanToPay.amtPayable?.size!!
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyInsVH) {
            val amtPay: AmtPayable = amtPayable ?: loanToPay.amtPayable!![position]
            if (amtPay.isRecommended == true) {
                holder.tvPayLess1?.text = amtPay.recommendedText

                holder.frameIns?.root?.visibility = GONE

                holder.tvTotalAmount1?.visibility = VISIBLE
                holder.rlRec?.visibility = VISIBLE

                holder.rlIns?.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.Accent1))
                holder.tvTotalTitle?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.tvTotalAmount?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.tvTotalAmount1?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.Secondary1))
                Glide.with(holder.itemView.context).load(R.raw.do_svg).into(holder.ivDot!!)
            } else {
                holder.frameIns?.root?.visibility = VISIBLE
                holder.frameIns?.llData?.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.Secondary1)
                holder.rlRec?.visibility = GONE

                holder.tvTotalAmount1?.visibility = GONE
                holder.tvTitle1?.visibility = GONE
                holder.tvPayLess1?.visibility = GONE
                holder.frameIns?.tvPayLess?.visibility = GONE

                holder.rlIns?.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.rlIns?.setBackground(ContextCompat.getDrawable(holder.itemView.context, R.drawable.just_corners))
                holder.tvTotalTitle?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                holder.tvTotalAmount?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                holder.viewDivider?.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.dark_gray))
                holder.frameIns?.tvAmount?.setTextColor(ContextCompat.getColorStateList(holder.itemView.context, R.color.cb_heading_color))
                holder.frameIns?.tvTitle?.setTextColor(ContextCompat.getColorStateList(holder.itemView.context, R.color.cb_heading_color))

                holder.frameIns?.tvPayLess?.text = amtPay.recommendedText
                holder.frameIns?.tvTitle?.text = amtPay.title
                holder.frameIns?.tvAmount?.text = holder.itemView.context.getString(R.string.indian_currency) + " " + amtPay.dueAmount?.toInt().toString()
                holder.frameIns?.tvDueDate?.text = "Due Date " + amtPay.dueDate
            }

            holder.tvTitle1?.text = amtPay.title
            holder.tvTitle1?.text = amtPay.title
            holder.tvTotalAmount1?.text = holder.itemView.context.getString(R.string.indian_currency) + " " + amtPay.dueAmount?.toString()
            holder.tvTotalAmount?.text = amtPay.dueAmount?.toString()

            holder.rvBreakUp?.layoutManager = LinearLayoutManager(holder.itemView.context)
            val adapter = SheetBreakUpAdapter(amtPay.priceBreakup, amtPay.isRecommended)
            holder.rvBreakUp?.adapter = adapter

            if (amtPay.isSelected == true) {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put("interaction type","UNSELECT Clicked")
                    obj.put("CNLID",loanToPay.lid)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, "ViewDetailsBottomSheetInteracted")


                holder.tvSelect?.text = "UNSELECT"
                if (amtPay.isRecommended == true) {
                    holder.tvSelect?.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.color_accent)
                    holder.tvSelect?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                } else {
                    holder.tvSelect?.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.color_accent)
                    holder.tvSelect?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                }
            } else {
                holder.tvSelect?.text = "SELECT"

                if (amtPay.isRecommended == true) {
                    //  holder.tvSelect?.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.apply_now_bg)
                    holder.tvSelect?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.color_accent))
                } else {
                    holder.tvSelect?.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGrey)
                    holder.tvSelect?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                }
            }

            holder.tvSelect?.setOnClickListener {
                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put("interaction type","SELECT Clicked")
                    obj.put("CNLID",loanToPay.lid)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, "ViewDetailsBottomSheetInteracted")

                callBack = activeLoansFragment
                (callBack as ActiveLoansFragment).selectedObj(amtPay, loanToPay)
                loanBottomSheetFragment.dismiss()
            }

        }
    }
}

class MyInsVH(binding: ItemLoansSheetBinding?) : RecyclerView.ViewHolder(binding!!.root) {

    val rvBreakUp = binding?.rvBreakUp
    val tvTotalAmount1 = binding?.tvTotalAmount1
    val tvPayLess1 = binding?.tvPayLess1
    val tvTitle1 = binding?.tvTitle1
    val rlIns = binding?.rlIns
    val tvTotalAmount = binding?.tvTotalAmount
    val tvTotalTitle = binding?.tvTotalTitle
    val viewDivider = binding?.viewDivider

    val frameIns = binding?.frameIns
    val tvSelect = binding?.tvSelect
    val rlRec = binding?.rlRec
    val ivDot = binding?.ivDot
}

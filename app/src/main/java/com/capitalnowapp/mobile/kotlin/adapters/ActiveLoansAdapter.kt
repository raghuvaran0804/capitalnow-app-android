package com.capitalnowapp.mobile.kotlin.adapters

import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.databinding.ItemActiveLoansBinding
import com.capitalnowapp.mobile.kotlin.fragments.ActiveLoansFragment
import com.capitalnowapp.mobile.kotlin.fragments.LoanBottomSheetFragment
import com.capitalnowapp.mobile.models.loan.LoansToPay
import com.capitalnowapp.mobile.util.TrackingUtil
import org.json.JSONException
import org.json.JSONObject


class ActiveLoansAdapter(
    private var loansToPay: List<LoansToPay>,
    private val activity: BaseActivity,
    private val activeLoansFragment: ActiveLoansFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var mListener: LoanSelectionListener? = null
    private var binding: ItemActiveLoansBinding? = null
    private var rvInsMain: ArrayList<RecyclerView>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemActiveLoansBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoansVH(binding)
    }

    interface LoanSelectionListener {
        fun onItemClick()
    }

    fun setOnItemClickListener(listener: LoanSelectionListener) {
        mListener = listener
    }

    fun setMyRazorpayLoanData(LoansToPay: List<LoansToPay>) {
        this.loansToPay = LoansToPay
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoansVH) {
            val loanToPay = loansToPay[position]
            if (position == 0){
                holder.view?.visibility = GONE
            }
            //holder.tvInfo?.paintFlags =
                //holder.tvInfo?.paintFlags!! or android.graphics.Paint.UNDERLINE_TEXT_FLAG
            holder.tvIssueDate?.text = loanToPay.loanIssueDate + "(" + loanToPay.loanExDays + ")"
            holder.tvLoanId?.text = loanToPay.qclId

            if (loanToPay.amtPaid != null && loanToPay.amtPaid!!.amount!! > 0) {
                holder.tvPart?.visibility = VISIBLE
                holder.tvPart?.text = loanToPay.amtPaid!!.title + " " + loanToPay.amtPaid!!.amount
            } else {
                holder.tvPart?.visibility = GONE
            }

            if (loanToPay.amtPayable != null && loanToPay.amtPayable!!.isNotEmpty()) {
                val layoutManager = LinearLayoutManager(
                    holder.itemView.context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                //val layoutManager = GridLayoutManager(holder.itemView.context, 3)
                //val layoutManager = GridLayoutManager(holder.itemView.context, 2, GridLayoutManager.HORIZONTAL, false)
                holder.rvIns!!.layoutManager = layoutManager
                val displayMetrics = DisplayMetrics()
                activity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
                val adapter = ActiveLoanInsAdapter(
                    loanToPay.amtPayable!!,
                    activeLoansFragment,
                    loanToPay,
                    displayMetrics.widthPixels,
                    this,
                    holder.bindingAdapterPosition
                )
                holder.rvIns.adapter = adapter
                rvInsMain?.add(holder.rvIns)
            }

            for (amt in loanToPay.amtPayable!!.withIndex()) {
                if (amt.value.priceBreakup!!.isNotEmpty()) {
                    holder.tvInfo?.visibility = VISIBLE
                } else {
                    holder.tvInfo?.visibility = INVISIBLE
                }
            }

            holder.tvInfo?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put("CNLID",loanToPay.lid)
                    obj.put("interaction type","View Details Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, "ActivePersonalLoansPageInteracted")

                openSheet(loanToPay)
            }

            /* holder.tvSelect?.setOnClickListener {
                 val selected = getRazorPayLoanData(position)?.isSelected
                 if (selected!!) {
                     holder.tvSelect.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.intro_page_body_color)
                     holder.llItem?.background = null
                     holder.tvSelect.visibility = View.VISIBLE
                     holder.tvSelect.text = "SELECT"
                     razorPayLoanData.isSelected = false
                 } else {
                     holder.llItem?.background = ContextCompat.getDrawable(activity, R.drawable.loan_selected_bg)
                     holder.tvSelect.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.green)
                     razorPayLoanData.isSelected = true
                     holder.tvSelect.text = "UN SELECT"
                 }
                 mListener?.onItemClick()
             }*/
        }
    }

    private fun openSheet(loanToPay: LoansToPay) {
        val fragment = LoanBottomSheetFragment(loanToPay, activeLoansFragment, null)
        fragment.show(activity.supportFragmentManager, "TAG")
    }

    override fun getItemCount(): Int {
        return loansToPay.size
    }


    fun getRazorPayLoanData(position: Int): LoansToPay? {
        return if (loansToPay.isNotEmpty()) {
            loansToPay[position]
        } else null
    }

    private inner class LoansVH(binding: ItemActiveLoansBinding?) :
        RecyclerView.ViewHolder(binding!!.root) {
        init {
            binding!!.root.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val tvInfo = binding?.tvInfo
        val tvLoanId = binding?.tvLoanId
        val tvIssueDate = binding?.tvIssueDate
        val tvPart = binding?.tvPart
        val rvIns = binding?.rvIns
        val llItem = binding?.llItem
        val view = binding?.view
    }

    fun getSelectedLoansList(): ArrayList<LoansToPay> {
        val selectedList = java.util.ArrayList<LoansToPay>()
        if (loansToPay.isNotEmpty()) {
            for (adapterPosition in loansToPay.indices) {
                /*if (loansToPay[adapterPosition].isSelected) {
                    selectedList.add(loansToPay[adapterPosition])
                }*/
            }
        }
        return selectedList
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateData(razorPayLoanData: List<LoansToPay>) {

    }

    fun setSelection(position: Int, bindingAdapterPosition: Int) {
        try {
            if (rvInsMain?.size!! > 0) {
                val rvIns = rvInsMain!![position]
                Handler().postDelayed({ rvIns.scrollToPosition(bindingAdapterPosition) }, 5)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
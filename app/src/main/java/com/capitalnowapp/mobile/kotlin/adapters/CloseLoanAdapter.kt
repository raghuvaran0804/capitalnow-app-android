package com.capitalnowapp.mobile.kotlin.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.ItemCloseLoansBinding
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.models.loan.LoanHistoryDatum
import com.capitalnowapp.mobile.util.Utility
import kotlinx.android.synthetic.main.apay_closed_loans.view.ivOnlyApay
import kotlinx.android.synthetic.main.apay_closed_loans.view.ivOnlyBank
import kotlinx.android.synthetic.main.apay_closed_loans.view.llApay
import kotlinx.android.synthetic.main.apay_closed_loans.view.llBank
import kotlinx.android.synthetic.main.apay_closed_loans.view.tvAmazonAmount
import kotlinx.android.synthetic.main.apay_closed_loans.view.tvBankAmount
import kotlin.math.floor
import kotlin.math.roundToInt

class CloseLoanAdapter(private var closedLoansDataList: List<LoanHistoryDatum>?) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var binding: ItemCloseLoansBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemCloseLoansBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CloseLoansVH(binding)
    }

    private inner class CloseLoansVH(binding: ItemCloseLoansBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        init {
            binding!!.root.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    fun setClosedLoanData(closedLoansDataList: List<LoanHistoryDatum>?) {
        this.closedLoansDataList = closedLoansDataList
    }

    override fun getItemCount(): Int {
        return closedLoansDataList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val closedLoanData: LoanHistoryDatum = closedLoansDataList!!.get(position)
        val loanIssuedDate = Utility.formatDate(Utility.convertStringToDate(closedLoanData.cloanIssueDate, Constants.SERVER_DATE_FORMAT), Constants.LOAN_CLOSED_DATE_DISPLAY_FORMAT)
        val loanClosedDate = Utility.formatDate(Utility.convertStringToDate(closedLoanData.cloanActualRepayDate, Constants.SERVER_DATE_FORMAT), Constants.LOAN_CLOSED_DATE_DISPLAY_FORMAT)

        binding?.tvQcCloseLoanId?.text = closedLoanData.cloanSequenceId
        binding?.txRupeesLoan!!.text = String.format("Rs %,d", Math.round(closedLoanData.cloanTotal!!.toFloat()))
        binding?.tvIssueDate!!.text = "$loanIssuedDate - $loanClosedDate"
        binding?.txTenureDays!!.text = String.format("%d Days", closedLoanData.cloanExhaustedDays!!.toInt())

        binding?.tvInfo?.paintFlags = binding?.tvInfo?.paintFlags!! or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        if (closedLoanData.rePoints != null && !closedLoanData.rePoints.equals("0")) {
            binding?.llRewards?.visibility = VISIBLE
            binding?.ivRewardIcon?.visibility = VISIBLE
            binding?.txRewardPoints?.text = closedLoanData.rePoints
        } else {
            binding?.llRewards?.visibility = GONE
            binding?.ivRewardIcon?.visibility = GONE
        }


        binding?.tvInfo?.setOnClickListener {
            showBreakup(closedLoanData, holder.itemView.context)
        }
    }

    private fun showBreakup(data: LoanHistoryDatum, context: Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_myloans_amount_info)
        val window = dialog.window
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 20)
        window!!.setBackgroundDrawable(inset)
        val width = (context.resources.displayMetrics.widthPixels * 0.75).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.30).toInt()
        dialog.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        val tvLoanId = dialog.findViewById<TextView>(R.id.tvLoanId)
        val tvAmount = dialog.findViewById<TextView>(R.id.tvAmount)
        val tvInterest = dialog.findViewById<TextView>(R.id.tvInterest)
        val tvFee = dialog.findViewById<TextView>(R.id.tvFee)
        val tvTotal = dialog.findViewById<TextView>(R.id.tvTotal)
        val ivCLose = dialog.findViewById<ImageView>(R.id.ivCLose)
        val llType = dialog.findViewById<LinearLayout>(R.id.llType)

        val tvDiscount = dialog.findViewById<TextView>(R.id.tvDiscount)
        val rlDiscount = dialog.findViewById<RelativeLayout>(R.id.rlDiscount)

        if (data.loandetails!!.cashback!! > 0) {
            rlDiscount.visibility = View.VISIBLE
            tvDiscount.text = "-${data.loandetails!!.cashback}"
        } else {
            rlDiscount.visibility = GONE
        }

        tvLoanId.text = data.cloanSequenceId
        tvAmount.text = data.loandetails!!.loanAmount.toString()
        tvInterest.text = data.loandetails!!.loanInterest.toString()
        tvFee.text = data.loandetails!!.loanFee.toString()
        tvTotal.text = floor(data.cloanTotal!!.toDouble()).toInt().toString()

        ivCLose.setOnClickListener {
            dialog.dismiss()
        }

        when (data.cloanIssueType) {
            AppConstants.LoanTypes.BankTransfer -> {
                llType?.llApay?.visibility = GONE
                llType?.llBank?.visibility = GONE
                llType?.ivOnlyApay?.visibility = GONE
                llType?.ivOnlyBank?.visibility = View.VISIBLE
            }
            AppConstants.LoanTypes.APayTransfer -> {
                llType?.llApay?.visibility = GONE
                llType?.llBank?.visibility = GONE
                llType?.ivOnlyApay?.visibility = View.VISIBLE
                llType?.ivOnlyBank?.visibility = GONE
            }
            AppConstants.LoanTypes.BankApay -> {
                llType?.tvBankAmount?.text = String.format("%,d", data.cloanIssueBankAmount!!.toFloat().roundToInt())
                llType?.tvAmazonAmount?.text = String.format("%,d", data.cloanIssueApayAmount!!.toFloat().roundToInt())

                llType?.llApay?.visibility = View.VISIBLE
                llType?.llBank?.visibility = View.VISIBLE
                llType?.ivOnlyApay?.visibility = GONE
                llType?.ivOnlyBank?.visibility = GONE
            }
        }

        dialog.show()
    }
}
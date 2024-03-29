package com.capitalnowapp.mobile.kotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.LoanInstallmentAdapterBinding
import com.capitalnowapp.mobile.kotlin.fragments.VehicleApplyLoanFragment
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.models.loan.InstalmentData
import java.util.Calendar
import kotlin.math.roundToInt

class VehicleLoanInstallmentAdapter(
    private var instalments: List<InstalmentData>?,
    private val amount: Int,
    private val width: Int,
    private val type: String,
    private val vehicleApplyLoanFragment: VehicleApplyLoanFragment,
    private val startMonth: Int,
    private val startDateInAMonth: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var instalmentsWithValues: ArrayList<InstalmentData>? = ArrayList()
    var context: Context? = null
    //private var offerList: List<Int> = ArrayList()

    class EmiVH(binding: LoanInstallmentAdapterBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        val tvBorrower = binding?.tvBorrower
        val tvInterest = binding?.tvInterest
        //val tvInterestOffer = binding?.tvInterestOffer
        val tvTotalAmount = binding?.tvTotalAmount
        val tvInstallmentTitle = binding?.tvInstallmentTitle
        val tvProcessAmount = binding?.tvProcessAmount
        val tvDueDate = binding?.tvDueDate
    }

    override fun getItemCount(): Int {
        return instalments!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EmiVH) {
            try {
                if (position == 0) {
                    setData(position)
                }
                val installment = instalments!![position]
                if (installment.title != null && installment.title!!.isNotEmpty()) {
                    holder.tvInstallmentTitle?.visibility = VISIBLE
                    holder.tvInstallmentTitle?.text = installment.title
                } else {
                    holder.tvInstallmentTitle?.visibility = GONE
                }
                val installmentAmount = amount * installment.barrowAmount!!
                holder.tvBorrower?.text = (installmentAmount).roundToInt().toString()
                var interest = 0
                if (type == AppConstants.LoanEMITypes.EMI) {
                    interest =
                        ((amount * installment.interestFee!! * installment.tenureDays!!).div(30)).roundToInt()
                    holder.tvDueDate?.text = getDueDate(installment.due_days!!.toInt(), position)
                } else {
                    interest = ((amount * installment.interestFee!! * 30).div(30)).roundToInt()
                    if (vehicleApplyLoanFragment.tenureDays >= 0) {
                        holder.tvDueDate?.text =
                            getDueDate(vehicleApplyLoanFragment.tenureDays, position)
                    }
                }
                if (installment.processingFee!! > 0) {
                    holder.tvProcessAmount?.visibility = GONE
                    holder.tvProcessAmount?.text = String.format(
                        context!!.getString(R.string.apply_loan_processing_fee_title),
                        (amount * installment.processingFee!!).roundToInt()
                    )
                } else {
                    holder.tvProcessAmount?.visibility = GONE
                }
                if (interest >= 0) {
                    holder.tvInterest?.text = (interest.toString())
                    holder.tvTotalAmount?.text = ((installmentAmount.roundToInt() + interest).toString())
                } else {
                    holder.tvInterest?.text = "0"
                    holder.tvTotalAmount?.text = ((installmentAmount.roundToInt()).toString())
                }

                /*if (offerList.size!! > 0) {
                    holder.tvInterestOffer?.paintFlags = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    holder.tvInterest?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
                    holder.tvInterestOffer?.text = (interest.toString())
                    holder.tvInterestOffer?.visibility = VISIBLE
                    holder.tvInterest?.text = (interest - (offerList[holder.bindingAdapterPosition])).toString()
                    holder.tvTotalAmount?.text =
                        ((installmentAmount + offerList[holder.bindingAdapterPosition]!!) - interest).toInt().toString()
                } else {
                    holder.tvInterestOffer?.visibility = View.INVISIBLE
                    holder.tvInterest?.text = (interest.toString())
                    holder.tvTotalAmount?.text =
                        ((installmentAmount + interest).toInt().toString())
                }*/

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setData(position: Int) {
        for ((index, ins) in instalments!!.withIndex()) {
            val installmentWithValue = InstalmentData()
            var interest = 0

            if (type == AppConstants.LoanEMITypes.EMI) {
                installmentWithValue.installmentDueDate =
                    getDueDate(ins.due_days!!.toInt(), position)
                interest = ((amount * ins.interestFee!! * ins.tenureDays!!).div(30)).roundToInt()
            } else {
                installmentWithValue.installmentDueDate =
                    getDueDate(vehicleApplyLoanFragment.tenureDays, position)
                interest = ((amount * ins.interestFee!! * 30).div(30)).roundToInt()
            }
            val installmentAmount = amount * ins.barrowAmount!!

            installmentWithValue.interestFee = interest.toDouble()
            installmentWithValue.barrowAmount = installmentAmount.toInt().toDouble()
            installmentWithValue.processingFee = ins.processingFee!!
            instalmentsWithValues?.add(installmentWithValue)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = LoanInstallmentAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        context = parent.context

        if (type == AppConstants.LoanEMITypes.EMI) {
            val width: Int = width
            val params: ViewGroup.LayoutParams = binding.root.layoutParams
            params.width = (width * 0.85).toInt()
            binding.root.layoutParams = params
        } else {
            val params: ViewGroup.LayoutParams = binding.root.layoutParams
            params.width = (width * 0.975).toInt()
            binding.root.layoutParams = params
        }

        return EmiVH(binding)
    }

    fun updateList(instalments: List<InstalmentData>) {
        this.instalments = instalments
        instalmentsWithValues = ArrayList()
        notifyDataSetChanged()
    }

    private fun getDueDate(days: Int, position: Int): String {
        var startYear = Calendar.getInstance()[Calendar.YEAR]
        var startDate = startDateInAMonth.toString()
        if (startDate.length == 1) {
            startDate = "0$startDate"
        }
        var startMonth = startMonth.plus(position)
        if (startMonth in 13..24) {
            startMonth = startMonth.minus(12)
            startYear = startYear.plus(1)
        } else if (startMonth >= 25) {
            startMonth = startMonth.minus(24)
            startYear = startYear.plus(2)
        }
        var startMonth1 = startMonth.toString()
        if (startMonth1.length == 1) {
            startMonth1 = "0$startMonth1"
        }
        return "$startDate/$startMonth1/$startYear"
    }

    fun getSelectedPosition(): ArrayList<InstalmentData>? {
        var instalmentsWithValues1 = ArrayList<InstalmentData>()

        for (data in instalmentsWithValues?.withIndex()!!){
            var insData = instalmentsWithValues!!.get(data.index)
            insData.installmentDueDate = getDueDate(0, data.index)
        instalmentsWithValues1.add(insData)
        }


        return instalmentsWithValues1
    }

    /*public fun setOfferAmountList(list :List<Int>){
        this.offerList = list
    }*/
}
package com.capitalnowapp.mobile.kotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.LoanInstallmentAdapterBinding
import com.capitalnowapp.mobile.kotlin.fragments.ApplyLoanEMIFragment
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.models.loan.InstalmentData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class LoanInstallmentAdapter(
    private var instalments: List<InstalmentData>?,
    private val amount: Int,
    private val width: Int,
    private val type: String,
    private val applyLoanEMIFragment: ApplyLoanEMIFragment


) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var instalmentsWithValues: ArrayList<InstalmentData>? = ArrayList()
    var context: Context? = null
    private var offerList: List<Int> = ArrayList()

    class EmiVH(binding: LoanInstallmentAdapterBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        val tvBorrower = binding?.tvBorrower
        val tvInterest = binding?.tvInterest
        val tvInterestOffer = binding?.tvInterestOffer
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
                    setData()
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

                if (applyLoanEMIFragment.applyLoanData?.irc == 1) {
                    if (type == AppConstants.LoanEMITypes.EMI) {
                        val rateOfInterest = getRateOfInterest(installment.tenureDays!!.toInt())
                        interest =
                            (installment.tenureDays!!.toInt() * amount * rateOfInterest.div(100)).roundToInt()
                        holder.tvDueDate?.text = getDueDate(installment.due_days!!.toInt())
                    } else {
                        val rateOfInterest = getRateOfInterest(applyLoanEMIFragment.tenureDays)
                        interest = (30 * amount * rateOfInterest.div(100)).roundToInt()
                        if (applyLoanEMIFragment.tenureDays >= 0) {
                            holder.tvDueDate?.text = getDueDate(applyLoanEMIFragment.tenureDays)
                        }
                    }
                    val pCharges =
                        applyLoanEMIFragment.applyLoanData!!.userInterestCharges.processing_charges.toFloat()
                            .toInt()
                    if (pCharges > 0) {
                        holder.tvProcessAmount?.visibility = GONE
                        if (amount > 5000) {
                            holder.tvProcessAmount?.text = String.format(
                                context!!.getString(R.string.apply_loan_processing_fee_title),
                                pCharges
                            )
                        } else {
                            if (pCharges > 100) holder.tvProcessAmount?.text = String.format(
                                context!!.getString(R.string.apply_loan_processing_fee_title),
                                pCharges / 2
                            ) else holder.tvProcessAmount?.text = String.format(
                                context!!.getString(R.string.apply_loan_processing_fee_title),
                                pCharges
                            )
                        }
                    } else {
                        holder.tvProcessAmount?.visibility = GONE
                    }
                } else {
                    if (type == AppConstants.LoanEMITypes.EMI) {
                        interest =
                            ((amount * installment.interestFee!! * installment.tenureDays!!).div(30)).roundToInt()
                        holder.tvDueDate?.text = getDueDate(installment.due_days!!.toInt())
                    } else {
                        interest = ((amount * installment.interestFee!! * 30).div(30)).roundToInt()
                        if (applyLoanEMIFragment.tenureDays >= 0) {
                            holder.tvDueDate?.text = getDueDate(applyLoanEMIFragment.tenureDays)
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
                }
                if (interest >= 0) {
                    if (offerList.size!! > 0) {
                        holder.tvInterestOffer?.paintFlags = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                        holder.tvInterest?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
                        holder.tvInterestOffer?.text = (interest.toString())
                        holder.tvInterestOffer?.visibility = VISIBLE
                        holder.tvInterest?.text = (interest - (offerList[holder.bindingAdapterPosition])).toString()
                        holder.tvTotalAmount?.text =
                            (installmentAmount +(interest -(offerList[holder.bindingAdapterPosition]!!))).roundToInt().toString()
                    } else {
                        holder.tvInterestOffer?.visibility = INVISIBLE
                        holder.tvInterest?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                        holder.tvInterest?.text = (interest.toString())
                        holder.tvTotalAmount?.text =
                            ((installmentAmount + interest).roundToInt().toString())
                    }
                } else {
                    holder.tvInterest?.text = "0"
                    holder.tvTotalAmount?.text = ((installmentAmount).toInt().toString())
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setData() {
        for ((index, ins) in instalments!!.withIndex()) {
            val installmentWithValue = InstalmentData()
            var interest = 0;

            if (applyLoanEMIFragment.applyLoanData?.irc == 1) {
                if (type == AppConstants.LoanEMITypes.EMI) {
                    installmentWithValue.installmentDueDate = getDueDate(ins.due_days!!.toInt())
                    val rateOfInterest = getRateOfInterest(ins.tenureDays!!.toInt())
                    interest =
                        (ins.tenureDays!!.toInt() * amount * rateOfInterest.div(100)).roundToInt()
                } else {
                    installmentWithValue.installmentDueDate =
                        getDueDate(applyLoanEMIFragment.tenureDays)
                    val rateOfInterest = getRateOfInterest(applyLoanEMIFragment.tenureDays)
                    interest = (30 * amount * rateOfInterest.div(100)).roundToInt()
                }
            } else {
                if (type == AppConstants.LoanEMITypes.EMI) {
                    installmentWithValue.installmentDueDate = getDueDate(ins.due_days!!.toInt())
                    interest =
                        ((amount * ins.interestFee!! * ins.tenureDays!!).div(30)).roundToInt()
                } else {
                    installmentWithValue.installmentDueDate =
                        getDueDate(applyLoanEMIFragment.tenureDays)
                    interest = ((amount * ins.interestFee!! * 30).div(30)).roundToInt()
                }
            }
            val installmentAmount = amount * ins.barrowAmount!!

            installmentWithValue.interestFee = interest.toDouble()
            installmentWithValue.barrowAmount = installmentAmount
            installmentWithValue.processingFee = amount * ins.processingFee!!.toDouble()
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

    private fun getDueDate(days: Int): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val c: Calendar = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.DATE, days)
        return sdf.format(c.time)
    }

    fun getSelectedPosition(): ArrayList<InstalmentData>? {
        return instalmentsWithValues
    }

    private fun getRateOfInterest(days: Int): Float {
        var rateOfInterest = 0.0f
        if (applyLoanEMIFragment.applyLoanData != null) {
            for (serviceCharges in applyLoanEMIFragment.applyLoanData!!.serviceChargesList) {
                val fromDay = serviceCharges.from_day.toInt()
                val toDay = serviceCharges.to_day.toInt()
                if (days in fromDay..toDay) {
                    rateOfInterest = serviceCharges.service_charges.toFloat()
                    break
                }
            }
        }
        return rateOfInterest
    }

    public fun setOfferAmountList(list :List<Int>){
        this.offerList = list
    }
}
package com.capitalnowapp.mobile.kotlin.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.databinding.LoanEmiAdapterBinding
import com.capitalnowapp.mobile.kotlin.fragments.ApplyLoanEMIFragment
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.capitalnowapp.mobile.models.loan.TenureData


class LoanEmiAdapter(
    private val tenureDataList: List<TenureData>?,
    private var amount: Int,
    private val applyLoanEMIFragment: ApplyLoanEMIFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var fragment: Fragment? = null
    var context: Context? = null
    private var tenure: TenureData? = TenureData()

    class EMIVH(binding: LoanEmiAdapterBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        val rbOption = binding?.rbOption
        val etDays = binding?.etDays
    }

    override fun getItemCount(): Int {
        return tenureDataList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EMIVH) {
            try {
                val tenure = tenureDataList!![position]
                holder.rbOption?.text = tenure.title

                holder.rbOption?.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (applyLoanEMIFragment.selectedPos >= 0) {
                            applyLoanEMIFragment.uncheckPosition()
                        }
                        applyLoanEMIFragment.isTenureSelected = true
                        holder.etDays?.isEnabled = true
                        if (amount >= tenure.minAmount!! && amount <= tenure.maxAmount!!) {
                            holder.rbOption.isChecked = true
                            if (tenure.type == AppConstants.LoanEMITypes.Days) {
                                if (holder.etDays?.text?.length!! > 0) {
                                    tenure.instalments?.let {
                                        tenure.type?.let { it1 ->
                                            applyLoanEMIFragment.setInstallments(
                                                it,
                                                it1,
                                                tenure
                                            )
                                        }
                                    }
                                } else {
                                    applyLoanEMIFragment.setInstallments(emptyList(), "", tenure)
                                }
                            } else {
                                tenure.instalments?.let {
                                    tenure.type?.let { it1 ->
                                        applyLoanEMIFragment.setInstallments(
                                            it,
                                            it1,
                                            tenure
                                        )
                                    }
                                }
                            }
                        } else {
                            holder.etDays?.isEnabled = false
                            holder.rbOption.isChecked = false
                            if (amount < applyLoanEMIFragment.applyLoanData?.applyLoanMinAmount!!) {
                                Toast.makeText(
                                    context,
                                    applyLoanEMIFragment.applyLoanData?.eligibilityMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    tenure.eligibilityMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        applyLoanEMIFragment.selectedPos = position
                    } else {

                        applyLoanEMIFragment.selectedPos = -1
                        applyLoanEMIFragment.isTenureSelected = false
                        holder.etDays?.setText("")
                        applyLoanEMIFragment.setInstallments(emptyList(), "", tenure)
                        holder.etDays?.isEnabled = false
                    }

                    if (tenure.type == AppConstants.LoanEMITypes.EMI) {
                        // holder.rbOption?.isChecked = amount >= tenure.minAmount!! && amount <= tenure.maxAmount!!
                        holder.etDays?.visibility = GONE
                        this.tenure = tenureDataList[holder.absoluteAdapterPosition]
                    } else {
                        //  holder.rbOption?.isChecked = amount >= tenure.minAmount!! && amount <= tenure.maxAmount!!
                        holder.etDays?.visibility = VISIBLE
                        this.tenure = tenure
                    }
                }

                if (tenure.type == AppConstants.LoanEMITypes.EMI) {
                    // holder.rbOption?.isChecked = amount >= tenure.minAmount!! && amount <= tenure.maxAmount!!
                    holder.etDays?.visibility = GONE
                    this.tenure = tenureDataList[holder.absoluteAdapterPosition]
                } else {
                    //holder.rbOption?.isChecked = amount >= tenure.minAmount!! && amount <= tenure.maxAmount!!
                    holder.rbOption?.isChecked = true
                    holder.etDays?.visibility = VISIBLE
                    this.tenure = tenure
                }

                holder.etDays?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        applyLoanEMIFragment.tenureDays = -1
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        if (s.toString().trim().isNotEmpty() && s.toString().trim().isNotEmpty()) {
                            if (s.toString().toInt() > tenure.maxDays!!) {
                                if (tenure.maxDaysMessage!!.isNotEmpty()) {
                                    Toast.makeText(
                                        context,
                                        tenure.maxDaysMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                holder.etDays.setText("")
                            } else {
                                tenure.instalments?.let {
                                    applyLoanEMIFragment.setDays(
                                        s.toString().toInt(), it, tenure
                                    )
                                }
                            }
                        } else {
                            if (tenure.type == AppConstants.LoanEMITypes.Days) {
                                tenure.instalments?.let {
                                    applyLoanEMIFragment.setDays(
                                        -1,
                                        it,
                                        tenure
                                    )
                                }
                            }
                        }
                    }
                })

                if (applyLoanEMIFragment.tenureDays >= 0) {
                    holder.etDays?.setText(applyLoanEMIFragment.tenureDays.toString())
                }
                if(position == tenureDataList.size.minus(1)){
                    if(tenure.type == AppConstants.LoanEMITypes.EMI){
                        holder.rbOption?.isChecked = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            LoanEmiAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        applyLoanEMIFragment.isTenureSelected = false
        return EMIVH(binding)
    }

    fun getSelectedPosition(): TenureData {
        return tenure!!
    }
}
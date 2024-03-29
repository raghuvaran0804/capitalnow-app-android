package com.capitalnowapp.mobile.kotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.databinding.LoanEmiAdapterBinding
import com.capitalnowapp.mobile.kotlin.fragments.VehicleApplyLoanFragment
import com.capitalnowapp.mobile.models.TwlTenureData
import com.capitalnowapp.mobile.models.loan.TenureData


class VehicleLoanEmiAdapter(
    private val tenureDataList: List<TwlTenureData>?,
    private var amount: Int,
    private val vehicleApplyLoanFragment: VehicleApplyLoanFragment
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

                if (vehicleApplyLoanFragment.tenureDays >= 0) {
                    holder.etDays?.setText(vehicleApplyLoanFragment.tenureDays.toString())
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
        vehicleApplyLoanFragment.isTenureSelected = false
        return EMIVH(binding)
    }

    fun getSelectedPosition(): TenureData {
        return tenure!!
    }
}
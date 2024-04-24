package com.capitalnowapp.mobile.kotlin.adapters

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemBankStatementTypeBinding
import com.capitalnowapp.mobile.kotlin.activities.UploadBankDetailsActivity
import com.capitalnowapp.mobile.models.AnalysisListData

class BankStatementTypeAdapter(
    private val Listener: OnTextChangeListener,
    private val listener: AdapterItemClickListener,
    private val typeList: List<AnalysisListData>?,
    private val uploadBankDetailsActivity: UploadBankDetailsActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedMobileNo: String? = null
    private var selectedType: String? = null
    private var mobileVisible: Boolean = false
    private var listType: AnalysisListData? = null
    private var binding: ItemBankStatementTypeBinding? = null
    private var fromClick = false
    var clickedPos = -1
    private var referrer: String? = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding =
            ItemBankStatementTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BankStatementTypeVH(binding!!)
    }

    override fun getItemCount(): Int {
        return typeList?.size!!
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            if (holder is BankStatementTypeVH) {
                listType = typeList!![position]
                holder.tvLable.text = listType!!.lable
                holder.tvTitle.text = listType!!.title
                holder.tvDesc.text = listType!!.desc
                holder.tvFooter.text = listType!!.footerText
                if (listType?.isMobileNumber == true) {
                    holder.tilMobileNumber.visibility = View.VISIBLE
                    holder.etMobileNumber.setText(listType?.mobNumbers!![0])
                } else {
                    holder.tilMobileNumber.visibility = View.GONE
                }
                if (listType?.isRecomended == true) {
                    holder.tvFooter.visibility = View.VISIBLE
                } else {
                    holder.tvFooter.visibility = View.GONE
                }
                if (listType?.caution == true && listType!!.checked == true) {
                    holder.tvFooter.visibility = View.VISIBLE
                }

                holder.llMain.setOnClickListener {
                    selectedType = if(typeList[holder.absoluteAdapterPosition].type != null){
                        typeList[holder.absoluteAdapterPosition].type
                    }else {
                        ""
                    }
                    if (typeList[holder.absoluteAdapterPosition].mobNumbers?.isNotEmpty()!!) {
                        if(selectedMobileNo == null) {
                            selectedMobileNo =
                                typeList[holder.absoluteAdapterPosition].mobNumbers!![0]
                        }
                    } else {
                        selectedMobileNo = ""
                    }

                    mobileVisible = holder.tilMobileNumber.visibility === View.VISIBLE
                    listener.onItemClicked(selectedType, selectedMobileNo,mobileVisible)
                    fromClick = true
                    clickedPos = holder.absoluteAdapterPosition
                    for (lt in typeList.withIndex()) {
                        typeList[lt.index].checked = lt.index == clickedPos
                    }
                    notifyDataSetChanged()
                }

                holder.etMobileNumber.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) = Unit

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) = Unit


                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            if(s.length == 10) {
                                selectedMobileNo = s.toString()
                                typeList[holder.absoluteAdapterPosition].mobNumbers = listOf(
                                    selectedMobileNo.toString()
                                )
                                Listener.onTextChanged(s.toString())
                            }
                        }

                    }

                })

                /*if (!fromClick && listType?.isSelected == true) {
                    typeList[position].checked = true
                }*/

                if (listType!!.checked == true) {
                    holder.llMain.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.Primary1)
                    //newApplyLoanActivity.getEligibleOfferDetails()
                } else {
                    holder.llMain.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.light_gray)
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    class BankStatementTypeVH(binding: ItemBankStatementTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvLable = binding.tvLable
        val tvTitle = binding.tvTitle
        val tvDesc = binding.tvDesc
        val tvFooter = binding.tvFooter
        val tilMobileNumber = binding.tilMobileNumber
        val etMobileNumber = binding.etMobileNumber
        val llMain = binding.llMain

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

interface AdapterItemClickListener {
    fun onItemClicked(value: String?, selectedMobileNo: String?, mobileVisible: Boolean)
}

interface OnTextChangeListener {
    fun onTextChanged(text: String)
}
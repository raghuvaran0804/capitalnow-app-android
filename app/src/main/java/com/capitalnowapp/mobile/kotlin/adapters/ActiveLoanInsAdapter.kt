package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemActiveLoansInsBinding
import com.capitalnowapp.mobile.interfaces.SelectedToPayCallback
import com.capitalnowapp.mobile.kotlin.fragments.ActiveLoansFragment
import com.capitalnowapp.mobile.kotlin.fragments.LoanBottomSheetFragment
import com.capitalnowapp.mobile.models.loan.AmtPayable
import com.capitalnowapp.mobile.models.loan.LoansToPay


class ActiveLoanInsAdapter(
    private val activeLoansIns: List<AmtPayable>,
    private val activeLoansFragment: ActiveLoansFragment,
    private val loanToPay: LoansToPay,
    private val widthPixels: Int,
    private val activeLoansAdapter: ActiveLoansAdapter,
    private val bindingAdapterPosition: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var binding: ItemActiveLoansInsBinding? = null
    private var callBack: SelectedToPayCallback? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding =
            ItemActiveLoansInsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        /*val params: ViewGroup.LayoutParams = binding!!.root.layoutParams
        params.width = ((widthPixels / 2.5).roundToInt())*/
        // params.height = ((heightPixels / 2.5).roundToInt())
        // binding!!.root.layoutParams = params

        return InsVH(binding)
    }

    override fun getItemCount(): Int {
        return if (activeLoansIns.size > 1) {
            activeLoansIns.size
        } else {
            activeLoansIns.size + 1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is InsVH) {
            if (position < activeLoansIns.size) {
                holder.tvDetails?.visibility = GONE
                val activeLoansInst = activeLoansIns[position]
                holder.tvTitle?.text = activeLoansInst.title
                holder.tvTitle1?.text = activeLoansInst.title
                Glide.with(holder.itemView.context).load(R.drawable.ic_active_info).into(holder.tvDetails!!)
                /*if(position ==0){
                    holder.tvTitle?.text = "dsdsdsdsdsddfsdsdsdsdhy5tgrfvemkjnyhtbfvecdsdsds"
                }*/
                holder.tvAmount?.text =
                    holder.itemView.context.getString(R.string.indian_currency) + " " + activeLoansInst.dueAmount?.toInt()
                        .toString()
                holder.tvAmount1?.text =
                    holder.itemView.context.getString(R.string.indian_currency) + " " + activeLoansInst.dueAmount?.toInt()
                        .toString()
                if (activeLoansInst.dueDate != null && !holder.tvDueDate?.equals("")!!) {
                    holder.tvDueDate.text = "Due Date " + activeLoansInst.dueDate
                } else {
                    holder.tvDueDate?.visibility = GONE
                }

                if(activeLoansIns[position].recommendedDesc != null && activeLoansIns[position].recommendedDesc  != ""){
                    holder.tvText?.text = activeLoansIns[position].recommendedDesc
                }else {
                    holder.tvText?.text = ""
                }

                if (activeLoansInst.isRecommended == true) {
                    holder.tvPayLess?.visibility = VISIBLE
                    holder.llFullPayment?.visibility = VISIBLE
                    holder.llEmiPayment?.visibility = GONE
                    holder.tvDueDate?.visibility = GONE

                    holder.llParent?.setBackgroundResource(R.drawable.bg_rectangle_transp_green)


                    if(activeLoansIns[position].offerDetails?.saveText!= null && activeLoansIns[position].offerDetails?.saveText!= "") {
                        holder.tvSaveText?.text = activeLoansIns[position].offerDetails?.saveText
                        holder.tvOnText?.text = activeLoansIns[position].offerDetails?.onText
                        holder.ivCnLogo?.visibility = GONE
                        holder.tvSaveText?.visibility = VISIBLE
                        holder.tvOnText?.visibility = VISIBLE
                    }else {
                        Glide.with(holder.itemView.context).load(R.drawable.splash_new).into(holder.ivCnLogo!!)
                        holder.ivCnLogo.visibility = VISIBLE
                        holder.tvSaveText?.visibility = GONE
                        holder.tvOnText?.visibility = GONE
                    }

                    Glide.with(holder.itemView.context).load(R.raw.do_svg).into(holder.ivDot!!)
                    holder.tvPayLess?.text = activeLoansInst.recommendedText
                    /*holder.llData?.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.Secondary2)
                    */holder.tvAmount?.setTextColor(
                        ContextCompat.getColorStateList(
                            holder.itemView.context,
                            R.color.black
                        )
                    )
                    holder.tvTitle?.setTextColor(
                        ContextCompat.getColorStateList(
                            holder.itemView.context,
                            R.color.Primary2
                        )
                    )

                    /*holder.tvDetails?.setTextColor(
                        ContextCompat.getColorStateList(
                            holder.itemView.context,
                            R.color.white
                        )
                    )
                    holder.tvDetails?.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.Primary1)
                    for (drawable in holder.tvDetails!!.compoundDrawablesRelative) {
                        if (drawable != null) {
                            drawable.colorFilter = PorterDuffColorFilter(
                                ContextCompat.getColor(
                                    holder.tvDetails.context,
                                    R.color.white
                                ), PorterDuff.Mode.SRC_IN
                            )
                        }
                    }*/
                } else {
                    holder.llFullPayment?.visibility = GONE
                    holder.llEmiPayment?.visibility = VISIBLE
                    if (activeLoansIns[position].isSelected == true) {
                        holder.llParent?.visibility = VISIBLE
                        //holder.tvFullLoanTextDec?.text = "Alert! Pay Full Loan Amount to save Rs {xxxx} on Interest,\navail a new loan instantly and increase your limit"

                        holder.llParent?.setBackgroundResource(R.drawable.bg_rectangle_transp_primary2)
                        if (activeLoansIns[position].saveAmountText != null && activeLoansIns[position].saveAmountText != ""){
                            holder.tvFullLoanTextDec?.visibility = VISIBLE
                            holder.tvFullLoanTextDec?.text = activeLoansIns[position].saveAmountText
                        }else{
                            holder.tvFullLoanTextDec?.visibility = GONE
                            holder.tvFullLoanTextDec?.text = ""

                        }
                    } else {
                        holder.tvFullLoanTextDec?.visibility = GONE
                        holder.llParent?.visibility = GONE
                    }
                    //holder.tvDetails?.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.colorInsBg)
                    /*holder.tvDetails?.background =
                        ContextCompat.getDrawable(holder.itemView.context, R.drawable.just_corners)
                    holder.tvDetails?.setTextColor(
                        ContextCompat.getColorStateList(
                            holder.itemView.context,
                            R.color.black
                        )
                    )*/

                    /*for (drawable in holder.tvDetails!!.compoundDrawablesRelative) {
                        if (drawable != null) {
                            drawable.colorFilter = PorterDuffColorFilter(
                                ContextCompat.getColor(
                                    holder.tvDetails.context,
                                    R.color.black
                                ), PorterDuff.Mode.SRC_IN
                            )
                        }
                    }*/
                    /* holder.llData?.backgroundTintList =
                         ContextCompat.getColorStateList(holder.itemView.context, R.color.Secondary1)
                     */holder.tvAmount?.setTextColor(
                        ContextCompat.getColorStateList(
                            holder.itemView.context,
                            R.color.cb_heading_color
                        )
                    )
                    holder.tvTitle?.setTextColor(
                        ContextCompat.getColorStateList(
                            holder.itemView.context,
                            R.color.cb_heading_color
                        )
                    )
                    holder.tvPayLess?.visibility = GONE
                }

                if (activeLoansIns[position].isSelected == true) {
                    holder.llParent?.visibility = VISIBLE
                } else {
                    holder.llParent?.visibility = GONE
                }

                holder.llData?.setOnClickListener {

                    callBack = activeLoansFragment
                    (callBack as ActiveLoansFragment).selectedObj(
                        activeLoansIns[position],
                        loanToPay
                    )

                    activeLoansAdapter.setSelection(bindingAdapterPosition, holder.bindingAdapterPosition)

                    /*activeLoansIns[position].isSelected = true
                    notifyItemChanged(position)*/
                }

                holder.tvDetails.setOnClickListener {
                    val fragment = LoanBottomSheetFragment(
                        loanToPay,
                        activeLoansFragment,
                        activeLoansIns[position]
                    )
                    fragment.show((activeLoansFragment.activity)!!.supportFragmentManager, "TAG")
                }
                holder.tvDetails2?.setOnClickListener {
                    val fragment = LoanBottomSheetFragment(
                        loanToPay,
                        activeLoansFragment,
                        activeLoansIns[position]
                    )
                    fragment.show((activeLoansFragment.activity)!!.supportFragmentManager, "TAG")
                }
            } else {
                holder.llData?.visibility = GONE
                holder.ivNoIns?.visibility = GONE
                /*val params =  holder.ivNoIns?.layoutParams as LinearLayout.LayoutParams
                params.height = height
                params.width = width
                holder.ivNoIns.layoutParams = params*/
            }

            /*if(position == 0){
                Handler().postDelayed({
                    if(!fromClick){
                        fromClick = true
                        holder.llData?.performClick()
                    }
                    // yourMethod()
                }, 3000)

            }*/


        }
    }
}

class InsVH(binding: ItemActiveLoansInsBinding?) : RecyclerView.ViewHolder(binding!!.root) {

    val llParent = binding?.llParent
    val tvTitle = binding?.tvTitle
    val tvTitle1 = binding?.tvTitle1
    val tvPayLess = binding?.tvPayLess
    val tvAmount = binding?.tvAmount
    val tvAmount1 = binding?.tvAmount1
    val tvDueDate = binding?.tvDueDate
    val tvSaveText = binding?.tvSaveText
    val tvOnText = binding?.tvOnText
    val llData = binding?.llData
    val ivNoIns = binding?.ivNoIns
    val ivDot = binding?.ivDot
    val tvDetails = binding?.tvDetails
    val tvDetails2 = binding?.tvDetails2
    val llFullPayment = binding?.llFullPayment
    val llEmiPayment = binding?.llEmiPayment
    val ivCnLogo = binding?.ivCnLogo
    val tvText = binding?.tvText
    val tvFullLoanTextDec = binding?.tvFullLoanTextDec
}

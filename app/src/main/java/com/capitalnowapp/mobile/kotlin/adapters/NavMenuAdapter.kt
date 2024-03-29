package com.capitalnowapp.mobile.kotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ItemNavMenuBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity


class NavMenuAdapter(
    private val items: ArrayList<String>,
    private val dashboardActivity: DashboardActivity,
    private var selectedTab: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var count: String? = "0"
    private var tvNotification: CNTextView? = null
    private var binding: ItemNavMenuBinding? = null
    private var fragment: Fragment? = null
    var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        binding = ItemNavMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return NavVH(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NavVH) {
            holder.tvItem?.text = items[position]
            var img: Int? = null

            if (items[position] == selectedTab) {
                holder.viewSelected?.visibility = View.VISIBLE
                holder.llItem?.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.llItem.context,
                        R.color.light_white
                    )
                )
            } else {
                holder.llItem?.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.llItem.context,
                        R.color.transparent
                    )
                )
                holder.viewSelected?.visibility = View.INVISIBLE
            }

            when (items[position]) {
                context?.getString(R.string.home) -> {
                    img =  R.drawable.ic_menu_home
                }
                context?.getString(R.string.profile) -> {
                    img = R.drawable.ic_profile_menu
                }
                context?.getString(R.string.apply_now) -> {
                    img = R.drawable.ic_apply_loan_menu
                }

                context?.getString(R.string.loan_partners) -> {
                    img = R.drawable.ic_partnership
                }
                context?.getString(R.string.our_partners) -> {
                    img = R.drawable.ic_partnership
                }
                context?.getString(R.string.twl_active_loans) -> {
                    img = R.drawable.ic_two_wheeler
                }
                context?.getString(R.string.upload_documents), context?.getString(R.string.latest_documents) -> {
                    img = null
                    Glide.with(holder.itemView.rootView.context).load(R.raw.docs_final)
                        .into(holder.ivImg!!)
                }
                context?.getString(R.string.add_references), context?.getString(R.string.add_latest_references) -> {
                    img = null
                    Glide.with(holder.itemView.rootView.context).load(R.raw.add_ref)
                        .into(holder.ivImg!!)
                }
                context?.getString(R.string.active_loans) -> {
                    img = R.drawable.ic_active_loans_menu
                }
                context?.getString(R.string.cleared_loans) -> {
                    img = R.drawable.ic_cleared_menu
                }
                context?.getString(R.string.reward_points) -> {
                    img = R.drawable.ic_reward_menu
                }
                context?.getString(R.string.cnpl_history) -> {
                    img = R.drawable.ic_cnplhistory
                }
                context?.getString(R.string.manager_details) -> {
                    img = R.drawable.ic_manager_menu
                }
                context?.getString(R.string.privacy_policy) -> {
                    img = R.drawable.ic_privacy_policy
                }
                context?.getString(R.string.transactions) -> {
                    img = R.drawable.ic_transaction_menu
                }
                context?.getString(R.string.credit_line) -> {
                    img = R.drawable.ic_creditline
                }
                context?.getString(R.string.data_deletion) -> {
                    img = R.drawable.ic_data_deletion
                }
                context?.getString(R.string.refer_and_earn) -> {
                    img = R.drawable.ic_refer_menu
                }
                context?.getString(R.string.talk_us) -> {
                    img = R.drawable.ic_talk_us
                }
                context?.getString(R.string.help) -> {
                    img = R.drawable.ic_help
                }
                context?.getString(R.string.contact_us) -> {
                    img = R.drawable.ic_contact_us
                }
                context?.getString(R.string.request_bank_chnage) -> {
                    img = R.drawable.ic_rbc_menu
                }
                "" -> {
                    holder.llItem?.visibility = View.INVISIBLE
                }
                context?.getString(R.string.logout) -> {
                    img = R.drawable.ic_log_out
                }
                context?.getString(R.string.borrower_agreement_consent) -> {
                    img = R.drawable.ic_consent_menu
                }
                context?.getString(R.string.menu_item_kyc) -> {
                    img = R.drawable.ic_consent_menu
                }
                context?.getString(R.string.add_signature) -> {
                    img = R.drawable.ic_consent_menu
                }
            }
            if (img != null) {
                holder.ivImg?.setImageResource(img)
            }
            holder.llItem?.setOnClickListener {
                setSelectedTab(holder.tvItem?.text.toString())
            }
        }
    }

    private fun onClick(str: String) {
        dashboardActivity.onItemClick(str)
    }

    fun setSelectedTab(selectedTab: String) {
        this.selectedTab = selectedTab
        onClick(selectedTab)
    }

    fun setNotificationCount(dataStr: String?) {
        if (tvNotification != null && !dataStr.equals("0")) {
            count = dataStr
            tvNotification?.text = dataStr
            tvNotification?.visibility = VISIBLE
        } else {
            tvNotification?.visibility = GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

class NavVH(binding: ItemNavMenuBinding?) : RecyclerView.ViewHolder(binding!!.root) {
    val tvItem = binding?.tvItem
    val tvNotificationCount = binding?.tvNotificationCount
    val ivImg = binding?.ivImg
    val llItem = binding?.llItem
    val viewSelected = binding?.viewSelected
}


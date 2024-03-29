package com.capitalnowapp.mobile.kotlin.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ItemFrequentlyUsedAppBinding
import com.capitalnowapp.mobile.kotlin.activities.FrequentlyUsedAppsActivity


class FrequentlyUsedAppsAdapter(private val appsList: ArrayList<String>, private val activity: FrequentlyUsedAppsActivity, private var savedList: ArrayList<String>?) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var binding: ItemFrequentlyUsedAppBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemFrequentlyUsedAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GetSTartedVH(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GetSTartedVH) {
            holder.tvTitle.text = appsList[position]
            holder.llItem.setOnClickListener {
                saveApps(appsList[position], holder.tvTitle)
            }
            if (savedList != null && savedList!!.isNotEmpty() && savedList!!.contains(appsList[position])) {
                changeColor(appsList[position], holder.tvTitle)
            }
        }
    }

    private fun saveApps(app: String, tv: CNTextView) {
        if (savedList != null && savedList!!.isNotEmpty()) {
            if (savedList!!.contains(app)) {
                savedList!!.remove(app)
                tv.setTypeface(null, Typeface.NORMAL);
                tv.backgroundTintList = null
                tv.setTextColor(ContextCompat.getColor(activity, R.color.intro_page_body_color))
            } else {
                savedList!!.add(app)
                changeColor(app, tv)
                tv.setTypeface(null, Typeface.BOLD);
            }
        } else {
            savedList = ArrayList()
            savedList!!.add(app)
            changeColor(app, tv)
        }
        activity.registerUserReq.frequentlyUsedApps = ""
        activity.registerUserReq.frequentlyUsedApps = android.text.TextUtils.join(",", savedList!!);
        activity.savedList = savedList
        activity.saveData()
        activity.checkValidation()
    }

    private fun changeColor(app: String, tv: CNTextView) {
        tv.setTextColor(ContextCompat.getColor(activity, R.color.Primary1))
        /*when (app) {
            AppConstants.FrequentApps.SWIGGY -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorSwiggy)
            }
            AppConstants.FrequentApps.FACEBOOK -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorFB)
            }
            AppConstants.FrequentApps.OLA -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorOLA)
            }
            AppConstants.FrequentApps.INSTAGRAM -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorInstagam_and_Uber)
            }
            AppConstants.FrequentApps.UBER -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorInstagam_and_Uber)
            }
            AppConstants.FrequentApps.ZOMATO -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorZomato)
            }
            AppConstants.FrequentApps.AMAZON -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorAmazon)
            }
            AppConstants.FrequentApps.FLIPKART -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorFlipCart)
            }
            AppConstants.FrequentApps.MYNTRA -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorMyntra)
            }
            AppConstants.FrequentApps.LINKED_IN -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorLN)
            }
            AppConstants.FrequentApps.NAUKRI -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorNaukri)
            }
            AppConstants.FrequentApps.QUICKRIDE -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorQuickRide)
            }
            AppConstants.FrequentApps.TOI -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorTOI)
            }
            AppConstants.FrequentApps.INVES_TAP -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorInvesTAP)
            }
            AppConstants.FrequentApps.SNAPCHAT -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorSnap)
            }
            AppConstants.FrequentApps.QUORA -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorQuora)
            }
            AppConstants.FrequentApps.INSHORTS -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorInShorts)
            }
            AppConstants.FrequentApps.FUNDS_INDIA -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorFundsIndia)
            }
            AppConstants.FrequentApps.MY_CAMS -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorMyCams)
            }
            AppConstants.FrequentApps.INDEED -> {
                tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorIndeed)
            }
        }*/
        tv.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.black)
    }

    override fun getItemCount(): Int {
        return appsList.size
    }

    private inner class GetSTartedVH internal constructor(binding: ItemFrequentlyUsedAppBinding?) : RecyclerView.ViewHolder(binding!!.root) {
        var tvTitle: CNTextView = binding!!.tvTitle
        var llItem: LinearLayout = binding!!.llItem
    }
}
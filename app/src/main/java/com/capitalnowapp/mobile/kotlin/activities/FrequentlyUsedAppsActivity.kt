package com.capitalnowapp.mobile.kotlin.activities

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ActivityFrequentlyUsedAppsBinding
import com.capitalnowapp.mobile.kotlin.adapters.FrequentlyUsedAppsAdapter
import com.capitalnowapp.mobile.kotlin.utils.AppConstants
import com.develop.rth.gragwithflowlayout.FlowDragLayoutConstant
import com.develop.rth.gragwithflowlayout.FlowDragLayoutManager
import com.igalata.bubblepicker.BubblePickerListener
import com.igalata.bubblepicker.model.PickerItem


class FrequentlyUsedAppsActivity : RegistrationHomeActivity(), BubblePickerListener {
    private var binding: ActivityFrequentlyUsedAppsBinding? = null
    private var appsList: ArrayList<String> = ArrayList()
    private var selectedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrequentlyUsedAppsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        refreshJson()
        initView(binding)
    }

    private fun initView(binding: ActivityFrequentlyUsedAppsBinding?) {
        loadData()
        setData()

        binding?.rvAppsList?.layoutManager = FlowDragLayoutManager(FlowDragLayoutConstant.CENTER) //StaggeredGridLayoutManager( 3, StaggeredGridLayoutManager.VERTICAL)
        //   binding?.rvAppsList?.layoutManager = StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL) //StaggeredGridLayoutManager( 3, StaggeredGridLayoutManager.VERTICAL)
        binding?.rvAppsList?.adapter = FrequentlyUsedAppsAdapter(appsList, this, savedList)
        binding?.ivSubmit?.setOnClickListener {
            submitData()
        }
        checkValidation()

       /* val titles = loadData()
        //val colors =
        //  val images = resources.obtainTypedArray(R.array.images)
        binding?.picker?.adapter = object : BubblePickerAdapter {
            override val totalCount = titles.size
            override fun getItem(position: Int): PickerItem {
                return PickerItem().apply {
                    title = titles[position]
                    textColor = ContextCompat.getColor(activityContext, android.R.color.white)
                }
            }
        }

        binding?.picker?.bubbleSize = 15
        binding?.picker?.centerImmediately = true
        binding?.picker?.listener = this*/
    }

    fun checkValidation() {
        binding?.ivSubmit?.isEnabled = validateApps()
    }

    private fun setData() {
        if (registerUserReq.frequentlyUsedApps != null && !registerUserReq.frequentlyUsedApps.equals("") && !registerUserReq.frequentlyUsedApps.equals("[]")) {
            val aa: List<String> = registerUserReq.frequentlyUsedApps!!.split(",")
            savedList = ArrayList(aa)
        }
    }

    private fun loadData(): ArrayList<String> {
        appsList = ArrayList()
        appsList.add(AppConstants.FrequentApps.FACEBOOK)
        appsList.add(AppConstants.FrequentApps.INSTAGRAM)
        appsList.add(AppConstants.FrequentApps.SNAPCHAT)
        appsList.add(AppConstants.FrequentApps.SWIGGY)
        appsList.add(AppConstants.FrequentApps.ZOMATO)
        appsList.add(AppConstants.FrequentApps.AMAZON)
        appsList.add(AppConstants.FrequentApps.FLIPKART)
        appsList.add(AppConstants.FrequentApps.MYNTRA)
        appsList.add(AppConstants.FrequentApps.LINKED_IN)
        appsList.add(AppConstants.FrequentApps.INDEED)
        appsList.add(AppConstants.FrequentApps.NAUKRI)
        appsList.add(AppConstants.FrequentApps.UBER)
        appsList.add(AppConstants.FrequentApps.FUNDS_INDIA)
        appsList.add(AppConstants.FrequentApps.QUICKRIDE)
        appsList.add(AppConstants.FrequentApps.QUORA)
        appsList.add(AppConstants.FrequentApps.TOI)
        appsList.add(AppConstants.FrequentApps.INSHORTS)
        appsList.add(AppConstants.FrequentApps.MY_CAMS)
        appsList.add(AppConstants.FrequentApps.INVES_TAP)
        appsList.add(AppConstants.FrequentApps.OLA)

        return appsList
    }

    private fun changeColor(app: String, childAt: View) {
        when (app) {
            AppConstants.FrequentApps.SWIGGY -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorSwiggy)
            }
            AppConstants.FrequentApps.FACEBOOK -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorFB)
            }
            AppConstants.FrequentApps.OLA -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorOLA)
            }
            AppConstants.FrequentApps.INSTAGRAM -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorInstagam_and_Uber)
            }
            AppConstants.FrequentApps.UBER -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorInstagam_and_Uber)
            }
            AppConstants.FrequentApps.ZOMATO -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorZomato)
            }
            AppConstants.FrequentApps.AMAZON -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorAmazon)
            }
            AppConstants.FrequentApps.FLIPKART -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorFlipCart)
            }
            AppConstants.FrequentApps.MYNTRA -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorMyntra)
            }
            AppConstants.FrequentApps.LINKED_IN -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorLN)
            }
            AppConstants.FrequentApps.NAUKRI -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorNaukri)
            }
            AppConstants.FrequentApps.QUICKRIDE -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorQuickRide)
            }
            AppConstants.FrequentApps.TOI -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorTOI)
            }
            AppConstants.FrequentApps.INVES_TAP -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorInvesTAP)
            }
            AppConstants.FrequentApps.SNAPCHAT -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorSnap)
            }
            AppConstants.FrequentApps.QUORA -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorQuora)
            }
            AppConstants.FrequentApps.INSHORTS -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorInShorts)
            }
            AppConstants.FrequentApps.FUNDS_INDIA -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorFundsIndia)
            }
            AppConstants.FrequentApps.MY_CAMS -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorMyCams)
            }
            AppConstants.FrequentApps.INDEED -> {
                childAt.backgroundTintList = ContextCompat.getColorStateList(activityContext, R.color.colorIndeed)
            }
        }
    }

    private fun changeColor(app: String): Int {
        when (app) {
            AppConstants.FrequentApps.SWIGGY -> {
                return R.color.colorSwiggy
            }
            AppConstants.FrequentApps.FACEBOOK -> {
                return R.color.colorFB
            }
            AppConstants.FrequentApps.OLA -> {
                return R.color.colorOLA
            }
            AppConstants.FrequentApps.INSTAGRAM -> {
                return R.color.colorInstagam_and_Uber
            }
            AppConstants.FrequentApps.UBER -> {
                return R.color.colorInstagam_and_Uber
            }
            AppConstants.FrequentApps.ZOMATO -> {
                return R.color.colorZomato
            }
            AppConstants.FrequentApps.AMAZON -> {
                return R.color.colorAmazon
            }
            AppConstants.FrequentApps.FLIPKART -> {
                return R.color.colorFlipCart
            }
            AppConstants.FrequentApps.MYNTRA -> {
                return R.color.colorMyntra
            }
            AppConstants.FrequentApps.LINKED_IN -> {
                return R.color.colorLN
            }
            AppConstants.FrequentApps.NAUKRI -> {
                return R.color.colorNaukri
            }
            AppConstants.FrequentApps.QUICKRIDE -> {
                return R.color.colorQuickRide
            }
            AppConstants.FrequentApps.TOI -> {
                return R.color.colorTOI
            }
            AppConstants.FrequentApps.INVES_TAP -> {
                return R.color.colorInvesTAP
            }
            AppConstants.FrequentApps.SNAPCHAT -> {
                return R.color.colorSnap
            }
            AppConstants.FrequentApps.QUORA -> {
                return R.color.colorQuora
            }
            AppConstants.FrequentApps.INSHORTS -> {
                return R.color.colorInShorts
            }
            AppConstants.FrequentApps.FUNDS_INDIA -> {
                return R.color.colorFundsIndia
            }
            AppConstants.FrequentApps.MY_CAMS -> {
                return R.color.colorMyCams
            }
            AppConstants.FrequentApps.INDEED -> {
                return R.color.colorIndeed
            }
        }
        return 0
    }

    override fun onPause() {
        super.onPause()
        binding?.picker?.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        binding?.picker?.onResume()
    }

    override fun onBubbleDeselected(item: PickerItem) {
        val cd = ColorDrawable(changeColor(item.title!!))
        item.backgroundImage = cd
    }

    override fun onBubbleSelected(item: PickerItem) {

    }
}

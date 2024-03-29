package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.appsflyer.AppsFlyerLib
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.ActivityBannerBinding
import com.facebook.appevents.AppEventsLogger
//import io.branch.referral.util.BranchEvent

class BannerActivity : BaseActivity() {
    private lateinit var binding: ActivityBannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        sharedPreferences?.putBoolean(Constants.MADE_IN_INDIA_SHOWN, true)
        binding.llBanner.setOnClickListener {
            closeActivity()
        }

        binding.llBanner.setOnTouchListener(View.OnTouchListener { v, event ->
            closeActivity()
            true
        })
    }

    private fun closeActivity() {
        val intent = Intent(this, GetStartedActivity::class.java)
      //  AdGyde.onSimpleEvent(getString(R.string.app_open_first_time))

        val params = HashMap<String, Any>()
        val key = getString(R.string.app_open_first_time)
        params[key] = getString(R.string.app_open_first_time) //patrametre name,value change to event
        AppsFlyerLib.getInstance().logEvent(this, key, params)

        val logger = AppEventsLogger.newLogger(this)
        logger.logEvent(getString(R.string.app_open_first_time), Bundle())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)

        /*BranchEvent("OpenedAfterInstallation")
            .addCustomDataProperty("OpenedAfterInstallation", "Opened_After_Installation")
            .setCustomerEventAlias("Opened_After_Installation")
            .logEvent(this@BannerActivity)*/
    }

    override fun onBackPressed() {

    }
}
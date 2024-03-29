package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.databinding.ActivityCspermissionsBinding

class CSPermissionsActivity : BaseActivity() {
    private var binding: ActivityCspermissionsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCspermissionsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        try {
            //checkPermissions()
            binding!!.ivBack.setOnClickListener {
                permissionsRedirectPage = 11
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
}
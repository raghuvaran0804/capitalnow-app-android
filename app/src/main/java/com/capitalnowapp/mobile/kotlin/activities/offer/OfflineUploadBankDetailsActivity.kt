package com.capitalnowapp.mobile.kotlin.activities.offer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.databinding.ActivityOfflineUploadBankDetailsBinding

class OfflineUploadBankDetailsActivity : AppCompatActivity() {
    private var binding: ActivityOfflineUploadBankDetailsBinding? = null
    private var activity: AppCompatActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfflineUploadBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        try{

        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
package com.capitalnowapp.mobile.kotlin.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.activities.SplashScreen
import com.capitalnowapp.mobile.databinding.ActivityDeepLinkBinding


class DeepLinkActivity : AppCompatActivity() {
    private var binding: ActivityDeepLinkBinding? = null
    private var activity: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_deep_link)
        binding = ActivityDeepLinkBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        try {
            val value = intent
            val uri = value.data.toString()
            val bundle = Bundle()
            val intent = Intent(applicationContext, SplashScreen::class.java)
            intent.putExtra("fromDeeplink",true)
            intent.putExtra("destination",uri)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
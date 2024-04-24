package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.databinding.ActivityDeepLink2Binding

class DeepLink2Activity : AppCompatActivity() {
    private  var binding: ActivityDeepLink2Binding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeepLink2Binding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val intent = intent
        val uri = intent.data
    }
}
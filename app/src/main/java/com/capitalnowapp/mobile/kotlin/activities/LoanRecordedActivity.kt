package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.databinding.ActivityLoanRecordedBinding

class LoanRecordedActivity : AppCompatActivity() {
    private var binding: ActivityLoanRecordedBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoanRecordedBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        binding?.tvContinue?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("from", "NewLoanActivity")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }
}
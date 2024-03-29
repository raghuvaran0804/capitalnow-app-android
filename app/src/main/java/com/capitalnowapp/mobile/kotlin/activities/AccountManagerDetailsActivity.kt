package com.capitalnowapp.mobile.kotlin.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.databinding.ActivityAccountManagerDetailsBinding


class AccountManagerDetailsActivity : AppCompatActivity() {

    private lateinit var accountManager: String
    private lateinit var accountManagerIcon: String
    private lateinit var contactNo: String
    private lateinit var hobbies: String
    private lateinit var city: String
    private lateinit var lang: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAccountManagerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras
        if (extras != null) {
            accountManagerIcon = extras.getString("accountManagerIcon")!!
            accountManager = extras.getString("accountManager")!!
            contactNo = extras.getString("contactNumber")!!
            hobbies = extras.getString("hobbies")!!
            lang = extras.getString("language")!!
            city = extras.getString("city")!!
        }

        binding.tvAccountManager.text = accountManager
        binding.tvContactNo.text = contactNo
        binding.tvHobbies.text = hobbies
        binding.tvLanguage.text = lang
        binding.tvCity.text = city
        if (accountManagerIcon != "") {
            Glide.with(this)
                    .load(accountManagerIcon)
                    .into(binding.ivAccountIcon)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvApplyLoan.setOnClickListener {
            setResult(Activity.RESULT_OK, null)
            finish()
        }

        binding.tvContactNo.setOnClickListener {
            try {
                if (contactNo != "") {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$contactNo")
                    startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

package com.capitalnowapp.mobile.kotlin.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.databinding.FragmentCreditLineBinding
import com.capitalnowapp.mobile.kotlin.activities.BBPSWebViewActivity


class CreditLineFragment : Fragment() {
    private var binding : FragmentCreditLineBinding? = null
    private var activity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditLineBinding.inflate(layoutInflater,container,false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        try {
            binding?.tvUnlock?.setOnClickListener {
                val intent = Intent(getActivity(), BBPSWebViewActivity::class.java)
                val pbKey = ""
                intent.putExtra("pbKey8", pbKey)
                startActivity(intent)
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
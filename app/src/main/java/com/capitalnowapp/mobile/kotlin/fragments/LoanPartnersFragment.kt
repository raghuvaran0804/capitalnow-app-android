package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.databinding.FragmentLoanPartnersBinding
import org.json.JSONObject

class LoanPartnersFragment : Fragment() {
    private var binding: FragmentLoanPartnersBinding? = null
    private var activity: Activity? = null
    private var loansResponse: JSONObject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoanPartnersBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        if (arguments != null) {
            val loanPartnerLink = requireArguments().getString("loan_partner_link")
            binding!!.wvLoanPartner.settings.javaScriptEnabled = true
            binding?.wvLoanPartner?.loadUrl(loanPartnerLink!!)
        }
    }
}
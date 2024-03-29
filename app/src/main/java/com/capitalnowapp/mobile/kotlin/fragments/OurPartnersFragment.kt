package com.capitalnowapp.mobile.kotlin.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.databinding.FragmentOurPartnersBinding
import org.json.JSONObject

class OurPartnersFragment : Fragment() {
    private var binding: FragmentOurPartnersBinding? = null
    private var activity: Activity? = null
    private var loansResponse: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOurPartnersBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (arguments != null) {
            val ourPartnerLink = requireArguments().getString("our_partner_link")
            binding!!.wvOurPartner.settings.javaScriptEnabled = true
            binding?.wvOurPartner?.loadUrl(ourPartnerLink!!)
        }
    }
}
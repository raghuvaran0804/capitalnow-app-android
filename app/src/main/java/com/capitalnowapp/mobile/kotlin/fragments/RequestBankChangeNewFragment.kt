package com.capitalnowapp.mobile.kotlin.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.databinding.FragmentRequestBankChangeNewBinding
import com.capitalnowapp.mobile.kotlin.activities.UploadBankDetailsActivity
import com.capitalnowapp.mobile.util.TrackingUtil
import org.json.JSONException
import org.json.JSONObject


class RequestBankChangeNewFragment : Fragment() {
    private var binding: FragmentRequestBankChangeNewBinding? = null
    private var activity: Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestBankChangeNewBinding.inflate(inflater,container,false)
        activity = getActivity()
        return binding!!.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        val obj = JSONObject()
        try {
            obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.request_bank_page_landed))

       binding!!.tvContinueFinBit.setOnClickListener {

           val obj = JSONObject()
           try {
               obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
               obj.put(getString(R.string.interaction_type),"CONTINUE Button Clicked")
           } catch (e: JSONException) {
               throw RuntimeException(e)
           }
           TrackingUtil.pushEvent(obj, getString(R.string.request_bank_page_interacted))

           val intent = Intent(getActivity(), UploadBankDetailsActivity::class.java)
           intent.putExtra("referrer", Constants.FIN_BIT_REFERRER.Req_Bank_Change)
           startActivity(intent)
       }
    }

}
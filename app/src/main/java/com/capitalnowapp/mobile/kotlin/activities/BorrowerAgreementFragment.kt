package com.capitalnowapp.mobile.kotlin.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.LoanAgreementConsent
import com.capitalnowapp.mobile.databinding.ActivityBorrowerAgreementBinding
import com.capitalnowapp.mobile.util.TrackingUtil
import kotlinx.android.synthetic.main.activity_borrower_agreement.tvC1
import kotlinx.android.synthetic.main.activity_borrower_agreement.tvC2
import kotlinx.android.synthetic.main.activity_borrower_agreement.tvC3
import kotlinx.android.synthetic.main.activity_borrower_agreement.tvC4
import kotlinx.android.synthetic.main.activity_borrower_agreement.tvValidate
import org.json.JSONException
import org.json.JSONObject

class BorrowerAgreementFragment : Fragment() {
    private lateinit var binding: ActivityBorrowerAgreementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityBorrowerAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val obj = JSONObject()
        try {
            obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.borrower_agreement_consent_page_landed))

        if(arguments!=null){
            val consentData : LoanAgreementConsent = requireArguments().getSerializable("loanData") as LoanAgreementConsent
            tvC1.text = consentData.passcode.substring(0, 1)
            tvC2.text = consentData.passcode.substring(1, 2)
            tvC3.text = consentData.passcode.substring(2, 3)
            tvC4.text = consentData.passcode.substring(3, 4)

            tvValidate.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"VIEW & AGREE Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.borrower_agreement_consent_page_interacted))

                showConsentOTP(consentData)
            }
        }
    }

    private fun showConsentOTP(consentData: LoanAgreementConsent) {
        try {
            (activity as DashboardActivity).showConsent(consentData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
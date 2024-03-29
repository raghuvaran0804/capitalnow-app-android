package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.kotlin.adapters.MyLoanInsAdapter
import com.capitalnowapp.mobile.models.loan.AmtPayable
import com.capitalnowapp.mobile.models.loan.LoansToPay
import com.capitalnowapp.mobile.util.TrackingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONException
import org.json.JSONObject

class LoanBottomSheetFragment(private val loanToPay: LoansToPay, private val activeLoansFragment: ActiveLoansFragment, private val amtPayable: AmtPayable?) : BottomSheetDialogFragment() {

    @SuppressLint("NotConstructor")
    fun LoanBottomSheetFragment() {
        // Required empty public constructor
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.active_loan_sheet, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.active_loan_sheet, null)
        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(ContextCompat.getColor(contentView.context, R.color.transparent))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvIns: RecyclerView = view.findViewById(R.id.rvIns)
        val tvLoanId: TextView = view.findViewById(R.id.tvLoanId)
        /*val tvPolicyNumber: TextView = view.findViewById(R.id.tvPolicyNumber)*/
        val ivViewPolicy: ImageView = view.findViewById(R.id.ivViewPolicy)
        /*val llInsurance : LinearLayout = view.findViewById(R.id.llInsurance)*/
        rvIns.layoutManager = LinearLayoutManager(context)

        if(loanToPay.insuranceData != null){
            ivViewPolicy.visibility = View.VISIBLE
            //tvPolicyNumber.text = loanToPay.insuranceData?.policyNo
        }else {
            ivViewPolicy.visibility = View.GONE
        }
        val policyLink = loanToPay.insuranceData?.policyLink
        ivViewPolicy.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                obj.put("CNLID",loanToPay.lid)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, "Download Acko Policy Clicked")

            loadPolicy(policyLink!!)
        }

        tvLoanId.text = loanToPay.qclId
        val adapter = MyLoanInsAdapter(loanToPay,activity as BaseActivity, activeLoansFragment, this, amtPayable)
        rvIns.adapter = adapter
    }

    private fun loadPolicy(policyLink: String) {
        val openURL = Intent(Intent.ACTION_VIEW,Uri.parse(policyLink)).addCategory(Intent.CATEGORY_BROWSABLE)
        openURL.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        openURL.data = Uri.parse(policyLink)
        startActivity(openURL)
    }
}
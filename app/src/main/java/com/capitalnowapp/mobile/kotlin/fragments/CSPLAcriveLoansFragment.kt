package com.capitalnowapp.mobile.kotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentCSPLAcriveLoansBinding
import com.capitalnowapp.mobile.kotlin.adapters.EmiListAdapter
import com.capitalnowapp.mobile.models.offerModel.CSActiveLoanReq
import com.capitalnowapp.mobile.models.offerModel.CSActiveLoanResponse
import com.capitalnowapp.mobile.models.offerModel.CSInstallment
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class CSPLAcriveLoansFragment : Fragment() {
    private var binding: FragmentCSPLAcriveLoansBinding? = null
    private var csActiveLoanResponse = CSActiveLoanResponse()
    private var csEMIDetailsList : List<CSInstallment> = ArrayList()
    private var emiListAdapter: EmiListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
       binding = FragmentCSPLAcriveLoansBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        csActiveLoanData()
        binding!!.rvEmiDetails.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    private fun csActiveLoanData() {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(context, 0)
            val csActiveLoanReq = CSActiveLoanReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.csActiveLoan(csActiveLoanReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                csActiveLoanResponse =
                    Gson().fromJson(responseBody, CSActiveLoanResponse::class.java)
                if (csActiveLoanResponse.status == true) {
                    if(csActiveLoanResponse.data !=null){
                        binding?.llNoLoanContent?.visibility = View.GONE
                        binding?.llDetails?.visibility = View.VISIBLE
                        binding?.rvEmiDetails?.visibility = View.VISIBLE
                    }else {
                        binding?.llNoLoanContent?.visibility = View.VISIBLE
                        binding?.llDetails?.visibility = View.GONE
                        binding?.rvEmiDetails?.visibility = View.GONE
                    }
                    csEMIDetailsList = csActiveLoanResponse.data?.installments!!
                    emiListAdapter = EmiListAdapter(csEMIDetailsList)
                    binding!!.rvEmiDetails.adapter = emiListAdapter
                    setData()
                }else {
                    Toast.makeText(activity, csActiveLoanResponse.message, Toast.LENGTH_SHORT)
                        .show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setData() {
        try{
            binding?.tvLoanType?.text = "Personal Loan"
            binding?.tvLoanReason?.text = csActiveLoanResponse.data?.loanData?.reason
            binding?.tvLoanAmount?.text = csActiveLoanResponse.data?.loanData?.loanAmount.toString()
            binding?.tvEmiAmount?.text = csActiveLoanResponse.data?.loanData?.pclEmi.toString()
            binding?.tvTenure?.text = csActiveLoanResponse.data?.loanData?.pclTenure.toString()
            binding?.tvLoanId?.text = csActiveLoanResponse.data?.loanData?.loanId
            binding?.tvUtrNumber?.text = csActiveLoanResponse.data?.loanData?.utrNumber.toString()
            binding?.tvAutoDebit?.text = csActiveLoanResponse.data?.loanData?.autoDebit.toString()
            binding?.tvLoanProvider?.text = csActiveLoanResponse.data?.loanData?.loanIsOfferedBy
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}
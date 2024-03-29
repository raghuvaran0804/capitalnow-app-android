package com.capitalnowapp.mobile.kotlin.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentTransactionsBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.TransactionDataAdapter
import com.capitalnowapp.mobile.models.GetTransactionReq
import com.capitalnowapp.mobile.models.GetTransactionResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.google.gson.Gson

class TransactionsFragment : Fragment() {
    private var getTransactionResponse: GetTransactionResponse? = null
    private var binding: FragmentTransactionsBinding? = null
    private var activity: Activity? = null
    private var transactionDataAdapter: TransactionDataAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionsBinding.inflate(layoutInflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        try {
            setTransactionData()
            binding?.tvBack?.setOnClickListener {
                val intent = Intent(context, DashboardActivity::class.java)
                intent.putExtra("from", "deleteAccount")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTransactionData() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val getTransactionReq = GetTransactionReq()
            getTransactionReq.pageNo = "1"
            getTransactionReq.limit = "1000"
            val token = (activity as BaseActivity).userToken
            genericAPIService.setTransactionData(getTransactionReq, token)
            Log.d("transaction req", Gson().toJson(getTransactionReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                getTransactionResponse =
                    Gson().fromJson(responseBody, GetTransactionResponse::class.java)
                if (getTransactionResponse != null && getTransactionResponse!!.status == true) {
                    if(getTransactionResponse?.data?.isEmpty() == true){
                        binding?.rvTransactionData?.visibility = View.GONE
                        binding?.llNoTransactions?.visibility=View.VISIBLE
                    }else {
                        binding?.rvTransactionData?.visibility = View.VISIBLE
                        binding?.llNoTransactions?.visibility=View.GONE
                    }
                    val transactionData = getTransactionResponse?.data
                    binding?.rvTransactionData?.layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    transactionDataAdapter = TransactionDataAdapter(transactionData,this)
                    binding?.rvTransactionData?.adapter = transactionDataAdapter
                } else {
                    CNAlertDialog.showAlertDialog(
                        activity,
                        resources.getString(R.string.title_alert), getTransactionResponse?.message
                    )
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
}
package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.util.NetworkConnectionDetector


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NoInternetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoInternetFragment : Fragment() {
    private var currentActivity: Activity? = null
    var text :String = ""
    private var btTryAgain : Button?=null

    @SuppressLint("NotConstructor")
    fun NoInternetFragment() {
        // Required empty public constructor
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_no_internet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentActivity = activity
        btTryAgain=view.findViewById(R.id.btTryAgain)

        btTryAgain?.setOnClickListener {
            if (NetworkConnectionDetector(currentActivity).isNetworkConnected){

                (activity as DashboardActivity?)?.openParticularFragment(text)
            }
        }
        arguments?.let {
            val bundle = this.arguments
            text = bundle!!.getString("fragmentInstance")!!
        }
    }

}

package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.activities.LoginActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.kotlin.adapters.ProfileBasicAdapter
import com.capitalnowapp.mobile.models.ContactUsRequest
import com.capitalnowapp.mobile.models.profile.ProfileBasic
import com.capitalnowapp.mobile.models.profile.ProfileResponse
import com.capitalnowapp.mobile.models.userdetails.UserDetails
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.TrackingUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_profile.rvBasic
import org.json.JSONException
import org.json.JSONObject


class ProfileFragment : Fragment() {
    var sharedPreferences: CNSharedPreferences? = null
    var userDetails: UserDetails? = null

    @SuppressLint("NotConstructor")
    fun ProfileFragment() {
        // empty constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val obj = JSONObject()
        try {
            obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.profile_page_landed))

        getUserDetails()
    }

    private fun getUserDetails() {
        CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(activity)
        val req = ContactUsRequest()
        req.setUserId((activity as BaseActivity).userId)
        req.setApiKey(sharedPreferences?.getString(Constants.USER_TOKEN))
        val token = (activity as BaseActivity).userToken
        genericAPIService.getUserDetails(req, token)

        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val profileResponse = Gson().fromJson(responseBody, ProfileResponse::class.java)
            if (profileResponse != null && profileResponse.status == true) {
                setData(profileResponse.data)
            }else {
                if (profileResponse.statusCode == Constants.STATUS_CODE_UNAUTHORISED) {
                    logout()
                }
            }
        }

        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
        }
    }

    private fun logout() {
        try{
            val logInIntent = Intent(requireContext(), LoginActivity::class.java)
            logInIntent.flags =
                Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logInIntent)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    ////

    private fun setData(data: List<ProfileBasic>?) {
        try {
            rvBasic.layoutManager = LinearLayoutManager(context)
            rvBasic.adapter = ProfileBasicAdapter(data!!, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
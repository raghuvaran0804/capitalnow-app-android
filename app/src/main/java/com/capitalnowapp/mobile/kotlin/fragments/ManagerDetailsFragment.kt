package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ManagerDetailsFragBinding
import com.capitalnowapp.mobile.models.ContactUsRequest
import com.capitalnowapp.mobile.models.managerdetails.ManagerResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.google.gson.Gson
import kotlinx.android.synthetic.main.manager_details_frag.img
import kotlinx.android.synthetic.main.manager_details_frag.ivAccountIcon
import kotlinx.android.synthetic.main.manager_details_frag.llNoData
import kotlinx.android.synthetic.main.manager_details_frag.llRM
import kotlinx.android.synthetic.main.manager_details_frag.llRc
import kotlinx.android.synthetic.main.manager_details_frag.svData
import kotlinx.android.synthetic.main.manager_details_frag.tvAccountManager
import kotlinx.android.synthetic.main.manager_details_frag.tvCity
import kotlinx.android.synthetic.main.manager_details_frag.tvContactNo
import kotlinx.android.synthetic.main.manager_details_frag.tvHobbies
import kotlinx.android.synthetic.main.manager_details_frag.tvLanguage
import kotlinx.android.synthetic.main.manager_details_frag.tvName
import kotlinx.android.synthetic.main.manager_details_frag.tvNum
import kotlinx.android.synthetic.main.manager_details_frag.tvText
import kotlinx.android.synthetic.main.manager_details_frag.tvTitle
import kotlinx.android.synthetic.main.manager_details_frag.tvTitle1


class ManagerDetailsFragment : Fragment() {

    private lateinit var mActivityRef: BaseActivity
    private var binding: ManagerDetailsFragBinding? = null
    private var genericResponse: ManagerResponse = ManagerResponse()
    var sharedPreferences: CNSharedPreferences? = null

    @SuppressLint("NotConstructor")
    fun ManagerDetailsFragment() {
        // Required empty public constructor
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = ManagerDetailsFragBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            loadData()

            tvContactNo.setOnClickListener {
                if (genericResponse.relationshipManager != null) {
                    dialNum(genericResponse.relationshipManager!!.mobno)
                }
            }

            tvNum.setOnClickListener {
                if (genericResponse.recoveryOfficer != null) {
                    dialNum(genericResponse.recoveryOfficer!!.mobno)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dialNum(mobno: String?) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$mobno")
        startActivity(intent)
    }

    private fun loadData() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity)
            val req = ContactUsRequest()
            req.setUserId((mActivityRef).userId)
            req.setApiKey(sharedPreferences?.getString(Constants.USER_TOKEN))
            val token = (activity as BaseActivity).userToken
            genericAPIService.getManagerData(req, token)

            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                genericResponse = Gson().fromJson(responseBody, ManagerResponse::class.java)
                if (genericResponse != null && genericResponse.status == true) {
                    if (genericResponse.relationshipManager != null) {
                        setData()
                    } else {
                        tvTitle1.visibility = GONE
                        llRM.visibility = GONE
                    }
                } else {
                    if (genericResponse.statusCode == Constants.STATUS_CODE_UNAUTHORISED) {
                        (activity as BaseActivity).logout()

                    }
                    else{
                        if (genericResponse != null && genericResponse.phImage.isNotEmpty()){
                            Glide.with(mActivityRef).load(genericResponse.phImage).into(object : CustomTarget<Drawable>(){
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    Glide.with(mActivityRef).load(genericResponse.phImage).into(img)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }

                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    binding?.img?.setBackgroundResource(R.drawable.no_manager)
                                }

                            })
                        }else {
                            binding?.img?.setBackgroundResource(R.drawable.no_manager)
                        }

                        tvText.text = genericResponse.message
                        svData.visibility = GONE
                        llNoData.visibility = VISIBLE
                    }
                }
            }

            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                tvTitle1.visibility = GONE
                llRM.visibility = GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setData() {
        try {
            tvTitle.text = genericResponse.relationshipManager!!.title
            tvAccountManager.text = genericResponse.relationshipManager!!.name
            tvContactNo.text = genericResponse.relationshipManager!!.mobno
            tvHobbies.text = genericResponse.relationshipManager!!.hobbies
            tvCity.text = genericResponse.relationshipManager!!.city
            tvLanguage.text = genericResponse.relationshipManager!!.lang

            if (genericResponse.relationshipManager!!.customerRelationshipAvatar != null) {
                Glide.with(mActivityRef).load(genericResponse.relationshipManager!!.customerRelationshipAvatar).into(ivAccountIcon)
            }

            if (genericResponse.recoveryOfficer != null) {
                llRM.visibility = VISIBLE
                llRc.visibility = VISIBLE
                tvTitle1.visibility = VISIBLE
                tvTitle1.text = genericResponse.recoveryOfficer!!.title
                tvName.text = genericResponse.recoveryOfficer!!.name
                tvNum.text = genericResponse.recoveryOfficer!!.mobno
            } else {
                tvTitle1.visibility = GONE
                llRM.visibility = GONE
                llRc.visibility = GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivityRef = context as BaseActivity
    }
}

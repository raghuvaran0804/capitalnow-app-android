package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentReferencesNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.models.SaveReferencesReq
import com.capitalnowapp.mobile.models.SaveReferencesResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject


class ReferencesNewFragment : Fragment() {

    private var refRequestCount: String? = null
    private var binding: FragmentReferencesNewBinding? = null
    private var activity: Activity? = null
    private var etName: String? = ""
    private var et1Name: String? = ""
    private var et2Name: String? = ""
    private var et3Name: String? = ""
    private var et4Name: String? = ""
    private var etNumber1: String? = ""
    private var et1Number: String? = ""
    private var et2Number: String? = ""
    private var et3Number: String? = ""
    private var et4Number: String? = ""
    private var etRelation1: String? = ""
    private var et1Relation: String? = ""
    private var et2Relation: String? = ""
    private var et3Relation: String? = ""
    private var et4Relation: String? = ""
    private var etName2: String? = ""
    private var etNumber2: String? = ""
    private var etRelation2: String? = ""
    private var etName3: String? = ""
    private var etNumber3: String? = ""
    private var etName4: String? = ""
    private var etNumber4: String? = ""
    private var etName5: String? = ""
    private var etNumber5: String? = ""
    private var validationMsg = ""
    private var relation1ListMap: LinkedHashMap<String, String>? = null
    var relation1ListKeys: Array<String>? = null
    private var relation2ListMap: LinkedHashMap<String, String>? = null
    var relation2ListKeys: Array<String>? = null
    private var newRelationListMap: LinkedHashMap<String, String>? = null
    var newRelationListKeys: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReferencesNewBinding.inflate(inflater, container, false)
        activity = getActivity()

        if ((activity as DashboardActivity).userDetails.references == false) {
            binding?.llPercentage?.visibility = View.GONE
            binding?.circularProgressBar?.progress = 80
            binding?.txtProgressCircular?.text = "80%"
        } else {
            binding?.llPercentage?.visibility = View.GONE
        }

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        try {

            val obj = JSONObject()
            try {
                obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.references_page_landed))

            binding?.tvBack?.setOnClickListener {
                /*val intent = Intent(context, DashboardActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)*/
                //(activity as DashboardActivity).onBackPressed()
                if ((activity as BaseActivity).sharedPreferences.getBoolean("fromDocs")) {
                    (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", false)
                    (activity as DashboardActivity).onBackPressed()
                } else {
                    val intent = Intent(context, DashboardActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }



            if (arguments != null) {
                refRequestCount = requireArguments().getString("ref_request_count")
                if (refRequestCount == "5" || refRequestCount!! == "0" || refRequestCount!! == "" || refRequestCount!! == null) {
                    binding?.llReferences?.visibility = View.VISIBLE
                    binding?.llReferences1?.visibility = View.GONE
                } else if (refRequestCount == "4") {
                    binding?.llReferences?.visibility = View.GONE
                    binding?.llReferences1?.visibility = View.VISIBLE
                } else if (refRequestCount == "3") {
                    binding?.llReferences?.visibility = View.GONE
                    binding?.llReferences1?.visibility = View.VISIBLE
                    binding?.cv4?.visibility = View.GONE
                } else if (refRequestCount == "2") {
                    binding?.llReferences?.visibility = View.GONE
                    binding?.llReferences1?.visibility = View.VISIBLE
                    binding?.cv4?.visibility = View.GONE
                    binding?.cv3?.visibility = View.GONE
                } else if (refRequestCount == "1") {
                    binding?.llReferences?.visibility = View.GONE
                    binding?.llReferences1?.visibility = View.VISIBLE
                    binding?.cv4?.visibility = View.GONE
                    binding?.cv3?.visibility = View.GONE
                    binding?.cv2?.visibility = View.GONE

                }
            }else {
                binding?.llReferences?.visibility = View.VISIBLE
                binding?.llReferences1?.visibility = View.GONE
            }
            /*if(arguments != null && refRequestCount == null){
                refRequestCount = "5"
                if (refRequestCount == "5" || refRequestCount!! == "0" || refRequestCount!! == "") {
                    binding?.llReferences?.visibility = View.VISIBLE
                    binding?.llReferences1?.visibility = View.GONE
                }
            }*/

            binding?.tvNext?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"NEXT Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))

                validateReferences()

            }
            binding?.etNumber1?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Add First Reference Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))
                /*val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(intent, 1)*/
            }
            binding?.etNumber2?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Add Second Reference Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))

                /*val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(intent, 2)*/
            }
            binding?.etNumber3?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Add Third Reference Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))

                /*val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(intent, 3)*/
            }
            binding?.etNumber4?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Add Fourth Reference Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))

                /*val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(intent, 4)*/
            }
            binding?.etNumber5?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Add Fifth Reference Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))

                /*val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(intent, 5)*/
            }

            binding?.et1Number?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Add First Reference Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))

                val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(intent, 6)
            }

            binding?.et2Number?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Add Second Reference Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))

                val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(intent, 7)
            }

            binding?.et3Number?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Add Third Reference Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))

                val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(intent, 8)
            }

            binding?.et4Number?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Add Fourth Reference Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.references_page_interacted))

                val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(intent, 9)
            }

            relation1ListMap = LinkedHashMap<String, String>()
            relation1ListMap!!["Father"] = "Father"
            relation1ListMap!!["Mother"] = "Mother"
            relation1ListMap!!["Spouse"] = "Spouse"
            relation1ListKeys = relation1ListMap!!.keys.toTypedArray()

            binding?.etRelation1?.setOnClickListener {
                relation1Dialog()

            }
            relation2ListMap = LinkedHashMap<String, String>()
            relation2ListMap!!["Father"] = "Father"
            relation2ListMap!!["Mother"] = "Mother"
            relation2ListMap!!["Spouse"] = "Spouse"
            relation2ListMap!!["Brother"] = "Brother"
            relation2ListMap!!["Sister"] = "Sister"
            relation2ListKeys = relation2ListMap!!.keys.toTypedArray()

            binding?.etRelation2?.setOnClickListener {
                    relation2Dialog()

            }

            newRelationListMap = LinkedHashMap<String, String>()
            newRelationListMap!!["Father"] = "Father"
            newRelationListMap!!["Mother"] = "Mother"
            newRelationListMap!!["Spouse"] = "Spouse"
            newRelationListMap!!["Colleague"] = "Colleague"
            newRelationListMap!!["Friend"] = "Friend"
            newRelationListKeys = newRelationListMap!!.keys.toTypedArray()

            binding?.et1Relation?.setOnClickListener {
                    new1RelationDialog()

            }
            binding?.et2Relation?.setOnClickListener {
                    new2RelationDialog()

            }
            binding?.et3Relation?.setOnClickListener {
                    new3RelationDialog()

            }
            binding?.et4Relation?.setOnClickListener {

                new4RelationDialog()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun relation2Dialog() {
        var relation2ListKeysTemp = relation2ListKeys
        val categoryList = relation2ListKeysTemp?.toCollection(ArrayList())
        var index = -1
        if (etRelation1 != null && etRelation1 != "") {
            for (rel in relation2ListKeysTemp?.withIndex()!!) {
                if (rel.value == etRelation1) {
                    index = rel.index
                    break
                }
            }
        } else {
            //relation2ListKeysTemp = relation2ListKeys
        }
        if (index >= 0) {
            categoryList?.removeAt(index)
            relation2ListKeysTemp = categoryList?.toTypedArray()
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setItems(relation2ListKeysTemp) { _, which ->
            binding?.etRelation2?.setText(relation2ListKeysTemp?.get(which))
            etRelation2 =
                relation2ListMap?.get(relation2ListKeysTemp?.get(which)!!)
        }
        builder.show()
    }

    private fun relation1Dialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setItems(relation1ListKeys) { _, which ->
            binding?.etRelation1?.setText(relation1ListKeys?.get(which))
            etRelation1 =
                relation1ListMap?.get(relation1ListKeys?.get(which)!!)
            etRelation2 = ""
            binding?.etRelation2?.setText("")
        }
        builder.show()
    }

    private fun new1RelationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setItems(newRelationListKeys) { _, which ->
            binding?.et1Relation?.setText(newRelationListKeys?.get(which))
            et1Relation =
                newRelationListMap?.get(newRelationListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun new2RelationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setItems(newRelationListKeys) { _, which ->
            binding?.et2Relation?.setText(newRelationListKeys?.get(which))
            et2Relation =
                newRelationListMap?.get(newRelationListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun new3RelationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setItems(newRelationListKeys) { _, which ->
            binding?.et3Relation?.setText(newRelationListKeys?.get(which))
            et3Relation =
                newRelationListMap?.get(newRelationListKeys?.get(which)!!)
        }
        builder.show()
    }

    private fun new4RelationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setItems(newRelationListKeys) { _, which ->
            binding?.et4Relation?.setText(newRelationListKeys?.get(which))
            et4Relation =
                newRelationListMap?.get(newRelationListKeys?.get(which)!!)
        }
        builder.show()
    }


    private fun validateReferences() {
        try {
            etName = binding?.etName?.text.toString().trim { it <= ' ' }
            etNumber1 = binding?.etNumber1?.text.toString().trim { it <= ' ' }
            etRelation1 = binding?.etRelation1?.text.toString().trim { it <= ' ' }

            etName2 = binding?.etName2?.text.toString().trim { it <= ' ' }
            etNumber2 = binding?.etNumber2?.text.toString().trim { it <= ' ' }
            etRelation2 = binding?.etRelation2?.text.toString().trim { it <= ' ' }

            etName3 = binding?.etName3?.text.toString().trim { it <= ' ' }
            etNumber3 = binding?.etNumber3?.text.toString().trim { it <= ' ' }

            etName4 = binding?.etName4?.text.toString().trim { it <= ' ' }
            etNumber4 = binding?.etNumber4?.text.toString().trim { it <= ' ' }

            etName5 = binding?.etName5?.text.toString().trim { it <= ' ' }
            etNumber5 = binding?.etNumber5?.text.toString().trim { it <= ' ' }

            et1Name = binding?.et1Name?.text.toString().trim { it <= ' ' }
            et2Name = binding?.et2Name?.text.toString().trim { it <= ' ' }
            et3Name = binding?.et3Name?.text.toString().trim { it <= ' ' }
            et4Name = binding?.et4Name?.text.toString().trim { it <= ' ' }

            et1Number = binding?.et1Number?.text.toString().trim { it <= ' ' }
            et2Number = binding?.et2Number?.text.toString().trim { it <= ' ' }
            et3Number = binding?.et3Number?.text.toString().trim { it <= ' ' }
            et4Number = binding?.et4Number?.text.toString().trim { it <= ' ' }

            et1Relation = binding?.et1Relation?.text.toString().trim { it <= ' ' }
            et2Relation = binding?.et2Relation?.text.toString().trim { it <= ' ' }
            et3Relation = binding?.et3Relation?.text.toString().trim { it <= ' ' }
            et4Relation = binding?.et4Relation?.text.toString().trim { it <= ' ' }

            if (refRequestCount == "5" || refRequestCount == null) {
                var count = 0
                if (etName!!.isEmpty()) {
                    validationMsg = "#1 Family Name is required and can't be empty"
                    count++
                } else if (etNumber1!!.isEmpty()) {
                    validationMsg = "#1 Family Number is required and can't be empty"
                    count++
                } else if (etRelation1!!.isEmpty()) {
                    validationMsg = "#1 Family Relation is required and can't be empty"
                    count++
                } else if (etName2!!.isEmpty()) {
                    validationMsg = "#2 Family Name is required and can't be empty"
                    count++
                } else if (etNumber2!!.isEmpty()) {
                    validationMsg = "#2 Family Number is required and can't be empty"
                    count++
                } else if (etRelation2!!.isEmpty()) {
                    validationMsg = "#2 Family Relation is required and can't be empty"
                    count++
                } else if (etName3!!.isEmpty()) {
                    validationMsg = "#3 Office Contact Name is required and can't be empty"
                    count++
                } else if (etNumber3!!.isEmpty()) {
                    validationMsg = "#3 Office Contact Number is required and can't be empty"
                    count++
                } else if (etName4!!.isEmpty()) {
                    validationMsg = "#4 Office Contact Name is required and can't be empty"
                    count++
                } else if (etNumber4!!.isEmpty()) {
                    validationMsg = "#4 Office Contact Number is required and can't be empty"
                    count++
                } else if (etName5!!.isEmpty()) {
                    validationMsg = "#5 Friend Contact Name is required and can't be empty"
                    count++
                } else if (etNumber5!!.isEmpty()) {
                    validationMsg = "#5 Friend Contact Number is required and can't be empty"
                    count++
                }
                if (count > 0) {
                    Toast.makeText(context, validationMsg, Toast.LENGTH_SHORT).show()
                } else {
                    saveReferences()
                }
            } else if (refRequestCount == "4") {
                var count = 0
                if (et1Name!!.isEmpty()) {
                    validationMsg = "#1 Contact Name is required and can't be empty"
                    count++
                } else if (et1Number!!.isEmpty()) {
                    validationMsg = "#1 Contact Number is required and can't be empty"
                    count++
                } else if (et1Relation!!.isEmpty()) {
                    validationMsg = "#1 Contact Relation is required and can't be empty"
                    count++
                } else if (et2Name!!.isEmpty()) {
                    validationMsg = "#2 Contact Name is required and can't be empty"
                    count++
                } else if (et2Number!!.isEmpty()) {
                    validationMsg = "#2 Contact Number is required and can't be empty"
                    count++
                } else if (et2Relation!!.isEmpty()) {
                    validationMsg = "#2 Contact Relation is required and can't be empty"
                    count++
                } else if (et3Name!!.isEmpty()) {
                    validationMsg = "#3 Contact Name is required and can't be empty"
                    count++
                } else if (et3Number!!.isEmpty()) {
                    validationMsg = "#3 Contact Number is required and can't be empty"
                    count++
                } else if (et3Relation!!.isEmpty()) {
                    validationMsg = "#3 Contact Relation is required and can't be empty"
                    count++
                } else if (et4Name!!.isEmpty()) {
                    validationMsg = "#4 Contact Name is required and can't be empty"
                    count++
                } else if (et4Number!!.isEmpty()) {
                    validationMsg = "#4 Contact Number is required and can't be empty"
                    count++
                } else if (et4Relation!!.isEmpty()) {
                    validationMsg = "#4 Contact Relation is required and can't be empty"
                    count++
                }
                if (count > 0) {
                    Toast.makeText(context, validationMsg, Toast.LENGTH_SHORT).show()
                } else {
                    saveReferences()
                }

            } else if (refRequestCount == "3") {
                var count = 0
                if (et1Name!!.isEmpty()) {
                    validationMsg = "#1 Contact Name is required and can't be empty"
                    count++
                } else if (et1Number!!.isEmpty()) {
                    validationMsg = "#1 Contact Number is required and can't be empty"
                    count++
                } else if (et1Relation!!.isEmpty()) {
                    validationMsg = "#1 Contact Relation is required and can't be empty"
                    count++
                } else if (et2Name!!.isEmpty()) {
                    validationMsg = "#2 Contact Name is required and can't be empty"
                    count++
                } else if (et2Number!!.isEmpty()) {
                    validationMsg = "#2 Number is required and can't be empty"
                    count++
                } else if (et2Relation!!.isEmpty()) {
                    validationMsg = "#2 Contact Relation is required and can't be empty"
                    count++
                } else if (et3Name!!.isEmpty()) {
                    validationMsg = "#3 Contact Name is required and can't be empty"
                    count++
                } else if (et3Number!!.isEmpty()) {
                    validationMsg = "#3 Contact Number is required and can't be empty"
                    count++
                } else if (et3Relation!!.isEmpty()) {
                    validationMsg = "#3 Contact Relation is required and can't be empty"
                    count++
                }
                if (count > 0) {
                    Toast.makeText(context, validationMsg, Toast.LENGTH_SHORT).show()
                } else {
                    saveReferences()
                }

            } else if (refRequestCount == "2") {
                var count = 0
                if (et1Name!!.isEmpty()) {
                    validationMsg = "#1 Contact Name is required and can't be empty"
                    count++
                } else if (et1Number!!.isEmpty()) {
                    validationMsg = "#1 Contact Number is required and can't be empty"
                    count++
                } else if (et1Relation!!.isEmpty()) {
                    validationMsg = "#1 Contact Relation is required and can't be empty"
                    count++
                } else if (et2Name!!.isEmpty()) {
                    validationMsg = "#2 Contact Name is required and can't be empty"
                    count++
                } else if (et2Number!!.isEmpty()) {
                    validationMsg = "#2 Contact Number is required and can't be empty"
                    count++
                } else if (et2Relation!!.isEmpty()) {
                    validationMsg = "#2 Contact Relation is required and can't be empty"
                    count++
                }
                if (count > 0) {
                    Toast.makeText(context, validationMsg, Toast.LENGTH_SHORT).show()
                } else {
                    saveReferences()
                }

            } else if (refRequestCount == "1") {
                var count = 0
                if (et1Name!!.isEmpty()) {
                    validationMsg = "#1 Contact Name is required and can't be empty"
                    count++
                } else if (et1Number!!.isEmpty()) {
                    validationMsg = "#1 Contact Number is required and can't be empty"
                    count++
                } else if (et1Relation!!.isEmpty()) {
                    validationMsg = "#1 Contact Relation is required and can't be empty"
                    count++
                }
                if (count > 0) {
                    Toast.makeText(context, validationMsg, Toast.LENGTH_SHORT).show()
                } else {
                    saveReferences()
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun saveReferences() {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity,0)
            val saveReferencesReq = SaveReferencesReq()
            saveReferencesReq.userId = (activity as BaseActivity).userDetails.userId
            saveReferencesReq.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
            if (refRequestCount == "5" || refRequestCount == null) {
                saveReferencesReq.contactName1 = etName
                saveReferencesReq.contactName2 = etName2
                saveReferencesReq.contactName3 = etName3
                saveReferencesReq.contactName4 = etName4
                saveReferencesReq.contactName5 = etName5
                saveReferencesReq.contactNo1 = etNumber1
                saveReferencesReq.contactNo2 = etNumber2
                saveReferencesReq.contactNo3 = etNumber3
                saveReferencesReq.contactNo4 = etNumber4
                saveReferencesReq.contactNo5 = etNumber5
                saveReferencesReq.contactRelation1 = etRelation1
                saveReferencesReq.contactRelation2 = etRelation2
                saveReferencesReq.contactRelation3 = "colleague1"
                saveReferencesReq.contactRelation4 = "colleague2"
                saveReferencesReq.contactRelation5 = "friend"
            } else {
                saveReferencesReq.contactName1 = et1Name
                saveReferencesReq.contactName2 = et2Name
                saveReferencesReq.contactName3 = et3Name
                saveReferencesReq.contactName4 = et4Name
                saveReferencesReq.contactNo1 = et1Number
                saveReferencesReq.contactNo2 = et2Number
                saveReferencesReq.contactNo3 = et3Number
                saveReferencesReq.contactNo4 = et4Number
                saveReferencesReq.contactRelation1 = et1Relation
                saveReferencesReq.contactRelation2 = et2Relation
                saveReferencesReq.contactRelation3 = et3Relation
                saveReferencesReq.contactRelation4 = et4Relation
            }
            val token = (activity as BaseActivity).userToken
            genericAPIService.saveReferences(saveReferencesReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val saveReferencesResponse =
                    Gson().fromJson(responseBody, SaveReferencesResponse::class.java)
                if (saveReferencesResponse != null && saveReferencesResponse.status == Constants.STATUS_SUCCESS) {
                    //updateSaveFiveReferencesStatus(saveReferencesResponse);
                    if((activity as BaseActivity).sharedPreferences.getBoolean("fromDocs")){
                        (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", false)
                        (activity as DashboardActivity).onBackPressed()
                    }else {
                        val intent = Intent(context, DashboardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    CNAlertDialog.showAlertDialog(
                        context,
                        resources.getString(R.string.title_alert),
                        saveReferencesResponse.message
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
            CNProgressDialog.hideProgressDialog()
            e.printStackTrace()
        }
    }

    private fun updateSaveFiveReferencesStatus(saveReferencesResponse: SaveReferencesResponse) {
        CNProgressDialog.hideProgressDialog()
        try {

            val status = saveReferencesResponse.status
            if (status == Constants.STATUS_SUCCESS) {
                goToProfile()
            } else {
                CNAlertDialog.showAlertDialog(
                    context,
                    resources.getString(R.string.title_alert),
                    saveReferencesResponse.message
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goToProfile() {
        (activity as DashboardActivity).selectedTab = getString(R.string.home)
        (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
        (activity as DashboardActivity).fromReference = true
        (activity as DashboardActivity).getApplyLoanData(false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            val contactUri = data?.getData()
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val cursor = contactUri?.let {
                requireActivity().contentResolver.query(
                    it, projection,
                    null, null, null
                )
            };
            if (cursor != null && cursor.moveToFirst()) {
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                var number = cursor.getString(numberIndex)
                number = number.replace(" ", "")
                if (number.length > 10) {
                    //number = number.substring(number.length, -10)
                    number = number.substring(Math.max(number.length - 10, 0))
                }
                when (requestCode) {
                    1 -> {
                        binding?.etNumber1?.setText(number)
                    }
                    2 -> {
                        binding?.etNumber2?.setText(number)
                    }
                    3 -> {
                        binding?.etNumber3?.setText(number)
                    }
                    4 -> {
                        binding?.etNumber4?.setText(number)
                    }
                    5 -> {
                        binding?.etNumber5?.setText(number)
                    }
                    6 -> {
                        binding?.et1Number?.setText(number)
                    }
                    7 -> {
                        binding?.et2Number?.setText(number)
                    }
                    8 -> {
                        binding?.et3Number?.setText(number)
                    }
                    9 -> {
                        binding?.et4Number?.setText(number)
                    }
                }


            }
        }
    }
}

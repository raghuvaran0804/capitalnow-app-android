package com.capitalnowapp.mobile.kotlin.fragments

//import com.appsflyer.AppsFlyerLib
//import io.branch.referral.util.BranchEvent
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentReferToEarnBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.activities.HistoryReferActivity
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.TrackingUtil
import kotlinx.android.synthetic.main.activity_dash_new.*
import org.json.JSONException
import org.json.JSONObject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReferToEarnFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReferToEarnFragment : Fragment() {

    var binding: FragmentReferToEarnBinding? = null

    private var myClipboard: ClipboardManager? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var currentActivity: Activity? = null
    private var userId: String? = null
    private var refer_code: String? = null
    private var refer_code_header_msg: String? = null
    private var refer_code_sms_msg: String? = null
    private var refer_code_email_msg: String? = null
    private var refer_code_social_msg: String? = null
    private var CNModel: CNModel? = null
    var sharedPreferences: CNSharedPreferences? = null

    @SuppressLint("NotConstructor")
    fun ReferToEarnFragment() {
        // empty constructor
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myClipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?


        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentReferToEarnBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val obj = JSONObject()
        try {
            obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.refer_N_earn_page_landed))


        currentActivity = activity
        userId = (currentActivity as BaseActivity).userDetails.userId

        CNModel = CNModel(context, currentActivity, Constants.RequestFrom.REFER_AND_EARN)
        getReferralCode()

        if ((currentActivity as BaseActivity).userDetails.userStatusId!! == "12") {
            binding?.tvApplyLoan!!.visibility = View.INVISIBLE
        } else {
            binding?.tvApplyLoan!!.visibility = View.GONE
        }

        binding!!.tvApplyLoan.setOnClickListener {
            (currentActivity as DashboardActivity).tvApplyLoan.callOnClick()
        }

        binding?.ivMenu?.setOnClickListener(View.OnClickListener {
            if (activity is DashboardActivity) {
                (activity as DashboardActivity).drawer_layout.openDrawer(GravityCompat.START)
            }
        })
        binding?.tvHistory?.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, HistoryReferActivity::class.java)
            (activity as DashboardActivity).startActivityForResult(intent, (activity as DashboardActivity).APPLY_LOAN_REQUEST_CODE)
        })
        binding?.ivWhatsApp?.setOnClickListener(View.OnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "WhatsApp Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.refer_N_earn_page_interacted))

            try {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TITLE, "CapitalNow App")
                sendIntent.putExtra(Intent.EXTRA_TEXT, refer_code_sms_msg)
                sendIntent.type = "text/plain"
                sendIntent.setPackage("com.whatsapp")
                startActivity(sendIntent)
                //adgydeCounting(getString(R.string.refer_earn_value_wp))
            } catch (e: Exception) {
                Toast.makeText(context, "Whatsapp not install", Toast.LENGTH_LONG).show()

            }

        })
        binding?.ivFacebookApp?.setOnClickListener(View.OnClickListener {
            try {
                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type), "Message Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.refer_N_earn_page_interacted))


                val uri = Uri.parse("smsto:")
                val it = Intent(Intent.ACTION_SENDTO, uri)
                it.putExtra("sms_body", refer_code_sms_msg)
                startActivity(it)
                //adgydeCounting(getString(R.string.refer_earn_value_sms))
//            try {
//                TrackingUtil.getInstance().logEvent(TrackingUtil.Event.REFER_EARN)
//                doSocialShare()
//            } catch (e: Exception) {
//                //e.toString();
//            }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        binding?.ivOthers?.setOnClickListener(View.OnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Others Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.refer_N_earn_page_interacted))

            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TITLE, "CapitalNow App")
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CapitalNow App - Refer & Earn")
                shareIntent.putExtra(Intent.EXTRA_TEXT, refer_code_sms_msg)
                shareIntent.putExtra(Constants.BUNDLE_REFER_CODE_SMS_MSG, refer_code_sms_msg)
                shareIntent.putExtra(Constants.BUNDLE_REFER_CODE_EMAIL_MSG, refer_code_email_msg)
                shareIntent.putExtra(Constants.BUNDLE_REFER_CODE_SOCIAL_MSG, refer_code_social_msg)
                var shareMessage = refer_code_sms_msg
                shareMessage = """
                    ${shareMessage}
                    

                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
                //adgydeCounting(getString(R.string.refer_earn_value_others))
            } catch (e: Exception) {
                //e.toString();
            }
        })

        binding?.ivCopy?.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "COPY CODE Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.refer_N_earn_page_interacted))
            copyText()
        }
    }

    private fun copyText() {
        val myClip = ClipData.newPlainText("text", binding?.tvReferralCode?.text.toString())
        myClipboard?.setPrimaryClip(myClip)
        Toast.makeText(context, "Referral Code Copied",
                Toast.LENGTH_LONG).show()
    }

    fun showAlertDialog(message: String?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(context, resources.getString(R.string.title_alert), message)
    }

    fun getReferralCode() {
        CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
        val token = (activity as BaseActivity).userToken
        CNModel!!.getReferralCode(this, userId,token)

    }

    fun doSocialShare() {
        try {
            val intent1 = Intent()
            intent1.setPackage("com.facebook.katana")
            intent1.action = "android.intent.action.SEND"
            intent1.type = "text/plain"
            intent1.putExtra("android.intent.extra.TEXT", refer_code_sms_msg)
            startActivity(intent1)
        } catch (e: java.lang.Exception) {
            // If we failed (not native FB app installed), try share through SEND
            val sharerUrl = "https://www.facebook.com/sharer/sharer.php?$refer_code_sms_msg"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
            startActivity(intent)
        }

    }


    fun updateReferralCode(response: JSONObject) {
        try {
            refer_code = response.getString("refer_code")
            this.refer_code_header_msg = response.getString("refer_code_header_msg")
            this.refer_code_sms_msg = response.getString("refer_code_sms_msg")
            this.refer_code_email_msg = response.getString("refer_code_email_msg")
            this.refer_code_social_msg = response.getString("refer_code_social_msg")
            binding?.tvReferralMsg?.text = refer_code_header_msg
            binding?.tvReferralCode?.text = refer_code

            //message = refer_code_social_msg + " is " + refer_code + ". Install app " + Constants.APP_LINK + "&referrer=" + refer_code;
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        CNProgressDialog.hideProgressDialog()
    }

    /*private fun adgydeCounting(value: String) {
        val params = HashMap<String, Any>()
        val key = getString(R.string.refer_earn_key)
        params[key] = value //patrametre name,value change to event
        AppsFlyerLib.getInstance().logEvent(activity as DashboardActivity,  key, params)

        val logger = AppEventsLogger.newLogger(this.context)
        val bundle = Bundle()
        bundle.putString(key,value)
        logger.logEvent(getString(R.string.refer_earn_key), bundle)

        BranchEvent("ReferAndEarn")
            .addCustomDataProperty("ReferAndEarn", "Refer_And_Earn")
            .setCustomerEventAlias("Refer_And_Earn")
            .logEvent(activity as DashboardActivity)
    }*/
}

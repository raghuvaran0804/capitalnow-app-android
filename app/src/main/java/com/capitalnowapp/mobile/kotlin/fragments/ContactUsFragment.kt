package com.capitalnowapp.mobile.kotlin.fragments

//import com.appsflyer.AppsFlyerLib
//import io.branch.referral.util.BranchEvent
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.FragmentContactUsBinding
import com.capitalnowapp.mobile.models.ContactUsRequest
import com.capitalnowapp.mobile.models.ContactUsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_contact_us.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactUsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactUsFragment : Fragment(), View.OnClickListener {

    private var genericResponse: ContactUsResponse = ContactUsResponse()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var binding: FragmentContactUsBinding? = null
    private var currentActivity: Activity? = null
    private var userId: String? = null
    var sharedPreferences: CNSharedPreferences? = null

    @SuppressLint("NotConstructor")
    fun ContactUsFragment() {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentContactUsBinding.inflate(inflater, container, false)
        currentActivity = activity
        userId = (currentActivity as BaseActivity).userDetails.userId

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*   binding?.ivFb?.setOnClickListener(this)
           binding?.ivInstagram?.setOnClickListener(this)
           binding?.ivTwitter?.setOnClickListener(this)
           binding?.ivYoutube?.setOnClickListener(this)
           binding?.ivLn?.setOnClickListener(this)
           binding?.ivGmail?.setOnClickListener(this)
           binding?.ivOutlook?.setOnClickListener(this)*/
        try {
            getData()

            llEMail.setOnClickListener {
                composeEmail()
            }

            llPhone.setOnClickListener {
                callToNum()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callToNum() {
        try {
            if (genericResponse.phone != "") {
                val num = genericResponse.phone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$num")
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun composeEmail() {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", genericResponse.email, null))
            startActivity(Intent.createChooser(emailIntent, "Choose to send email..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getData() {
        try {
            CNProgressDialog.showProgressDialog(currentActivity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(currentActivity,0)
            val req = ContactUsRequest()
            req.setUserId((currentActivity as BaseActivity).userId)
            req.setApiKey(sharedPreferences?.getString(Constants.USER_TOKEN))
            val token = (currentActivity as BaseActivity).userToken
            genericAPIService.getContactUsData(req, token)

            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                try {
                    genericResponse = Gson().fromJson(responseBody, ContactUsResponse::class.java)
                    if (genericResponse.status == true) {
                        if (genericResponse.email!!.isNotEmpty()) {
                            llEMail?.visibility = VISIBLE
                            tvEmail.text = genericResponse.email
                        } else {
                            llEMail?.visibility = GONE
                        }
                        if (genericResponse.phone!!.isNotEmpty()) {
                            llPhone?.visibility = VISIBLE
                            tvPhone.text = genericResponse.phone
                        } else {
                            llPhone?.visibility = GONE
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            /*when (v?.id) {
                R.id.ivFb -> {
                    if (isAppInstalled(AppConstants.ContactUsPackages.FB)) {
                        currentActivity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + AppConstants.ContactUsPages.FB)))
                    } else {
                        currentActivity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + AppConstants.ContactUsPages.FB)))
                    }
                }
                R.id.ivInstagram -> {
                    val uri = Uri.parse("http://instagram.com/" + AppConstants.ContactUsPages.Insta)
                    if (isAppInstalled(AppConstants.ContactUsPackages.Insta)) {
                        val insta = Intent(Intent.ACTION_VIEW, uri)
                        insta.setPackage(AppConstants.ContactUsPackages.Insta)
                        currentActivity?.startActivity(insta)
                    } else {
                        currentActivity?.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                }
                R.id.ivYoutube -> {
                    val intent: Intent? = null
                    if (isAppInstalled(AppConstants.ContactUsPackages.Youtube)) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setPackage(AppConstants.ContactUsPackages.Youtube)
                        intent.data = Uri.parse("https://www.youtube.com/channel/" + AppConstants.ContactUsPages.Youtube)
                        startActivity(intent)
                    } else {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/channel/" + AppConstants.ContactUsPages.Youtube)))
                    }
                }
                R.id.ivTwitter -> {
                    var intent: Intent? = null
                    if (isAppInstalled(AppConstants.ContactUsPackages.Twitter)) {
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + AppConstants.ContactUsPages.Twitter))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + AppConstants.ContactUsPages.Twitter))
                        startActivity(intent)
                    }
                }
                R.id.ivLn -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val urlBrowser = "http://www.linkedin.com/company/" + AppConstants.ContactUsPages.LinkedIN
                    intent.data = Uri.parse(urlBrowser)
                    if (isAppInstalled(AppConstants.ContactUsPackages.LinkedIN)) {
                        intent.setPackage(AppConstants.ContactUsPackages.LinkedIN)
                        startActivity(intent)
                    } else {
                        startActivity(intent)
                    }
                }
                R.id.ivGmail -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "message/rfc822"
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@capitalnow.in"))
                    intent.putExtra(Intent.EXTRA_SUBJECT, "")
                    intent.setPackage("com.google.android.gm")
                    startActivity(intent)
                    adgydeCounting(getString(R.string.contact_us_gmail))
                }
                R.id.ivOutlook -> {
                    var intent: Intent? = null
                    if (isAppInstalled(AppConstants.ContactUsPackages.Outlook)) {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "message/rfc822"
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@capitalnow.in"))
                        intent.putExtra(Intent.EXTRA_SUBJECT, "")
                        intent.setPackage(AppConstants.ContactUsPackages.Outlook)
                        startActivity(intent)
                        adgydeCounting(getString(R.string.contact_us_outlook))
                    } else {
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://outlook.live.com/"))
                        startActivity(intent)
                        adgydeCounting(getString(R.string.contact_us_outlook))
                    }
                }
            }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isAppInstalled(pack: String): Boolean {
        return try {
            context?.packageManager?.getApplicationInfo(pack, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /*fun adgydeCounting(value: String) {
        val params = HashMap<String, Any>()
        val key = getString(R.string.contact_us_key)
        params[key] = value //patrametre name,value change to event
        //  AdGyde.onCountingEvent(key, params) //eventid,params
        AppsFlyerLib.getInstance().logEvent(activity as DashboardActivity, key, params)
        val logger = AppEventsLogger.newLogger(this.context)
        val bundle = Bundle()
        bundle.putString(key,key)
        logger.logEvent(getString(R.string.contact_us_key), bundle)

        BranchEvent("ContactUs")
            .addCustomDataProperty("ContactUs", "Contact_Us")
            .setCustomerEventAlias("Contact_Us")
            .logEvent(activity as DashboardActivity)
    }*/
}

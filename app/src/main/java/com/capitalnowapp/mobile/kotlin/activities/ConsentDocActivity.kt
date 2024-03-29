package com.capitalnowapp.mobile.kotlin.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.LoanAgreementConsent
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.util.TrackingUtil
import kotlinx.android.synthetic.main.activity_consent_doc.cbAgreeTerms
import kotlinx.android.synthetic.main.activity_consent_doc.code
import kotlinx.android.synthetic.main.activity_consent_doc.ivGif
import kotlinx.android.synthetic.main.activity_consent_doc.llAccessLock
import kotlinx.android.synthetic.main.activity_consent_doc.llData
import kotlinx.android.synthetic.main.activity_consent_doc.llScroll
import kotlinx.android.synthetic.main.activity_consent_doc.tvOk
import kotlinx.android.synthetic.main.activity_consent_doc.tvScrollText
import kotlinx.android.synthetic.main.activity_consent_doc.tv_privacy_link
import kotlinx.android.synthetic.main.activity_consent_doc.viewHover
import kotlinx.android.synthetic.main.activity_consent_doc.viewHover1
import kotlinx.android.synthetic.main.activity_consent_doc.webView
import kotlinx.android.synthetic.main.fragment_home.tvViewConsent
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference


class ConsentDocActivity : BaseActivity() {
    var canRedirect: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent_doc)
        initView()
    }

    private fun initView() {
        try {

            val obj = JSONObject()
            try {
                obj.put("cnid",userDetails.qcId)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.borrower_agreement_consent_validation_page_landed))

            val data: LoanAgreementConsent = intent.extras?.getSerializable("data") as LoanAgreementConsent
            tvViewConsent.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid",userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Give Access Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.borrower_agreement_consent_validation_page_interacted))

                if (code.text.toString().trim().isEmpty() || code.text.toString().trim().length != 4
                        || code.text.toString().trim() != data.passcode) {
                    displayToast(data.agreement_validation)
                } else {
                    hideKeyboard(this)
                    viewHover.visibility = GONE
                    llScroll.visibility = VISIBLE
                    viewHover1.visibility = VISIBLE
                    llAccessLock.visibility = GONE
                    llData.visibility = VISIBLE
                    Glide.with(this).load(R.raw.consent_help).into(ivGif)
                    tvScrollText.text = data.agreementHint
                }
            }

            viewHover1.setOnClickListener {
                if (llScroll.visibility == VISIBLE) {
                    viewHover1.visibility = GONE
                    llScroll.visibility = GONE
                    llData.visibility = VISIBLE
                }
            }

            tv_privacy_link.text = data.agreementHint

            /* we are handling this check function from backend now */
            cbAgreeTerms?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tvOk.isEnabled = true
                    tvOk.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Primary1)
                } else {
                    tvOk.isEnabled = false
                    tvOk.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorGrey)
                }
            }
            tvOk.setOnClickListener {
                sendConsent(data)
            }

            val jsInterface = JavaScriptInterface(this)
            webView.settings.javaScriptEnabled = true
            webView.addJavascriptInterface(jsInterface, "JSInterface")

            val mActivityRef = WeakReference<Activity>(this)
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    when {
                        url.startsWith("mailto:") -> {
                            val activity: Activity = mActivityRef.get()!!
                            val mt = MailTo.parse(url)
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(mt.to))
                            intent.putExtra(Intent.EXTRA_TEXT, mt.body)
                            intent.putExtra(Intent.EXTRA_SUBJECT, mt.subject)
                            intent.putExtra(Intent.EXTRA_CC, mt.cc)
                            intent.type = "message/rfc822"
                            activity.startActivity(intent)
                            view.reload()
                            return true
                        }
                        url == "https://api.capitalnow.in/closewindow" -> {
                            refreshData()
                        }
                        else -> {
                            view.loadUrl(url)
                        }
                    }
                    return true
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    //view.visibility = View.GONE
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    // view.visibility = View.VISIBLE
                    super.onPageFinished(view, url)
                }
            }
            webView.loadUrl(data.agreementLink)
            webView.settings.builtInZoomControls = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendConsent(data: LoanAgreementConsent) {
        try {
            // CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val token = userToken
            cnModel.postUserConsent(this, userDetails.userId, data.passcode,token)
            startActivity(Intent(this@ConsentDocActivity, DashboardActivity::class.java))

            finishAffinity()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun refreshData() {
        setResult(RESULT_OK)
        getApplyLoanData(true)
        this.finish()
    }

    class JavaScriptInterface(private val activity: ConsentDocActivity) {
        @JavascriptInterface
        fun refreshAndroidData() {
            activity.refreshData()
        }
    }

    fun getApplyLoanData(canRedirect: Boolean) {
        try{
            val currentScreen = Constants.CURRENT_SCREEN
            val token = userToken
            this.canRedirect = true
            cnModel.getApplyLoanData(userId, token, currentScreen, canRedirect)
            Constants.CURRENT_SCREEN = ""

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

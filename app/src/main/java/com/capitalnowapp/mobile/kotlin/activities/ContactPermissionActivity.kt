package com.capitalnowapp.mobile.kotlin.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.UserTermsData
import com.capitalnowapp.mobile.customviews.CNTextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_app_permissions.cb_agree_terms
import kotlinx.android.synthetic.main.activity_app_permissions.tv_privacy_link
import java.lang.ref.WeakReference

class ContactPermissionActivity : BaseActivity() {
    private var userTermsData: UserTermsData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_permission)
        val termsData = sharedPreferences.getString("terms_data")
        if (termsData != null && !termsData.isEmpty()) {
            userTermsData = Gson().fromJson(termsData, UserTermsData::class.java)
        }

        initView()

    }

    private fun initView() {
        cb_agree_terms.isChecked = false
        cb_agree_terms.setOnClickListener(View.OnClickListener {
            if (cb_agree_terms.isChecked) {
                //enableAgreeButton()
            }
        })

        if (userTermsData != null) {
            setLink()
        }

    }

    private fun setLink() {
        var startIndex: Int
        var endIndex: Int
        var ss: SpannableString? = null
        if (userTermsData != null && userTermsData!!.message != null) {
            val words: List<String> = userTermsData!!.findText!!.split("||")
            val links: List<String> = userTermsData!!.replaceLinks!!.split("||")
            ss = SpannableString(userTermsData!!.message)
            for (w in words.withIndex()) {
                val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        showTermsPolicyDialog(w.value, links[w.index])
                    }
                }
                startIndex = ss.indexOf(w.value, 0)
                endIndex = startIndex + w.value.length
                ss.setSpan(termsAndCondition, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.Primary2)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_privacy_link!!.text = ss
                tv_privacy_link!!.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder(this, R.style.RulesAlertDialogStyle)
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_terms_conditions, null)
        alert.setView(dialogView)
        val tvTitle: CNTextView = dialogView.findViewById(R.id.et_title)
        val tvBack: CNTextView = dialogView.findViewById(R.id.tvBack)
        tvTitle.text = title
        val pb = dialogView.findViewById<ProgressBar>(R.id.pb)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        val dialog: Dialog = alert.create()
        val mActivityRef = WeakReference<Activity>(this)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("mailto:")) {
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
                } else {
                    view.loadUrl(url)
                }
                return true
            }
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                pb.visibility = View.VISIBLE
                view.visibility = View.GONE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                pb.visibility = View.GONE
                view.visibility = View.VISIBLE
                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl(link)
        tvBack.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }




}
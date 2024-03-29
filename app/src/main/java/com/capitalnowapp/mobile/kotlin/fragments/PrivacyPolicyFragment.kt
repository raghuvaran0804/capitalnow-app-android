package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicyFragment : Fragment() {
    private var binding: FragmentPrivacyPolicyBinding? = null
    private var activity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivacyPolicyBinding.inflate(inflater, container,false)
        activity = getActivity()
        return binding!!.root
       }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.wvWebView?.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }
        }
        binding?.wvWebView!!.loadUrl("https://api.capitalnow.in/mpage/privacy-and-security-policy")
        binding?.wvWebView!!.settings.javaScriptEnabled = true
        binding?.wvWebView!!.settings.allowContentAccess = true
        binding?.wvWebView!!.settings.domStorageEnabled = true
        binding?.wvWebView!!.settings.useWideViewPort = true
    }
}
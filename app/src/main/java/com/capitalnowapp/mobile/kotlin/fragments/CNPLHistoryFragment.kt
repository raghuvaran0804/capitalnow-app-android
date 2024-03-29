package com.capitalnowapp.mobile.kotlin.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.databinding.FragmentCNPLHistoryBinding

class CNPLHistoryFragment : Fragment() {
    private var loadUrl: String? = null
    private var binding: FragmentCNPLHistoryBinding? = null
    private var activity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//new commit
        // Inflate the layout for this fragment
        binding = FragmentCNPLHistoryBinding.inflate(layoutInflater,container,false)
        activity = getActivity()
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        try {
            val bundle = arguments
            if (bundle != null) {
                loadUrl =
                    bundle.getString("url",)
            }
            loadWebView(loadUrl)

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun loadWebView(loadUrl: String?) {
        try {
            binding?.wvWebView!!.settings.javaScriptEnabled = true
            binding?.wvWebView!!.settings.allowContentAccess = true
            binding?.wvWebView!!.settings.domStorageEnabled = true
            binding?.wvWebView!!.settings.useWideViewPort = true
            binding?.wvWebView!!.settings.domStorageEnabled = true
            binding!!.wvWebView.loadUrl(loadUrl!!)
            binding?.wvWebView?.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url!!)
                    return true
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}
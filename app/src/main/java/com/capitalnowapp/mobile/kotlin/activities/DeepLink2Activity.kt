package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.databinding.ActivityDeepLink2Binding
import com.capitalnowapp.mobile.kotlin.activities.offer.ProgressActivity

class DeepLink2Activity : AppCompatActivity() {
    private  var binding: ActivityDeepLink2Binding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeepLink2Binding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val intent = intent
        val uri = intent.data


        /*  final WebView webView = findViewById(R.id.webView);
        final ProgressBar pb = findViewById(R.id.pb);
        webView.loadUrl(String.valueOf(uri));

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
            }
        });*/
        val intent1 = Intent(this@DeepLink2Activity, ProgressActivity::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent1)
        finish()
    }
}
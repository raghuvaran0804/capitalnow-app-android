package com.capitalnowapp.mobile.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.util.CNSharedPreferences;

public class LinkedInWebViewActivity extends BaseActivity {
    private WebView webView;
    private ProgressBar progressBar;

    private static final String SCOPES = "r_liteprofile%20r_emailaddress";

    //These are constants used for build the urls
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/oauth/v2/authorization";
    private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String RESPONSE_TYPE_VALUE = "code";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String SCOPE_PARAM = "scope";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin_web_view);

        try {
            applicationContext = getApplicationContext();
            activityContext = LinkedInWebViewActivity.this;

            sharedPreferences = new CNSharedPreferences(activityContext);

            progressBar = (ProgressBar) findViewById(R.id.progressBar);

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            //webView.getSettings().setLoadWithOverviewMode(true);
            //webView.getSettings().setUseWideViewPort(true);
            webView.setWebViewClient(new CustomWebClient());
            webView.loadUrl(getAuthorizationUrl());
            webView.requestFocus(View.FOCUS_DOWN);

            if (Build.VERSION.SDK_INT >= 26) {
                webView.getSettings().setSafeBrowsingEnabled(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that generates the url for get the authorization token from the Service
     *
     * @return Url
     */
    private static String getAuthorizationUrl() {
        String authorizationURL = String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&state=%s&scope=%s", Constants.LINKED_IN_AUTHORIZATION_URL,
                Constants.LINKED_IN_CLIENT_ID, Constants.LINKED_IN_REDIRECT_URL, Constants.LINKED_IN_STATE, SCOPES);
        return authorizationURL;
    }

    public class CustomWebClient extends WebViewClient {

        @TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return shouldInterceptRequest(view, request.getUrl().toString());
        }

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();

            return shouldOverrideUrlLoading(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url) {
            if (!progressBar.isShown()) {
                progressBar.animate();
                progressBar.setVisibility(View.VISIBLE);
            }

            //This method will be called when the Auth proccess redirect to our RedirectUri.
            //We will check the url looking for our RedirectUri.
            if (url.startsWith(Constants.LINKED_IN_REDIRECT_URL)) {
                Log.i("Authorize", "");
                Uri uri = Uri.parse(url);

                //We take from the url the authorizationToken and the state token. We have to check that the state token returned by the Service is the same we sent.
                //If not, that means the request may be a result of CSRF and must be rejected.
                String stateToken = uri.getQueryParameter(STATE_PARAM);
                if (stateToken == null || !stateToken.equals(Constants.LINKED_IN_STATE)) {
                    Log.e("Authorize", "State token doesn't match");
                    return true;
                }

                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);

                if (authorizationToken == null) {
                    Log.i("Authorize", "The user doesn't allow authorization.");

                    String error = uri.getQueryParameter("error");
                    String error_description = uri.getQueryParameter("error_description");

                    bundle.putString(Constants.BUNDLE_LINKED_IN_ERROR_CODE, error);
                    bundle.putString(Constants.BUNDLE_LINKED_IN_ERROR_DESCRIPTION, error_description);
                } else {
                    Log.i("Authorize", "Auth token received: " + authorizationToken);

                    bundle.putString(Constants.BUNDLE_LINKED_IN_AUTHORIZATION_CODE, authorizationToken);
                }

                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_down_out);
                finish();

            } else {
                //Default behaviour
                Log.i("Authorize", "Redirecting to: " + url);
                webView.loadUrl(url);
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressBar.isShown()) {
                progressBar.clearAnimation();
                progressBar.setVisibility(View.INVISIBLE);
            }

            super.onPageFinished(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
        }
    }
}

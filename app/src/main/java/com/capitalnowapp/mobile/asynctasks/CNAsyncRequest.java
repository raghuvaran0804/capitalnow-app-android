package com.capitalnowapp.mobile.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.customviews.CNAlertDialog;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.interfaces.OnAsyncRequestCompleteListener;
import com.capitalnowapp.mobile.util.NetworkManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class CNAsyncRequest extends AsyncTask<Void, Void, String> implements OnAsyncRequestCompleteListener {
    private Context context;
    private String requestURL = null;
    private JSONObject parameters = null;
    private Map<String, String> headers = null;
    private MethodType methodType = MethodType.GET;
    private Constants.RequestCode requestCode = null;

    public enum MethodType {
        GET,
        POST,
        GET_WITH_HEADERS,
        POST_WITH_URL_ENCODED
    }

    // Constructors
    public CNAsyncRequest(Context context) {
        this.context = context;
    }

    public CNAsyncRequest(Context context, String url, MethodType method) {
        this.context = context;
        this.requestURL = url;
        this.methodType = method;
    }

    public CNAsyncRequest(Context context, String url, MethodType method, JSONObject params) {
        this.context = context;
        this.requestURL = url;
        this.methodType = method;
        this.parameters = params;
    }

    public CNAsyncRequest(Context context, String url, MethodType method, Map<String, String> headers) {
        this.context = context;
        this.requestURL = url;
        this.methodType = method;
        this.headers = headers;
    }

    public CNAsyncRequest(Context context, String url, MethodType method, Constants.RequestCode reqCode) {
        this.context = context;
        this.requestURL = url;
        this.methodType = method;
        this.requestCode = reqCode;
    }

    public CNAsyncRequest(Context context, String url, MethodType method, JSONObject params, Constants.RequestCode reqCode) {
        this.context = context;
        this.requestURL = url;
        this.methodType = method;
        this.parameters = params;
        this.requestCode = reqCode;
    }

    @Override
    protected void onPreExecute() {
        /*if (new NetworkConnectionDetector(context).isNetworkConnected()) {
            MCProgressDialog.showProgressDialog(context, context.getResources().getString(R.string.loading_message));
        }*/
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = null;
        try {
            if (methodType == MethodType.POST) {
                response = new NetworkManager().makeHttpPostConnection(requestURL, parameters);
            } else if (methodType == MethodType.GET_WITH_HEADERS) {
                response = new NetworkManager().makeHttpGetConnectionWithHeaders(requestURL, headers);
            } else if (methodType == MethodType.POST_WITH_URL_ENCODED) {
                response = new NetworkManager().makeHttpPostConnectionWithURLEncodeContentType(requestURL, (HashMap<String, String>) headers);
            } else {
                response = new NetworkManager().makeHttpGetConnection(requestURL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String responseString) {
        if (requestURL.equals(Constants.GET_LINKED_IN_MEMBER_PROFILE_URL)) {
            onResponseReceived(responseString);
        } else {
            // Sending response back to listener
            if (responseString.equals("failure") || responseString.equals("error")) {
                CNProgressDialog.hideProgressDialog();
                CNAlertDialog.showAlertDialog(context, context.getResources().getString(R.string.title_error), context.getResources().getString(R.string.error_failure));
            } else if (responseString.equals("timeout")) {
                CNProgressDialog.hideProgressDialog();
                CNAlertDialog.showAlertDialog(context, context.getResources().getString(R.string.title_error), context.getResources().getString(R.string.error_request_timeout));
            } else {
                if (requestCode == null)
                    onResponseReceived(responseString);
                else
                    onResponseReceived(responseString, requestCode);
            }
        }
    }

    @Override
    protected void onCancelled(String response) {
        CNProgressDialog.hideProgressDialog();

        // Sending response back to listener
        if (response.equals("failure") || response.equals("error")) {
            CNAlertDialog.showAlertDialog(context, context.getResources().getString(R.string.title_error), context.getResources().getString(R.string.error_failure));
        } else if (response.equals("timeout")) {
            CNAlertDialog.showAlertDialog(context, context.getResources().getString(R.string.title_error), context.getResources().getString(R.string.error_request_timeout));
        } else {
            if (requestCode == null)
                onResponseReceived(response);
            else
                onResponseReceived(response, requestCode);
        }
    }

    public void onResponseReceived(String response) {
        // Empty body and we can override this method with your implementation in sub class.
    }

    public void onResponseReceived(String response, Constants.RequestCode requestCode) {
        // Empty body and we can override this method with your implementation if you want.
    }
}

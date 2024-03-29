package com.capitalnowapp.mobile.kotlin.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.activities.BaseActivity;
import com.capitalnowapp.mobile.beans.OrderData;
import com.capitalnowapp.mobile.beans.PaymentClearData;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.constants.Constants.AvenuesParams;
import com.capitalnowapp.mobile.customviews.CNAlertDialog;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener;
import com.capitalnowapp.mobile.models.CCAResponseDetails;
import com.capitalnowapp.mobile.util.RSAUtility;
import com.capitalnowapp.mobile.util.ServiceUtility;
import com.capitalnowapp.mobile.util.TrackingUtil;
import com.capitalnowapp.mobile.util.Utility;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

public class CCAWebActivity extends BaseActivity {

    String encVal;
    String vResponse;
    OrderData orderData;
    boolean fromRes = false;
    boolean fromTwlLoan = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ccaweb);

        orderData = (OrderData) getIntent().getSerializableExtra("orderData");
        fromTwlLoan = getIntent().getBooleanExtra("fromTwlLoan",false);
        get_RSA_key(orderData.getCcaGatewayDetails().getAccessCode(), orderData.getOrderId());
    }

    public void updateRsaError() {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog();
        CNAlertDialog.setRequestCode(1);
        CNAlertDialog.showAlertDialog(
                this,
                getResources().getString(R.string.title_alert),
                getResources().getString(R.string.error_failure)
        );

        CNAlertDialog.showAlertDialogWithCallback(activityContext, getResources().getString(R.string.title_alert), getResources().getString(R.string.error_failure), false, getString(R.string.update_app_alert_ok_text), "");
        CNAlertDialog.setListener(new AlertDialogSelectionListener() {
            @Override
            public void alertDialogCallback() {

            }

            @Override
            public void alertDialogCallback(Constants.ButtonType buttonType, int requestCode) {
                if (buttonType == Constants.ButtonType.POSITIVE) {
                    CCAWebActivity.this.finish();
                }
            }
        });
    }

    public void updateRsaSuccess(String response) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog();
        if (response != null && response != "") {
            if (response.contains("!ERROR!")) {
                updateRsaError();
            } else {
                vResponse = response;
                new RenderView().execute();
            }
        } else {
            updateRsaError();
        }
    }


    private class RenderView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialo
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (!ServiceUtility.chkNull(vResponse).equals("")
                    && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1) {
                StringBuffer vEncVal = new StringBuffer("");
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, orderData.getAmount()));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, "INR"));
                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), vResponse);  //encrypt amount and currency
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog();
            @SuppressWarnings("unused")
            class MyJavaScriptInterface {
                @JavascriptInterface
                public void processHTML(String response) {
                    Log.d("ccaResponse", response);
                    fromRes = true;
                    CCAResponseDetails ccaResponseDetails = new Gson().fromJson(response, CCAResponseDetails.class);
                    if (ccaResponseDetails != null) {
                        switch (ccaResponseDetails.getOrderStatus()) {
                            case Constants.CCAOrderStatus.Success:
                            case Constants.CCAOrderStatus.Fail:
                                invokeTransactionStatus(ccaResponseDetails);
                                break;
                            case Constants.CCAOrderStatus.Cancel:
                                if (ccaResponseDetails.getStatusMessage().toLowerCase().equals(Constants.CCAOrderStatus.Initiation_Failed)) {
                                    displayToast(getString(R.string.payment_initiation_failed));
                                } else {
                                    displayToast(getString(R.string.transaction_cancelled_by_user));
                                }
                                CCAWebActivity.this.finish();
                                break;
                            case Constants.CCAOrderStatus.Awaited:
                                displayToast(getString(R.string.transaction_timed_out));
                                CCAWebActivity.this.finish();
                                break;
                            default:
                                onBackPressed();
                                break;
                        }
                    } else {
                        onBackPressed();
                    }
                }
            }
            final WebView webview = findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(webview, url);
                    if (CNProgressDialog.isProgressDialogShown)
                        CNProgressDialog.hideProgressDialog();
                    Log.d("ccavResponseHandler", url);
                    if (url.contains(orderData.getCcaGatewayDetails().getRedirectUrl())) {
                        //webview.loadUrl(url);
                        //loaded[0] = true;
                        webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementById('payment_res').value);");
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    try {
                        Intent intent;
                        if (url.contains("upi://pay?pa")) {
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                            return true;
                        }
                    } catch (ActivityNotFoundException e) {
                        view.stopLoading();
                        Toast.makeText(getApplicationContext(), "UPI supported applications not found", Toast.LENGTH_LONG).show();
                        view.loadUrl("javascript:(function() { document.getElementsByClassName(\"intent-off\")[0].click();})()");
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE);
                    super.onPageStarted(view, url, favicon);
                }
            });


            try {
                String postData = AvenuesParams.ACCESS_CODE + "=" +
                        URLEncoder.encode(orderData.getCcaGatewayDetails().getAccessCode(), "UTF-8")
                        + "&" + AvenuesParams.MERCHANT_ID + "="
                        + URLEncoder.encode(orderData.getCcaGatewayDetails().getMerchantId(), "UTF-8")
                        + "&" + AvenuesParams.Merchant_Param2 + "="
                        + URLEncoder.encode(userDetails.getUserId(), "UTF-8")
                        + "&" + AvenuesParams.ORDER_ID + "="
                        + URLEncoder.encode(orderData.getOrderId(), "UTF-8")
                        + "&" + AvenuesParams.REDIRECT_URL + "="
                        + URLEncoder.encode(orderData.getCcaGatewayDetails().getRedirectUrl(), "UTF-8")
                        + "&" + AvenuesParams.CANCEL_URL + "="
                        + URLEncoder.encode(orderData.getCcaGatewayDetails().getCancelUrl(), "UTF-8")
                        + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8")
                        + "&" + AvenuesParams.DataAccept + "=" + URLEncoder.encode(orderData.getCcaGatewayDetails().getDataAccept(), "UTF-8");
                webview.postUrl(orderData.getCcaGatewayDetails().getTransactionUrl(), postData.getBytes());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    private void invokeTransactionStatus(CCAResponseDetails ccaResponseDetails) {
        try {
            PaymentClearData paymentClearData = new PaymentClearData();
            paymentClearData.setAmount(ccaResponseDetails.getAmount());
            paymentClearData.setPayment_id(ccaResponseDetails.getBankRefNo());
            if (ccaResponseDetails.getOrderStatus().equals(Constants.CCAOrderStatus.Success)) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("cnid",userDetails.getQcId());
                    obj.put("isSuccess","true");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj, getString(R.string.personal_Loan_payment_server_Event));
                paymentClearData.setTransaction_status(1);
                String token = getUserToken();
                sharedPreferences.putBoolean("processingFeeSuccess", true);
                uploadCCAResponse(ccaResponseDetails, token , fromTwlLoan);
            } else {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("cnid",userDetails.getQcId());
                    obj.put("isSuccess","false");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj, getString(R.string.personal_Loan_payment_server_Event));
                paymentClearData.setTransaction_status(0);
            }
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            // dbFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            paymentClearData.setTransaction_initiated_at(dbFormat.format(format.parse(ccaResponseDetails.getTransDate())));
            paymentClearData.setTransaction_ended_at(Utility.formatTime(System.currentTimeMillis(), Constants.YYYY_MM_DD_HH_MM_SS));
            Intent intent = new Intent(this, PaymentStatusActivity.class);
            intent.putExtra(Constants.BUNDLE_PAYMENT_STATUS, paymentClearData);
            startActivity(intent);
            CCAWebActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void get_RSA_key(final String ac, final String od) {
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE);
        cnModel.getRSA(this, ac, od);
    }


    @Override
    public void onBackPressed() {
        if (!fromRes) {
            CNAlertDialog.setRequestCode(1);
            CNAlertDialog.showAlertDialogWithCallback(this, "Cancel Payment", getString(R.string.cancel_pay_confirmation), true, "YES", "NO");
            CNAlertDialog.setListener(new AlertDialogSelectionListener() {
                @Override
                public void alertDialogCallback() {

                }

                @Override
                public void alertDialogCallback(Constants.ButtonType buttonType, int requestCode) {
                    if (buttonType == Constants.ButtonType.POSITIVE) {
                        CCAWebActivity.this.finish();
                    }
                }
            });
        } else {
            CCAWebActivity.this.finish();
        }
    }
}
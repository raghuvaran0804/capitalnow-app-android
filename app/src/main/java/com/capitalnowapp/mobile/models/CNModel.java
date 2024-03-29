package com.capitalnowapp.mobile.models;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.capitalnowapp.mobile.BuildConfig;
import com.capitalnowapp.mobile.activities.BaseActivity;
import com.capitalnowapp.mobile.activities.LoginActivity;
import com.capitalnowapp.mobile.activities.SplashScreen;
import com.capitalnowapp.mobile.beans.ApplyLoan;
import com.capitalnowapp.mobile.beans.BankDetails;
import com.capitalnowapp.mobile.beans.MasterData;
import com.capitalnowapp.mobile.beans.NotificationObj;
import com.capitalnowapp.mobile.beans.OrderData;
import com.capitalnowapp.mobile.beans.PaymentClearData;
import com.capitalnowapp.mobile.beans.RequestBankChangeData;
import com.capitalnowapp.mobile.beans.RewardPointsData;
import com.capitalnowapp.mobile.beans.SocialRegistration;
import com.capitalnowapp.mobile.beans.UserData;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.controller.AppController;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.fragments.HomeFragment;
import com.capitalnowapp.mobile.fragments.NotificationsFragment;
import com.capitalnowapp.mobile.kotlin.activities.BankDetailsActivity;
import com.capitalnowapp.mobile.kotlin.activities.CCAWebActivity;
import com.capitalnowapp.mobile.kotlin.activities.ConsentDocActivity;
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity;
import com.capitalnowapp.mobile.kotlin.activities.FederalRegistrationActivity;
import com.capitalnowapp.mobile.kotlin.activities.HistoryReferActivity;
import com.capitalnowapp.mobile.kotlin.activities.NewApplyLoanActivity;
import com.capitalnowapp.mobile.kotlin.activities.NewLoanActivity;
import com.capitalnowapp.mobile.kotlin.activities.Reg1Activity;
import com.capitalnowapp.mobile.kotlin.activities.RegistrationHomeActivity;
import com.capitalnowapp.mobile.kotlin.activities.UploadBankDetailsActivity;
import com.capitalnowapp.mobile.kotlin.fragments.ActiveLoansHomeFragment;
import com.capitalnowapp.mobile.kotlin.fragments.AddressBottomSheetFragment;
import com.capitalnowapp.mobile.kotlin.fragments.ApplyLoanEMIFragment;
import com.capitalnowapp.mobile.kotlin.fragments.ApplyLoanFragment;
import com.capitalnowapp.mobile.kotlin.fragments.CloseLoansFragment;
import com.capitalnowapp.mobile.kotlin.fragments.CloseLoansHomeFragment;
import com.capitalnowapp.mobile.kotlin.fragments.MyLoansFrag;
import com.capitalnowapp.mobile.kotlin.fragments.ReferToEarnFragment;
import com.capitalnowapp.mobile.models.loan.AmtPayable;
import com.capitalnowapp.mobile.models.loan.InstalmentData;
import com.capitalnowapp.mobile.models.loan.MyLoansResponse;
import com.capitalnowapp.mobile.models.userdetails.RegisterUserReq;
import com.capitalnowapp.mobile.models.userdetails.UserDetails;
import com.capitalnowapp.mobile.util.CNSharedPreferences;
import com.capitalnowapp.mobile.util.NetworkConnectionDetector;
import com.capitalnowapp.mobile.util.TrackingUtil;
import com.capitalnowapp.mobile.util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CNModel {
    private Context context;
    private Activity activity;
    private Constants.RequestFrom requestFrom;
    private String TAG;
    private Utility utility;
    public UserDetails userDetails;
    public CNSharedPreferences sharedPreferences;
    private String deviceUniqueId = "";


    public CNModel(Context context, Activity activity, Constants.RequestFrom requestFrom) {
        this.context = context;
        this.activity = activity;
        this.requestFrom = requestFrom;
        this.TAG = activity.getClass().getSimpleName();
        utility = Utility.getInstance();

        if (deviceUniqueId.isEmpty()) {
            deviceUniqueId = utility.getDeviceUniqueId(activity);
        }
    }

    public void getAppVersion() {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getAppVersionReqURL = String.format("%s/%s", Constants.MAIN_URL_1, Constants.NODE_GET_APP_VERSION);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", Constants.CN_API_KEY);
                reqObject.put("request_input", BuildConfig.VERSION_CODE);
                reqObject.put("device_unique_id", deviceUniqueId);
                reqObject.put("platform", "Android");

                JsonObjectRequest getAppVersionReq = new JsonObjectRequest(Request.Method.POST, getAppVersionReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (activity instanceof SplashScreen) {
                                        if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                            ((SplashScreen) activity).updateAppVersionCodeAndName(response.getString("app_version_code"), response.getString("app_version_name"), response.getInt("update_level"), response);
                                        } else {
                                            if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                                logout();
                                            } else {
                                                ((SplashScreen) activity).updateAppVersionCodeAndName("", "", 0, response);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "******* Get App Version Error : " + error);
                                /*CNAlertDialog.showAlertDialog(context, "Oops! Cant connect right now", "What you can do\n" +
                                        "- Check your location settings and ensure you are in India.\n" +
                                        "- Verify your internet connection.\n" +
                                        "- Wait a few minutes and try again.\n" +
                                        "\n" +
                                        "Need help? Email us at support@capitalnow.com" +
                                        "- Verify your internet connection.");*/

                                ((SplashScreen) activity).showErrorDialog();
                                //((LoginActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getAppVersionReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMyLoansData(final MyLoansFrag myLoansFragment, String userId, String token) {
        try {
            if (new NetworkConnectionDetector(myLoansFragment.getContext()).isNetworkConnected()) {
                String getMyLoansDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.MY_LOANS);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getMyLoansDataReq = new JsonObjectRequest(Request.Method.POST, getMyLoansDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    CNProgressDialog.hideProgressDialog();
                                    MyLoansResponse myLoansData = new Gson().fromJson(response.toString(), MyLoansResponse.class);
                                    if (myLoansData.getStatus().equals(Constants.STATUS_SUCCESS)) {
                                        myLoansFragment.updateMyLoansResponse(myLoansData);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            myLoansFragment.showAlertDialog(myLoansData.getMessage());
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getMyLoansDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getActiveLoans(final ActiveLoansHomeFragment myLoansFragment, String userId, String token) {
        try {
            if (new NetworkConnectionDetector(myLoansFragment.getContext()).isNetworkConnected()) {
                String getMyLoansDataReqURL = String.format("%s/%s", Constants.MAIN_URL_1, Constants.ACTIVE_LOANS_NODE);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getMyLoansDataReq = new JsonObjectRequest(Request.Method.POST, getMyLoansDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    CNProgressDialog.hideProgressDialog();
                                    MyLoansResponse myLoansData = new Gson().fromJson(response.toString(), MyLoansResponse.class);
                                    myLoansFragment.updateMyLoansResponse(myLoansData);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getMyLoansDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getClearedLoans(final Fragment myLoansFragment, String userId, String token, int flag) {
        try {
            if (new NetworkConnectionDetector(myLoansFragment.getContext()).isNetworkConnected()) {
                String getMyLoansDataReqURL = "";
                if (flag != 1) {
                    getMyLoansDataReqURL = String.format("%s/%s", Constants.MAIN_URL_1, Constants.CLEAR_LOANS_NODE);
                } else {
                    getMyLoansDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.TWL_CLEAR_LOANS);
                }

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getMyLoansDataReq = new JsonObjectRequest(Request.Method.POST, getMyLoansDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                        CNProgressDialog.hideProgressDialog();
                                        MyLoansResponse myLoansData = new Gson().fromJson(response.toString(), MyLoansResponse.class);
                                        if (myLoansFragment instanceof CloseLoansHomeFragment) {
                                            ((CloseLoansHomeFragment) myLoansFragment).updateMyLoansResponse(myLoansData);
                                        } else if (myLoansFragment instanceof CloseLoansFragment) {
                                            ((CloseLoansFragment) myLoansFragment).updateMyLoansResponse(myLoansData);
                                        }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getMyLoansDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getApplyLoanData(String userId, String token, String currentScreen, boolean canRedirect) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getApplyLoanDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.APPLY_LOAN_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("current_screen", currentScreen);
                reqObject.put("device_unique_id", deviceUniqueId);


                JsonObjectRequest getApplyLoanDataReq = new JsonObjectRequest(Request.Method.POST, getApplyLoanDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        ((DashboardActivity) activity).updateApplyLoansData(response, canRedirect);
                                    }else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else if (response.getString("status").equals(Constants.STATUS_FAILURE)) {
                                            String message = response.getString("message");
                                            ((DashboardActivity) activity).capAlertDialog(message);
                                        }
                                    }
                                    if(response.getString("status").equals("limit_exhausted")){
                                        String message = response.getString("message");
                                        ((DashboardActivity) activity).capAlertDialog(message);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((DashboardActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getApplyLoanDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getProfileData(final HomeFragment homeFragment, String userId, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getProfileDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.PROFILE_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);
                //reqObject.put("token",token);

                JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getProfileDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        UserData userData = new Gson().fromJson(response.toString(), UserData.class);
                                        homeFragment.updateProfileData(userData);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            homeFragment.showAlertDialog(response.getString("message"));
                                        }


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((DashboardActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBannerImages(final HomeFragment homeFragment, String userId, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getProfileDataReqURL = String.format("%s/%s", Constants.MAIN_URL_1, Constants.PROFILE_BANNERS_NODE);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getProfileDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        homeFragment.updateBannerImages(response.getJSONArray("images"));
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            homeFragment.showAlertDialog(response.getString("message"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((DashboardActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void validateOTPAndApplyLoan(final ApplyLoanFragment applyLoanFragment, String userId, ApplyLoan applyLoan, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String validateOTPAndApplyLoanReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.VERIFY_OTP);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("otp_password", applyLoan.getOtpPassword());
                reqObject.put("amount", applyLoan.getAmount());
                reqObject.put("tenure_days", applyLoan.getTenureDays());
                reqObject.put("service_fee", applyLoan.getServiceFee());
                reqObject.put("processing_charges", applyLoan.getProcessingCharges());
                reqObject.put("new_processing_charges", applyLoan.getNewProcessingCharges());
                reqObject.put("total", applyLoan.getTotal());
                reqObject.put("promo_code", applyLoan.getPromo_code());
                reqObject.put("Qcr_req_promo_code", applyLoan.getQcr_req_promo_code());
                reqObject.put("amazon_pay_number", applyLoan.getAmazonNumber());
                reqObject.put("toAmazon", applyLoan.getAmazonAmount());
                reqObject.put("toBank", applyLoan.getBankAmount());
                reqObject.put("loan_type", applyLoan.getLoanType());
                reqObject.put("current_location", applyLoan.getCurrent_location());
                reqObject.put("cashback_amt", applyLoan.getCashback_amt());
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest requestOTPReq = new JsonObjectRequest(Request.Method.POST, validateOTPAndApplyLoanReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        //applyLoanOTPFragment.updateApplyLoanStatus(response.getString("message"), response.getInt("app_rated"));
                                        applyLoanFragment.updateApplyLoanStatus(response);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            applyLoanFragment.showAlertDialog(response.getString("message"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(requestOTPReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validateOTPAndApplyLoanEMI(final ApplyLoanEMIFragment applyLoanFragment, String userId, ApplyLoan applyLoan, int Qcr_accept_pre_agreement, String token) {
        try {
            if (new NetworkConnectionDetector(applyLoanFragment.getContext()).isNetworkConnected()) {
                String validateOTPAndApplyLoanReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.VERIFY_OTP);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("otp_password", applyLoan.getOtpPassword());
                reqObject.put("amount", applyLoan.getAmount());
                reqObject.put("tenure_days", applyLoan.getTenureDays());
                reqObject.put("service_fee", applyLoan.getServiceFee());
                reqObject.put("processing_charges", applyLoan.getProcessingCharges());
                reqObject.put("new_processing_charges", applyLoan.getNewProcessingCharges());
                reqObject.put("total", applyLoan.getTotal());
                reqObject.put("promo_code", applyLoan.getPromo_code());
                reqObject.put("Qcr_req_promo_code", applyLoan.getQcr_req_promo_code());
                reqObject.put("amazon_pay_number", applyLoan.getAmazonNumber());
                reqObject.put("toAmazon", applyLoan.getAmazonAmount());
                reqObject.put("toBank", applyLoan.getBankAmount());
                reqObject.put("loan_type", applyLoan.getLoanType());
                reqObject.put("cashback_amt", applyLoan.getCashback_amt());
                reqObject.put("Qcr_purpose_of_loan", applyLoan.getQcr_purpose_of_loan());
                reqObject.put("Qcr_custom_purpose", applyLoan.getQcr_custom_purpose());
                reqObject.put("Qcr_accept_pre_agreement", Qcr_accept_pre_agreement);
                reqObject.put("tenure_type", applyLoan.getTenureType());
                reqObject.put("emi_count", applyLoan.getEmiCount());
                reqObject.put("current_location", applyLoan.getCurrent_location());

                Gson gson = new Gson();
                String listString = gson.toJson(
                        applyLoan.getInstalmentDataList(),
                        new TypeToken<ArrayList<InstalmentData>>() {
                        }.getType());

                JSONArray myCustomArray = new JSONArray(listString.replaceAll("\\/", "-"));
                reqObject.put("instalments", myCustomArray);

                reqObject.put("device_unique_id", deviceUniqueId);

                Log.d("applyloan_emi_req", reqObject.toString());

                JsonObjectRequest requestOTPReq = new JsonObjectRequest(Request.Method.POST, validateOTPAndApplyLoanReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        //applyLoanOTPFragment.updateApplyLoanStatus(response.getString("message"), response.getInt("app_rated"));
                                        applyLoanFragment.updateApplyLoanStatus(response);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            if(response.has("status_code") && response.getInt("status_code") == 999){
                                                applyLoanFragment.showNewAlertDialog(response);
                                            }else {
                                                applyLoanFragment.showAlertDialog(response.getString("message"));
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(requestOTPReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validateOTPAndApplyLoanEMI2(NewApplyLoanActivity newApplyLoanActivity, String userId, ApplyLoanNew applyLoan,  String token) {
        try {

                String validateOTPAndApplyLoanReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.VERIFY_OTP);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("otp_password", applyLoan.getOtpPassword());
                reqObject.put("amount", applyLoan.getAmount());
                reqObject.put("tenure_days", applyLoan.getTenureDays());
                reqObject.put("service_fee", applyLoan.getServiceFee());
                reqObject.put("processing_charges", applyLoan.getProcessingCharges());
                reqObject.put("new_processing_charges", applyLoan.getNewProcessingCharges());
                reqObject.put("total", applyLoan.getTotal());
                reqObject.put("promo_code", applyLoan.getPromo_code());
                reqObject.put("Qcr_req_promo_code", applyLoan.getQcr_req_promo_code());
                reqObject.put("amazon_pay_number", applyLoan.getAmazonNumber());
                reqObject.put("toAmazon", applyLoan.getAmazonAmount());
                reqObject.put("toBank", applyLoan.getBankAmount());
                reqObject.put("loan_type", applyLoan.getLoanType());
                reqObject.put("cashback_amt", applyLoan.getCashback_amt());
                reqObject.put("Qcr_purpose_of_loan", applyLoan.getQcr_purpose_of_loan());
                reqObject.put("Qcr_custom_purpose", applyLoan.getQcr_custom_purpose());
                reqObject.put("Qcr_accept_pre_agreement", "");
                reqObject.put("tenure_type", applyLoan.getTenureType());
                reqObject.put("emi_count", applyLoan.getEmiCount());
                reqObject.put("Qcr_accept_pre_agreement",applyLoan.getQcr_accept_pre_agreement());
                reqObject.put("current_location", applyLoan.getCurrent_location());

                Gson gson = new Gson();
                String listString = gson.toJson(
                        applyLoan.getInstalmentDataList(),
                        new TypeToken<ArrayList<InstalmentData>>() {
                        }.getType());

                JSONArray myCustomArray = new JSONArray(listString.replaceAll("\\/", "-"));
                reqObject.put("instalments", myCustomArray);

                reqObject.put("device_unique_id", deviceUniqueId);

                Log.d("applyloan_emi_req", reqObject.toString());

                JsonObjectRequest requestOTPReq = new JsonObjectRequest(Request.Method.POST, validateOTPAndApplyLoanReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        //applyLoanOTPFragment.updateApplyLoanStatus(response.getString("message"), response.getInt("app_rated"));
                                        newApplyLoanActivity.updateApplyLoanStatus(response);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            newApplyLoanActivity.showAlertDialog(response.getString("message"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(requestOTPReq, TAG);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doSocialRegistration(SocialRegistration socialRegistration, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String doSocialRegistrationReqURL = String.format("%s/%s", Constants.MAIN_URL_1, Constants.NODE_SOCIAL_REGISTER);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("oauth_provider", socialRegistration.getOauthProvider());
                reqObject.put("oauth_uid", socialRegistration.getOauthId());
                reqObject.put("user_social_name", socialRegistration.getName());
                reqObject.put("email", socialRegistration.getEmail());
                reqObject.put("gender", socialRegistration.getGender());
                reqObject.put("locale", socialRegistration.getLocale());
                reqObject.put("link", socialRegistration.getLink());
                reqObject.put("count", socialRegistration.getCount());
                reqObject.put("picture", socialRegistration.getPicture());
                reqObject.put("mobile_version", socialRegistration.getMobileVersion());
                reqObject.put("mobile_location", socialRegistration.getLocation());
                reqObject.put("device_unique_id", socialRegistration.getDeviceUniqueId());
                reqObject.put("device_token", socialRegistration.getDeviceToken());
                reqObject.put("contacts_array", socialRegistration.getPhoneNumbersArray());
                reqObject.put("emails_array", socialRegistration.getEmailAddressesArray());
                reqObject.put("mobile_no", socialRegistration.getMobile_no());
                reqObject.put("user_id", socialRegistration.getUser_id());

                JsonObjectRequest doSocialRegistrationReq = new JsonObjectRequest(Request.Method.POST, doSocialRegistrationReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ((FederalRegistrationActivity) activity).updateSocialProfileResult(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((FederalRegistrationActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(doSocialRegistrationReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveOSRFValue(String userId, final int fieldType, String value, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String saveOSRFValueReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.PROFILE_SAVE_AJAX_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("argument", fieldType);
                reqObject.put("field_data", value);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest saveOSRFValueReq = new JsonObjectRequest(Request.Method.POST, saveOSRFValueReqURL, reqObject,
                        response -> {
                            try {
                                ((RegistrationHomeActivity) activity).updateSaveOSRFValueResponse(response.getString("status"), response.getString("message"), fieldType);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            //((OneTimeRegistrationActivity) activity).onVolleyErrorResponse(error);
                        }
                );

                AppController.getInstance(context).addToRequestQueue(saveOSRFValueReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCompanyOSRF(String userId, final int fieldType, String value, int id, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String saveOSRFValueReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.PROFILE_SAVE_AJAX_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("argument", fieldType);
                reqObject.put("field_data", value);
                reqObject.put("id", id);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest saveOSRFValueReq = new JsonObjectRequest(Request.Method.POST, saveOSRFValueReqURL, reqObject,
                        response -> {
                            try {
                                Log.d("compny_ajax", "saved");
                                ((RegistrationHomeActivity) activity).updateSaveOSRFValueResponse(response.getString("status"), response.getString("message"), fieldType);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            //((OneTimeRegistrationActivity) activity).onVolleyErrorResponse(error);
                        }
                );

                AppController.getInstance(context).addToRequestQueue(saveOSRFValueReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveOneTimeRegistration(String userId, RegisterUserReq registerUserReq, UserDetails userDetails, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String saveOneTimeRegistrationReqURL = String.format("%s/%s", Constants.MAIN_URL_1, Constants.SAVE_ONETIME_REG_NODE);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("email", userDetails.getEmail());
                reqObject.put("name", registerUserReq.getFirstName());
                reqObject.put("middle_name", registerUserReq.getMiddleName());
                reqObject.put("last_name", registerUserReq.getLastName());
                reqObject.put("user_mobile", userDetails.getUserMobile());
                reqObject.put("alt_mobile", registerUserReq.getAltMobile());
                reqObject.put("experience", registerUserReq.getExperience());
                reqObject.put("sec_email", registerUserReq.getSecEmail());
                reqObject.put("city", registerUserReq.getWorkCityId());
                reqObject.put("dob", registerUserReq.getDob());
                reqObject.put("proinfo_type", registerUserReq.getEmpType());
                reqObject.put("company_name", registerUserReq.getCompanyName());
                reqObject.put("company_id", registerUserReq.getCompanyId());
                reqObject.put("department", registerUserReq.getDepartment());
                reqObject.put("designation", registerUserReq.getDesignation());
                reqObject.put("ctc", registerUserReq.getCtc());
                reqObject.put("pan_number", registerUserReq.getPanNumber());
                reqObject.put("rf_type", registerUserReq.getRefType());
                reqObject.put("promo_code", registerUserReq.getPromoCode());
                reqObject.put("proinfo_monthly_sal", registerUserReq.getMonthlySal());
                reqObject.put("native_city_id", registerUserReq.getNativeCityId());
                reqObject.put("mode_of_pay", registerUserReq.getModeOfPay());
                reqObject.put("gender", registerUserReq.getGender());
                reqObject.put("mart_stat", registerUserReq.getMaritalStatus());
                reqObject.put("residence", registerUserReq.getResidence());
                reqObject.put("proinfo_industry", registerUserReq.getIndustry());
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest saveOneTimeRegistrationReq = new JsonObjectRequest(Request.Method.POST, saveOneTimeRegistrationReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        if (response.has("is_autohold") && response.getBoolean("is_autohold")) {
                                            //Mixpanel hold event
                                            JSONObject obj = new JSONObject();
                                            try {
                                                obj.put("cnid",userDetails.getQcId());
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                            TrackingUtil.pushEvent(obj, "UserMembershipPutOnHold");

                                            ((RegistrationHomeActivity) activity).logoutUser(response.getString("user_id"), response.getString("message"));
                                        } else {
                                            ((RegistrationHomeActivity) activity).updateOneStepRegistrationResponse(response.getString("user_id"), response.getString("user_status_id"), response.getString("message")
                                                    , response.getInt("show_popup"));
                                        }
                                    } else {
                                        ((RegistrationHomeActivity) activity).showAlertDialog(response.getString("message"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((RegistrationHomeActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(saveOneTimeRegistrationReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout() {

        Intent logInIntent = new Intent(getApplicationContext(), LoginActivity.class);
        /*logInIntent.flags =
                FLAG_ACTIVITY_NO_ANIMATION || Intent.FLAG_ACTIVITY_NEW_TASK || Intent.FLAG_ACTIVITY_CLEAR_TASK*/
        activity.startActivity(logInIntent);
        //overridePendingTransition(R.anim.left_in, R.anim.right_out);
        activity.finish();
    }


    public void SaveAddress() {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String saveAddressReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.SAVE_ADDRESS);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", Constants.CN_API_KEY);
                /*reqObject.put("user_id", userId);
                reqObject.put("addressline1",saveAddress.getAddress1());
                reqObject.put("city",saveAddress.getCity());
                reqObject.put("state",saveAddress.getState());
                reqObject.put("pincode",saveAddress.getPinCode());*/

                JsonObjectRequest saveAddressReq = new JsonObjectRequest(Request.Method.POST, saveAddressReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {

                                    } else {
                                        ((RegistrationHomeActivity) activity).showAlertDialog(response.getString("message"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((RegistrationHomeActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(saveAddressReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBankDetails(final MyLoansFrag myLoansFragment, String userId, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getBankDetailsReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.LENDER_BANK_DETAILS);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getBankDetailsReq = new JsonObjectRequest(Request.Method.POST, getBankDetailsReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {

                                        String relationship_details = response.getString("customer_relationship_details");

                                        Type bankDetailsListType = new TypeToken<ArrayList<BankDetails>>() {
                                        }.getType();

                                        List<BankDetails> bankDetailsList = new Gson().fromJson(response.getString("lender_bank_details"), bankDetailsListType);
                                        myLoansFragment.updateBankDetails(bankDetailsList, relationship_details);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            myLoansFragment.showAlertDialog(response.getString("message"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getBankDetailsReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getOrderData(final DashboardActivity dashboardActivity, String userId, int totalPaidAmount, ArrayList<AmtPayable> amtPayableList, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getOrderDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.ORDER_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("amount", totalPaidAmount);
                Gson gson = new Gson();
                String listString = gson.toJson(amtPayableList, new TypeToken<ArrayList<InstalmentData>>() {
                }.getType());
                JSONArray myCustomArray = new JSONArray(listString.replaceAll("\\/", "-"));
                reqObject.put("selected_ids", myCustomArray);
                reqObject.put("wclient", 0);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getOrderDataReqURL, reqObject,
                        response -> {
                            try {
                                if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                    OrderData orderData = new Gson().fromJson(response.toString(), OrderData.class);
                                    dashboardActivity.updateOrderData(orderData);
                                } else {
                                    if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                        logout();
                                    } else {
                                        dashboardActivity.showAlertDialog(response.getString("message"));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> ((BaseActivity) activity).onVolleyErrorResponse(error)
                );

                AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePaymentData(final DashboardActivity myLoansFragment, String userId, String paymentId, String orderId, String signature, String token) {
        try {
            if (new NetworkConnectionDetector(myLoansFragment).isNetworkConnected()) {
                String getOrderDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.SAVE_PAYMENT_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("paymentId", paymentId);
                reqObject.put("orderId", orderId);
                reqObject.put("signature", signature);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getOrderDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        PaymentClearData paymentClearData = new Gson().fromJson(response.toString(), PaymentClearData.class);
                                        myLoansFragment.updatePaymentData(paymentClearData);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            myLoansFragment.onPaymentError(0, "", null);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    myLoansFragment.onError(e.toString());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                myLoansFragment.onError(error.toString());
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );
                AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePayUData(JSONObject valuesObj) {
        try {
            String getOrderDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.SAVE_PAYU_DATA);
            JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getOrderDataReqURL, valuesObj,
                    response -> {

                    },
                    error -> {

                    }
            );
            AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTwlPayUData(JSONObject valuesObj) {
        try {
            String getOrderDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.SAVE_TWL_PAYU_DATA);
            JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getOrderDataReqURL, valuesObj,
                    response -> {

                    },
                    error -> {

                    }
            );
            AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCCAData(JSONObject valuesObj) {
        try {
            String getOrderDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.SAVE_CCA_DATA);
            Log.d("ccaupdatePostData", new Gson().toJson(valuesObj));
            JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getOrderDataReqURL, valuesObj,
                    response -> {
                        Log.d("ccaupdateresponse", response.toString());
                    },
                    error -> {
                        Log.d("ccaupdateresponse", error.toString());
                    }
            );
            AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTwlCCAData(JSONObject valuesObj) {
        try {
            String getOrderDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.SAVE_TWL_CCA_DATA);
            Log.d("ccaupdatePostData", new Gson().toJson(valuesObj));
            JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getOrderDataReqURL, valuesObj,
                    response -> {
                        Log.d("ccaupdateresponse", response.toString());
                    },
                    error -> {
                        Log.d("ccaupdateresponse", error.toString());
                    }
            );
            AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRSA(CCAWebActivity ccaWebActivity, String access, String orderId) {
        try {
            String getRSA = Constants.CCA.GET_RSA;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getRSA,
                    response -> {
                        ccaWebActivity.updateRsaSuccess(response);
                    },
                    error -> {
                        ccaWebActivity.updateRsaError();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constants.AvenuesParams.ACCESS_CODE, access);
                    params.put(Constants.AvenuesParams.ORDER_ID, orderId);
                    return params;
                }

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance(context).addToRequestQueue(stringRequest, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNotifications(final NotificationsFragment notificationsFragment, String userId, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getNotificationsReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.NOTIFICATIONS);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getNotificationsReq = new JsonObjectRequest(Request.Method.POST, getNotificationsReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getBoolean("status")) {
                                        CNProgressDialog.hideProgressDialog();
                                        if (response.has("data")) {
                                            Type notificationsListType = new TypeToken<ArrayList<NotificationObj>>() {
                                            }.getType();

                                            ArrayList<NotificationObj> notificationsList = new Gson().fromJson(response.getString("data"), notificationsListType);

                                            notificationsFragment.updateNotifications(notificationsList);
                                        } else {
                                            notificationsFragment.updateNotifications(null);
                                        }
                                    } else {
                                        notificationsFragment.showAlertDialog(response.getString("message"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getNotificationsReq, TAG);
            }
        } catch (Exception e) {
            CNProgressDialog.hideProgressDialog();
            e.printStackTrace();
        }
    }

    public void saveUserRatedAppStatus(String userId, int status, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String saveUserRatedAppStatusReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.APP_RATING);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("uapp_rated", status);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest saveUserRatedAppStatusReq = new JsonObjectRequest(Request.Method.POST, saveUserRatedAppStatusReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // We are not showing any acknowledgement after updating user rated app flag.
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(saveUserRatedAppStatusReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMasterData(final String masterDataType, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getMasterDataReqURL = String.format("%s/%s", Constants.MAIN_URL_1, Constants.GET_MASTER_DATA_NODE);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("table_cond", masterDataType);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getMasterDataReq = new JsonObjectRequest(Request.Method.POST, getMasterDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ((RegistrationHomeActivity) activity).updateMasterDataResponse(response, masterDataType);
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        Type masterDataListType = new TypeToken<ArrayList<MasterData>>() {
                                        }.getType();
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        error -> ((BaseActivity) activity).onVolleyErrorResponse(error)
                );

                AppController.getInstance(context).addToRequestQueue(getMasterDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMasterJson() {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                JsonObjectRequest getMasterDataReqURL = new JsonObjectRequest(Request.Method.GET, Constants.MASTER_JSON_REGISTRATION, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ((Reg1Activity) activity).updateMasterJson(response);
                                    //((Reg2Activity) activity).updateMasterJson(response);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((Reg1Activity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getMasterDataReqURL, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMasterJson2() {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                JsonObjectRequest getMasterDataReqURL = new JsonObjectRequest(Request.Method.GET, Constants.MASTER_JSON_REGISTRATION, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ((Reg1Activity) activity).updateMasterJson(response);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((Reg1Activity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getMasterDataReqURL, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDepatmentJson() {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                JsonObjectRequest getMasterDataReqURL = new JsonObjectRequest(Request.Method.GET, Constants.DEPARTMENT_JSON_REGISTRATION, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ((Reg1Activity) activity).updateDepartmentJson(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((Reg1Activity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getMasterDataReqURL, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDesignationJson() {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                JsonObjectRequest getMasterDataReqURL = new JsonObjectRequest(Request.Method.GET, Constants.DESIGNATION_JSON_REGISTRATION, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ((Reg1Activity) activity).updateDesignationJson(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((RegistrationHomeActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getMasterDataReqURL, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pinCode(String pincode){
        try{
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String pinCodeUrl = Constants.PINCODE_API+pincode;
                JsonObjectRequest getPinCodeReqURL = new JsonObjectRequest(Request.Method.GET, pinCodeUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    AddressBottomSheetFragment.updateStateAndCity(response);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //((RegistrationHomeActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );
                AppController.getInstance(context).addToRequestQueue(getPinCodeReqURL, TAG);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getCompanies(GenericRequest genericRequest, String token, Activity activity) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                genericRequest.setApiKey(token);
                JSONObject jsonObject = new JSONObject(new Gson().toJson(genericRequest));
                String req = String.format("%s/%s", Constants.MAIN_URL_1, Constants.GetCompanies_Node);
                Log.d("companies api call", "calling");
                JsonObjectRequest getMasterDataReqURL = new JsonObjectRequest(Request.Method.POST, req, jsonObject,
                        response -> {
                            Log.d("companies api call", "response");
                            try {
                                if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                    //((RegistrationHomeActivity) activity).updateCompanies(response.getJSONArray("company_data"), activity);
                                    ((Reg1Activity) activity).updateCompanies(response.getJSONArray("company_data"), activity);
                                } else {
                                    if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                        logout();
                                    } else {
                                        ((Reg1Activity) activity).updateCompanies(new JSONArray(), activity);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> ((Reg1Activity) activity).onVolleyErrorResponse(error)
                );

                AppController.getInstance(context).addToRequestQueue(getMasterDataReqURL, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMasterJsonLoan(ApplyLoanEMIFragment applyLoanEMIFragment) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                JsonObjectRequest getMasterDataReqURL = new JsonObjectRequest(Request.Method.GET, Constants.MASTER_JSON_REGISTRATION, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    applyLoanEMIFragment.updateMasterJson(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getMasterDataReqURL, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMasterJsonLoan1() {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                JsonObjectRequest getMasterDataReqURL = new JsonObjectRequest(Request.Method.GET, Constants.MASTER_JSON_REGISTRATION, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ((NewLoanActivity) activity).updateMasterJson(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getMasterDataReqURL, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveStep2Registration(String userId, RegisterUserReq registerUserReq, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String saveStep2RegistrationReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.SAVE_TWO_STEP_REG);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("clg_grad_yr", registerUserReq.getYog());
                reqObject.put("experience", registerUserReq.getExperience());
                reqObject.put("mart_stat", registerUserReq.getMaritalStatus());
                reqObject.put("residence", registerUserReq.getResidence());
                reqObject.put("clg_name_list", registerUserReq.getCollegeName());
                reqObject.put("credit_card_list", registerUserReq.getCardType());
                reqObject.put("freq_app", "");
                reqObject.put("frequently_used_apps", registerUserReq.getFrequentlyUsedApps());
                reqObject.put("user_survey_purpose_of_loan", registerUserReq.getLoanPurposeId());
                reqObject.put("user_survey_custom_purpose", registerUserReq.getLoanPurposeCustom());
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest saveStep2RegistrationReq = new JsonObjectRequest(Request.Method.POST, saveStep2RegistrationReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        if (activity instanceof UploadBankDetailsActivity) {
                                            ((UploadBankDetailsActivity) activity).updateStep2Response(response.getString("user_id"), response);
                                        } else {
                                            ((BaseActivity) activity).updateStep2RegistrationResponse(response.getString("user_id"), response.getString("message"));
                                        }
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            // logout();
                                            if (activity instanceof UploadBankDetailsActivity) {
                                                ((UploadBankDetailsActivity) activity).logoutUser(response.getString("user_id"), response.getString("message"));
                                            }else{
                                                ((BankDetailsActivity) activity).logoutUser(response.getString("user_id"), response.getString("message"));
                                            }
                                        } else {
                                            // ((BasAc) activity).updateError(response.getString("message"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (activity instanceof UploadBankDetailsActivity) {
                                    ((UploadBankDetailsActivity) activity).onVolleyErrorResponse(error);
                                }else {
                                    ((BankDetailsActivity) activity).onVolleyErrorResponse(error);
                                }
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(saveStep2RegistrationReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReferralCode(final ReferToEarnFragment referAndEarnFragment, String userId, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getReferralCodeReqURL = String.format("%s/%s", Constants.MAIN_URL_1, Constants.GET_USER_REFERRAL_CODE_NODE);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getReferralCodeReq = new JsonObjectRequest(Request.Method.POST, getReferralCodeReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        referAndEarnFragment.updateReferralCode(response);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            referAndEarnFragment.showAlertDialog(response.getString("message"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getReferralCodeReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReferralHistory(final HistoryReferActivity historyReferActivity, String userId, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getReferralHistoryReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.GET_USER_REWARD_CASHBACK_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getReferralHistoryReq = new JsonObjectRequest(Request.Method.POST, getReferralHistoryReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        Type rewardPointsDataListType = new TypeToken<ArrayList<RewardPointsData>>() {
                                        }.getType();
                                        ArrayList<RewardPointsData> rewardPointsDataList = new Gson().fromJson(response.getString("reward_pts_data"), rewardPointsDataListType);
                                        historyReferActivity.updateRewardPointsAndCashBackData(rewardPointsDataList, response);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            historyReferActivity.showAlertDialog(response.getString("message"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getReferralHistoryReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postUserConsent(final ConsentDocActivity consentDocActivity, String userId, String code, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String url = String.format("%s/%s", Constants.MAIN_URL, Constants.AgreePreLoanAgreement);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("passcode", code);
                reqObject.put("user_consent", 1);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getReferralHistoryReq = new JsonObjectRequest(Request.Method.POST, url, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getBoolean("status")) {
                                        consentDocActivity.refreshData();
                                    } else {
                                        consentDocActivity.showAlertDialog(response.getString("message"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getReferralHistoryReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveBankChangeRequestData(String userId, RequestBankChangeData requestBankChangeData, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String saveOneTimeRegistrationReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.SAVE_BANK_UPDATE_DETAILS);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("account_holder_name", requestBankChangeData.getAccountHolderName());
                reqObject.put("account_number", requestBankChangeData.getAccountNumber());
                reqObject.put("ifsc_code", requestBankChangeData.getIfscCode());
                reqObject.put("bank_name", requestBankChangeData.getBankName());
                reqObject.put("branch_name", requestBankChangeData.getBranchName());
                reqObject.put("bank_statement", requestBankChangeData.getBankStatement());
                reqObject.put("slip_password", requestBankChangeData.getBankAccountPassword());
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest saveOneTimeRegistrationReq = new JsonObjectRequest(Request.Method.POST, saveOneTimeRegistrationReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    CNProgressDialog.hideProgressDialog();
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        Toast.makeText(activity, response.getString("message"), Toast.LENGTH_LONG).show();
                                        activity.onBackPressed();
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            Toast.makeText(activity, response.getString("message"), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(saveOneTimeRegistrationReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTwlOrderData(final DashboardActivity dashboardActivity, String userId, TwlAmtPayable twlAmtPayable, String token) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getOrderDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.TWL_ORDER_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("amount", twlAmtPayable.getDueAmount());
                Gson gson = new Gson();
                ArrayList<TwlAmtPayable> list = new ArrayList();
                list.add(twlAmtPayable);
                String listString = gson.toJson(list, new TypeToken<ArrayList<TwlAmtPayable>>() {
                }.getType());
                JSONArray myCustomArray = new JSONArray(listString.replaceAll("\\/", "-"));
                reqObject.put("selected_ids", myCustomArray);
                reqObject.put("wclient", 0);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getOrderDataReqURL, reqObject,
                        response -> {
                            try {
                                if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                    OrderData orderData = new Gson().fromJson(response.toString(), OrderData.class);
                                    dashboardActivity.updateOrderData(orderData);
                                } else {
                                    if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                        logout();
                                    } else {
                                        dashboardActivity.showAlertDialog(response.getString("message"));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> ((BaseActivity) activity).onVolleyErrorResponse(error)
                );

                AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTwlPaymentData(final DashboardActivity dashboardActivity, String userId, String paymentId, String orderId, String signature, String token) {
        try {
            if (new NetworkConnectionDetector(dashboardActivity).isNetworkConnected()) {
                String getOrderDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.SAVE_TWL_PAYMENT_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("paymentId", paymentId);
                reqObject.put("orderId", orderId);
                reqObject.put("signature", signature);
                reqObject.put("device_unique_id", deviceUniqueId);
                Log.d("saveTwlPaymentData", new Gson().toJson(reqObject));


                JsonObjectRequest getProfileDataReq = new JsonObjectRequest(Request.Method.POST, getOrderDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        PaymentClearData paymentClearData = new Gson().fromJson(response.toString(), PaymentClearData.class);
                                        dashboardActivity.updatePaymentData(paymentClearData);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            dashboardActivity.onPaymentError(0, "", null);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    dashboardActivity.onError(e.toString());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dashboardActivity.onError(error.toString());
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );
                AppController.getInstance(context).addToRequestQueue(getProfileDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getApplyLoanDataBase(String userId, String token, Activity activity, boolean canRedirect) {
        try {
            if (new NetworkConnectionDetector(context).isNetworkConnected()) {
                String getApplyLoanDataReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.APPLY_LOAN_DATA);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("device_unique_id", deviceUniqueId);

                JsonObjectRequest getApplyLoanDataReq = new JsonObjectRequest(Request.Method.POST, getApplyLoanDataReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (activity instanceof BaseActivity) {
                                        ((BaseActivity) activity).updateApplyLoansData(response, canRedirect);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((DashboardActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(getApplyLoanDataReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkCanApply(final ApplyLoanEMIFragment applyLoanFragment, String userId, ApplyLoan applyLoan, int Qcr_accept_pre_agreement, String token) {
        try {
            if (new NetworkConnectionDetector(applyLoanFragment.getContext()).isNetworkConnected()) {
                String validateOTPAndApplyLoanReqURL = String.format("%s/%s", Constants.MAIN_URL, Constants.CHECK_CAN_APPLY);

                JSONObject reqObject = new JSONObject();
                reqObject.put("api_key", token);
                reqObject.put("user_id", userId);
                reqObject.put("otp_password", applyLoan.getOtpPassword());
                reqObject.put("amount", applyLoan.getAmount());
                reqObject.put("tenure_days", applyLoan.getTenureDays());
                reqObject.put("service_fee", applyLoan.getServiceFee());
                reqObject.put("processing_charges", applyLoan.getProcessingCharges());
                reqObject.put("new_processing_charges", applyLoan.getNewProcessingCharges());
                reqObject.put("total", applyLoan.getTotal());
                reqObject.put("promo_code", applyLoan.getPromo_code());
                reqObject.put("Qcr_req_promo_code", applyLoan.getQcr_req_promo_code());
                reqObject.put("amazon_pay_number", applyLoan.getAmazonNumber());
                reqObject.put("toAmazon", applyLoan.getAmazonAmount());
                reqObject.put("toBank", applyLoan.getBankAmount());
                reqObject.put("loan_type", applyLoan.getLoanType());
                reqObject.put("cashback_amt", applyLoan.getCashback_amt());
                reqObject.put("Qcr_purpose_of_loan", applyLoan.getQcr_purpose_of_loan());
                reqObject.put("Qcr_custom_purpose", applyLoan.getQcr_custom_purpose());
                reqObject.put("Qcr_accept_pre_agreement", Qcr_accept_pre_agreement);
                reqObject.put("tenure_type", applyLoan.getTenureType());
                reqObject.put("emi_count", applyLoan.getEmiCount());
                reqObject.put("current_location", applyLoan.getCurrent_location());

                Gson gson = new Gson();
                String listString = gson.toJson(
                        applyLoan.getInstalmentDataList(),
                        new TypeToken<ArrayList<InstalmentData>>() {
                        }.getType());

                JSONArray myCustomArray = new JSONArray(listString.replaceAll("\\/", "-"));
                reqObject.put("instalments", myCustomArray);

                reqObject.put("device_unique_id", deviceUniqueId);

                Log.d("applyloan_emi_req", reqObject.toString());

                JsonObjectRequest requestOTPReq = new JsonObjectRequest(Request.Method.POST, validateOTPAndApplyLoanReqURL, reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals(Constants.STATUS_SUCCESS)) {
                                        applyLoanFragment.takeConfirmationToApplyForLoan(true);
                                    } else {
                                        if (response.has("status_code") && response.getInt("status_code") == Constants.STATUS_CODE_UNAUTHORISED) {
                                            logout();
                                        } else {
                                            if(response.has("status_code") && response.getInt("status_code") == 999){
                                                applyLoanFragment.showNewAlertDialog(response);
                                            }else {
                                                applyLoanFragment.showAlertDialog(response.getString("message"));
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((BaseActivity) activity).onVolleyErrorResponse(error);
                            }
                        }
                );

                AppController.getInstance(context).addToRequestQueue(requestOTPReq, TAG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

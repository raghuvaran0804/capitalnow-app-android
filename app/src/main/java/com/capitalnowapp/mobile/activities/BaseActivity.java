package com.capitalnowapp.mobile.activities;

import static com.capitalnowapp.mobile.CapitalNowApp.mp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.appsflyer.AppsFlyerLib;
import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.beans.GlobalContent;
import com.capitalnowapp.mobile.beans.LoanAgreementConsent;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.customviews.CNAlertDialog;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity;
import com.capitalnowapp.mobile.kotlin.activities.GetStartedActivity;
import com.capitalnowapp.mobile.kotlin.activities.MandatoryPermissionsActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.AadharVerificationActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.ApplyLoanAmountActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.BestOfferActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.EMandateActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.ESignActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.FinalOfferActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.OfferReferencesActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.ProgressActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.RegistrationBankDetailsActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.RegistrationForm1Activity;
import com.capitalnowapp.mobile.kotlin.activities.offer.RegistrationForm2Activity;
import com.capitalnowapp.mobile.kotlin.activities.offer.RegistrationForm3Activity;
import com.capitalnowapp.mobile.kotlin.activities.offer.RegistrationPanActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.SalarySlipActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.SelfieActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.StatusActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.UnderReviewActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.VerifyBankDetailsActivity;
import com.capitalnowapp.mobile.models.CCAResponseDetails;
import com.capitalnowapp.mobile.models.CNModel;
import com.capitalnowapp.mobile.models.GenericRequest;
import com.capitalnowapp.mobile.models.userdetails.UserDetails;
import com.capitalnowapp.mobile.models.userdetails.UserDetailsResponse;
import com.capitalnowapp.mobile.retrofit.GenericAPIService;
import com.capitalnowapp.mobile.util.CNEncryptDecrypt;
import com.capitalnowapp.mobile.util.CNSharedPreferences;
import com.capitalnowapp.mobile.util.TrackingUtil;
import com.capitalnowapp.mobile.util.Utility;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

//import io.branch.referral.util.BranchEvent;

public class BaseActivity extends AppCompatActivity {

    public LoanAgreementConsent loanAgreementConsent = null;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1005;
    private static final int BANNER_REQUEST_CODE = 10006;

    public Context applicationContext, activityContext;

    public CNSharedPreferences sharedPreferences;
    protected Constants.RequestFrom requestFrom;
    public CNModel cnModel;
    protected CNEncryptDecrypt CNEncryptDecrypt;
    protected GlobalContent globalContent;
    public String userId;
    public UserDetails userDetails;
    public boolean canApplyLoan = true;

    public boolean rewBankChange = false;
    public static int permissionsRedirectPage = -1;
    public String canApplyLoanMsg = "";
    public String docsApplyLoanMsg = "";
    public Activity currentActivity = null;
    public long mLastClickTime = 0;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates = false;

    private boolean turnOnDisabledByUser = false;

    private String currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            applicationContext = getApplicationContext();
            activityContext = BaseActivity.this;
            sharedPreferences = new CNSharedPreferences(this);
            CNEncryptDecrypt = new CNEncryptDecrypt();
            globalContent = GlobalContent.getInstance();
            userDetails = new Gson().fromJson(sharedPreferences.getString(Constants.USER_DETAILS_DATA), UserDetails.class);
            cnModel = new CNModel(activityContext, this, requestFrom);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserToken() {
        if (sharedPreferences == null) {
            sharedPreferences = new CNSharedPreferences(this);
        }
        return sharedPreferences.getString(Constants.USER_TOKEN);
    }

    public void displayLongToast(String message) {
        Utility.displayToast(activityContext, message, Toast.LENGTH_LONG);
    }

    public void displayToast(String message) {
        Utility.displayToast(this, message, 0);
    }

    public void onVolleyErrorResponse(VolleyError error) {
        if (CNProgressDialog.isProgressDialogShown)
            CNProgressDialog.hideProgressDialog();

        CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_error), getResources().getString(R.string.error_failure));
    }

    public void showAlertDialog(String message) {
        if (CNProgressDialog.isProgressDialogShown)
            CNProgressDialog.hideProgressDialog();

        CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_alert), message);
    }

    public String getEncryptedValue(String input) throws Exception {
        return CNEncryptDecrypt.encryptString(input);
    }

    public String getDecryptedValue(String value) throws Exception {
        return CNEncryptDecrypt.decryptString(value);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPreferences.getBoolean(Constants.SP_IS_GET_STARTED_SHOWN)) {
            if (userDetails != null && userDetails.getUserId() != null) {
                if (!allMandatoryPermissionsGranted(false)) {
                    if (!sharedPreferences.getBoolean(Constants.PERMISSIONS_REQUESTED) && currentActivity == null) {
                        //startActivity(new Intent(BaseActivity.this, AppPermissionsActivity.class));
                    }
                }
            } else {
                proceed();
            }
        } else {
            if (activityContext instanceof GetStartedActivity) {

            } else {
                Intent intent = new Intent(activityContext, GetStartedActivity.class);
                //   AdGyde.onSimpleEvent(getString(R.string.app_open_first_time));
                HashMap<String, Object> params = new HashMap<>();
                String key = getString(R.string.app_open_first_time);
                AppsFlyerLib.getInstance().logEvent(this, key, params);
                AppEventsLogger logger = AppEventsLogger.newLogger(this);
                logger.logEvent(getString(R.string.app_open_first_time), new Bundle());

                TrackingUtil.pushEvent(new JSONObject(), getString(R.string.app_open_first_time));

                /*new BranchEvent("OpenedAfterInstallation")
                        .addCustomDataProperty("OpenedAfterInstallation", "Opened_After_Installation")
                        .setCustomerEventAlias("Opened_After_Installation")
                        .logEvent(BaseActivity.this);*/

                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }
        }

    }

    public boolean allMandatoryPermissionsGranted(boolean doRequest) {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);


        Vector<String> listPermissionsNeeded = new Vector<>();
        if (locationPermission == PackageManager.PERMISSION_GRANTED ||
                cameraPermission == PackageManager.PERMISSION_GRANTED ||
                audioPermission == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        } else {
            if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (audioPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
            }
        }
        if (!listPermissionsNeeded.isEmpty() && doRequest) {
            String[] permissions = listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

        return false;
    }

    public boolean allMandatoryPermissionsGranted1(boolean doRequest) {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);


        Vector<String> listPermissionsNeeded = new Vector<>();
        if (locationPermission == PackageManager.PERMISSION_GRANTED &&
                cameraPermission == PackageManager.PERMISSION_GRANTED &&
                audioPermission == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        } else {
            if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (audioPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
            }

        }

        if (!listPermissionsNeeded.isEmpty() && doRequest) {
            String[] permissions = listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void launchDesiredActivity() {
        Intent intent = null;
        if (sharedPreferences.getBoolean(Constants.SP_IS_GET_STARTED_SHOWN)) {
            if (userDetails != null && userDetails.getUserId() != null) {
                if (allMandatoryPermissionsGranted(false)) {
                    proceed();
                } else if (!sharedPreferences.getBoolean(Constants.PERMISSIONS_REQUESTED) && currentActivity == null) {
                    intent = new Intent(activityContext, MandatoryPermissionsActivity.class);
                }

            } else {
                proceed();
            }

        } else {
            intent = new Intent(activityContext, GetStartedActivity.class);
            //   AdGyde.onSimpleEvent(getString(R.string.app_open_first_time));
            HashMap<String, Object> params = new HashMap<>();
            String key = getString(R.string.app_open_first_time);
            AppsFlyerLib.getInstance().logEvent(this, key, params);
            AppEventsLogger logger = AppEventsLogger.newLogger(this);
            logger.logEvent(getString(R.string.app_open_first_time), new Bundle());

            /*new BranchEvent("OpenedAfterInstallation")
                    .addCustomDataProperty("OpenedAfterInstallation", "Opened_After_Installation")
                    .setCustomerEventAlias("Opened_After_Installation")
                    .logEvent(BaseActivity.this);*/
        }

        /*if (allMandatoryPermissionsGranted(false)) {
            boolean madeInIndiaShown = sharedPreferences.getBoolean(Constants.MADE_IN_INDIA_SHOWN);
            if (madeInIndiaShown) {
                if (sharedPreferences.getBoolean(Constants.SP_IS_GET_STARTED_SHOWN)) {
                    proceed();
                } else {

                }
            } else {
                intent = new Intent(this, BannerActivity.class);
            }
        } else if (!sharedPreferences.getBoolean(Constants.PERMISSIONS_REQUESTED) && currentActivity == null) {
            intent = new Intent(activityContext, AppPermissionsActivity.class);
        }*/
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    private void proceed() {
        Intent intent;
        if (userDetails != null && userDetails.getUserId() != null) {
            getProfile(userDetails.getUserId());
        } else {
            if (currentActivity == null) {
                intent = new Intent(activityContext, LoginActivity.class);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                startActivity(intent);
            }
        }
    }

    public void updateStep2RegistrationResponse(String user_id, String message) {
        if (sharedPreferences.getBoolean("fromDocs")) {
            onBackPressed();
        } else {
            getProfile(user_id);
        }
    }

    public void getProfile(String userId) {
        this.userId = userId;
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE);
        GenericAPIService genericAPIService = new GenericAPIService(activityContext, 0);
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setUserId(userId);
        genericRequest.setDeviceUniqueId(Utility.getInstance().getDeviceUniqueId(this));

        genericAPIService.getUserData(genericRequest);

        genericAPIService.setOnDataListener(new GenericAPIService.DataInterface() {
            @Override
            public void responseData(String responseBody) {
                CNProgressDialog.hideProgressDialog();
                UserDetailsResponse userDetailsResponse = new Gson().fromJson(responseBody, UserDetailsResponse.class);
                if (userDetailsResponse != null && userDetailsResponse.getStatusCode() == Constants.STATUS_CODE_UNAUTHORISED) {
                    logout();
                } else {
                    if (userDetailsResponse != null && userDetailsResponse.getUserDetails() != null && userDetailsResponse.getUserDetails().getQcId() != null
                            && !userDetailsResponse.getUserDetails().getQcId().equals("")) {
                        sharedPreferences.putString(Constants.RAZOR_PAY_API_KEY, userDetailsResponse.getRazorPayApiKey());
                        sharedPreferences.putString(Constants.USER_DETAILS_DATA, new Gson().toJson(userDetailsResponse.getUserDetails()));
                        userDetails = userDetailsResponse.getUserDetails();
                        loadLaunchIntent(userDetails);
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.error_failure), Toast.LENGTH_SHORT).show();
                        userDetails = new Gson().fromJson(sharedPreferences.getString(Constants.USER_DETAILS_DATA), UserDetails.class);
                        loadLaunchIntent(userDetails);
                    }
                }
            }
        });

        genericAPIService.setOnErrorListener(new GenericAPIService.ErrorInterface() {
            @Override
            public void errorData(Throwable throwable) {
                CNProgressDialog.hideProgressDialog();
                Toast.makeText(applicationContext, getString(R.string.error_failure), Toast.LENGTH_SHORT).show();
                loadLaunchIntent(null);
            }
        });
    }

    private void loadLaunchIntent(UserDetails userDetails) {
        Intent intent = null;
        if (userDetails != null) {
            if (CSPermissionsGranted()) {
                //mp = MixpanelAPI.getInstance(this, "7cb71e1bee9407fccf103de8a4c802bd", true);
                if (userDetails.getFirstName() != null) {
                    mp.identify(userDetails.getQcId());
                    mp.getPeople().set("$first_name", userDetails.getUserId());
               /* mp.getPeople().set("$last_name", userDetails.getLastName());
                mp.getPeople().set("$phone", userDetails.getUserMobile());
                mp.getPeople().set("$email", userDetails.getEmail());*/
                }
                if (userDetails.getUserStatusId().equals("1")) {

                    if (userDetails.getEmail() != null && !userDetails.getEmail().equals("")) {
                        intent = new Intent(activityContext, DashboardActivity.class);
                        intent.putExtra("from","fromBaseActivity");
                        startActivity(intent);
                    } else {
                        intent = new Intent(activityContext, DashboardActivity.class);
                    }

                } /*else if (userDetails.getUserStatusId().equals("23") && userDetails.getUserStatus().equals("I")) {
                sharedPreferences.putString(Constants.USER_DETAILS_DATA, null);
                intent = new Intent(activityContext, LoginActivity.class);
            }*/ else {
                    if (userDetails.getHasMembership().equals(1)) {
                        intent = new Intent(activityContext, DashboardActivity.class);
                    } else {
                        intent = new Intent(activityContext, DashboardActivity.class);
                    }


                    //getApplyLoanDataBase();

                }
            } else {
                ((BaseActivity) currentActivity).permissionsRedirectPage = 104;
                Intent i = new Intent(this, MandatoryPermissionsActivity.class);
                startActivity(i);
            }
        } else {
            intent = new Intent(activityContext, LoginActivity.class);
        }
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
            finish();
        }
    }

    private boolean CSPermissionsGranted() {

        int locationPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return locationPermission == PackageManager.PERMISSION_GRANTED;

    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadCCAResponse(CCAResponseDetails ccaResponseDetails, String token,
                                  boolean fromTwlLoan) {
        String param = new Gson().toJson(ccaResponseDetails);
        Utility utility = Utility.getInstance();
        try {
            JSONObject valuesObj = new JSONObject();
            JSONObject obj = new JSONObject(param);
            valuesObj.put("result", obj);
            valuesObj.put("device_unique_id", utility.getDeviceUniqueId(this));
            valuesObj.put("api_key", token);
            if (!fromTwlLoan) {
                cnModel.saveCCAData(valuesObj);
            } else {
                cnModel.saveTwlCCAData(valuesObj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void proceedToContactUs() {
        startActivity(new Intent(this, DashboardActivity.class).putExtra("redirect", getString(R.string.contact_us)));
    }

    public void proceedToEmailSupport() {
        startActivity(new Intent(this, DashboardActivity.class).putExtra("redirect", getString(R.string.email_support)));
    }

    public void proceedToActiveLoans() {
        startActivity(new Intent(this, DashboardActivity.class).putExtra("redirect", getString(R.string.active_loans)));
    }

    public void proceedToUploadDoc() {
        startActivity(new Intent(this, DashboardActivity.class).putExtra("redirect", getString(R.string.upload_documents)));
    }

    public void proceedToRBC() {
        startActivity(new Intent(this, DashboardActivity.class).putExtra("redirect", getString(R.string.request_bank_chnage)));
    }

    public void proceedToRewards() {
        startActivity(new Intent(this, DashboardActivity.class).putExtra("redirect", getString(R.string.reward_points)));
    }

    public void logout() {
        userDetails = null;
        sharedPreferences.putString(Constants.USER_DETAILS_DATA, new Gson().toJson(userDetails));
        sharedPreferences.putString(Constants.USER_REGISTRATION_DATA, null);
        Intent logInIntent = new Intent(getApplicationContext(), LoginActivity.class);
        /*logInIntent.setFlags() =
                FLAG_ACTIVITY_NO_ANIMATION || Intent.FLAG_ACTIVITY_NEW_TASK || Intent.FLAG_ACTIVITY_CLEAR_TASK;*/
        startActivity(logInIntent);
        //overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }


    //Redirect
    public void getApplyLoanDataBase(boolean canRedirect) {
        CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE);
        cnModel.getApplyLoanDataBase(userDetails.getUserId(), getUserToken(), this, canRedirect);
    }

    public void updateApplyLoansData(JSONObject response, boolean canRedirect) {
        CNProgressDialog.hideProgressDialog();
        if (canRedirect) {
            try {
                Log.d("redirect", String.valueOf(response.getInt("status_redirect")));
                switch (response.getInt("status_redirect")) {
                    case Constants.STATUS_REDIRECT_CODE_OFFER_REG_1: {
                        launchOfferReg1();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_REG_2: {
                        launchOfferReg2();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_REG_3: {
                        launchOfferBankDetails();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_REG_4: {
                        launchOfferReg3();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_PAN: {
                        launchOfferPan();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_APPLY_LOAN: {
                        launchOfferApplyLoanAmount();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_SAL_SLIP: {
                        launchOfferSalarySlip();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_OFFLINE_AADHAR: {
                        launchOfferOfflineAadhar();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_SELFIE: {
                        launchOfferSelfie();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_BANK_STATEMENT: {
                        launchOfferVerifyBankDetails();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_FINAL_OFFER: {
                        launchOfferFinalOffer();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_E_NACH: {
                        launchOfferEMandate();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_KFS: {
                        launchOfferESign();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_FINAL_STATUS: {
                        launchOfferStatus();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_REF: {
                        launchOfferReferences();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_LOADER: {
                        launchOfferLoader();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_GENERATE_OFFER: {
                        launchProcessingPage();
                        break;
                    }
                    case Constants.STATUS_REDIRECT_CODE_OFFER_UNDER_REVIEW: {
                        launchUnderReviewPage();
                        break;
                    }
                    default: {
                        launchDashboardActivity();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void launchDashboardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private void launchUnderReviewPage() {
        Intent intent = new Intent(this, UnderReviewActivity.class);
        startActivity(intent);
    }

    private void launchProcessingPage() {
        Intent intent = new Intent(this, BestOfferActivity.class);
        startActivity(intent);
    }

    private void launchOfferLoader() {
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);

    }

    private void launchOfferReferences() {
        Intent intent = new Intent(this, OfferReferencesActivity.class);
        startActivity(intent);

    }

    private void launchOfferStatus() {
        Intent intent = new Intent(this, StatusActivity.class);
        startActivity(intent);

    }

    private void launchOfferESign() {
        Intent intent = new Intent(this, ESignActivity.class);
        startActivity(intent);

    }

    private void launchOfferEMandate() {
        Intent intent = new Intent(this, EMandateActivity.class);
        startActivity(intent);

    }

    private void launchOfferFinalOffer() {
        Intent intent = new Intent(this, FinalOfferActivity.class);
        startActivity(intent);

    }

    private void launchOfferVerifyBankDetails() {
        Intent intent = new Intent(this, VerifyBankDetailsActivity.class);
        startActivity(intent);

    }

    private void launchOfferSelfie() {
        Intent intent = new Intent(this, SelfieActivity.class);
        startActivity(intent);

    }

    private void launchOfferOfflineAadhar() {
        Intent intent = new Intent(this, AadharVerificationActivity.class);
        startActivity(intent);

    }

    private void launchOfferBankDetails() {
        Intent intent = new Intent(this, RegistrationBankDetailsActivity.class);
        startActivity(intent);

    }

    private void launchOfferReg1() {
        Intent intent = new Intent(this, RegistrationForm1Activity.class);
        startActivity(intent);

    }

    private void launchOfferReg2() {
        Intent intent = new Intent(this, RegistrationForm2Activity.class);
        startActivity(intent);

    }

    private void launchOfferReg3() {
        Intent intent = new Intent(this, RegistrationForm3Activity.class);
        startActivity(intent);

    }

    private void launchOfferPan() {
        Intent intent = new Intent(this, RegistrationPanActivity.class);
        startActivity(intent);

    }

    private void launchOfferApplyLoanAmount() {
        Intent intent = new Intent(this, ApplyLoanAmountActivity.class);
        startActivity(intent);

    }

    private void launchOfferSalarySlip() {
        Intent intent = new Intent(this, SalarySlipActivity.class);
        startActivity(intent);

    }
}

package com.capitalnowapp.mobile.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.broadcastreceivers.SMSRetrieverBroadCastReceiver;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.customviews.CNAlertDialog;
import com.capitalnowapp.mobile.customviews.CNButton;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.customviews.CNTextView;
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener;
import com.capitalnowapp.mobile.interfaces.SMSListener;
import com.capitalnowapp.mobile.kotlin.fragments.OTPSheetFragment;
import com.capitalnowapp.mobile.kotlin.utils.Validator;
import com.capitalnowapp.mobile.models.AutofillResponse;
import com.capitalnowapp.mobile.models.CNModel;
import com.capitalnowapp.mobile.models.GenericResponse;
import com.capitalnowapp.mobile.models.UpdateNewLocationReq;
import com.capitalnowapp.mobile.models.UpdateNewLocationResponse;
import com.capitalnowapp.mobile.models.login.GetOTPRequest;
import com.capitalnowapp.mobile.models.login.VerifyOTPRequest;
import com.capitalnowapp.mobile.models.userdetails.UserDetails;
import com.capitalnowapp.mobile.retrofit.GenericAPIService;
import com.capitalnowapp.mobile.util.AppSignatureHelper;
import com.capitalnowapp.mobile.util.NetworkConnectionDetector;
import com.capitalnowapp.mobile.util.TrackingUtil;
import com.capitalnowapp.mobile.util.Utility;
import com.chaos.view.BuildConfig;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

//import io.branch.referral.util.BranchEvent;

public class LoginActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = LoginActivity.class.getSimpleName();

    private static final String PREF_NAME = "MyPreferences";
    private static final String LAST_EXECUTION_DATE = "last_execution_date";

    private CNButton login_button;
    private CNModel cnModel;
    private Utility utility;
    private String currentLocation = null, deviceUniqueId = "", deviceToken = "";
    private boolean exitApp = false;
    private boolean isPhoneNumberShown = false;
    private final static int RESOLVE_HINT = 1011;
    String mobNumber;
    String newMobNumber;

    /* Location Fetching Related Variables */
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates = false;

    private SMSRetrieverBroadCastReceiver mSmsBroadcastReceiver;
    private com.capitalnowapp.mobile.databinding.ActivityLoginNewBinding binding;
    //private PinView otpPinView;
    private EditText otpEditText;
    private boolean turnOnDisabledByUser = false;
    public String otpStr = "";
    private OTPSheetFragment fragment;
    //private PinView otpViewDialog;
    private EditText otpViewDialog;
    private boolean isResend = false;
    private TextView tvConcent;
    private View currentLayout;
    private GetOTPRequest getOTPRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        /*String s = "a";
        Log.d("qwerty", s.substring(0, 10));*/
        binding = com.capitalnowapp.mobile.databinding.ActivityLoginNewBinding.inflate(getLayoutInflater());
        Constants.CURRENT_SCREEN = "4";
        currentActivity = this;
        // ic_federal_lin order to hide keyboard when this screen appears because of EditTextView.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(binding.getRoot());
        //final ImageView btnLogin = findViewById(R.id.btnLogin);
        tvConcent = findViewById(R.id.tvConcent);

        tvConcent.setText(Html.fromHtml("By continuing, I agree to have read and understood the\n <a href=\"https://api.capitalnow.in/mpage/terms-and-conditions\"><b><font color='black'>Terms &amp; Conditions</font></b></a> and the <a href=\"https://api.capitalnow.in/mpage/privacy-and-security-policy\"><b><font color='black'>Privacy Policy</font></b></a> of Capital Now. "));
        tvConcent.setMovementMethod(LinkMovementMethod.getInstance());
        tvConcent.setLinkTextColor(Color.BLACK);
        //btnLogin Not using
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("mobileNUmber", "");
                    obj.put(getString(R.string.interaction_type), "VERIFY Button Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj, getString(R.string.otp_submitted));
                verifyOTP(binding.llOTP.mobileNumber.getText().toString().trim(), otpStr);

                //startActivity(new Intent(LoginActivity.this, FederalRegistrationActivity.class));
            }
        });
        sharedPreferences.putString(Constants.USER_DETAILS_DATA, new Gson().toJson(new UserDetails()));
        sharedPreferences.putString(Constants.USER_REGISTRATION_DATA, null);
        sharedPreferences.putBoolean(Constants.From_Vehicle_Details, false);
        userDetails = null;

        binding.llOTP.mobileNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isPhoneNumberShown){
                    phoneSelection();
                    isPhoneNumberShown = true;
                }else{
                    //binding.llOTP.mobileNumber.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Primary1)));
                    binding.llOTP.mobileNumber.setFocusable(true);
                    binding.llOTP.mobileNumber.setFocusableInTouchMode(true);
                    binding.llOTP.mobileNumber.setClickable(true);
                    binding.llOTP.mobileNumber.setEnabled(true);
                    InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.showSoftInput(binding.llOTP.mobileNumber, 0);
                }
                //phoneSelection();
            }

        });

        binding.llOTP.tvMobileNumber1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                phoneSelection();
            }
        });

        binding.llOTP.otp.setEnabled(false);
        binding.llOTP.getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("mobileNUmber", "");
                    obj.put(getString(R.string.interaction_type), "GET OTP Button Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj, getString(R.string.get_otp_clicked));
                //hideKeyboard(LoginActivity.this);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Validator.validatePhoneNum(binding.llOTP.mobileNumber)) {
                    //startActivity(new Intent(LoginActivity.this, FederalRegistrationActivity.class));
                    if (new NetworkConnectionDetector(LoginActivity.this).isNetworkConnected()) {
                        requestOTP();
                    }
                } else {
                    Toast.makeText(activityContext, getString(R.string.error_invalid_mobile_no), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.llOTP.mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    binding.llOTP.getOtp.setTextColor(activityContext.getResources().getColor(R.color.white));
                    binding.llOTP.getOtp.setBackgroundTintList(activityContext.getResources().getColorStateList(R.color.Primary2));
                    binding.llOTP.tilMobileNumber.setBoxStrokeColor(getResources().getColor(R.color.colorAlertDialog));
                    binding.llOTP.getOtp.setEnabled(true);
                    binding.llOTP.otp.setEnabled(true);
                    binding.llOTP.getOtp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_forward_24_white, 0);
                    hideKeyboard(LoginActivity.this);
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("mobileNumber", "");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.mobile_number_entered));
                } else if (s.length() == 0) {
                    binding.llOTP.tilMobileNumber.setBoxStrokeColor(getResources().getColor(R.color.Primary1));
                } else {
                    binding.llOTP.otp.setEnabled(false);
                    binding.llOTP.getOtp.setEnabled(false);
                    binding.llOTP.getOtp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_forward_24, 0);
                    binding.llOTP.tilMobileNumber.setBoxStrokeColor(getResources().getColor(R.color.cb_errorRed));
                    binding.llOTP.getOtp.setBackgroundTintList(activityContext.getResources().getColorStateList(R.color.Secondary1));
                    binding.llOTP.getOtp.setTextColor(activityContext.getResources().getColor(R.color.black));
                }
            }
        });

        otpEditText = findViewById(R.id.otp);
        binding.btnLogin.setEnabled(false);
        otpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 6) {
                    hideKeyboard(LoginActivity.this);
                    binding.btnLogin.setEnabled(true);
                    binding.btnLogin.callOnClick();
                } else {
                    binding.btnLogin.setEnabled(false);
                    binding.btnLogin.setEnabled(false);
                }
                binding.llOTP.mobileNumber.setEnabled(s.toString().length() <= 0);
            }
        });

        try {
            applicationContext = getApplicationContext();
            activityContext = LoginActivity.this;

            requestFrom = Constants.RequestFrom.LOGIN_PAGE;
            cnModel = new CNModel(activityContext, this, requestFrom);
            utility = Utility.getInstance();

            deviceToken = sharedPreferences.getString(Constants.SP_DEVICE_TOKEN);
            // Getting Android Device Unique Id to identify it Uniquely.
            deviceUniqueId = sharedPreferences.getString(Constants.SP_DEVICE_UNIQUE_ID);

            if (deviceUniqueId.isEmpty()) {
                deviceUniqueId = utility.getDeviceUniqueId(this);
                sharedPreferences.putString(Constants.SP_DEVICE_UNIQUE_ID, deviceUniqueId);
            }

            login_button = findViewById(R.id.login_button);
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // attemptLogin();
                }
            });
              initLocationServices();
              restoreValuesFromBundle(savedInstanceState);
              checkAndRequestLocationPermissions();


            /*new BranchEvent("LoggedIn")
                    .addCustomDataProperty("LoggedIn", "Logged_In")
                    .setCustomerEventAlias("Logged_In")
                    .logEvent(LoginActivity.this);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        autoFill();
    }

    protected void phoneSelection() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }

        binding.llOTP.mobileNumber.setFocusable(true);
        binding.llOTP.mobileNumber.setFocusableInTouchMode(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    mobNumber = credential.getId();
                    if(mobNumber.contains("+91")){
                        newMobNumber  = mobNumber.replace("+91","");
                    }else if (mobNumber.contains("91")){
                        newMobNumber = mobNumber.replace("91","");
                    }else if (mobNumber.contains("0")){
                        newMobNumber = mobNumber.replace("0","");
                    }
                    else {
                        newMobNumber = mobNumber;
                    }
                    binding.llOTP.mobileNumber.setText(newMobNumber);
                    binding.llOTP.tvMobileNumber1.setText(newMobNumber);

                } else {
                    binding.llOTP.mobileNumber.setText("No phone number available");
                }
            } else {
                //
            }
        }
    }

    private void requestOTP() {

        mSmsBroadcastReceiver = new SMSRetrieverBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(mSmsBroadcastReceiver, intentFilter);

        checkMobileNumber(binding.llOTP.mobileNumber.getText().toString().trim());
        readOTP();
    }

    public void readOTP() {
        try {
            // Utility.displayToast(activityContext, "Fetching OTP.. Please wait...", 1);

            SMSRetrieverBroadCastReceiver.bindListener(new SMSListener() {
                @Override
                public void onOTPReceived(String otpText) {
                    if (otpViewDialog != null) {
                        otpViewDialog.setText(otpText);
                    }
                }

                @Override
                public void onOTPTimeOut() {
                    // Utility.displayToast(context, "SMS Retriever API Timeout.", 1);
                    Log.i("SMS Retriever ", "API Timeout");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            if (CNAlertDialog.isAlertDialogShown && CNAlertDialog.alertDialog != null && CNAlertDialog.alertDialog.isShowing()) {
                CNAlertDialog.alertDialog.dismiss();
            }

            if (CNProgressDialog.isProgressDialogShown) {
                CNProgressDialog.hideProgressDialog();
            }

            if (mRequestingLocationUpdates) {
                // pausing location updates
                stopLocationUpdates();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (checkPermissions() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNewLocation() {
        try {
            GenericAPIService genericAPIService = new GenericAPIService(activityContext, 0);
            UpdateNewLocationReq updateNewLocationReq = new UpdateNewLocationReq();
            String token = getUserToken();
            String platform = "Android";
            updateNewLocationReq.setPlatform(platform);
            updateNewLocationReq.setCurrentNewLocation(currentLocation);
            genericAPIService.UpdateNewLocation(updateNewLocationReq, token);
            genericAPIService.setOnDataListener(new GenericAPIService.DataInterface() {
                @Override
                public void responseData(String responseBody) {
                    UpdateNewLocationResponse updateNewLocationResponse = new Gson().fromJson(responseBody, UpdateNewLocationResponse.class);
                    if (updateNewLocationResponse.getStatus()) {
                        //Toast.makeText(SplashScreen.this, "Location Fetched", Toast.LENGTH_SHORT).show();
                    } else {
                        //CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_alert), updateNewLocationResponse.getMessage());
                    }
                }
            });

            genericAPIService.setOnErrorListener(new GenericAPIService.ErrorInterface() {
                @Override
                public void errorData(Throwable throwable) {
                    CNProgressDialog.hideProgressDialog();
                    CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_alert), getString(R.string.error_failure));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (exitApp) {
            super.onBackPressed();
            finishAffinity();
        } else {
            displayToast(getResources().getString(R.string.back_button_double_click_exit));
            exitApp = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitApp = false;
                }
            }, 3 * 1000); // 3 sec
        }
    }

    public void autoFill(){
        GenericAPIService genericAPIService = new GenericAPIService(activityContext, 0);
        VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest();
        verifyOTPRequest.setDeviceUniqueId(deviceUniqueId);
        genericAPIService.autoFill(verifyOTPRequest);
        genericAPIService.setOnDataListener(new GenericAPIService.DataInterface() {
            @Override
            public void responseData(String responseBody) {
                CNProgressDialog.hideProgressDialog();
                AutofillResponse autofillResponse = new Gson().fromJson(responseBody, AutofillResponse.class);
                if (autofillResponse.getStatus()) {
                    if(autofillResponse.getAutoFill()) {
                        binding.llOTP.llAutoFill.setVisibility(View.VISIBLE);
                        binding.llOTP.tilMobileNumber.setVisibility(View.GONE);
                    }else {
                        binding.llOTP.llAutoFill.setVisibility(View.GONE);
                        binding.llOTP.tilMobileNumber.setVisibility(View.VISIBLE);
                    }
                }else {
                    binding.llOTP.tilMobileNumber.setVisibility(View.VISIBLE);
                }
            }
        });
        genericAPIService.setOnErrorListener(new GenericAPIService.ErrorInterface() {
            @Override
            public void errorData(Throwable throwable) {
                binding.llOTP.tilMobileNumber.setVisibility(View.VISIBLE);
            }
        });
    }

    public void verifyOTP(String mobile, String otp) {

        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE);
        GenericAPIService genericAPIService = new GenericAPIService(activityContext, 0);
        VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest();
        verifyOTPRequest.setMobileNo(mobile);
        verifyOTPRequest.setOtp(otp);
        verifyOTPRequest.setDeviceToken(deviceToken);
        verifyOTPRequest.setDeviceUniqueId(deviceUniqueId);
        verifyOTPRequest.setMobileVersion(Build.VERSION.RELEASE);
        verifyOTPRequest.setCurrentLocation(currentLocation);
        verifyOTPRequest.setPlatform("Android");
        verifyOTPRequest.setDeviceName(Utility.getDeviceName());
        verifyOTPRequest.setDeviceResolution(Utility.getScreenResolution(this));

        genericAPIService.verifyOTP(verifyOTPRequest);


        genericAPIService.setOnDataListener(new GenericAPIService.DataInterface() {
            @Override
            public void responseData(String responseBody) {
                CNProgressDialog.hideProgressDialog();
                GenericResponse genericResponse = new Gson().fromJson(responseBody, GenericResponse.class);
                if (genericResponse.getStatus()) {
                    if (genericResponse.getUsertoken() != null && !genericResponse.getUsertoken().equals("")) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("mobileNUmber", "");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        TrackingUtil.pushEvent(obj, getString(R.string.user_sign_in));
                        sharedPreferences.putString(Constants.USER_TOKEN, genericResponse.getUsertoken());
                        getProfile(genericResponse.getMessage());
                        sharedPreferences.putString(Constants.LOGGED_TIME, new SimpleDateFormat("dd MMM yyyy hh:mm:ss").format(new Date()));
                    }
                } else {
                    CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_alert), genericResponse.getMessage());
                }
            }
        });

        genericAPIService.setOnErrorListener(new GenericAPIService.ErrorInterface() {
            @Override
            public void errorData(Throwable throwable) {
                CNProgressDialog.hideProgressDialog();
                CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_alert), getString(R.string.error_failure));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_GET_CURRENT_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                Log.i(TAG, "Permission granted, updates requested, starting location updates");

                //if (mRequestingLocationUpdates) {
                startLocationUpdates();

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Constants.PERMISSION_GET_CURRENT_LOCATION)) {
                    CNAlertDialog alertDialog = new CNAlertDialog();
                    CNAlertDialog.setRequestCode(1);
                    CNAlertDialog.showAlertDialogWithCallback(activityContext, "", getResources().getString(R.string.need_location_permission), true, "", "");
                    CNAlertDialog.setListener(new AlertDialogSelectionListener() {
                        @Override
                        public void alertDialogCallback() {

                        }

                        @Override
                        public void alertDialogCallback(Constants.ButtonType buttonType, int requestCode) {
                            if (buttonType == Constants.ButtonType.POSITIVE) {
                                mRequestingLocationUpdates = true;
                                openSettings();
                            }
                        }
                    });
                } else {
                    displayToast("Go to settings and enable Location permission.");
                }
            }
        }
    }

    public void checkMobileNumber(String mobileNo) {
        try {
            CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE);
            GenericAPIService genericAPIService = new GenericAPIService(activityContext,0);
            GetOTPRequest getOTPRequest = new GetOTPRequest();
            getOTPRequest.setMobileNo(mobileNo);
            getOTPRequest.setDeviceToken(deviceToken);
            getOTPRequest.setDeviceUniqueId(deviceUniqueId);
            getOTPRequest.setPlatform("Android");
            getOTPRequest.setDeviceName(Utility.getDeviceName());
            getOTPRequest.setDeviceResolution(Utility.getScreenResolution(this));
            getOTPRequest.setMobileVersion(Build.VERSION.RELEASE);

            AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
            ArrayList<String> hashKeys = appSignatureHelper.getAppSignatures();
            if (hashKeys != null && hashKeys.size() > 0) {
                getOTPRequest.setOtpHash(hashKeys.get(0));
            }
            genericAPIService.requestOTP(getOTPRequest);
            Log.d("getOTPRequest", getOTPRequest.toString());

            genericAPIService.setOnDataListener(responseBody -> {
                CNProgressDialog.hideProgressDialog();
                GenericResponse genericResponse = new Gson().fromJson(responseBody, GenericResponse.class);
                if (genericResponse.getStatus()) {
                    displayLongToast(genericResponse.getMessage());
                    startSMSService();
                    //openOtpSheet(LoginActivity.this);
                    isResend = true;
                    showOtpDialog();
                } else {
                    binding.llOTP.tilMobileNumber.setBoxStrokeColor(getResources().getColor(R.color.cb_errorRed));
                    CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_alert), genericResponse.getMessage());
                }
            });

            genericAPIService.setOnErrorListener(throwable -> {
                CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_alert), getString(R.string.error_failure));
                CNProgressDialog.hideProgressDialog();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOtpDialog() {
        try {
            //final Dialog dialog = new Dialog(this);
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.otp_sheet);

            otpViewDialog = dialog.findViewById(R.id.otp);
            TextView tvNum = dialog.findViewById(R.id.tvNum);
            GifImageView givTimer = dialog.findViewById(R.id.givTimer);
            TextInputLayout tilMobile = dialog.findViewById(R.id.tilMobile);
            ImageView ivEdit = dialog.findViewById(R.id.ivEdit);
            TextView tvTimer = dialog.findViewById(R.id.tvTimer);
            TextView tvCalOTP = dialog.findViewById(R.id.tvCalOTP);
            CNTextView btnLogin = dialog.findViewById(R.id.btnLogin);
            tvNum.setText("We just sent a otp to your mobile number\n" + binding.llOTP.mobileNumber.getText().toString().trim());

            ivEdit.setOnClickListener(v -> {
                dialog.dismiss();
            });

            btnLogin.setOnClickListener(v -> {
                String otp = otpViewDialog.getText().toString().trim();
                if (otp != null && otp.length() == 6) {
                    btnLogin.setBackgroundTintList(activityContext.getResources().getColorStateList(R.color.Primary2));
                    tilMobile.setBoxStrokeColor(getResources().getColor(R.color.colorAlertDialog));
                    btnLogin.setTextColor(getResources().getColor(R.color.white));
                    otpStr = otp;
                    binding.btnLogin.callOnClick();
                } else if (otp.length() == 0) {
                    tilMobile.setBoxStrokeColor(getResources().getColor(R.color.Primary1));
                } else {
                    btnLogin.setBackgroundTintList(activityContext.getResources().getColorStateList(R.color.Secondary1));
                    tilMobile.setBoxStrokeColor(getResources().getColor(R.color.cb_errorRed));
                    btnLogin.setTextColor(getResources().getColor(R.color.black));
                    otpStr = "";
                }
            });

            otpViewDialog.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() == 6) {
                        hideKeyboard(LoginActivity.this);
                        btnLogin.setBackgroundTintList(activityContext.getResources().getColorStateList(R.color.Primary2));
                        tilMobile.setBoxStrokeColor(getResources().getColor(R.color.colorAlertDialog));
                        btnLogin.setTextColor(getResources().getColor(R.color.white));
                        btnLogin.setEnabled(true);
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("mobileNUmber", "");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        TrackingUtil.pushEvent(obj, getString(R.string.otp_entered));
                        btnLogin.callOnClick();
                    } else if (s.length() == 0) {
                        tilMobile.setBoxStrokeColor(getResources().getColor(R.color.Primary1));
                    } else {
                        btnLogin.setBackgroundTintList(activityContext.getResources().getColorStateList(R.color.Secondary1));
                        tilMobile.setBoxStrokeColor(getResources().getColor(R.color.cb_errorRed));
                        btnLogin.setTextColor(getResources().getColor(R.color.black));
                        btnLogin.setEnabled(false);
                    }
                }
            });

            /*Window window = dialog.getWindow();
            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 20);
            window.setBackgroundDrawable(inset);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);*/

            Window window = dialog.getWindow();
            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            tvCalOTP.setText(getResources().getString(R.string.payu_resend_otp));
            new CountDownTimer(121000, 1000) {

                public void onTick(long millisUntilFinished) {
                    tvTimer.setText("in " + millisUntilFinished / 1000 + " sec(s)");
                    //here you can have your logic to set text to edittext
                    if (millisUntilFinished < 1500) {
                        tvCalOTP.setEnabled(true);
                        tvCalOTP.setTypeface(tvCalOTP.getTypeface(), Typeface.BOLD);
                        tvCalOTP.setTextColor(getResources().getColor(R.color.black));
                        givTimer.setVisibility(View.VISIBLE);
                        tvTimer.setVisibility(View.INVISIBLE);
                    }
                }

                public void onFinish() {
                    tvCalOTP.setEnabled(true);
                    givTimer.setVisibility(View.GONE);
                    tvCalOTP.setTypeface(tvCalOTP.getTypeface(), Typeface.BOLD);
                    tvCalOTP.setTextColor(getResources().getColor(R.color.Primary2));
                    tvTimer.setVisibility(View.INVISIBLE);
                }
            }.start();

            tvCalOTP.setOnClickListener(v -> {
                if (new NetworkConnectionDetector(LoginActivity.this).isNetworkConnected()) {
                    tvCalOTP.setEnabled(false);
                    tvCalOTP.setTextColor(getResources().getColor(R.color.dark_gray));

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("mobileNUmber", "");
                        obj.put(getString(R.string.interaction_type), "Resend OTP Button Clicked");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.resend_otp_clicked));
                    getOTPCall(isResend, tvCalOTP, tvTimer, givTimer);
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCallTimer(TextView tvCalOTP, TextView tvTimer, GifImageView givTimer) {
        isResend = false;
        tvTimer.setVisibility(View.VISIBLE);
        tvCalOTP.setTextColor(ContextCompat.getColor(this, R.color.dark_gray));
        tvCalOTP.setEnabled(false);
        tvCalOTP.setText(getResources().getString(R.string.get_otp_via_call));
        new CountDownTimer(31000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("in " + millisUntilFinished / 1000 + " sec(s)");

                JSONObject obj = new JSONObject();
                try {
                    obj.put("mobileNUmber", "");
                    obj.put(getString(R.string.interaction_type), "GetOTPViaCallClicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj, getString(R.string.get_OTP_via_call_clicked));
                //here you can have your logic to set text to edittext
                if (millisUntilFinished < 1500) {
                    tvCalOTP.setEnabled(true);
                    tvCalOTP.setTypeface(tvCalOTP.getTypeface(), Typeface.BOLD);
                    tvCalOTP.setTextColor(getResources().getColor(R.color.black));
                    tvTimer.setVisibility(View.INVISIBLE);

                }
            }

            public void onFinish() {
                tvCalOTP.setEnabled(true);
                tvCalOTP.setTypeface(tvCalOTP.getTypeface(), Typeface.BOLD);
                tvCalOTP.setTextColor(getResources().getColor(R.color.Primary2));
                tvTimer.setVisibility(View.INVISIBLE);
            }

        }.start();
    }

    private void getOTPCall(boolean isResend, TextView tvCalOTP, TextView tvTimer, GifImageView givTimer) {

        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE);
        GenericAPIService genericAPIService = new GenericAPIService(activityContext, 0);
        GetOTPRequest getOTPRequest = new GetOTPRequest();
        getOTPRequest.setNumber(binding.llOTP.mobileNumber.getText().toString().trim());
        getOTPRequest.setDeviceToken(deviceToken);
        getOTPRequest.setDeviceUniqueId(deviceUniqueId);
        getOTPRequest.setPlatform("Android");
        getOTPRequest.setDeviceName(Utility.getDeviceName());
        getOTPRequest.setDeviceResolution(Utility.getScreenResolution(this));
        getOTPRequest.setMobileVersion(Build.VERSION.RELEASE);

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        ArrayList<String> hashKeys = appSignatureHelper.getAppSignatures();
        if (hashKeys != null && hashKeys.size() > 0) {
            getOTPRequest.setOtpHash(hashKeys.get(0));
        }

        genericAPIService.requestCallOTP(getOTPRequest, isResend);

        genericAPIService.setOnDataListener(responseBody -> {
            CNProgressDialog.hideProgressDialog();
            GenericResponse genericResponse = new Gson().fromJson(responseBody, GenericResponse.class);
            if (genericResponse.getStatus()) {
                displayLongToast(genericResponse.getMessage());
                //startSMSService();
                //openOtpSheet(LoginActivity.this);
                //showOtpDialog();
                startSMSService();

                /*if (isResend) {
                    onCallTimer(tvCalOTP, tvTimer,givTimer);
                }*/
            } else {
                CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_alert), genericResponse.getMessage());
            }
        });

        genericAPIService.setOnErrorListener(throwable -> {
            CNAlertDialog.showAlertDialog(activityContext, getResources().getString(R.string.title_alert), getString(R.string.error_failure));
            CNProgressDialog.hideProgressDialog();
        });

    }

    private void openOtpSheet(LoginActivity loginActivity) {
        try {
            fragment = new OTPSheetFragment(loginActivity);
            fragment.setCancelable(false);
            fragment.show(getSupportFragmentManager(), "TAG");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSMSService() {
        try {
            SmsRetrieverClient client = SmsRetriever.getClient(this);
            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Successfully started retriever, expect broadcast intent
                    Log.d(TAG, "Successfully started sms retriever.");
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Failed to start retriever, inspect Exception for more details
                    Log.e(TAG, "Failed to start sms retriever.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activityContext);

        return status == ConnectionResult.SUCCESS;
    }

    public void initLocationServices() {
        try {
            if (checkGooglePlayServices()) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                mSettingsClient = LocationServices.getSettingsClient(this);

                mRequestingLocationUpdates = false;

                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        // location is received
                        mCurrentLocation = locationResult.getLastLocation();

                        updateLocation();
                    }
                };

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(mLocationRequest);
                mLocationSettingsRequest = builder.build();
            } else {
                Utility.displayToast(activityContext, getResources().getString(R.string.google_play_services_not_available), Toast.LENGTH_LONG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putBoolean(Constants.IS_REQUESTING_UPDATES, mRequestingLocationUpdates);
            outState.putParcelable(Constants.LAST_KNOWN_LOCATION, mCurrentLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constants.IS_REQUESTING_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(Constants.IS_REQUESTING_UPDATES);
            }

            if (savedInstanceState.containsKey(Constants.LAST_KNOWN_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(Constants.LAST_KNOWN_LOCATION);
            }
        }

        updateLocation();
    }

    private void checkAndRequestLocationPermissions() {
        if (Utility.verifyAndRequestUserForPermissions(LoginActivity.this, Constants.PERMISSION_GET_CURRENT_LOCATION, Constants.PERMISSIONS_GET_CURRENT_LOCATION, Constants.REQUEST_CODE_GET_CURRENT_LOCATION)) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        try {
            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            Log.i(TAG, "All location settings are satisfied.");

                            //noinspection MissingPermission
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                            updateLocation();
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        if (!turnOnDisabledByUser) {
                                            turnOnDisabledByUser = true;
                                            ResolvableApiException rae = (ResolvableApiException) e;
                                            rae.startResolutionForResult(LoginActivity.this, Constants.REQUEST_CODE_GET_CURRENT_LOCATION);
                                        }
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.i(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                    Log.e(TAG, errorMessage);

                                    Toast.makeText(activityContext, errorMessage, Toast.LENGTH_LONG).show();
                            }

                            updateLocation();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Utils.displayToast(context, "Location updates stopped!");
                    }
                });
    }

    public void updateLocation() {
        if (mCurrentLocation != null) {
            currentLocation = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();

            if (mRequestingLocationUpdates) {
                // pausing location updates

                if (shouldExecuteFunction()) {
                    // Call your function here
                    updateNewLocation();

                    // Update the last execution date
                    updateLastExecutionDate();
                }

                stopLocationUpdates();
            }
        }
    }

    private boolean shouldExecuteFunction() {
        // Retrieve the last execution date from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String lastExecutionDate = preferences.getString(LAST_EXECUTION_DATE, "");

        // Get the current date
        String currentDate = getCurrentDate();

        // Check if the function should be executed based on the last execution date
        return !currentDate.equals(lastExecutionDate);
    }

    private void updateLastExecutionDate() {
        // Save the current date to SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_EXECUTION_DATE, getCurrentDate());
        editor.apply();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSRetrieverBroadCastReceiver.unbindListener();
        if (mSmsBroadcastReceiver != null) {
            unregisterReceiver(mSmsBroadcastReceiver);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

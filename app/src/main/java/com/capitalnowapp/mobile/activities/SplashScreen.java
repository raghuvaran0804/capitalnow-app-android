package com.capitalnowapp.mobile.activities;

import static com.capitalnowapp.mobile.CapitalNowApp.mp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.broadcastreceivers.SMSRetrieverBroadCastReceiver;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.customviews.CNAlertDialog;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.customviews.CNTextView;
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener;
import com.capitalnowapp.mobile.kotlin.activities.BannerActivity;
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity;
import com.capitalnowapp.mobile.kotlin.activities.MandatoryPermissionsActivity;
import com.capitalnowapp.mobile.models.CNModel;
import com.capitalnowapp.mobile.models.GenericRequest;
import com.capitalnowapp.mobile.models.UpdateNewLocationReq;
import com.capitalnowapp.mobile.models.UpdateNewLocationResponse;
import com.capitalnowapp.mobile.models.userdetails.UserDetails;
import com.capitalnowapp.mobile.models.userdetails.UserDetailsResponse;
import com.capitalnowapp.mobile.retrofit.GenericAPIService;
import com.capitalnowapp.mobile.util.CNEncryptDecrypt;
import com.capitalnowapp.mobile.util.CNSharedPreferences;
import com.capitalnowapp.mobile.util.NetworkConnectionDetector;
import com.capitalnowapp.mobile.util.Utility;
import com.chaos.view.BuildConfig;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

//import io.branch.referral.Branch;
//import io.branch.referral.BranchError;

public class SplashScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public static final String TAG = SplashScreen.class.getSimpleName();

    private static final String PREF_NAME = "MyPreferences";
    private static final String LAST_EXECUTION_DATE = "last_execution_date";

    private Context activityContext;
    private Runnable mRunnable;
    private Handler mHandler = new Handler();
    private CNSharedPreferences sharedPreferences;
    public CNModel cnModel;
    public String userId;
    protected Constants.RequestFrom requestFrom;
    protected CNEncryptDecrypt CNEncryptDecrypt;
    private UserDetails userDetails;
    public boolean isDeeplinking = false;
    public boolean isPushNoti = false;

    private String destination = "";
    private Integer pnRedirectCode = -1;

    private SMSRetrieverBroadCastReceiver mSmsBroadcastReceiver;

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
    protected void onStart() {
        super.onStart();
        //Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        /*if (intent != null &&
                intent.hasExtra("branch_force_new_session") &&
                intent.getBooleanExtra("branch_force_new_session",false)) {
            Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
        }*/
    }

    /*private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash_screen);
            Constants.CURRENT_SCREEN = "4";
            activityContext = SplashScreen.this;
            CNEncryptDecrypt = new CNEncryptDecrypt();
            sharedPreferences = new CNSharedPreferences(this);
            sharedPreferences.putString(Constants.PROFILE_BANNERS, "");
            sharedPreferences.putBoolean(Constants.MADE_IN_INDIA_SHOWN, true);
            sharedPreferences.putBoolean(Constants.DOCS_HELP_SHOWN, false);
            sharedPreferences.putBoolean(Constants.From_Vehicle_Details, false);
            sharedPreferences.putBoolean("fromProcessingFee", false);
            cnModel = new CNModel(activityContext, this, requestFrom);


            // We are generating FCM device token if it is not already saved.
            String deviceToken = sharedPreferences.getString(Constants.SP_DEVICE_TOKEN);
            if (deviceToken.isEmpty()) {
                generateFCMDeviceToken();
            }else {
                Log.d("- fcmToken: ", deviceToken);
            }

            if(getIntent() != null) {
                if(getIntent().hasExtra("fromDeeplink")) {
                    isDeeplinking = getIntent().getBooleanExtra("fromDeeplink",false);
                    destination = getIntent().getStringExtra("destination");
                }
                else if(getIntent().hasExtra("redirectCode")){

                    isPushNoti = getIntent().getBooleanExtra("isPushNoti",false);
                    pnRedirectCode = getIntent().getIntExtra("redirectCode",-1);

                }

            }

                initLocationServices();
                restoreValuesFromBundle(savedInstanceState);
                checkAndRequestLocationPermissions();



            mRunnable = () -> {
                mHandler.removeCallbacks(mRunnable);
                sharedPreferences.putBoolean(Constants.PERMISSIONS_REQUESTED, false);
                getAppVersionAndSuggestAppUpdate();
            };
            if (new NetworkConnectionDetector(activityContext).isNetworkConnected()) {
                mHandler.postDelayed(mRunnable, 2000);
            }
            ImageView ivSplash = findViewById(R.id.ivSplash);

            Glide.with(this)
                    .load(R.drawable.splash_new)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource instanceof GifDrawable) {
                                ((GifDrawable) resource).setLoopCount(1);
                            }
                            return false;
                        }
                    })
                    .into(ivSplash);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private void showBannerDialog() {
        startActivityForResult(new Intent(this, BannerActivity.class), 10001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001 && resultCode == RESULT_OK) {
            proceed();
        }
    }

    private void generateFCMDeviceToken() {
        // Get FCM Device Token
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if(!task.isSuccessful()){
                        Utility.logResult(TAG, "********* Get InstanceId failed : " + task.getException());
                        return;
                    }
                    String fcmToken = task.getResult();
                    Utility.logResult(TAG, "********** generateFCMDeviceToken - fcmToken: " + fcmToken);
                    Log.d("- fcmToken: ", fcmToken);
                    sharedPreferences.putString(Constants.SP_DEVICE_TOKEN, fcmToken);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void logout() {
        userDetails = null;
        sharedPreferences.putString(Constants.USER_DETAILS_DATA, new Gson().toJson(userDetails));
        sharedPreferences.putString(Constants.USER_REGISTRATION_DATA, null);
        Intent logInIntent = new Intent(getApplicationContext(), LoginActivity.class);
        /*logInIntent.flags =
                FLAG_ACTIVITY_NO_ANIMATION || Intent.FLAG_ACTIVITY_NEW_TASK || Intent.FLAG_ACTIVITY_CLEAR_TASK*/
        startActivity(logInIntent);
        //overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }

    public void getAppVersionAndSuggestAppUpdate() {
        cnModel.getAppVersion();
    }

    public void updateAppVersionCodeAndName(String encryptedVersionCode, String encryptedVersionName, Integer update_level, JSONObject response) {
        try {
            String decryptedVersionName = CNEncryptDecrypt.decryptString(encryptedVersionName);
            if (response.has("tanc_text")) {
                String userTermsData = String.valueOf(response.getJSONObject("tanc_text"));
                sharedPreferences.putString("terms_data", userTermsData);
            }
            int mainatinace = 0;
            if (response.has("mainatinace")) {
                mainatinace = response.getInt("mainatinace");
            }
            final String appPackageName = activityContext.getPackageName(); // getPackageName() from Context or Activity object
            if (update_level > 0) {
                boolean is_skip;
                if (update_level == 2) {
                    is_skip = false;
                } else is_skip = update_level == 1;

                CNAlertDialog.setRequestCode(1);
                CNAlertDialog.showAlertDialogWithCallback(activityContext, String.format("New version (%s) available", decryptedVersionName), getResources().getString(R.string.app_update_message), is_skip, getString(R.string.update_app_alert_ok_text), getString(R.string.update_app_alert_skip_text));
                //CNAlertDialog.showAlertDialogWithCallback(activityContext, String.format("New version (%s) available"), getResources().getString(R.string.app_update_message), is_skip, getString(R.string.update_app_alert_ok_text), getString(R.string.update_app_alert_skip_text));
                CNAlertDialog.setListener(new AlertDialogSelectionListener() {
                    @Override
                    public void alertDialogCallback() {

                    }

                    @Override
                    public void alertDialogCallback(Constants.ButtonType buttonType, int requestCode) {
                        if (buttonType == Constants.ButtonType.POSITIVE) {
                            Utility utility = Utility.getInstance();
                            utility.openAppInPlayStore(activityContext, appPackageName,response);
                            SplashScreen.this.finish();
                        } else {
                            proceed();
                        }

                    }
                });
            } else if (mainatinace > 0) {
                CNAlertDialog.setRequestCode(1);
                CNAlertDialog.showAlertDialogWithCallback(activityContext, "", response.getString("message"), false, "Ok", getString(R.string.update_app_alert_skip_text));
                CNAlertDialog.setListener(new AlertDialogSelectionListener() {
                    @Override
                    public void alertDialogCallback() {
                    }

                    @Override
                    public void alertDialogCallback(Constants.ButtonType buttonType, int requestCode) {
                        finishAffinity();
                    }
                });
            } else {
                proceed();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showErrorDialog() {
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.error_alert);

            CNTextView btnok = (CNTextView) dialog.findViewById(R.id.btnOk);
            CNTextView tvEmailText = (CNTextView) dialog.findViewById(R.id.tvEmailText);
            btnok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });
            tvEmailText.setMovementMethod(LinkMovementMethod.getInstance());


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceed() {
        Intent intent;
        try {
            userDetails = new Gson().fromJson(sharedPreferences.getString(Constants.USER_DETAILS_DATA), UserDetails.class);
            if (userDetails != null && userDetails.getUserId() != null) {

                if (sharedPreferences.getString(Constants.LOGGED_TIME) != null && !sharedPreferences.getString(Constants.LOGGED_TIME).equals("")) {
                    String loggedDate = sharedPreferences.getString(Constants.LOGGED_TIME);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                    String currentDate = sdf.format(new Date());
                    Date d1 = sdf.parse(loggedDate);
                    Date d2 = sdf.parse(currentDate);
                    long diff = d2.getTime() - d1.getTime();
                    int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
                    Log.d("loginDays", String.valueOf(numOfDays));
                    if (numOfDays >= 90) {
                        userDetails = null;
                        sharedPreferences.putString(Constants.USER_DETAILS_DATA, new Gson().toJson(userDetails));
                        sharedPreferences.putString(Constants.USER_REGISTRATION_DATA, null);
                        Intent logInIntent = new Intent(activityContext, LoginActivity.class);
                        logInIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        logInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        logInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(logInIntent);
                        overridePendingTransition(R.anim.left_in, R.anim.right_out);
                        finish();
                    } else {
                        getProfile(userDetails.getUserId());
                    }
                } else {
                    getProfile(userDetails.getUserId());
                }
            } else {
                intent = new Intent(activityContext, LoginActivity.class);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getProfile(String userId) {
        //   CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE);
        GenericAPIService genericAPIService = new GenericAPIService(activityContext,0);
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setUserId(userId);
        genericRequest.setDeviceUniqueId(Utility.getInstance().getDeviceUniqueId(this));
        genericAPIService.getUserData(genericRequest);

        genericAPIService.setOnDataListener(responseBody -> {
            CNProgressDialog.hideProgressDialog();
            UserDetailsResponse userDetailsResponse = new Gson().fromJson(responseBody, UserDetailsResponse.class);
            if(userDetailsResponse != null && userDetailsResponse.getStatusCode() == Constants.STATUS_CODE_UNAUTHORISED ) {
                logout();
            }else {
                if (userDetailsResponse != null && userDetailsResponse.getUserDetails() != null && userDetailsResponse.getUserDetails().getQcId() != null
                        && !userDetailsResponse.getUserDetails().getQcId().equals("")) {
                    sharedPreferences.putString(Constants.RAZOR_PAY_API_KEY, userDetailsResponse.getRazorPayApiKey());
                    sharedPreferences.putString(Constants.USER_DETAILS_DATA, new Gson().toJson(userDetailsResponse.getUserDetails()));
                    userDetails = userDetailsResponse.getUserDetails();
                    loadLaunchIntent(userDetails);
                } else {
                    Toast.makeText(SplashScreen.this, getString(R.string.error_failure), Toast.LENGTH_SHORT).show();
                    loadLaunchIntent(null);
                }
            }
        });

        genericAPIService.setOnErrorListener(throwable -> {
            CNProgressDialog.hideProgressDialog();
            Toast.makeText(SplashScreen.this, getString(R.string.error_failure), Toast.LENGTH_SHORT).show();
            loadLaunchIntent(null);
        });
    }

    private void loadLaunchIntent(UserDetails userDetails) {
        Intent intent = null;

        if (userDetails != null) {
            if (CSPermissionsGranted()) {
                if (userDetails.getFirstName() != null) {
                    mp.identify(userDetails.getQcId());
                    mp.getPeople().set("$first_name", userDetails.getUserId());
                }
                if (Objects.equals(userDetails.getUserStatusId(), "1")) {
                    if (userDetails.getEmail() != null && !userDetails.getEmail().equals("")) {
                            intent = new Intent(activityContext, DashboardActivity.class);
                    } else {
                        intent = new Intent(activityContext, DashboardActivity.class);
                    }
                } else if (Objects.equals(userDetails.getUserStatusId(), "23") && userDetails.getUserStatus().equals("I")) {
                    //sharedPreferences.putString(Constants.USER_DETAILS_DATA, null);
                    intent = new Intent(activityContext, DashboardActivity.class);
                    if (isDeeplinking) {
                        intent.putExtra("destination", destination);
                    }
                    if (isPushNoti) {
                        intent.putExtra("redirectCode", pnRedirectCode);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    }
                    //getApplyLoanDataBase();
                } else {
                    intent = new Intent(activityContext, DashboardActivity.class);
                    if (isDeeplinking) {
                        intent.putExtra("destination", destination);
                    }
                    if (isPushNoti) {
                        intent.putExtra("redirectCode", pnRedirectCode);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    }
                    //getApplyLoanDataBase();
                }
            }
            else {
                Intent i = new Intent(this, MandatoryPermissionsActivity.class);
                startActivity(i);
            }
        } else {
            intent = new Intent(activityContext, DashboardActivity.class);
            if(isDeeplinking){
                intent.putExtra("destination",destination);
            }
            if(isPushNoti){
                intent.putExtra("redirectCode",pnRedirectCode);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            }
            //getApplyLoanDataBase();
        }
        if(intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
            finish();
        }
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

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activityContext);

        return status == ConnectionResult.SUCCESS;
    }

    private void checkAndRequestLocationPermissions() {
        if (Utility.verifyAndRequestUserForPermissions(SplashScreen.this, Constants.PERMISSION_GET_CURRENT_LOCATION, Constants.PERMISSIONS_GET_CURRENT_LOCATION, Constants.REQUEST_CODE_GET_CURRENT_LOCATION)) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

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
                                            rae.startResolutionForResult(SplashScreen.this, Constants.REQUEST_CODE_GET_CURRENT_LOCATION);
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

    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
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

                    Toast.makeText(SplashScreen.this, "Go to settings and enable Location permission.", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    public void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private boolean CSPermissionsGranted() {

        int locationPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return locationPermission == PackageManager.PERMISSION_GRANTED;

    }
    public String getUserToken() {
        if (sharedPreferences == null) {
            sharedPreferences = new CNSharedPreferences(this);
        }
        return sharedPreferences.getString(Constants.USER_TOKEN);
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

package com.capitalnowapp.mobile;

import static com.facebook.FacebookSdk.setAdvertiserIDCollectionEnabled;

import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Map;

//import io.branch.referral.Branch;

public class CapitalNowApp extends Application {

    private static CapitalNowApp sInstance;
    private static final String AF_DEV_KEY = "2G7FYK6sA2tKZGmNoHedWk";
    public static MixpanelAPI mp;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(getApplicationContext()));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // Default channel is Organic
        FirebaseApp.initializeApp(this);
        //AdGyde.init(this, "J724852572964218", "Organic");
        //AdGyde.setDebugEnabled(true);
        initializeAppsFlyer();
        AppsFlyerLib.getInstance().start(this);

        //FacebookSdk.setAutoLogAppEventsEnabled(true);
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        setAdvertiserIDCollectionEnabled(true);

        // Branch logging for debugging
        /*if(BuildConfig.DEBUG) {
            Branch.enableTestMode();
        }*/
        // Branch object initialization
        //Branch.getAutoInstance(this);

        //Mixpanel Live Initilization
        //mp = MixpanelAPI.getInstance(this, getString(R.string.mixpanel_live_id), true);

        //Mixpanel development Initilization dev
        //mp = MixpanelAPI.getInstance(this, getString(R.string.mixpanel_dev_id), true);
        //Mixpanel development Initilization live
        mp = MixpanelAPI.getInstance(this, getString(R.string.mixpanel_live_id), true);

    }

    /*public void handleUncaughtException (Thread thread, Throwable e)
    {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

        Intent intent = new Intent();
        intent.putExtra("from","Exception");
        intent = new Intent(this, DashboardActivity.class);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity (intent);

        System.exit(1); // kill off the crashed app
    }*/

    private void initializeAppsFlyer() {

        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {

                for (String attrName : conversionData.keySet()) {
                    Log.d("LOG_TAG", "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d("LOG_TAG", "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                for (String attrName : attributionData.keySet()) {
                    Log.d("LOG_TAG", "attribute: " + attrName + " = " + attributionData.get(attrName));
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d("LOG_TAG", "error onAttributionFailure : " + errorMessage);
            }
        };

        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, this);
    }



    public static CapitalNowApp getInstance() {
        return sInstance;
    }

}

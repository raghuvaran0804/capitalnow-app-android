package com.capitalnowapp.mobile.util;

import android.os.Bundle;

import com.capitalnowapp.mobile.CapitalNowApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

public class TrackingUtil {

    private static TrackingUtil trackingUtil;

    public static TrackingUtil getInstance() {
        if (trackingUtil == null) {
            trackingUtil = new TrackingUtil();
        }
        return trackingUtil;
    }

    public void logEvent(String eventName) {
        FirebaseAnalytics.getInstance(CapitalNowApp.getInstance()).logEvent(eventName, null);
    }

    public void logEvent(String eventName, Bundle bundle) {
        FirebaseAnalytics.getInstance(CapitalNowApp.getInstance()).logEvent(eventName, bundle);
    }
    // common method to send events to mixpanel
    public static void pushEvent(JSONObject obj, String eventName){
        try{
            CapitalNowApp.mp.track(eventName, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static class Event {
        public static final String LOGIN_CHECK = "login_user";
        public static final String VALIDATE_SUBMIT_FTR = "validate_first_time_reg";
        public static final String REFER_EARN = "refer_earn_button";
        public static final String APPLY_LOAN = "apply_loan_btn";
    }
}

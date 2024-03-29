package com.capitalnowapp.mobile.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.util.CNSharedPreferences;

public class InstallReferrerReceiver extends BroadcastReceiver {
    private CNSharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.e("InstallReferrerReceiver", "started");
            sharedPreferences = new CNSharedPreferences(context);
            String referrer = intent.getStringExtra("referrer");
            Log.e("InstallReferrerReceiver", referrer);
            //System.out.println("******* referrer = " + referrer);
            if (!referrer.contains("utm_source=google-play")) {
                sharedPreferences.putString(Constants.SP_REFER_CODE, referrer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

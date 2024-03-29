package com.capitalnowapp.mobile.util;

import android.app.Activity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class MyCustomExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Activity activity;

    public MyCustomExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    public void uncaughtException(final Thread t, final Throwable e) {
        FirebaseCrashlytics.getInstance().recordException(e);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            this.activity.finish();
            System.exit(2);
        }
    }
}
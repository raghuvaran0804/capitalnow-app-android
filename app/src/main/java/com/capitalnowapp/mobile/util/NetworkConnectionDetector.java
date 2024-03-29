package com.capitalnowapp.mobile.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.activities.SplashScreen;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.customviews.CNAlertDialog;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener;

public class NetworkConnectionDetector {

    private Context _context;

    /**
     * Constructor
     *
     * @param activity Activity context
     */
    public NetworkConnectionDetector(Context activity) {
        _context = activity;
    }

    /**
     * Checks the Internet Connectivity status
     *
     * @return Boolean
     */
    public boolean isNetworkConnected() {
        boolean status = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

            if (activeNetwork != null) {
                status = true;

                /*if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to WiFi
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the Mobile provider's data plan
                }*/
            } else {
                // not connected to the internet
                status = false;
            }
        }

        if (!status) {
            displayNoNetworkError(_context);
        }

        return status;
    }

    /**
     *
     */
    public static void displayNoNetworkError(Context context) {
        if (CNProgressDialog.isProgressDialogShown)
            CNProgressDialog.hideProgressDialog();

        CNAlertDialog.setRequestCode(1);
        CNAlertDialog.showAlertDialogWithCallback(context, context.getResources().getString(R.string.error_no_internet_title), context.getResources().getString(R.string.error_no_internet), false, "", "");
        CNAlertDialog.setListener(new AlertDialogSelectionListener() {
            @Override
            public void alertDialogCallback() {

            }

            @Override
            public void alertDialogCallback(Constants.ButtonType buttonType, int requestCode) {
                if (buttonType == Constants.ButtonType.POSITIVE) {
                    Intent intent = new Intent(context, SplashScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        });

        Toast.makeText(context, context.getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
    }
}
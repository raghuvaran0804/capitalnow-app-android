package com.capitalnowapp.mobile.customviews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.capitalnowapp.mobile.R;

public class WhyDownPaymentDialog {
    private static Dialog progressDialog;
    public static boolean isProgressDialogShown = false;
    public static void showProgressDialog(Context ctx, String msg) {
        try {
            progressDialog = new Dialog(ctx);
            // no tile for the dialog
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.why_downpayment_dialog);
            progressDialog.setTitle(msg);
            // you can change or add this line according to your need
            progressDialog.setCancelable(false);
            RotateAnimation rotate = new RotateAnimation(
                    0, 360,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 1f
            );
            rotate.setDuration(3000);
            rotate.setRepeatCount(Animation.INFINITE);
            //ivBg.startAnimation(rotate);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.show();
            isProgressDialogShown = true;

        } catch (Exception e) {
            isProgressDialogShown = false;
            e.printStackTrace();
        }
    }

    /**
     * Method to hide the dialog.
     */
    public static void hideProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                isProgressDialogShown = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

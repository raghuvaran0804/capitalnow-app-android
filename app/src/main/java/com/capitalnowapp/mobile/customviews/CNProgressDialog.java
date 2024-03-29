package com.capitalnowapp.mobile.customviews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.capitalnowapp.mobile.R;

public class CNProgressDialog {
    private static Dialog progressDialog;
    public static boolean isProgressDialogShown = false;

    /**
     * @param ctx Context
     * @param msg message
     */
    public static void showProgressDialog(Context ctx, String msg) {
        try {
            progressDialog = new Dialog(ctx);
            // no tile for the dialog
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.custom_progress_dialog);
            progressDialog.setTitle(msg);
            // you can change or add this line according to your need
            progressDialog.setCancelable(false);
            RotateAnimation rotate = new RotateAnimation(
                    0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
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

    public static void showProgressDialogText(Context ctx, String msg) {
        try {
            progressDialog = new Dialog(ctx);
            // no tile for the dialog
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.custom_progress_dialog_text);
            progressDialog.setTitle(msg);
            // you can change or add this line according to your need
            progressDialog.setCancelable(false);
            ImageView ivBg = progressDialog.findViewById(R.id.ivBg);
            TextView tvMsg = progressDialog.findViewById(R.id.tvMsg);
            RotateAnimation rotate = new RotateAnimation(
                    0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            rotate.setDuration(3000);
            rotate.setRepeatCount(Animation.INFINITE);
            ivBg.startAnimation(rotate);
            tvMsg.setText(msg);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.show();
            isProgressDialogShown = true;

        } catch (Exception e) {
            isProgressDialogShown = false;
            e.printStackTrace();
        }
    }

    public static void showUploadProgressDialog(Context ctx, String msg) {
        try {
            progressDialog = new Dialog(ctx);
            // no tile for the dialog
            /*progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.custom_progress_dialog);
            progressDialog.setTitle(msg);
            // you can change or add this line according to your need
            */progressDialog.setCancelable(false);
            /*ImageView ivBg = progressDialog.findViewById(R.id.ivBg);
            ivBg.setVisibility(View.GONE);*/
            progressDialog.setCanceledOnTouchOutside(false);

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

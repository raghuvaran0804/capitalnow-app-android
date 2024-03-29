package com.capitalnowapp.mobile.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener;

public class CNAlertDialog {
    public static AlertDialog alertDialog = null;
    public static boolean isAlertDialogShown = false;
    public static AlertDialogSelectionListener listener = null;
    public static int requestCode = 0;

    public static void setListener(AlertDialogSelectionListener listener) {
        CNAlertDialog.listener = listener;
    }

    public static void setRequestCode(int requestCode) {
        CNAlertDialog.requestCode = requestCode;
    }

    /**
     * @param title
     * @param message
     */
    public static void showAlertDialog(Context context, String title, String message) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(false);

           /* alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });*/

            alertDialog = alertDialogBuilder.create();
            //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            TextView tvOk = dialogView.findViewById(R.id.tvOk);

            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            Window window = alertDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;

            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 100);
            window.setBackgroundDrawable(inset);
            window.setAttributes(wlp);

            if (title != null && !title.equals("")) {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
            if (message != null && !message.equals("")) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            } else {
                tvMessage.setVisibility(View.GONE);
            }

            isAlertDialogShown = true;

        } catch (Exception e) {
            isAlertDialogShown = false;
            e.printStackTrace();
        }
    }

    public static void showMaterialAlertDialog(Context context, String okBtntxt, String message, int drawableId, boolean isCancel, int color) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_material_alert, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(isCancel);

           /* alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });*/

            alertDialog = alertDialogBuilder.create();
            //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            TextView tvOk = dialogView.findViewById(R.id.tvOk);
            FrameLayout frameBg = dialogView.findViewById(R.id.frameBg);

            frameBg.setBackgroundTintList(context.getResources().getColorStateList(color));

            tvOk.setOnClickListener(v -> {
                okCallback(Constants.ButtonType.POSITIVE);
                alertDialog.dismiss();
            });

            Window window = alertDialog.getWindow();

            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 45);
            window.setBackgroundDrawable(inset);

            if (message != null && !message.equals("")) {
                tvMessage.setText(message);
            } else {
                tvMessage.setVisibility(View.GONE);
            }
            if (okBtntxt != null && !okBtntxt.equals("")) {
                tvOk.setText(okBtntxt);
            }

            ImageView ivImage = alertDialog.findViewById(R.id.ivImage);
            ivImage.setImageResource(drawableId);

            isAlertDialogShown = true;

        } catch (Exception e) {
            isAlertDialogShown = false;
            e.printStackTrace();
        }
    }

    public static void showAlertDialogWithCallback(Context context, String title, String message, boolean showCancelButton, String okTxt, String cancelTxt) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_with_cancel, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(false);

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            TextView tvOk = dialogView.findViewById(R.id.tvOk);
            TextView tvOk1 = dialogView.findViewById(R.id.tvOk1);
            TextView tvCancel = dialogView.findViewById(R.id.tvCancel);
            LinearLayout llWithCancel = dialogView.findViewById(R.id.llWithCancel);

            if (okTxt != null && !okTxt.equals("")) {
                tvOk.setText(okTxt);
                tvOk1.setText(okTxt);
            }
            if (cancelTxt != null && !cancelTxt.equals("")) {
                tvCancel.setText(cancelTxt);
            }

            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.POSITIVE);
                    alertDialog.dismiss();
                }
            });

            tvOk1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.POSITIVE);
                    alertDialog.dismiss();
                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.NEGATIVE);
                    alertDialog.dismiss();
                }
            });

            Window window = alertDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;

            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 100);
            window.setBackgroundDrawable(inset);
            window.setAttributes(wlp);

            if (title != null && !title.equals("")) {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
            if (message != null && !message.equals("")) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            } else {
                tvMessage.setVisibility(View.GONE);
            }

            if (showCancelButton) {
                llWithCancel.setVisibility(View.VISIBLE);
                tvOk1.setVisibility(View.GONE);
            } else {
                llWithCancel.setVisibility(View.GONE);
                tvOk1.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            isAlertDialogShown = false;
            e.printStackTrace();
        }
    }

    public static void showStatusWithCallback(Context context,String message, String okTxt, int drawable, int okColor) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.status_alert, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(false);

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            TextView tvOk = dialogView.findViewById(R.id.tvOk);
            ImageView ivStatus = dialogView.findViewById(R.id.ivStatus);
            ivStatus.setImageResource(drawable);

            if (okTxt != null && !okTxt.equals("")) {
                tvOk.setText(okTxt);
            }
            tvOk.setBackgroundTintList(ContextCompat.getColorStateList(context, okColor));

            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.POSITIVE);
                    alertDialog.dismiss();
                }
            });

            Window window = alertDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;

            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 100);
            window.setBackgroundDrawable(inset);
            window.setAttributes(wlp);

            if (message != null && !message.equals("")) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            } else {
                tvMessage.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            isAlertDialogShown = false;
            e.printStackTrace();
        }
    }

    public static void showAlertDialogWithCallback_1(Context context, String title, String message, boolean showCancelButton, String okTxt, String cancelTxt) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_with_cancel_1, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(false);

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            TextView tvOption = dialogView.findViewById(R.id.tvOption);
            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            TextView tvOk = dialogView.findViewById(R.id.tvOk);
            TextView tvOk1 = dialogView.findViewById(R.id.tvOk1);
            TextView tvCancel = dialogView.findViewById(R.id.tvCancel);
            LinearLayout llWithCancel = dialogView.findViewById(R.id.llWithCancel);

            if (okTxt != null && !okTxt.equals("")) {
                tvOk.setText(okTxt);
                tvOk1.setText(okTxt);
            }
            if (cancelTxt != null && !cancelTxt.equals("")) {
                tvOption.setText(cancelTxt);
            }

            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.POSITIVE);
                    alertDialog.dismiss();
                }
            });

            tvOk1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.POSITIVE);
                    alertDialog.dismiss();
                }
            });

            tvOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestCode = 20;
                    okCallback(Constants.ButtonType.POSITIVE);
                    alertDialog.dismiss();
                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.NEGATIVE);
                    alertDialog.dismiss();
                }
            });

            Window window = alertDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;

            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 100);
            window.setBackgroundDrawable(inset);
            window.setAttributes(wlp);

            if (title != null && !title.equals("")) {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
            if (message != null && !message.equals("")) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            } else {
                tvMessage.setVisibility(View.GONE);
            }

            if (showCancelButton) {
                llWithCancel.setVisibility(View.VISIBLE);
                tvOk1.setVisibility(View.GONE);
            } else {
                llWithCancel.setVisibility(View.GONE);
                tvOk1.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            isAlertDialogShown = false;
            e.printStackTrace();
        }
    }

    public static void showSettingsAlertDialog(Context context, String title, String message, boolean showCancelButton, String okTxt, String cancelTxt) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_settings_alert, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(false);

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            TextView tvOk = dialogView.findViewById(R.id.tvOk);
            TextView tvOk1 = dialogView.findViewById(R.id.tvOk1);
            TextView tvCancel = dialogView.findViewById(R.id.tvCancel);
            LinearLayout llWithCancel = dialogView.findViewById(R.id.llWithCancel);

            if (okTxt != null && !okTxt.equals("")) {
                tvOk.setText(okTxt);
                tvOk1.setText(okTxt);
            }
            if (cancelTxt != null && !cancelTxt.equals("")) {
                tvCancel.setText(cancelTxt);
            }

            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.POSITIVE);
                    alertDialog.dismiss();
                }
            });

            tvOk1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.POSITIVE);
                    alertDialog.dismiss();
                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    okCallback(Constants.ButtonType.NEGATIVE);
                    alertDialog.dismiss();
                }
            });

            Window window = alertDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;

            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 100);
            window.setBackgroundDrawable(inset);
            window.setAttributes(wlp);

            if (title != null && !title.equals("")) {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
            if (message != null && !message.equals("")) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            } else {
                tvMessage.setVisibility(View.GONE);
            }

            if (showCancelButton) {
                llWithCancel.setVisibility(View.VISIBLE);
                tvOk1.setVisibility(View.GONE);
            } else {
                llWithCancel.setVisibility(View.GONE);
                tvOk1.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            isAlertDialogShown = false;
            e.printStackTrace();
        }
    }

    public static void showRateDialog(Context context) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.rate_app_dialog, null);
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setCancelable(false);

            AlertDialog alertDialog = alertDialogBuilder.create();

            TextView tvRateNow = dialogView.findViewById(R.id.tvRateNow);
            TextView tvNoThanks = dialogView.findViewById(R.id.tvNoThanks);
            TextView tvLater = dialogView.findViewById(R.id.tvLater);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            tvRateNow.setOnClickListener(v -> {
                requestCode = 1;
                okCallback(Constants.ButtonType.POSITIVE);
                alertDialog.dismiss();
            });
            tvNoThanks.setOnClickListener(v -> {
                requestCode = 3;
                okCallback(Constants.ButtonType.NEGATIVE);
                alertDialog.dismiss();
            });

            tvLater.setOnClickListener(v -> {
                requestCode = 2;
                okCallback(Constants.ButtonType.POSITIVE);
                alertDialog.dismiss();
            });

            Window window = alertDialog.getWindow();

            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 20);
            window.setBackgroundDrawable(inset);

            isAlertDialogShown = true;

            alertDialog.show();

        } catch (Exception e) {
            isAlertDialogShown = false;
            e.printStackTrace();
        }
    }

    public static void okCallback(Constants.ButtonType buttonType) {
        if (listener != null) {
            if (requestCode > 0)
                listener.alertDialogCallback(buttonType, requestCode);
            else
                listener.alertDialogCallback();
        }
    }

    public static void dismiss() {
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
    }
}

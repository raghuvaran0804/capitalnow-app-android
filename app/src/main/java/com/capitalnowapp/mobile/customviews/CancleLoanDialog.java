package com.capitalnowapp.mobile.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener;

public class CancleLoanDialog {

    public static AlertDialog alertDialog = null;
    public static RecyclerView recyclerView;

    public static void setListener(AlertDialogSelectionListener listener) {
        CNAlertDialog.listener = listener;
    }
    public static void setRequestCode(int requestCode) {
        CNAlertDialog.requestCode = requestCode;
    }
    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.cancle_loan_dialog, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        Spinner spinnerReason = dialogView.findViewById(R.id.spinnerReason);
        TextView tvCancel = dialogView.findViewById(R.id.tvCancel);
        TextView tvConfirm = dialogView.findViewById(R.id.tvConfirm);
       // recyclerView recycler_view = dialogView.findViewById(R.id.recycler_view);
    }
}

package com.capitalnowapp.mobile.customviews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.capitalnowapp.mobile.R;

public class CNDialogWithCustomView extends Dialog {
    private Context context;
    private String title;
    private String okButtonTitle;
    private View contentView;

    private CNTextView custom_dialog_title, custom_dialog_ok_text_view;

    public CNDialogWithCustomView(Context context, String title, String okButtonTitle, View contentView) {
        super(context, R.style.DialogWithListViewStyle);

        this.context = context;
        this.title = title;
        this.okButtonTitle = okButtonTitle;
        this.contentView = contentView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView);

        try {
            setCancelable(false);

          //  custom_dialog_title = (CNTextView) contentView.findViewById(R.id.custom_dialog_title);
           // custom_dialog_ok_text_view = (CNTextView) contentView.findViewById(R.id.custom_dialog_ok_text_view);

            custom_dialog_title.setText(title);

            if (!okButtonTitle.isEmpty())
                custom_dialog_ok_text_view.setText(okButtonTitle);

            custom_dialog_ok_text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

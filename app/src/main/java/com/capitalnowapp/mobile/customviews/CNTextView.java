package com.capitalnowapp.mobile.customviews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CNTextView extends AppCompatTextView {

    public CNTextView(Context context) {
        super(context);

        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public CNTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontUtils.applyCustomFont(this, context, attrs);
    }

    public CNTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CustomFontUtils.applyCustomFont(this, context, attrs);
    }
}

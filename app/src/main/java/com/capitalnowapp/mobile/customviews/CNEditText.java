package com.capitalnowapp.mobile.customviews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class CNEditText extends AppCompatEditText {
    public CNEditText(Context context) {
        super(context);

        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public CNEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }

    public CNEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }
}

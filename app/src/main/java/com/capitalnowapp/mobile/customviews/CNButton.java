package com.capitalnowapp.mobile.customviews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class CNButton extends AppCompatButton {

    public CNButton(Context context) {
        super(context);

        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public CNButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }

    public CNButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }
}

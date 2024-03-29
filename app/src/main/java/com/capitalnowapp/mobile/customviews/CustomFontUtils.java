package com.capitalnowapp.mobile.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.util.FontCache;

public class CustomFontUtils {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public static void applyCustomFont(TextView customFontTextView, Context context, AttributeSet attrs) {
        try {
            TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);

            String fontName = attributesArray.getString(R.styleable.CustomFontTextView_fontName);

            // check if a special textStyle was used (e.g. extra bold)
            int textStyle = attributesArray.getInt(R.styleable.CustomFontTextView_textStyle, 0);

            // if nothing extra was used, fall back to regular android:textStyle parameter
            if (textStyle == 0 && attrs != null) {
                textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
            }

            Typeface customFont = selectTypeface(context, textStyle);
            customFontTextView.setTypeface(customFont);

            attributesArray.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Typeface selectTypeface(Context context, int textStyle) {
        switch (textStyle) {
            case Typeface.BOLD:
                return FontCache.getTypeface(context, "fonts/Metropolis-Bold.otf");
            case Typeface.NORMAL:
                return FontCache.getTypeface(context, "fonts/Metropolis-Regular.otf");
            default:
                return FontCache.getTypeface(context, "fonts/Metropolis-Light.otf");
        }
    }
}

package com.capitalnowapp.mobile.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class FontCache {
    private static Map<String, Typeface> fontsMap = new HashMap<String, Typeface>();

    public static Typeface getTypeface(Context context, String fontName) {
        try {
            if (fontsMap.containsKey(fontName)) {
                return fontsMap.get(fontName);
            } else {
                Typeface tf = Typeface.createFromAsset(context.getAssets(), fontName);
                fontsMap.put(fontName, tf);
                return tf;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

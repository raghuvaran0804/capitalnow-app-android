package com.capitalnowapp.mobile.util;

import android.content.Context;

import com.capitalnowapp.mobile.constants.Constants;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class CNSharedPreferences {
    private android.content.SharedPreferences sp;
    private android.content.SharedPreferences.Editor editor;

    /**
     * @param context
     */
    public CNSharedPreferences(Context context) {
        try {
            if (sp == null)
                sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method to store the inputs to shared preference
     *
     * @param key   Stored key
     * @param value Stored value
     */
    public void putString(String key, String value) {
        editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Method to get the stored value based on a input key
     *
     * @param key Stored key
     * @return String
     */
    public String getString(String key) {
        return sp.getString(key, "");
    }

    public void putInt(String key, int value) {
        editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return sp.getInt(key, -1);
    }

    public void putLong(String key, long value) {
        editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key) {
        return sp.getLong(key, -1);
    }

    public boolean putBoolean(String key, boolean value) {
        editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
        return value;
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    /**
     * Method to remove specific stored value based on key
     *
     * @param key Stored key
     */
    public void remove(String key) {
        editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * Method to clear all the stored data in shared preference
     */
    public void clear() {
        editor = sp.edit();
        clearPreferences();
        editor.apply(); // commit changes
    }

    private void clearPreferences() {
        boolean isRegistered = getBoolean(Constants.SP_REFER_CODE_IS_REGISTERED);
        String referalCode = sp.getString(Constants.SP_REFER_CODE, "");
        if (referalCode.contains("utm_source=google-play")) {
            referalCode = "";
        }
        editor.clear();
        editor.apply();
        putBoolean(Constants.SP_REFER_CODE_IS_REGISTERED, isRegistered);
        putString(Constants.SP_REFER_CODE, referalCode);
    }

    public <T> void setList(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        putString(key, json);
    }

    public <T> List<T> getList(String key, Type typeOf) {
        String value = getString(key);
        return new Gson().fromJson(value, typeOf);
    }

}

package com.capitalnowapp.mobile.interfaces;


import com.capitalnowapp.mobile.constants.Constants;

public interface OnAsyncRequestCompleteListener {
    void onResponseReceived(String response);

    void onResponseReceived(String response, Constants.RequestCode requestCode);
}

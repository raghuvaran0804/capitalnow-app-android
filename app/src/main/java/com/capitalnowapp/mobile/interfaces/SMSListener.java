package com.capitalnowapp.mobile.interfaces;

public interface SMSListener {
    void onOTPReceived(String otpText);

    void onOTPTimeOut();
}

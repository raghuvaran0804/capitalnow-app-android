package com.capitalnowapp.mobile.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.interfaces.SMSListener;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSRetrieverBroadCastReceiver extends BroadcastReceiver {

    private static SMSListener smsListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                try {
                    String message1 = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.d("sms is...", "status is..." + status);
                    Log.d("sms is...", "msg is..." + message1);
                }catch (Exception e){
                    Log.d("sms is...", "exception is..." + e.getLocalizedMessage());
                }
                switch (status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get SMS message contents
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        // Extract one-time code from the message and complete verification
                        // by sending the code back to your server.

                        // Retrieve verification code from sms
                        String verificationCode = message.replace(Constants.SMS_RETRIEVER_HASH_KEY, "").replaceAll("[^0-9]", "");
                        if (smsListener != null)
                            smsListener.onOTPReceived(verificationCode);

                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Waiting for SMS timed out (5 minutes)
                        // Handle the error ...
                        if (smsListener != null)
                            smsListener.onOTPTimeOut();

                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bindListener(SMSListener listener) {
        smsListener = listener;
    }

    public static void unbindListener() {
        smsListener = null;
    }
}

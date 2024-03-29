package com.capitalnowapp.mobile.kotlin.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.capitalnowapp.mobile.interfaces.SMSListener
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SMSRetrieverBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: ")
        try {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val mStatus = extras!![SmsRetriever.EXTRA_STATUS] as Status?
                when (mStatus!!.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS message contents'
                        var message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                        message = extractDigits(message)
                        Log.d(TAG, "onReceive: success $message")
                        if (smsListener != null) smsListener!!.onOTPReceived(message)
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Waiting for SMS timed out (5 minutes)
                        Log.d(TAG, "onReceive: failure")
                        if (smsListener != null) smsListener!!.onOTPTimeOut()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "SmsBroadcastReceiver"
        private var smsListener: SMSListener? = null
        fun bindListener(listener: SMSListener?) {
            smsListener = listener
        }

        fun unbindListener() {
            smsListener = null
        }

        fun extractDigits(`in`: String?): String {
            val p = Pattern.compile("(\\d{6})")
            val m = p.matcher(`in`)
            return if (m.find()) {
                m.group(0)
            } else ""
        }
    }
}

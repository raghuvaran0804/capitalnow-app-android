package com.capitalnowapp.mobile.kotlin.push_fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.SplashScreen
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.Utility
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            // [START_EXCLUDE]
            // There are two types of messages data messages and notification messages. Data messages
            // are handled
            // here in onMessageReceived whether the app is in the foreground or background. Data
            // messages are the type
            // traditionally used with GCM. Notification messages are only received here in
            // onMessageReceived when the app
            // is in the foreground. When the app is in the background an automatically generated
            // notification is displayed.
            // When the user taps on the notification they are returned to the app. Messages
            // containing both notification
            // and data payloads are treated as notification messages. The Firebase console always
            // sends notification
            // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
            // [END_EXCLUDE]
            Utility.logResult(TAG, "********* remoteMessage = $remoteMessage")

            // TODO(developer): Handle FCM messages here.
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            Utility.logResult(TAG, "**** From: " + remoteMessage.from)
            Utility.logResult(TAG, "**** RemoteMessage : $remoteMessage")

            // Check if message contains a data payload.
            if (remoteMessage.data.size > 0) {
                Utility.logResult(TAG, "**** Message data payload: " + remoteMessage.data)
            }
            Utility.logResult(TAG, "*********** remoteMessage = " + remoteMessage.data["message"])
            var title: String? = ""
            var messageBody: String? = ""
            var imageUrl: String? = null
            var redirect_code = 1

            // Check if message contains a notification payload - when we send test message from Google FCM Cloud Messaging Notification this will execute.
            if (remoteMessage.notification != null) {
                title = remoteMessage.notification!!.title
                messageBody = remoteMessage.notification!!.body
                Utility.logResult(TAG, "*** Message Notification Title: $title")
                Utility.logResult(TAG, "*** Message Notification Body: $messageBody")
            } else {
                if (remoteMessage.data["title"] != null) title = remoteMessage.data["title"]
                messageBody = remoteMessage.data["message"]
                imageUrl = remoteMessage.data["image_url"]
                if (remoteMessage.data["redirect_code"] != null) redirect_code = remoteMessage.data["redirect_code"]!!.toInt()
            }
            Utility.logResult(TAG, "***** imageUrl : $imageUrl")
            counter++
            if (imageUrl == null || imageUrl.isEmpty()) {
                sendNotification(title, messageBody, imageUrl, null, redirect_code)
            } else {
                //To get a Bitmap image from the URL received
                val bitmap = getBitmapFromUrl(imageUrl)
                sendNotification(title, messageBody, imageUrl, bitmap, redirect_code)

                //getNotificationImage(title, messageBody, imageUrl);
            }
            val badgeCount = remoteMessage.data["badge"]
            Utility.logResult(TAG, "***** Badge Count : $badgeCount")

            /*if (badgeCount != null) {
                Preference_Helper.saveBadgeCount(this, badgeCount);

                if (!badgeCount.equals("") && !badgeCount.equals("0"))
                    Utils.setBadge(this, Integer.parseInt(badgeCount));
            }*/

            //counter++;

            //sendNotification(remoteMessage.getData().get("message"));

            //Intent fcm_rec = new Intent("update_notification_count");
            //LocalBroadcastManager.getInstance(this).sendBroadcast(fcm_rec);

            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // [END receive_message]
    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Utility.logResult(TAG, "********** onNewToken - Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]
    /**
     * Persist token to third-party servers.
     *
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement this method to send token to your app server.
        val sharedPreferences = CNSharedPreferences(applicationContext)
        sharedPreferences.putString(Constants.SP_DEVICE_TOKEN, token)
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    @SuppressLint("NewApi")
    private fun sendNotification(title: String?, messageBody: String?, imageUrl: String?, notificationIcon: Bitmap?, redirect_code: Int) {
        var title = title
        try {
            if (messageBody != null) {
                val sharedPreferences = CNSharedPreferences(applicationContext)
                val isUserLoggedIn = sharedPreferences.getBoolean(Constants.SP_IS_USER_LOGGED_IN)
                val bundle = Bundle()
                bundle.putString(Constants.BUNDLE_PN_TITLE, title)
                bundle.putString(Constants.BUNDLE_PN_MESSAGE, messageBody)
                bundle.putString(Constants.BUNDLE_PN_IMAGE_URL, imageUrl)
                bundle.putInt(Constants.BUNDLE_PN_REDIRECT_CODE, redirect_code)
                val intent = Intent(this, SplashScreen::class.java)
                /*if (isUserLoggedIn)
                    intent = new Intent(this, HomePageActivity.class);
                else
                    intent = new Intent(this, LoginActivity.class);*/
                intent.putExtra("message", messageBody)
                intent.putExtra("isPushNoti",true)
                intent.putExtra("redirectCode",redirect_code)
                intent.putExtras(bundle)
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val millis = System.currentTimeMillis().toString()
                val numbers: String = millis.substring(millis.length - 4)
                //7891027163917
                val pendingIntent = PendingIntent.getActivity(this,  numbers.toInt()/* Request code */, intent,
                        PendingIntent.FLAG_IMMUTABLE)
                if (title == null || title.isEmpty()) title = "CapitalNow.in"
                val channelId = getString(R.string.default_notification_channel_id)
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationBuilder = Notification.Builder(this, channelId)
                        .setSmallIcon(R.drawable.notification_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.notification_logo))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(Notification.BigTextStyle().bigText(messageBody))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                if (notificationIcon != null) {
                    notificationBuilder.style = Notification.BigPictureStyle().bigPicture(notificationIcon)

                }
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(channelId,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(counter /* ID of notification */, notificationBuilder.build())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     *To get a Bitmap image from the URL received
     * */
    fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } /*public void getNotificationImage(String title, String messageBody, String imageUrl) {
        new GeneratePictureStyleNotification(this, title, messageBody, imageUrl).execute();
    }

    public class GeneratePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {
        private Context mContext;
        private String title, message, imageUrl;

        public GeneratePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();

            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            sendNotification(title, message, result);
        }
    }*/

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        var counter = 0
    }
}
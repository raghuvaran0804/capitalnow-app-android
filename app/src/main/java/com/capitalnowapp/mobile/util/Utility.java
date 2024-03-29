package com.capitalnowapp.mobile.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.capitalnowapp.mobile.CapitalNowApp;
import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.beans.MasterData;
import com.capitalnowapp.mobile.constants.Constants;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class Utility {

    private static Utility instance;
    public static final String NO_VALUE = "N/A";

    public static Utility getInstance() {
        if (instance == null)
            instance = new Utility();
        return instance;
    }

    public static int getDeviceOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    public static boolean isLandscapeOrientation(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else
            return false;
    }

    public static int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static void displayToast(Context context, String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    public static void logResult(String tag, String message) {
        Log.d(tag, message);
    }

    //Method to convert UTC time to Local time
    public static String formatTime(long timeInMillSecs, String format) {
        Date utcTime = new Date(timeInMillSecs);
        DateFormat df = new SimpleDateFormat(format);
        return df.format(utcTime);
    }

    public static String formatDate(Date date, String format) {
        String dateStr = "";
        try {
            DateFormat df = new SimpleDateFormat(format);
            dateStr = df.format(date);
        } catch (Exception e) {
            return dateStr;
        }
        return dateStr;
    }

    public static Date convertStringToDate(String stringDate, String format) {
        Date date = null;
        try {
            DateFormat df = new SimpleDateFormat(format);
            date = df.parse(stringDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String formatYYYYMMDD_DDMMYYYY(String dbDate) throws ParseException {
        String result = "";
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy");
            simpleFormat.setTimeZone(TimeZone.getDefault());
            result = simpleFormat.format(dbFormat.parse(dbDate));
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static String formatYYYYMMDD_DDMMMYYYY(String dbDate) throws ParseException {
        String result = "";
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MMM-yyyy");
            simpleFormat.setTimeZone(TimeZone.getDefault());
            result = simpleFormat.format(dbFormat.parse(dbDate));
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static boolean isValidEmail(String emailId) {
        /*boolean flag = false;

        Pattern pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailId);

        if (matcher.matches())
            flag = true;
        else
            flag = false;

        return flag;*/

        // We can use below code also.
        return Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
    }

    public static boolean validString(String data) {
        return !TextUtils.isEmpty(data) && !TextUtils.isEmpty(data.trim())
                && !data.trim().equals("null");
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static int getPixelFromDips(Resources resources, float pixels) {
        // Get the screen's density scale
        final float scale = resources.getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public static String getHexaStringForColor(int colorCode) {
        return String.format("#%06X", (0xFFFFFF & colorCode));
    }

    /**
     * @param values
     * @param delimiter
     * @return
     */
    public static String convertArrayListToString(ArrayList values, String delimiter) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : values) {
            sb.append(obj);

            if (values.size() > 1)
                sb.append(delimiter);
        }

        if (values.size() > 1)
            sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    /**
     * Checks if the device is a tablet or a phone
     * <p/>
     * Note:
     * 320dp : a typical phone screen (240x320 ldpi, 320x480 mdpi, 480x800 hdpi, etc).
     * 480dp : a tweener tablet like the Streak (480x800 mdpi).
     * 600dp : a 7” tablet (600x1024 mdpi).
     * 720dp : a 10” tablet (720x1280 mdpi, 800x1280 mdpi, etc).
     *
     * @param context The context.
     * @return Returns true if the device is a Tablet
     */
    public static boolean isTabletDevice(Context context) {
        boolean isTablet = false;

        try {
            float smallestWidth = context.getResources().getConfiguration().smallestScreenWidthDp;

            // if smallestWidth >= 600 - Device is a 7" tablet & if smallestWidth >= 720 - Device is a 10" tablet
            if (smallestWidth >= 600) {
                isTablet = true;
            }

            //isTablet = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isTablet;
    }

    public static boolean isLargeScreen(Context context) {
        if (context.getResources().getDisplayMetrics().widthPixels > Constants.DEVICE_MIN_WIDTH)
            return true;
        else
            return false;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }

        return result;
    }

    public static Spanned getFormattedKeyAndColouredNormalText(String key, String text, String colorCode) {
        return fromHtml(String.format("%s : <font color='%s'> %s </font>", key, colorCode, text));
    }

    public static Spanned getFormattedColouredNormalText(String text, String colorCode) {
        return fromHtml(String.format("<font color='%s'> %s </font>", colorCode, text));
    }

    public static Spanned getFormattedColouredBoldText(String text, String colorCode) {
        return fromHtml(String.format("<b><font color='%s'> %s </font></b>", colorCode, text));
    }

    public static Spanned getFormattedKeyAndColouredBoldText(String key, String text, String colorCode) {
        return fromHtml(String.format("%s : <b><font color='%s'> %s </font></b>", key, colorCode, text));
    }

    public static Spanned getFormattedKeyAndColouredNumber(String key, int value, String colorCode) {
        return fromHtml(String.format("%s : <font color='%s'> %,d </font>", key, colorCode, value));
    }

    public static Spanned getFormattedColouredTextWithUnderscore(String value, String colorCode) {
        return fromHtml(String.format("<u><font color='%s'>%s</font></u>", colorCode, value));
    }

    public static Spanned getFormattedTextWithUnderscore(String value) {
        return fromHtml(String.format("<u>%s</u>", value));
    }

    public static Spanned getFormattedKeyAndColouredTextWithUnderscore(String key, String value, String colorCode) {
        return fromHtml(String.format("%s : <u><font color='%s'>%s</font></u>", key, colorCode, value));
    }

    public static Spanned getFormattedKeyAndColouredNumberWithUnderscore(String key, int value, String colorCode) {
        return fromHtml(String.format("%s : <u><font color='%s'>%s</font></u>", key, colorCode, value));
    }

    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * Checks if the app has permission to do required operation
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    @TargetApi(23)
    public static boolean verifyAndRequestUserForPermissions(Activity activity, String checkPermission, String[] requiredPermissions, int permissionRequestCode) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Check if we have write permission
            int permission = ContextCompat.checkSelfPermission(activity, checkPermission);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                //ActivityCompat.requestPermissions(activity, requiredPermissions, permissionRequestCode);
            } else
                return true;

        } else {
            return true;
        }

        return false;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "img", null);
        return Uri.parse(path);
    }

    public static String getMapKeyFromValue(Map<String, String> map, String value) {
        String key = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equalsIgnoreCase(entry.getValue())) {
                key = entry.getKey();
                break;
            }
        }

        return key;
    }

    /**
     * Method to get device UDID or UniqueId
     */
    public String getDeviceUniqueId(Activity activity) {
        String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + "," + model;
    }

    public static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return "" + width + "*" + height + "";
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public void openAppInPlayStore(Context context, String packageName, JSONObject response) {
        /*try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }*/
        try {
            if(response.has("redirection_link")){
                String redirectionLink = String.valueOf(response.getString("redirection_link"));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(redirectionLink));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                if (intent.resolveActivity(context.getPackageManager()) != null)
                    context.startActivity(intent);
            }else {
                String market_uri = "https://play.google.com/store/apps/details?id=" + packageName;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(market_uri));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                if (intent.resolveActivity(context.getPackageManager()) != null)
                    context.startActivity(intent);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * hide keyboard from different activities
     *
     * @param mActivity
     * @param mView
     */
    public static void hideKeyboard(Activity mActivity, View mView) {
        try {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getOnlyNamesFromMasterDataList(ArrayList<MasterData> masterData) {
        ArrayList<String> namesList = new ArrayList<>();

        if (masterData != null) {
            for (MasterData data : masterData) {
                namesList.add(data.getName());
            }
        }

        return namesList;
    }

    public int getIndexForValueFromArrayList(ArrayList<MasterData> list, String value) {
        int index = 0;

        if (list != null) {
            for (MasterData masterData : list) {
                if (masterData.getId().equals(value)) {
                    return index;
                }

                index++;
            }
        }

        return index;
    }

    public static String rightSubString(String value, int length) {
        // To get right characters from a string, change the begin index.
        return value.substring(value.length() - length);
    }

    public static boolean validateSalary(String sal) {
        return sal.length() > 0 && !sal.startsWith("0");
    }

    public static String getTimeAgo(String date, Context ctx) {
        String timeAgo = "";
        try {
            if (date == null) {
                return null;
            }
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // dbFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            long time = dbFormat.parse(date).getTime();
            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }

            int dim = getTimeDistanceInMinutes(time);


            if (dim == 0) {
                timeAgo = "Just Now";
                return timeAgo;
            } else if (dim == 1) {
                return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
            } else if (dim >= 2 && dim <= 59) {
                timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
            } else if (dim >= 60 && dim <= 119) {
                timeAgo = ctx.getResources().getString(R.string.date_util_term_an) + " " + ctx.getResources().getString(R.string.date_util_unit_hour);
            } else if (dim >= 120 && dim <= 1439) {
                timeAgo = (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
            } else if (dim >= 1440 && dim <= 2519) {
                timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
            } else if (dim >= 2520 && dim <= 43199) {
                timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
            } else if (dim >= 43200 && dim <= 86399) {
                timeAgo = ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_month);
            } else if (dim >= 86400 && dim <= 525599) {
                timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
            } else if (dim >= 525600 && dim <= 655199) {
                timeAgo = ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
            } else if (dim >= 655200 && dim <= 914399) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
            } else if (dim >= 914400 && dim <= 1051199) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
            } else {
                timeAgo = (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = System.currentTimeMillis() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    public static Spannable increaseFontSizeForPath(Spannable spannable, String path, float increaseTime) {
        int startIndexOfPath = spannable.toString().indexOf(path);
        spannable.setSpan(new RelativeSizeSpan(increaseTime), startIndexOfPath,
                startIndexOfPath + path.length(), 0);

        spannable.setSpan(new StyleSpan(Typeface.BOLD), startIndexOfPath, startIndexOfPath + path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannable;
    }

    public static String getAppCategory(Activity thisActivity, String packageName) {
        final PackageManager pm = thisActivity.getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        CharSequence catName = "unknown";
        if (ai != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                catName = ApplicationInfo.getCategoryTitle(thisActivity, ai.category);
            }
        }
        catName = catName!=null? catName: "unknown";
        return catName.toString();
    }
    // common method to send events to mixpanel
    public static void pushEvent(JSONObject obj, String eventName){
        try{
            CapitalNowApp.mp.track(eventName, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserLoginData implements Serializable {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_status_id")
    @Expose
    private String userStatusId;
    @SerializedName("freshchat_id")
    @Expose
    private String freshChatId;
    @SerializedName("google_contacts_saved")
    @Expose
    private int googleContactsSaved;
    @SerializedName("contacts_saved")
    @Expose
    private String contactsSaved;
    @SerializedName("location_saved")
    @Expose
    private String locationSaved;
    @SerializedName("sms_saved")
    @Expose
    private String smsSaved;
    @SerializedName("sms_limit")
    @Expose
    private int smsLimit;
    @SerializedName("razor_pay_api_key")
    @Expose
    private String razorPayApiKey;
    @SerializedName("call_log_saved")
    @Expose
    private String callLogSaved;
    @SerializedName("call_log_limit")
    @Expose
    private int callLogLimit;
    @SerializedName("active_profile")
    @Expose
    private int activeProfile;
    @SerializedName("freeze_one")
    @Expose
    private int freezeOne;
    @SerializedName("show_permissions")
    @Expose
    private int showPermissions;
    @SerializedName("user_data")
    @Expose
    private UserData userData;
    @SerializedName("has_taken_first_loan")
    @Expose
    private int hasTakenFirstLoan;

    public String getUserId() {
        return userId;
    }

    public String getUserStatusId() {
        return userStatusId;
    }

    public String getFreshChatId() {
        return freshChatId;
    }

    public int getGoogleContactsSaved() {
        return googleContactsSaved;
    }

    public String getContactsSaved() {
        return contactsSaved;
    }

    public String getLocationSaved() {
        return locationSaved;
    }

    public String getSmsSaved() {
        return smsSaved;
    }

    public int getSmsLimit() {
        return smsLimit;
    }

    public String getRazorPayApiKey() {
        return razorPayApiKey;
    }

    public String getCallLogSaved() {
        return callLogSaved;
    }

    public int getCallLogLimit() {
        return callLogLimit;
    }

    public int getActiveProfile() {
        return activeProfile;
    }

    public int getFreezeOne() {
        return freezeOne;
    }

    public int getShowPermissions() {
        return showPermissions;
    }

    public UserData getUserData() {
        return userData;
    }
    public int getHasTakenFirstLoan() {
        return hasTakenFirstLoan;
    }

    @Override
    public String toString() {
        return "UserLoginData{" +
                "userId='" + userId + '\'' +
                ", userStatusId='" + userStatusId + '\'' +
                ", freshChatId='" + freshChatId + '\'' +
                ", googleContactsSaved=" + googleContactsSaved +
                ", contactsSaved='" + contactsSaved + '\'' +
                ", locationSaved='" + locationSaved + '\'' +
                ", smsSaved='" + smsSaved + '\'' +
                ", smsLimit=" + smsLimit +
                ", callLogSaved='" + callLogSaved + '\'' +
                ", callLogLimit=" + callLogLimit +
                ", activeProfile=" + activeProfile +
                ", freezeOne=" + freezeOne +
                ", showPermissions=" + showPermissions +
                ", userData=" + userData +
                ", hasTakenFirstLoan=" + hasTakenFirstLoan +
                ", razorPayApiKey=" + razorPayApiKey +
                '}';
    }
}

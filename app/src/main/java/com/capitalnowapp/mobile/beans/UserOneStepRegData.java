package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserOneStepRegData implements Serializable {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_status_id")
    @Expose
    private String userStatusId;
    @SerializedName("google_contacts_saved")
    @Expose
    private int googleContactsSaved;
    @SerializedName("contacts_saved")
    @Expose
    private String contactsSaved;
    @SerializedName("sms_saved")
    @Expose
    private String smsSaved;
    @SerializedName("sms_limit")
    @Expose
    private int smsLimit;
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
    @SerializedName("user_one_step_data")
    @Expose
    private UserOneStepData userOneStepData;

    public String getUserId() {
        return userId;
    }

    public String getUserStatusId() {
        return userStatusId;
    }

    public int getGoogleContactsSaved() {
        return googleContactsSaved;
    }

    public String getContactsSaved() {
        return contactsSaved;
    }

    public String getSmsSaved() {
        return smsSaved;
    }

    public int getSmsLimit() {
        return smsLimit;
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

    public UserOneStepData getUserOneStepData() {
        return userOneStepData;
    }

    @Override
    public String toString() {
        return "UserOneStepRegData{" +
                "userId='" + userId + '\'' +
                ", userStatusId='" + userStatusId + '\'' +
                ", googleContactsSaved=" + googleContactsSaved +
                ", contactsSaved='" + contactsSaved + '\'' +
                ", smsSaved='" + smsSaved + '\'' +
                ", smsLimit=" + smsLimit +
                ", callLogSaved='" + callLogSaved + '\'' +
                ", callLogLimit=" + callLogLimit +
                ", activeProfile=" + activeProfile +
                ", freezeOne=" + freezeOne +
                ", userOneStepData=" + userOneStepData +
                '}';
    }
}

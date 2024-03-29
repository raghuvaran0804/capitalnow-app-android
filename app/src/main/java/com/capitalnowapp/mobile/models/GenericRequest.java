package com.capitalnowapp.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GenericRequest implements Serializable {

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("api_key")
    @Expose
    private String apiKey;

    @SerializedName("request_input")
    @Expose
    private String dataStr;

    @SerializedName("device_unique_id")
    @Expose
    private String deviceUniqueId = "";


    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

}

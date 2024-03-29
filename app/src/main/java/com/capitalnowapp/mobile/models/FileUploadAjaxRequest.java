package com.capitalnowapp.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FileUploadAjaxRequest implements Serializable {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("urls_for")
    @Expose
    private String urlsFor;
    @SerializedName("file_urls")
    @Expose
    private String fileUrls = null;

    @SerializedName("device_unique_id")
    @Expose
    private String deviceUniqueId = "";

    @SerializedName("token")
    @Expose
    private String usertoken;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrlsFor() {
        return urlsFor;
    }

    public void setUrlsFor(String urlsFor) {
        this.urlsFor = urlsFor;
    }

    public String getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(String fileUrls) {
        this.fileUrls = fileUrls;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }
}

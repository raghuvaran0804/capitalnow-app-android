package com.capitalnowapp.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GenericResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("status_v")
    @Expose
    private Boolean statusv;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("status_redirect")
    @Expose
    private Integer statusRedirect;

    @SerializedName("token")
    @Expose
    private String usertoken;

    @SerializedName("status_code")
    @Expose
    private int statusCode;

    @SerializedName("code")
    @Expose
    private int Code;

    @SerializedName("data")
    @Expose
    private String dataStr = null;

    @SerializedName("reference_redirection")
    @Expose
    private boolean referenceRedirection = false;

    public Boolean getStatus() {
        return status;
    }

    public Integer getStatusRedirect() {
        return statusRedirect;
    }

    public void setStatusRedirect(Integer statusRedirect) {
        this.statusRedirect = statusRedirect;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    public boolean isReferenceRedirection() {
        return referenceRedirection;
    }

    public void setReferenceRedirection(boolean referenceRedirection) {
        this.referenceRedirection = referenceRedirection;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Boolean getStatusv() {
        return statusv;
    }

    public void setStatusv(Boolean statusv) {
        this.statusv = statusv;
    }
}

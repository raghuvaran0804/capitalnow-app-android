package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserTermsData implements Serializable {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("find_text")
    @Expose
    private String findText;
    @SerializedName("replace_links")
    @Expose
    private String replaceLinks;
    @SerializedName("ba_message")
    @Expose
    private String ba_message;
    @SerializedName("ba_find_text")
    @Expose
    private String ba_find_text;
    @SerializedName("ba_replace_linksX")
    @Expose
    private String ba_replace_links;
    @SerializedName("ba_uncheck_info")
    @Expose
    private String ba_uncheck_info;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFindText() {
        return findText;
    }

    public void setFindText(String findText) {
        this.findText = findText;
    }

    public String getReplaceLinks() {
        return replaceLinks;
    }

    public void setReplaceLinks(String replaceLinks) {
        this.replaceLinks = replaceLinks;
    }

    public String getBa_message() {
        return ba_message;
    }

    public void setBa_message(String ba_message) {
        this.ba_message = ba_message;
    }

    public String getBa_find_text() {
        return ba_find_text;
    }

    public void setBa_find_text(String ba_find_text) {
        this.ba_find_text = ba_find_text;
    }

    public String getBa_replace_links() {
        return ba_replace_links;
    }

    public void setBa_replace_links(String ba_replace_links) {
        this.ba_replace_links = ba_replace_links;
    }

    public String getBa_uncheck_info() {
        return ba_uncheck_info;
    }

    public void setBa_uncheck_info(String ba_uncheck_info) {
        this.ba_uncheck_info = ba_uncheck_info;
    }
}

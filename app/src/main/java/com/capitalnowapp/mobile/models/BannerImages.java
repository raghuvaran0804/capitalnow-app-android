package com.capitalnowapp.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BannerImages implements Serializable {

    @SerializedName("banner_link")
    @Expose
    private String bannerLink;
    @SerializedName("is_clickable")
    @Expose
    private String isClickable;
    @SerializedName("b_type")
    @Expose
    private String bType;
    @SerializedName("b_btn_position")
    @Expose
    private String bBtnPosition;

    public String getBannerLink() {
        return bannerLink;
    }

    public void setBannerLink(String bannerLink) {
        this.bannerLink = bannerLink;
    }

    public String getIsClickable() {
        return isClickable;
    }

    public void setIsClickable(String isClickable) {
        this.isClickable = isClickable;
    }

    public String getBType() {
        return bType;
    }

    public void setBType(String bType) {
        this.bType = bType;
    }

    public String getBBtnPosition() {
        return bBtnPosition;
    }

    public void setBBtnPosition(String bBtnPosition) {
        this.bBtnPosition = bBtnPosition;
    }
}

package com.capitalnowapp.mobile.beans;

import org.json.JSONArray;

import java.io.Serializable;

public class SocialRegistration implements Serializable {
    private String oauthProvider;
    private String oauthId;
    private String name;
    private String email;
    private String gender;
    private String locale;
    private String link;
    private int count;
    private String picture;
    private String mobileVersion;
    private String location;
    private String deviceUniqueId;
    private String deviceToken;
    private JSONArray phoneNumbersArray;
    private JSONArray emailAddressesArray;
    private String mobile_no;
    private String user_id;


    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getMobileVersion() {
        return mobileVersion;
    }

    public void setMobileVersion(String mobileVersion) {
        this.mobileVersion = mobileVersion;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public JSONArray getPhoneNumbersArray() {
        return phoneNumbersArray;
    }

    public void setPhoneNumbersArray(JSONArray phoneNumbersArray) {
        this.phoneNumbersArray = phoneNumbersArray;
    }

    public JSONArray getEmailAddressesArray() {
        return emailAddressesArray;
    }

    public void setEmailAddressesArray(JSONArray emailAddressesArray) {
        this.emailAddressesArray = emailAddressesArray;
    }

    @Override
    public String toString() {
        return "SocialRegistration{" +
                "oauthProvider='" + oauthProvider + '\'' +
                ", oauthId='" + oauthId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", locale='" + locale + '\'' +
                ", link='" + link + '\'' +
                ", count=" + count +
                ", picture='" + picture + '\'' +
                ", mobileVersion='" + mobileVersion + '\'' +
                ", location='" + location + '\'' +
                ", deviceUniqueId='" + deviceUniqueId + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", phoneNumbersArray=" + phoneNumbersArray +
                ", emailAddressesArray=" + emailAddressesArray +
                '}';
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

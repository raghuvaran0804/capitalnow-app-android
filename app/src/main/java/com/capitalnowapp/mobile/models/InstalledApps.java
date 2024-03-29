package com.capitalnowapp.mobile.models;

public class InstalledApps {



    private String api_key;
    private String appname;
    private String pname;
    private String versionName;
    private String versionCode;
    private String category;

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}
    public String getApi_key() {return api_key;}
    public void setApi_key(String api_key) {this.api_key = api_key;}
    public String getAppname() {
        return appname;
    }
    public void setAppname(String appname) {
        this.appname = appname;
    }
    public String getPname() {
        return pname;
    }
    public void setPname(String pname) {
        this.pname = pname;
    }
    public String getVersionName() {
        return versionName;
    }
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    public String getVersionCode() {
        return versionCode;
    }
    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}

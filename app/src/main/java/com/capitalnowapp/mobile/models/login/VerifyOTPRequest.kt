package com.capitalnowapp.mobile.models.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VerifyOTPRequest {
    @SerializedName("mobile_no")
    @Expose
    var mobileNo: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("otp")
    @Expose
    var otp: String? = null

    @SerializedName("device_unique_id")
    @Expose
    var deviceUniqueId: String? = null

    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = null

    @SerializedName("mobile_version")
    @Expose
    var mobileVersion: String? = null

    @SerializedName("current_location")
    @Expose
    var currentLocation: String? = null


    @SerializedName("device_name")
    @Expose
    var deviceName: String? = ""

    @SerializedName("device_resolution")
    @Expose
    var deviceResolution: String? = ""

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null

    @SerializedName("platform")
    @Expose
    var platform: String? = null


    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }
}

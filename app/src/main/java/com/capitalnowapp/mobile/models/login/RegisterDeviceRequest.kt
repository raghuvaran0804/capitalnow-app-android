package com.capitalnowapp.mobile.models.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RegisterDeviceRequest {


    @SerializedName("device_id")
    @Expose
    var deviceUniqueId: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = null

    @SerializedName("device_type")
    @Expose
    var devicetype: String? = null

    @SerializedName("long")
    @Expose
    var long1: String? = null

    @SerializedName("lat")
    @Expose
    var lat: String? = null

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null


    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }
}
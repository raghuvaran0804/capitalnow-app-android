package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class AnalyseCapabilityReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null

    @SerializedName("device_type")
    @Expose
    var devicetype: String? = null


    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }

    fun setdevicetype(devicetype: String) {
        this.devicetype = devicetype
    }

}
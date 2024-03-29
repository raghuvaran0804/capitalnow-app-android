package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CreateMandateReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("platform")
    @Expose
    var platform: String? = null

    @SerializedName("current_location")
    @Expose
    var currentLocation: String? = null

    @SerializedName("device_id")
    @Expose
    var deviceId: String? = null
}
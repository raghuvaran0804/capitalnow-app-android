package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AadharOtpReq : Serializable{

    @SerializedName("long")
    @Expose
    var long: String? = ""

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("lat")
    @Expose
    var lat: String? = ""

    @SerializedName("aadharno")
    @Expose
    var aadharno: String? = null

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null

    @SerializedName("aadhar_captcha")
    @Expose
    var aadharCaptcha: String? = null

    @SerializedName("session_id")
    @Expose
    var sessionId: String? = null


    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }
    
}
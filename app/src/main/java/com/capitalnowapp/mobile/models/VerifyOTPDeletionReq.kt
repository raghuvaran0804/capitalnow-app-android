package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VerifyOTPDeletionReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("mobile_no")
    @Expose
    var mobileNo: String? = null

    @SerializedName("otp")
    @Expose
    var otp: String? = null
}
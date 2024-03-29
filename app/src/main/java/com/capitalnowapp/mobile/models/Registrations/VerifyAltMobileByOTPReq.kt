package com.capitalnowapp.mobile.models.Registrations

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VerifyAltMobileByOTPReq :Serializable{

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

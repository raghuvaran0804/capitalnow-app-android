package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyLoansResponse {
    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = false

    @SerializedName("status_code")
    @Expose
    var statusCode: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = ""

    @SerializedName("ph_image")
    @Expose
    var defaultImg: String? = ""

    @SerializedName("user_data")
    @Expose
    var userData: UserData? = null
}
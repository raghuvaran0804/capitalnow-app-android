package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class WebLinkRes :Serializable{

    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("weblink")
    @Expose
    var weblink: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

}

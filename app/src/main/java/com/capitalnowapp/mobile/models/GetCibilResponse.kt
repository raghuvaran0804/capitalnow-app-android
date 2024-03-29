package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetCibilResponse :Serializable {
    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("user_status_id")
    @Expose
    var userStatusId: Int? = null

    @SerializedName("show_popup")
    @Expose
    var showPopup: Int? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null
}
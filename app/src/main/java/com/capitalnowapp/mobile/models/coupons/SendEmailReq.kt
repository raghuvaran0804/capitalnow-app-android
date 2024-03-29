package com.capitalnowapp.mobile.models.coupons

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SendEmailReq : Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("coupon_selected")
    @Expose
    var couponSelected: String? = null

    @SerializedName("to_email")
    @Expose
    var toEmail: String? = null

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null


    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }
}
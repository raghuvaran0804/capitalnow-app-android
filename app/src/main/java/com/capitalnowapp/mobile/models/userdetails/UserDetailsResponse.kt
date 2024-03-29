package com.capitalnowapp.mobile.models.userdetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserDetailsResponse : Serializable {
    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("data")
    @Expose
    var userDetails: UserDetails? = null

    @SerializedName("status_code")
    @Expose
    var statusCode: Int = 0


}
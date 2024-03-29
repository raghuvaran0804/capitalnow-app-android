package com.capitalnowapp.mobile.models.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ProfileResponse : Serializable {

    @SerializedName("razor_pay_api_key")
    @Expose
    val razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("code")
    @Expose
    val code: Int? = null

    @SerializedName("status_code")
    @Expose
    val statusCode: Int? = null

    @SerializedName("data")
    @Expose
    val data: List<ProfileBasic>? = null
}
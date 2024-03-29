package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VerifyAlternateEmailReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("sec_email")
    @Expose
    var secEmail: String? = null

    @SerializedName("is_official")
    @Expose
    var isOfficial: Int? = 0
}
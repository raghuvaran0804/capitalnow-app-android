package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetCaptchaResponse : Serializable{

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("sessionId")
    @Expose
    var sessionId: String? = null

    @SerializedName("captchaImage")
    @Expose
    var captchaImage: String? = null

    @SerializedName("captchaRequired")
    @Expose
    var captchaRequired: Boolean? = null
}

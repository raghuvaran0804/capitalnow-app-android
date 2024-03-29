package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SendOTPDeletionResponse : Serializable{

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

}

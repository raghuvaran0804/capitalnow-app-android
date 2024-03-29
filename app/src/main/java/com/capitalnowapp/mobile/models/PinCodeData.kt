package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PinCodeData : Serializable{

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("Status")
    @Expose
    var status: String? = null

    @SerializedName("PostOffice")
    @Expose
    var postOffice: List<PostOffice>? = null
}
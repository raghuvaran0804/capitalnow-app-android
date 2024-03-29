package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BannerImageResponse : Serializable{

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("images")
    @Expose
    var images: List<HomeBannerImage>? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
}
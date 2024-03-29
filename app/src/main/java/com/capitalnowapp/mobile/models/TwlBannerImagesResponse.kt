package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlBannerImagesResponse : Serializable {
    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("images")
    @Expose
    var images: List<Image>? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
}
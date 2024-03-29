package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class OfferScrollResponse : Serializable {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("color")
    @Expose
    var color: String? = null

    @SerializedName("new_offer")
    @Expose
    var newOffer: Int? = null

    @SerializedName("data")
    @Expose
    var data: String? = null

    @SerializedName("change_color")
    @Expose
    var changeColor: ScrollChangeColor? = null
}
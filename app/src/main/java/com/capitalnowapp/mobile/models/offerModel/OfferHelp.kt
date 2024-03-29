package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class OfferHelp : Serializable {

    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("phone")
    @Expose
    var phone: List<String>? = null

    @SerializedName("email")
    @Expose
    var email: List<String>? = null

}

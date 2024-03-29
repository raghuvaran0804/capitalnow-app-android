package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PromoCodeData : Serializable{
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("interest_offer")
    @Expose
    var array: List<Int>? = null
}

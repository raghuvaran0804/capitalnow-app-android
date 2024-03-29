package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class StatusData : Serializable{
    @SerializedName("thank_you_1")
    @Expose
    var thankYou1: String? = null

    @SerializedName("thank_you_2")
    @Expose
    var thankYou2: String? = null

    @SerializedName("denied_1")
    @Expose
    var denied1: String? = null

    @SerializedName("denied_2")
    @Expose
    var denied2: String? = null

    @SerializedName("hold_1")
    @Expose
    var hold1: String? = null

    @SerializedName("hold_2")
    @Expose
    var hold2: String? = null

}

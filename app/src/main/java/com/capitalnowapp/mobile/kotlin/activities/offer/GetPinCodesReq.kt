package com.capitalnowapp.mobile.kotlin.activities.offer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetPinCodesReq : Serializable{
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("p_city")
    @Expose
    var pCity: String? = null
}

package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetAreaListReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("city_name")
    @Expose
    var cityName: String? = null

    @SerializedName("dealer_id")
    @Expose
    var dealerId: String? = null
}
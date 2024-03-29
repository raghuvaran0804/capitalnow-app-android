package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetBrandListReq : Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("city_name")
    @Expose
    var cityName: String? = null

    @SerializedName("area_name")
    @Expose
    var areaName: String? = null
}
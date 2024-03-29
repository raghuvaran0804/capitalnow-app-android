package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetDealerListReq : Serializable{
    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("request_input")
    @Expose
    var requestInput: String? = null

    @SerializedName("city_name")
    @Expose
    var cityName: String? = null

    @SerializedName("area_name")
    @Expose
    var areaName: String? = null

    @SerializedName("brand")
    @Expose
    var brand: String? = null
}
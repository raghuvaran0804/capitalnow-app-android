package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetColorListReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("dealer_id")
    @Expose
    var dealerId: String? = null

    @SerializedName("brand")
    @Expose
    var brand: String? = null

    @SerializedName("model")
    @Expose
    var model: String? = null

    @SerializedName("twlv_varient")
    @Expose
    var twlvVarient: String? = null

}
package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetVarientListReq : Serializable{
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
}
package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetVehiclesListReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("dealer_id")
    @Expose
    var dealerId: String? = null
    @SerializedName("brand")
    @Expose
    var brand: String? = null
}
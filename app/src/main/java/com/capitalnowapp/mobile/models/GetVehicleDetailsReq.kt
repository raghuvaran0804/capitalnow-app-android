package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetVehicleDetailsReq : Serializable{
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("city_name")
    @Expose
    var cityName: String? = null

    @SerializedName("dealer_id")
    @Expose
    var dealerId: String? = null

    @SerializedName("dealer_name")
    @Expose
    var dealerName: String? = null

    @SerializedName("area_name")
    @Expose
    var areaName: String? = null

    @SerializedName("vehicle_id")
    @Expose
    var vehicleId: String? = null

    @SerializedName("varient_name")
    @Expose
    var varientName: String? = null

    @SerializedName("color")
    @Expose
    var color: String? = null

    @SerializedName("brand")
    @Expose
    var brand: String? = null


}
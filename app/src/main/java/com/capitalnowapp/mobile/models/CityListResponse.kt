package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CityListResponse : Serializable {
    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("current_city")
    @Expose
    var currentCity: String? = ""

    @SerializedName("table_data")
    @Expose
    var cityTableData: List<CityTableData>? = null
}
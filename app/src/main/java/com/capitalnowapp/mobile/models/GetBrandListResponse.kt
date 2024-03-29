package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetBrandListResponse :Serializable {
    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("table_data")
    @Expose
    var brandTableData: List<BrandTableData>? = null
}
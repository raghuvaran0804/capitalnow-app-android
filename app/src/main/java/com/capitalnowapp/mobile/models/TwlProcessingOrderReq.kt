package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlProcessingOrderReq :Serializable {
    @SerializedName("selected_ids")
    @Expose
    var selectedIds: Int? = null

    @SerializedName("wclient")
    @Expose
    var wclient: Int? = null

    @SerializedName("device_unique_id")
    @Expose
    var deviceUniqueId: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("amount")
    @Expose
    var amount: Int? = null
}
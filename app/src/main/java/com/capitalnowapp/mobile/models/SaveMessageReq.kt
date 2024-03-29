package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveMessageReq :Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("data_type")
    @Expose
    var dataType: Int? = null

    @SerializedName("device_unique_id")
    @Expose
    var deviceUniqueId: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Sms>? = null

}
package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MonitoringCardData : Serializable {

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("button")
    @Expose
    var button: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("bank_name")
    @Expose
    var bankName: String? = null

    @SerializedName("bank_code")
    @Expose
    var bankCode: String? = null

    @SerializedName("readonly")
    @Expose
    var readOnly: Boolean? = null
}
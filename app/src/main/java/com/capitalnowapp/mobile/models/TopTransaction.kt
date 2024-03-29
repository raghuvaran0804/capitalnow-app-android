package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TopTransaction : Serializable {
    @SerializedName("sendorCode")
    @Expose
    var sendorCode: String? = null

    @SerializedName("senderName")
    @Expose
    var senderName: String? = null

    @SerializedName("senderOrgType")
    @Expose
    var senderOrgType: String? = null

    @SerializedName("transactionAmount")
    @Expose
    var transactionAmount: Float? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("logoUrl")
    @Expose
    var logoUrl: String? = null

}

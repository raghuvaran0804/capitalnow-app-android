package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SpendsBanksAccount : Serializable {

    @SerializedName("senderCode")
    @Expose
    var senderCode: String? = null

    @SerializedName("senderName")
    @Expose
    var senderName: String? = null

    @SerializedName("senderOrgType")
    @Expose
    var senderOrgType: String? = null

    @SerializedName("transactionType")
    @Expose
    var transactionType: String? = null

    @SerializedName("accountNumber")
    @Expose
    var accountNumber: String? = null

    @SerializedName("accountBalance")
    @Expose
    var accountBalance: Double? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("logoUrl")
    @Expose
    var logoUrl: String? = null

}

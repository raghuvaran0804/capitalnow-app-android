package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CheckPromoCodeReq : Serializable {
    @SerializedName("is_pl")
    @Expose
    var isPl: String? = null

    @SerializedName("loan_type")
    @Expose
    var loanType: String? = null

    @SerializedName("platform")
    @Expose
    var platform: String? = null

    @SerializedName("amount")
    @Expose
    var amount: Int? = null

    @SerializedName("emi_count")
    @Expose
    var emiCount: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("code")
    @Expose
    var code: String? = null
}
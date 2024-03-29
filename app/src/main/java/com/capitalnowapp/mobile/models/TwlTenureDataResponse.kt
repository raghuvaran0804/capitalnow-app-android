package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlTenureDataResponse : Serializable {
    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("minimum_amount")
    @Expose
    var minAmount: Int? = 0

    @SerializedName("maximum_amount")
    @Expose
    var maxAmount: Int? = 0

    @SerializedName("preferred_month")
    @Expose
    var preferredMonth: Int? = 0

    @SerializedName("twl_tenure_data")
    @Expose
    var twlTenureData: List<TwlTenureData>? = null

    @SerializedName("eligibility_amount")
    @Expose
    var eligibilityAmount: Int? = 0

    @SerializedName("amount_text")
    @Expose
    var amountText: String? = null

    @SerializedName("eligibility_message")
    @Expose
    var eligibilityMessage: String? = null

}
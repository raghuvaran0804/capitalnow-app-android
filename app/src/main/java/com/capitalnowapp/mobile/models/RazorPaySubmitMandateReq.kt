package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RazorPaySubmitMandateReq : Serializable {
    @SerializedName("order_id")
    @Expose
    var orderId: String? = null

    @SerializedName("customer_id")
    @Expose
    var customerId: String? = null

    @SerializedName("payment_id")
    @Expose
    var paymentId: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null
}
package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LoanStatusResponse : Serializable {
    @SerializedName("razor_pay_api_key")
    @Expose
    val razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: LoanStatusDetails? = null
}